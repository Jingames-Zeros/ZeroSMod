package org.darkoro.zerosmod.tpbank;

import JinRyuu.JRMCore.JRMCoreH;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.darkoro.zerosmod.config.TpBankConfig;

/**
 * Stores Training Points that {@code /jrmctp} gives above DBC's cap, which DBC would discard.
 *
 * <p>The bank lives in JRMCore's {@code PlayerPersisted} compound next to {@code jrmcTpint}. It
 * survives death, and because the DBC addon swaps that whole compound when a CNPC profile slot
 * changes, every DBC character gets its own balance and unlock state.</p>
 */
public final class TpBank {

  private static final String TP_NBT = "jrmcTpint";
  private static final String BALANCE_NBT = "zsTpBank";
  private static final String UNLOCKED_NBT = "zsTpBankUnlocked";
  /** Epoch millis up to which interest has been applied. */
  private static final String INTEREST_NBT = "zsTpBankInterestAt";

  private TpBank() {}

  public static int getMaxTP() {
    return JRMCoreH.getMaxTP();
  }

  public static int getCurrentTP(EntityPlayer player) {
    return JRMCoreH.getInt(player, TP_NBT);
  }

  private static NBTTagCompound data(EntityPlayer player) {
    return JRMCoreH.nbt(player, "pres");
  }

  public static boolean isUnlocked(EntityPlayer player) {
    NBTTagCompound data = data(player);
    return data.hasKey(UNLOCKED_NBT) ? data.getBoolean(UNLOCKED_NBT) : TpBankConfig.isUnlockedByDefault();
  }

  /** Locking freezes the balance; unlocking restarts interest from now, so locked time never accrues. */
  public static void setUnlocked(EntityPlayer player, boolean unlocked) {
    if (unlocked == isUnlocked(player)) {
      return;
    }
    NBTTagCompound data = data(player);
    if (!unlocked) {
      applyInterest(player, data);
    }
    data.setBoolean(UNLOCKED_NBT, unlocked);
    data.setLong(INTEREST_NBT, System.currentTimeMillis());
    TpBankStats.syncBalance(player, data.getLong(BALANCE_NBT), unlocked, data.getLong(INTEREST_NBT));
  }

  public static long getBanked(EntityPlayer player) {
    NBTTagCompound data = data(player);
    if (isUnlocked(player)) {
      applyInterest(player, data);
    }
    return Math.max(0L, data.getLong(BALANCE_NBT));
  }

  public static void setBanked(EntityPlayer player, long amount) {
    NBTTagCompound data = data(player);
    if (isUnlocked(player)) {
      applyInterest(player, data);
    }
    store(player, data, amount);
  }

  /** Admin correction of the balance by {@code amount}, which may be negative. Not counted as a deposit. */
  public static void addBanked(EntityPlayer player, long amount) {
    long banked = getBanked(player);
    // Saturate, so a huge amount cannot wrap the balance around.
    setBanked(player, amount > 0 && banked > Long.MAX_VALUE - amount ? Long.MAX_VALUE : banked + amount);
  }

  /** @return TP actually added, which the balance limit can make smaller than {@code amount} */
  public static long deposit(EntityPlayer player, long amount) {
    long banked = getBanked(player);
    long added = Math.max(0L, Math.min(amount, TpBankConfig.getMaxBalance() - banked));
    store(player, data(player), banked + added);
    if (added > 0) {
      TpBankStats.recordDeposit(player, added);
    }
    return added;
  }

  /** @return the most TP a withdrawal can hand the player right now */
  public static long getWithdrawable(EntityPlayer player) {
    if (!isUnlocked(player)) {
      return 0L;
    }
    long room = (long) getMaxTP() - Math.max(0, getCurrentTP(player));
    return Math.max(0L, Math.min(room, afterFee(getBanked(player))));
  }

