package org.darkoro.zerosmod.tpbank;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.config.TpBankConfig;
import org.darkoro.zerosmod.network.ServerTaskScheduler;
import org.darkoro.zerosmod.tpbank.network.TpBankAdminRequest;
import org.darkoro.zerosmod.tpbank.network.TpBankAdminSnapshot;
import org.darkoro.zerosmod.tpbank.network.TpBankNetwork;

/**
 * Server side of the TP Bank admin screen. A client can send these packets without ever opening
 * the screen, so every request re-checks that the sender is still an operator.
 */
public final class TpBankAdminServer {
  private static final int PERMISSION_LEVEL = 2;
  private static final int MAX_ROWS = 500;
  private static final SecureRandom RANDOM = new SecureRandom();
  private static final Map<UUID, Long> SESSIONS = new ConcurrentHashMap<>();

  private TpBankAdminServer() {}

  public static void open(EntityPlayerMP player) {
    long session = RANDOM.nextLong();
    SESSIONS.put(player.getUniqueID(), session);
    TpBankAdminSnapshot snapshot = snapshot(session, 0, "Statistics are estimates until a player's next bank change.", false);
    snapshot.open = true;
    TpBankNetwork.channel.sendTo(snapshot, player);
  }

  public static void forget(UUID player) {
    SESSIONS.remove(player);
  }

  public static void enqueue(EntityPlayerMP player, TpBankAdminRequest request) {
    ServerTaskScheduler.schedule(() -> handle(player, request));
  }

  private static boolean allowed(EntityPlayerMP player) {
    return player.canCommandSenderUseCommand(PERMISSION_LEVEL, "zsmod");
  }

  private static void handle(EntityPlayerMP player, TpBankAdminRequest request) {
    if (player.playerNetServerHandler == null || player.isDead) {
      return;
    }
    Long session = SESSIONS.get(player.getUniqueID());
    if (session == null || session != request.session || !allowed(player)) {
      SESSIONS.remove(player.getUniqueID());
      if (request.action != TpBankAdminRequest.CLOSE) {
        TpBankAdminSnapshot closed = new TpBankAdminSnapshot();
        closed.session = request.session;
        closed.requestId = request.requestId;
        closed.close = true;
        TpBankNetwork.channel.sendTo(closed, player);
      }
      return;
    }
    String status;
    boolean configSaved = false;
    switch (request.action) {
      case TpBankAdminRequest.CLOSE:
        SESSIONS.remove(player.getUniqueID(), session);
        return;
      case TpBankAdminRequest.SAVE_CONFIG:
        status = saveConfig(player, request.settings);
        configSaved = !status.startsWith("!");
        break;
      case TpBankAdminRequest.SET:
      case TpBankAdminRequest.ADD:
      case TpBankAdminRequest.LOCK:
      case TpBankAdminRequest.UNLOCK:
        status = editPlayer(player, request);
        break;
      default:
        status = "Refreshed.";
        break;
    }
    boolean error = status.startsWith("!");
    TpBankAdminSnapshot reply = snapshot(session, request.requestId, error ? status.substring(1) : status, error);
    reply.configSaved = configSaved;
    TpBankNetwork.channel.sendTo(reply, player);
  }

  private static String saveConfig(EntityPlayerMP admin, TpBankAdminSnapshot.Settings settings) {
    boolean written = TpBankConfig.update(settings.enabled, settings.unlockedByDefault, settings.notifyOnDeposit,
        settings.maxBalance, settings.depositLoss, settings.withdrawFee, settings.interest, settings.periodMinutes);
    ZeroSMod.LOGGER.info("[TP Bank] " + admin.getCommandSenderName() + " changed the config: enabled=" + TpBankConfig.isEnabled()
        + ", unlocked_by_default=" + TpBankConfig.isUnlockedByDefault() + ", notify_on_deposit=" + TpBankConfig.notifyOnDeposit()
        + ", max_balance=" + TpBankConfig.getMaxBalanceSetting() + ", deposit_loss_percent=" + TpBankConfig.getDepositLossPercent()
        + ", withdraw_fee_percent=" + TpBankConfig.getWithdrawFeePercent() + ", interest_percent=" + TpBankConfig.getInterestPercent()
        + ", interest_period_minutes=" + TpBankConfig.getInterestPeriodMinutes());
    return written ? "Config saved and applied." : "!Config applied, but the file could not be written. It will revert on restart.";
  }

