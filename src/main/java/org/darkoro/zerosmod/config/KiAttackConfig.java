package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public final class KiAttackConfig {

  private static final String CONFIG_FILE_NAME = "ki_attacks.cfg";
  private static final String SECTION_SAFETY = "Safety";
  private static final String SECTION_LIMITS = "Limits";
  private static final String SECTION_SCRIPTING = "Scripting";

  private static File configFile;

  private static boolean enabled = true;
  private static boolean killLoadedFromNbt = true;
  private static boolean killOnChunkUnload = true;
  private static boolean killOutsideLoadedChunk = true;
  private static boolean safePowerFormula = true;
  private static boolean exposeScriptData = true;

  private static int maxWaveTicks = 120;
  private static int maxBlastTicks = 80;
  private static int maxDiskTicks = 80;
  private static int maxLaserTicks = 80;
  private static int maxSpiralTicks = 80;
  private static int maxLargeBlastTicks = 80;
  private static int maxBarrageTicks = 35;
  private static int maxShieldTicks = 120;
  private static int maxExplosionTicks = 40;
  private static int maxOrphanTicks = 1;
  private static int maxKiPerChunk = 60;
  private static int maxBarragePerChunk = 24;
  private static int maxKiPerOwner = 90;
  private static int maxBarragePerOwner = 30;
  private static int maxCollisionListSize = 96;

  private static double maxY = 256.0D;
  private static double maxMotion = 8.0D;
  private static double maxDamage = 1.0E15D;
  private static float maxSize = 32.0F;

  private KiAttackConfig() {}

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
      writeDefaultConfig();
      return;
    }

    readConfig();
  }

  private static void resetDefaults() {
    enabled = true;
    killLoadedFromNbt = true;
    killOnChunkUnload = true;
    killOutsideLoadedChunk = true;
    safePowerFormula = true;
    exposeScriptData = true;
    maxWaveTicks = 120;
    maxBlastTicks = 80;
    maxDiskTicks = 80;
    maxLaserTicks = 80;
    maxSpiralTicks = 80;
    maxLargeBlastTicks = 80;
    maxBarrageTicks = 35;
    maxShieldTicks = 120;
    maxExplosionTicks = 40;
    maxOrphanTicks = 1;
    maxKiPerChunk = 60;
    maxBarragePerChunk = 24;
    maxKiPerOwner = 90;
    maxBarragePerOwner = 30;
    maxCollisionListSize = 96;
    maxY = 256.0D;
    maxMotion = 8.0D;
    maxDamage = 1.0E15D;
    maxSize = 32.0F;
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
    if (section.equals(ConfigHandler.normalizeKey(SECTION_SAFETY))) {
      enabled = getBoolean(values, "enabled", enabled);
      killLoadedFromNbt = getBoolean(values, "kill_loaded_from_nbt", killLoadedFromNbt);
      killOnChunkUnload = getBoolean(values, "kill_on_chunk_unload", killOnChunkUnload);
      killOutsideLoadedChunk = getBoolean(values, "kill_outside_loaded_chunk", killOutsideLoadedChunk);
      safePowerFormula = getBoolean(values, "safe_power_formula", safePowerFormula);
      maxY = getDouble(values, "max_y", maxY, 1.0D, 4096.0D);
      maxMotion = getDouble(values, "max_motion", maxMotion, 0.1D, 128.0D);
      maxDamage = getDouble(values, "max_damage", maxDamage, 2147483647.0D, Double.MAX_VALUE);
      maxSize = (float)getDouble(values, "max_size", maxSize, 0.1D, 256.0D);
    } else if (section.equals(ConfigHandler.normalizeKey(SECTION_LIMITS))) {
      maxWaveTicks = getInt(values, "max_wave_ticks", maxWaveTicks, 1, 12000);
      maxBlastTicks = getInt(values, "max_blast_ticks", maxBlastTicks, 1, 12000);
      maxDiskTicks = getInt(values, "max_disk_ticks", maxDiskTicks, 1, 12000);
      maxLaserTicks = getInt(values, "max_laser_ticks", maxLaserTicks, 1, 12000);
      maxSpiralTicks = getInt(values, "max_spiral_ticks", maxSpiralTicks, 1, 12000);
      maxLargeBlastTicks = getInt(values, "max_large_blast_ticks", maxLargeBlastTicks, 1, 12000);
      maxBarrageTicks = getInt(values, "max_barrage_ticks", maxBarrageTicks, 1, 12000);
      maxShieldTicks = getInt(values, "max_shield_ticks", maxShieldTicks, 1, 12000);
      maxExplosionTicks = getInt(values, "max_explosion_ticks", maxExplosionTicks, 1, 12000);
      maxOrphanTicks = getInt(values, "max_orphan_ticks", maxOrphanTicks, 0, 12000);
      maxKiPerChunk = getInt(values, "max_ki_per_chunk", maxKiPerChunk, 1, 10000);
      maxBarragePerChunk = getInt(values, "max_barrage_per_chunk", maxBarragePerChunk, 1, 10000);
      maxKiPerOwner = getInt(values, "max_ki_per_owner", maxKiPerOwner, 1, 10000);
      maxBarragePerOwner = getInt(values, "max_barrage_per_owner", maxBarragePerOwner, 1, 10000);
      maxCollisionListSize = getInt(values, "max_collision_list_size", maxCollisionListSize, 1, 10000);
    } else if (section.equals(ConfigHandler.normalizeKey(SECTION_SCRIPTING))) {
      exposeScriptData = getBoolean(values, "expose_script_data", exposeScriptData);
    }
  }

  private static void writeDefaultConfig() {
    PrintWriter writer = null;
    try {
      writer = new PrintWriter(new FileWriter(configFile));
      writer.println("# ZeroSMod DBC ki attack hardening");
      writer.println("#");
      writer.println("# These settings guard JinRyuu.JRMCore.entity.EntityEnergyAtt without replacing the DBC entity.");
      writer.println("# Ticks are server ticks. 20 ticks = 1 second.");
      writer.println();
      writer.println("[" + SECTION_SAFETY + "]");
      writer.println("enabled = " + enabled);
      writer.println("kill_loaded_from_nbt = " + killLoadedFromNbt);
      writer.println("kill_on_chunk_unload = " + killOnChunkUnload);
      writer.println("kill_outside_loaded_chunk = " + killOutsideLoadedChunk);
      writer.println("safe_power_formula = " + safePowerFormula);
      writer.println("max_y = " + maxY);
      writer.println("max_motion = " + maxMotion);
      writer.println("max_damage = " + maxDamage);
      writer.println("max_size = " + maxSize);
      writer.println();
      writer.println("[" + SECTION_LIMITS + "]");
      writer.println("max_wave_ticks = " + maxWaveTicks);
      writer.println("max_blast_ticks = " + maxBlastTicks);
      writer.println("max_disk_ticks = " + maxDiskTicks);
      writer.println("max_laser_ticks = " + maxLaserTicks);
      writer.println("max_spiral_ticks = " + maxSpiralTicks);
      writer.println("max_large_blast_ticks = " + maxLargeBlastTicks);
      writer.println("max_barrage_ticks = " + maxBarrageTicks);
      writer.println("max_shield_ticks = " + maxShieldTicks);
      writer.println("max_explosion_ticks = " + maxExplosionTicks);
      writer.println("max_orphan_ticks = " + maxOrphanTicks);
      writer.println("max_ki_per_chunk = " + maxKiPerChunk);
      writer.println("max_barrage_per_chunk = " + maxBarragePerChunk);
      writer.println("max_ki_per_owner = " + maxKiPerOwner);
      writer.println("max_barrage_per_owner = " + maxBarragePerOwner);
      writer.println("max_collision_list_size = " + maxCollisionListSize);
      writer.println();
      writer.println("[" + SECTION_SCRIPTING + "]");
      writer.println("expose_script_data = " + exposeScriptData);
      writer.println();
    } catch (IOException ignored) {
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
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

  public static boolean killLoadedFromNbt() {
    return enabled && killLoadedFromNbt;
  }

  public static boolean killOnChunkUnload() {
    return enabled && killOnChunkUnload;
  }

  public static boolean killOutsideLoadedChunk() {
    return enabled && killOutsideLoadedChunk;
  }

  public static boolean useSafePowerFormula() {
    return enabled && safePowerFormula;
  }

  public static boolean exposeScriptData() {
    return enabled && exposeScriptData;
  }

  public static int maxTicksForType(int type) {
    switch (type) {
      case 0: return maxWaveTicks;
      case 1: return maxBlastTicks;
      case 2: return maxDiskTicks;
      case 3: return maxLaserTicks;
      case 4: return maxSpiralTicks;
      case 5: return maxLargeBlastTicks;
      case 6: return maxBarrageTicks;
      case 7: return maxShieldTicks;
      case 8: return maxExplosionTicks;
      default: return maxBlastTicks;
    }
  }

  public static int getMaxOrphanTicks() {
    return maxOrphanTicks;
  }

  public static int getMaxKiPerChunk() {
    return maxKiPerChunk;
  }

  public static int getMaxBarragePerChunk() {
    return maxBarragePerChunk;
  }

  public static int getMaxKiPerOwner() {
    return maxKiPerOwner;
  }

  public static int getMaxBarragePerOwner() {
    return maxBarragePerOwner;
  }

  public static int getMaxCollisionListSize() {
    return maxCollisionListSize;
  }

  public static double getMaxY() {
    return maxY;
  }

  public static double getMaxMotion() {
    return maxMotion;
  }

  public static double getMaxDamage() {
    return maxDamage;
  }

  public static float getMaxSize() {
    return maxSize;
  }
}