  /**
   * Gives the player up to {@code amount} TP from the bank, never past DBC's cap. The withdrawal
   * fee is taken from the bank on top of what the player receives.
   *
   * @return TP the player received
   */
  public static long withdraw(EntityPlayer player, long amount) {
    long received = Math.min(Math.max(0L, amount), getWithdrawable(player));
    if (received <= 0) {
      return 0L;
    }
    long banked = getBanked(player);
    long cost = Math.min(banked, costOf(received));
    store(player, data(player), banked - cost);
    TpBankStats.recordWithdraw(player, received, Math.max(0L, cost - received));
    JRMCoreH.setInt(Math.max(0, getCurrentTP(player)) + (int) received, player, TP_NBT);
    return received;
  }

  /** @return banked TP needed for the player to receive {@code received} TP */
  public static long costOf(long received) {
    double keep = 1.0D - TpBankConfig.getWithdrawFeePercent() / 100.0D;
    return keep >= 1.0D ? received : (long) Math.ceil(received / keep);
  }

  private static long afterFee(long banked) {
    double keep = 1.0D - TpBankConfig.getWithdrawFeePercent() / 100.0D;
    return keep >= 1.0D ? banked : (long) Math.floor(banked * keep);
  }

  private static void store(EntityPlayer player, NBTTagCompound data, long amount) {
    long stored = Math.max(0L, Math.min(amount, TpBankConfig.getMaxBalance()));
    data.setLong(BALANCE_NBT, stored);
    TpBankStats.syncBalance(player, stored, isUnlocked(player), data.getLong(INTEREST_NBT));
  }

  /** Applies every whole interest period that elapsed since the last application. */
  private static void applyInterest(EntityPlayer player, NBTTagCompound data) {
    long now = System.currentTimeMillis();
    long period = TpBankConfig.getInterestPeriodMillis();
    if (!data.hasKey(INTEREST_NBT)) {
      data.setLong(INTEREST_NBT, now);
      return;
    }
    long last = data.getLong(INTEREST_NBT);
    long periods = (now - last) / period;
    if (periods <= 0) {
      return;
    }
    data.setLong(INTEREST_NBT, last + periods * period);
    long balance = data.getLong(BALANCE_NBT);
    double rate = TpBankConfig.getInterestPercent() / 100.0D;
    if (balance <= 0 || rate == 0.0D) {
      return;
    }
    double factor = Math.pow(1.0D + rate, periods);
    double next = Math.floor(balance * factor);
    store(player, data, next >= Long.MAX_VALUE ? Long.MAX_VALUE : (long) next);
    TpBankStats.recordInterest(data.getLong(BALANCE_NBT) - balance);
  }

  /**
   * Banks the TP above the cap.
   *
   * @param before the player's TP before the gain
   * @param total  the player's unclamped TP after the gain
   * @return {@code total} when nothing was banked, otherwise the cap
   */
  public static long captureOverflow(EntityPlayer player, int before, long total) {
    int max = getMaxTP();
    if (total <= max || !TpBankConfig.isEnabled() || !(player instanceof EntityPlayerMP)
        || player.worldObj == null || player.worldObj.isRemote || !isUnlocked(player)) {
      return total;
    }
    long overflow = total - Math.max(max, before);
    if (overflow > 0) {
      long afterLoss = (long) Math.floor(overflow * (1.0D - TpBankConfig.getDepositLossPercent() / 100.0D));
      long amount = TpBankEventHooks.fireOverflow((EntityPlayerMP) player, overflow, afterLoss);
      if (amount <= 0) {
        TpBankStats.recordDepositLoss(overflow);
      } else {
        long added = deposit(player, amount);
        TpBankStats.recordDepositLoss(Math.max(0L, overflow - added));
        if (TpBankConfig.notifyOnDeposit()) {
          String full = added < amount ? " Your bank is full; " + format(amount - added) + " TP were lost." : "";
          player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "TP cap reached: "
              + format(added) + " TP stored in your TP Bank. Balance: " + format(getBanked(player)) + " TP." + full));
        }
      }
    }
    return max;
  }

  public static String format(long value) {
    return String.format("%,d", value);
  }
}