  private static String editPlayer(EntityPlayerMP admin, TpBankAdminRequest request) {
    EntityPlayerMP target = MinecraftServer.getServer().getConfigurationManager().func_152612_a(request.target);
    if (target == null) {
      return "!" + request.target + " is offline. Only online players can be edited.";
    }
    if (!TpBankStats.slotId(target).equals(request.slot)) {
      return "!" + target.getCommandSenderName() + " is no longer on slot " + request.slot + ". Nothing was changed.";
    }
    String change;
    switch (request.action) {
      case TpBankAdminRequest.SET:
      case TpBankAdminRequest.ADD:
        long before = TpBank.getBanked(target);
        if (request.action == TpBankAdminRequest.SET) {
          TpBank.setBanked(target, request.amount);
        } else {
          TpBank.addBanked(target, request.amount);
        }
        change = "changed the balance from " + TpBank.format(before) + " to " + TpBank.format(TpBank.getBanked(target)) + " TP";
        break;
      default:
        boolean unlock = request.action == TpBankAdminRequest.UNLOCK;
        TpBank.setUnlocked(target, unlock);
        change = (unlock ? "unlocked" : "locked") + " the bank";
        break;
    }
    ZeroSMod.LOGGER.info("[TP Bank] " + admin.getCommandSenderName() + " " + change + " for " + target.getCommandSenderName());
    return target.getCommandSenderName() + ": " + change + ".";
  }

  private static TpBankAdminSnapshot snapshot(long session, int requestId, String message, boolean error) {
    TpBankAdminSnapshot snapshot = new TpBankAdminSnapshot();
    snapshot.session = session;
    snapshot.requestId = requestId;
    snapshot.message = message;
    snapshot.error = error;
    TpBankStats.Totals totals = TpBankStats.totals();
    snapshot.deposited = totals.deposited;
    snapshot.withdrawn = totals.withdrawn;
    snapshot.eatenByDepositLoss = totals.eatenByDepositLoss;
    snapshot.eatenByWithdrawFee = totals.eatenByWithdrawFee;
    snapshot.eatenByInterest = totals.eatenByInterest;
    snapshot.gainedFromInterest = totals.gainedFromInterest;
    snapshot.settings.enabled = TpBankConfig.isEnabled();
    snapshot.settings.unlockedByDefault = TpBankConfig.isUnlockedByDefault();
    snapshot.settings.notifyOnDeposit = TpBankConfig.notifyOnDeposit();
    snapshot.settings.maxBalance = TpBankConfig.getMaxBalanceSetting();
    snapshot.settings.depositLoss = TpBankConfig.getDepositLossPercent();
    snapshot.settings.withdrawFee = TpBankConfig.getWithdrawFeePercent();
    snapshot.settings.interest = TpBankConfig.getInterestPercent();
    snapshot.settings.periodMinutes = TpBankConfig.getInterestPeriodMinutes();
    fillRows(snapshot);
    return snapshot;
  }

  /** Stats rows, with online players' current slots read live so never-used banks can be unlocked too. */
  private static void fillRows(TpBankAdminSnapshot snapshot) {
    List<TpBankAdminSnapshot.Row> rows = snapshot.rows;
    List<?> online = new ArrayList<Object>(MinecraftServer.getServer().getConfigurationManager().playerEntityList);
    // Reading the balance first applies pending interest, which refreshes the stats row below.
    for (Object entry : online) {
      TpBank.getBanked((EntityPlayerMP) entry);
      TpBankStats.refresh((EntityPlayerMP) entry);
    }
    Map<String, TpBankAdminSnapshot.Row> byId = new HashMap<>();
    for (TpBankStats.Row stat : TpBankStats.rows()) {
      TpBankAdminSnapshot.Row row = new TpBankAdminSnapshot.Row();
      byId.put(stat.uuid + "/" + stat.slot, row);
      row.name = stat.name;
      row.slot = stat.slot;
      row.banked = stat.estimate;
      row.unlocked = stat.entry.unlocked;
      row.deposited = stat.entry.deposited;
      row.withdrawn = stat.entry.withdrawn;
      rows.add(row);
    }
    for (Object entry : online) {
      EntityPlayerMP player = (EntityPlayerMP) entry;
      String slot = TpBankStats.slotId(player);
      TpBankAdminSnapshot.Row row = byId.get(player.getUniqueID() + "/" + slot);
      if (row == null) {
        row = new TpBankAdminSnapshot.Row();
        row.slot = slot;
        rows.add(row);
      }
      row.name = player.getCommandSenderName();
      row.live = true;
      row.banked = TpBank.getBanked(player);
      row.unlocked = TpBank.isUnlocked(player);
    }
    snapshot.slotCount = rows.size();
    for (TpBankAdminSnapshot.Row row : rows) {
      snapshot.totalBanked += row.banked;
    }
    rows.sort((a, b) -> Long.compare(b.banked, a.banked));
    // Drop the poorest rows past the limit, but never a live one: those are the editable ones.
    int kept = 0;
    for (java.util.Iterator<TpBankAdminSnapshot.Row> it = rows.iterator(); it.hasNext(); ) {
      TpBankAdminSnapshot.Row row = it.next();
      if (++kept > MAX_ROWS && !row.live) {
        it.remove();
      }
    }
  }

}
