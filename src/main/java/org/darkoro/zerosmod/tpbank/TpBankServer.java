package org.darkoro.zerosmod.tpbank;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import org.darkoro.zerosmod.config.TpBankConfig;
import org.darkoro.zerosmod.network.ServerTaskScheduler;
import org.darkoro.zerosmod.tpbank.network.TpBankNetwork;
import org.darkoro.zerosmod.tpbank.network.TpBankRequest;
import org.darkoro.zerosmod.tpbank.network.TpBankSnapshot;

/** Server side of the TP Bank screen. Only a session opened by the server can withdraw. */
public final class TpBankServer {
  public static final TpBankServer INSTANCE = new TpBankServer();
  private static final SecureRandom RANDOM = new SecureRandom();
  private static final Map<UUID, Long> SESSIONS = new ConcurrentHashMap<>();

  private TpBankServer() {}

  public static void open(EntityPlayerMP player) {
    long session = RANDOM.nextLong();
    SESSIONS.put(player.getUniqueID(), session);
    TpBankSnapshot snapshot = snapshot(player, session, 0, null, false);
    snapshot.open = true;
    TpBankNetwork.channel.sendTo(snapshot, player);
  }

  public static void enqueue(EntityPlayerMP player, TpBankRequest request) {
    ServerTaskScheduler.schedule(() -> handle(player, request));
  }

  private static void handle(EntityPlayerMP player, TpBankRequest request) {
    if (player.playerNetServerHandler == null || player.isDead) {
      return;
    }
    Long session = SESSIONS.get(player.getUniqueID());
    if (session == null || session != request.session) {
      if (request.action != TpBankRequest.CLOSE) {
        TpBankSnapshot closed = new TpBankSnapshot();
        closed.session = request.session;
        closed.requestId = request.requestId;
        closed.close = true;
        TpBankNetwork.channel.sendTo(closed, player);
      }
      return;
    }
    switch (request.action) {
      case TpBankRequest.CLOSE:
        SESSIONS.remove(player.getUniqueID(), session);
        return;
      case TpBankRequest.WITHDRAW:
      case TpBankRequest.WITHDRAW_MAX:
        long amount = request.action == TpBankRequest.WITHDRAW_MAX ? Long.MAX_VALUE : request.amount;
        reply(player, request, withdraw(player, amount));
        return;
      default:
        reply(player, request, "Balances refreshed.");
    }
  }

  /** @return status message, prefixed with '!' when it is an error */
  private static String withdraw(EntityPlayerMP player, long amount) {
    if (!TpBankConfig.isEnabled() || !TpBank.isUnlocked(player)) {
      return "!" + lockedMessage();
    }
    if (amount <= 0) {
      return "!Enter a whole number of TP greater than zero.";
    }
    long banked = TpBank.getBanked(player);
    if (banked <= 0) {
      return "!Your TP Bank is empty.";
    }
    if (TpBank.getCurrentTP(player) >= TpBank.getMaxTP()) {
      return "!You already have the maximum TP. Spend some before withdrawing.";
    }
    if (amount != Long.MAX_VALUE && TpBank.costOf(amount) > banked) {
      return "!Not enough TP in the bank. You can withdraw up to "
          + TpBank.format(TpBank.getWithdrawable(player)) + " TP.";
    }
    long received = TpBank.withdraw(player, amount);
    if (received <= 0) {
      return "!The withdrawal failed. Your bank balance was not changed.";
    }
    String capped = received < amount && amount != Long.MAX_VALUE ? " (limited by the TP cap)" : "";
    long fee = TpBank.costOf(received) - received;
    String feeText = fee > 0 ? " Fee: " + TpBank.format(fee) + " TP." : "";
    return "Withdrew " + TpBank.format(received) + " TP" + capped + "." + feeText;
  }

  private static String lockedMessage() {
    return TpBankConfig.isEnabled() ? "Your TP Bank is locked. Its balance is frozen until it is unlocked."
        : "The TP Bank is disabled on this server.";
  }

  private static void reply(EntityPlayerMP player, TpBankRequest request, String status) {
    boolean error = status.startsWith("!");
    TpBankNetwork.channel.sendTo(snapshot(player, request.session, request.requestId,
        error ? status.substring(1) : status, error), player);
  }

  private static TpBankSnapshot snapshot(EntityPlayerMP player, long session, int requestId, String message,
                                         boolean error) {
    TpBankSnapshot snapshot = new TpBankSnapshot();
    snapshot.session = session;
    snapshot.requestId = requestId;
    snapshot.locked = !TpBankConfig.isEnabled() || !TpBank.isUnlocked(player);
    snapshot.banked = TpBank.getBanked(player);
    snapshot.withdrawable = TpBank.getWithdrawable(player);
    snapshot.current = TpBank.getCurrentTP(player);
    snapshot.maximum = TpBank.getMaxTP();
    snapshot.withdrawFeePercent = TpBankConfig.getWithdrawFeePercent();
    snapshot.rules = rules();
    if (message == null) {
      message = snapshot.locked ? lockedMessage() : "Withdraw banked TP up to DBC's TP cap.";
      error = snapshot.locked;
    }
    snapshot.message = message;
    snapshot.error = error;
    return snapshot;
  }

  private static String rules() {
    DecimalFormat percent = new DecimalFormat("0.##");
    StringBuilder rules = new StringBuilder();
    double interest = TpBankConfig.getInterestPercent();
    if (interest != 0.0D) {
      rules.append(interest > 0 ? "Interest +" : "Decay ").append(percent.format(interest))
          .append("% every ").append(TpBankConfig.getInterestPeriodMinutes()).append(" min");
    }
    append(rules, TpBankConfig.getDepositLossPercent() > 0,
        "Deposit loss " + percent.format(TpBankConfig.getDepositLossPercent()) + "%");
    append(rules, TpBankConfig.getWithdrawFeePercent() > 0,
        "Fee " + percent.format(TpBankConfig.getWithdrawFeePercent()) + "%");
    append(rules, TpBankConfig.getMaxBalance() != Long.MAX_VALUE,
        "Limit " + TpBank.format(TpBankConfig.getMaxBalance()));
    return rules.toString();
  }

  private static void append(StringBuilder rules, boolean condition, String rule) {
    if (condition) {
      rules.append(rules.length() > 0 ? "  |  " : "").append(rule);
    }
  }

  @SubscribeEvent public void loggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    TpBankStats.refresh(event.player);
  }

  @SubscribeEvent public void loggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
    SESSIONS.remove(event.player.getUniqueID());
    TpBankAdminServer.forget(event.player.getUniqueID());
  }
}
