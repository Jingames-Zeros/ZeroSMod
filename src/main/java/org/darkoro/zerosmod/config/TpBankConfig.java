package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TpBankConfig {

  private static final String CONFIG_FILE_NAME = "tp_bank.cfg";
  private static final String SECTION_GENERAL = "General";
  private static final String SECTION_DEPOSIT = "Deposit";
  private static final String SECTION_WITHDRAW = "Withdraw";
  private static final String SECTION_INTEREST = "Interest";

  private static File configFile;

  private static boolean enabled = true;
  private static boolean unlockedByDefault = false;
  private static boolean notifyOnDeposit = true;
  private static long maxBalance = 0L;
  private static double depositLossPercent = 0.0D;
  private static double withdrawFeePercent = 0.0D;
  private static double interestPercent = 0.0D;
  private static int interestPeriodMinutes = 20;

  private TpBankConfig() {}

  public static void load(FMLPreInitializationEvent event) {
    configFile = new File(ConfigHandler.getConfigDir(event), CONFIG_FILE_NAME);
    reload();
  }

  public static void reload() {
    if (configFile == null) {
      return;
    }

    resetDefaults();
    if (!configFile.exists()) {
      writeConfig();
      return;
    }

    readConfig();
  }

  private static void resetDefaults() {
    enabled = true;
    unlockedByDefault = false;
    notifyOnDeposit = true;
    maxBalance = 0L;
    depositLossPercent = 0.0D;
    withdrawFeePercent = 0.0D;
    interestPercent = 0.0D;
    interestPeriodMinutes = 20;
  }

  private static void readConfig() {
    final SectionCollector collector = new SectionCollector();
    ConfigHandler.parseIni(configFile, collector);
    applySection(collector.currentSection, collector.values);
  }

  private static final class SectionCollector implements ConfigHandler.IniVisitor {
    private String currentSection;
    private Map<String, String> values = new LinkedHashMap<String, String>();

    @Override public void onSection(String name) {
      applySection(currentSection, values);
      currentSection = name;
      values = new LinkedHashMap<String, String>();
    }

    @Override public void onKeyValue(String key, String value) {
      values.put(ConfigHandler.normalizeKey(key), value);
    }
  }

  private static void applySection(String sectionName, Map<String, String> values) {
    if (sectionName == null || values == null || values.isEmpty()) {
      return;
    }

    String section = ConfigHandler.normalizeKey(sectionName);
    if (section.equals(ConfigHandler.normalizeKey(SECTION_GENERAL))) {
      enabled = getBoolean(values, "enabled", enabled);
      unlockedByDefault = getBoolean(values, "unlocked_by_default", unlockedByDefault);
      notifyOnDeposit = getBoolean(values, "notify_on_deposit", notifyOnDeposit);
      maxBalance = getLong(values, "max_balance", maxBalance, 0L, Long.MAX_VALUE);
    } else if (section.equals(ConfigHandler.normalizeKey(SECTION_DEPOSIT))) {
      depositLossPercent = getDouble(values, "deposit_loss_percent", depositLossPercent, 0.0D, 100.0D);
    } else if (section.equals(ConfigHandler.normalizeKey(SECTION_WITHDRAW))) {
      withdrawFeePercent = getDouble(values, "withdraw_fee_percent", withdrawFeePercent, 0.0D, 99.0D);
    } else if (section.equals(ConfigHandler.normalizeKey(SECTION_INTEREST))) {
      interestPercent = getDouble(values, "interest_percent", interestPercent, -100.0D, 1000.0D);
      interestPeriodMinutes = getInt(values, "interest_period_minutes", interestPeriodMinutes, 1, 525600);
    }
  }

  private static boolean writeConfig() {
    PrintWriter writer = null;
    try {
      writer = new PrintWriter(new FileWriter(configFile));
      writer.println("# ZeroSMod TP Bank");
      writer.println("#");
      writer.println("# Stores the Training Points /jrmctp gives above the DBC TP cap, which DBC would discard. Each DBC character");
      writer.println("# (CNPC profile slot) has its own bank, which starts locked unless unlocked_by_default");
      writer.println("# is true. Scripts unlock it with player.getDBCPlayer().setTpBankUnlocked(true).");
      writer.println("# Reload with /zsmod reload, or edit in game with /zsmod tpbank gui (which rewrites this file).");
      writer.println();
      writer.println("[" + SECTION_GENERAL + "]");
      writer.println("# false disables banking entirely: DBC discards overflow TP as usual.");
      writer.println("enabled = " + enabled);
      writer.println("unlocked_by_default = " + unlockedByDefault);
      writer.println("# Chat message to the player each time overflow TP is banked.");
      writer.println("notify_on_deposit = " + notifyOnDeposit);
      writer.println("# Largest balance one bank can hold. 0 = no limit. TP above it is lost.");
      writer.println("max_balance = " + maxBalance);
      writer.println();
      writer.println("[" + SECTION_DEPOSIT + "]");
      writer.println("# Share of the overflow lost on the way in. 20 banks 80% of the TP above the cap.");
      writer.println("deposit_loss_percent = " + depositLossPercent);
      writer.println();
      writer.println("[" + SECTION_WITHDRAW + "]");
      writer.println("# Share of each withdrawal kept by the bank. 5 means receiving 95 TP costs 100 banked TP.");
      writer.println("withdraw_fee_percent = " + withdrawFeePercent);
      writer.println();
      writer.println("[" + SECTION_INTEREST + "]");
      writer.println("# Applied to the balance every interest_period_minutes of real time, offline included.");
      writer.println("# Positive values grow the balance, negative values shrink it: -10 removes 10% each period.");
      writer.println("# 0 disables. A locked bank is frozen and accrues nothing.");
      writer.println("interest_percent = " + interestPercent);
      writer.println("interest_period_minutes = " + interestPeriodMinutes);
      writer.println();
      return !writer.checkError();
    } catch (IOException ignored) {
      return false;
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  /**
   * Applies new values, limited to the same ranges as the file, and rewrites the file. Hand-written
   * comments in the file are replaced by the standard ones.
   *
   * @return whether the file was written; the values apply either way
   */
  public static boolean update(boolean newEnabled, boolean newUnlockedByDefault, boolean newNotifyOnDeposit,
                               long newMaxBalance, double newDepositLoss, double newWithdrawFee,
                               double newInterest, int newPeriodMinutes) {
    enabled = newEnabled;
    unlockedByDefault = newUnlockedByDefault;
    notifyOnDeposit = newNotifyOnDeposit;
    maxBalance = Math.max(0L, newMaxBalance);
    depositLossPercent = limit(newDepositLoss, depositLossPercent, 0.0D, 100.0D);
    withdrawFeePercent = limit(newWithdrawFee, withdrawFeePercent, 0.0D, 99.0D);
    interestPercent = limit(newInterest, interestPercent, -100.0D, 1000.0D);
    interestPeriodMinutes = Math.max(1, Math.min(525600, newPeriodMinutes));
    return configFile != null && writeConfig();
  }

  private static double limit(double value, double fallback, double min, double max) {
    return Double.isNaN(value) || Double.isInfinite(value) ? fallback : Math.max(min, Math.min(max, value));
  }

  /** @return the configured balance limit, 0 when unlimited */
  public static long getMaxBalanceSetting() {
    return maxBalance;
  }

  private static boolean getBoolean(Map<String, String> values, String key, boolean fallback) {
    String value = values.get(ConfigHandler.normalizeKey(key));
    if (value == null) {
      return fallback;
    }

    String clean = value.trim();
    return clean.equalsIgnoreCase("true") || clean.equals("1") || clean.equalsIgnoreCase("yes");
  }

  private static int getInt(Map<String, String> values, String key, int fallback, int min, int max) {
    String value = values.get(ConfigHandler.normalizeKey(key));
    if (value == null) {
      return fallback;
    }

    try {
      int parsed = Integer.parseInt(value.trim());
      return Math.max(min, Math.min(max, parsed));
    } catch (NumberFormatException ignored) {
      return fallback;
    }
  }

  private static long getLong(Map<String, String> values, String key, long fallback, long min, long max) {
    String value = values.get(ConfigHandler.normalizeKey(key));
    if (value == null) {
      return fallback;
    }

    try {
      long parsed = Long.parseLong(value.trim().replace("_", ""));
      return Math.max(min, Math.min(max, parsed));
    } catch (NumberFormatException ignored) {
      return fallback;
    }
  }

  private static double getDouble(Map<String, String> values, String key, double fallback, double min, double max) {
    String value = values.get(ConfigHandler.normalizeKey(key));
    if (value == null) {
      return fallback;
    }

    try {
      double parsed = Double.parseDouble(value.trim());
      if (Double.isNaN(parsed) || Double.isInfinite(parsed)) {
        return fallback;
      }
      return Math.max(min, Math.min(max, parsed));
    } catch (NumberFormatException ignored) {
      return fallback;
    }
  }

  public static boolean isEnabled() {
    return enabled;
  }

  public static boolean isUnlockedByDefault() {
    return unlockedByDefault;
  }

  public static boolean notifyOnDeposit() {
    return notifyOnDeposit;
  }

  /** @return the balance limit, or {@link Long#MAX_VALUE} when unlimited */
  public static long getMaxBalance() {
    return maxBalance <= 0L ? Long.MAX_VALUE : maxBalance;
  }

  public static double getDepositLossPercent() {
    return depositLossPercent;
  }

  public static double getWithdrawFeePercent() {
    return withdrawFeePercent;
  }

  public static double getInterestPercent() {
    return interestPercent;
  }

  public static long getInterestPeriodMillis() {
    return interestPeriodMinutes * 60_000L;
  }

  public static int getInterestPeriodMinutes() {
    return interestPeriodMinutes;
  }
}
