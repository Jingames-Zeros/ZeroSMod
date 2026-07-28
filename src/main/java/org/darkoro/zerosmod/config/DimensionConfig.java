package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class DimensionConfig {

  private static final String CONFIG_FILE_NAME = "dimensions.cfg";
  private static final String PHYLACTERY_SECTION = "Phylactery";
  private static final String DRAGON_REALM_SECTION = "Dragon Realm";
  private static final String KEY_VISIBILITY = "visibility";
  private static final String KEY_FIXED_TIME = "fixed_time";
  private static final String KEY_SUN_BRIGHTNESS = "sun_brightness";

  private static final float DEFAULT_PHYLACTERY_VISIBILITY = 1.0F;
  private static final int DEFAULT_PHYLACTERY_FIXED_TIME = 6000;
  private static final float DEFAULT_PHYLACTERY_SUN_BRIGHTNESS = 1.0F;
  private static final float DEFAULT_DRAGON_REALM_VISIBILITY = 1.0F;

  private static File dimensionsFile;

  private static float phylacteryVisibility = DEFAULT_PHYLACTERY_VISIBILITY;
  private static int phylacteryFixedTime = DEFAULT_PHYLACTERY_FIXED_TIME;
  private static float phylacterySunBrightness = DEFAULT_PHYLACTERY_SUN_BRIGHTNESS;
  private static float dragonRealmVisibility = DEFAULT_DRAGON_REALM_VISIBILITY;

  private DimensionConfig() {}

  public static void load(FMLPreInitializationEvent event) {
    dimensionsFile = new File(ConfigHandler.getConfigDir(event), CONFIG_FILE_NAME);
    reload();
  }

  public static void reload() {
    if (dimensionsFile == null) {
      return;
    }

    resetDefaults();
    if (!dimensionsFile.exists()) {
      writeCleanConfig();
      return;
    }

    readCleanConfig();
    if (!hasCleanSectionsWithAllKeys()) {
      writeCleanConfig();
    }
  }

  private static void resetDefaults() {
    phylacteryVisibility = DEFAULT_PHYLACTERY_VISIBILITY;
    phylacteryFixedTime = DEFAULT_PHYLACTERY_FIXED_TIME;
    phylacterySunBrightness = DEFAULT_PHYLACTERY_SUN_BRIGHTNESS;
    dragonRealmVisibility = DEFAULT_DRAGON_REALM_VISIBILITY;
  }

  private static void readCleanConfig() {
    SectionCollector collector = new SectionCollector();
    ConfigHandler.parseIni(dimensionsFile, collector);
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
      values.put(normalizeKey(key), value);
    }
  }

  private static void applySection(String sectionName, Map<String, String> values) {
    if (values == null || values.isEmpty()) {
      return;
    }

    if (isPhylacterySection(sectionName)) {
      phylacteryVisibility = getFloat(values, KEY_VISIBILITY, phylacteryVisibility, 0.0F, 1.0F);
      phylacteryFixedTime = getInt(values, KEY_FIXED_TIME, phylacteryFixedTime, 0, 23999);
      phylacterySunBrightness = getFloat(values, KEY_SUN_BRIGHTNESS, phylacterySunBrightness, 0.0F, 1.0F);
    } else if (isDragonRealmSection(sectionName)) {
      dragonRealmVisibility = getFloat(values, KEY_VISIBILITY, dragonRealmVisibility, 0.0F, 1.0F);
    }
  }

  private static boolean hasCleanSectionsWithAllKeys() {
    final Map<String, String> phylacteryValues = new LinkedHashMap<String, String>();
    final Map<String, String> dragonRealmValues = new LinkedHashMap<String, String>();

    ConfigHandler.parseIni(dimensionsFile, new ConfigHandler.IniVisitor() {
      private boolean readingPhylactery;
      private boolean readingDragonRealm;

      @Override public void onSection(String name) {
        readingPhylactery = isPhylacterySection(name);
        readingDragonRealm = isDragonRealmSection(name);
      }

      @Override public void onKeyValue(String key, String value) {
        if (readingPhylactery) {
          phylacteryValues.put(normalizeKey(key), value);
        } else if (readingDragonRealm) {
          dragonRealmValues.put(normalizeKey(key), value);
        }
      }
    });

    return phylacteryValues.size() == 3
        && phylacteryValues.containsKey(normalizeKey(KEY_VISIBILITY))
        && phylacteryValues.containsKey(normalizeKey(KEY_FIXED_TIME))
        && phylacteryValues.containsKey(normalizeKey(KEY_SUN_BRIGHTNESS))
        && dragonRealmValues.size() == 1
        && dragonRealmValues.containsKey(normalizeKey(KEY_VISIBILITY));
  }

  private static void writeCleanConfig() {
    PrintWriter writer = null;
    try {
      writer = new PrintWriter(new FileWriter(dimensionsFile));
      writer.println("# ZeroSMod dimensions config");
      writer.println("#");
      writer.println("# visibility ranges from 0.0 to 1.0.");
      writer.println("# 0.0 is heavy void haze, 1.0 is Overworld-like render-distance visibility.");
      writer.println("# Phylactery fixed_time controls where the sun sits in the sky. 6000 is noon.");
      writer.println("# Phylactery sun_brightness ranges from 0.0 to 1.0 and controls skylight strength.");
      writer.println();
      writer.println("[" + PHYLACTERY_SECTION + "]");
      writer.println(KEY_VISIBILITY + " = " + formatFloat(phylacteryVisibility));
      writer.println(KEY_FIXED_TIME + " = " + phylacteryFixedTime);
      writer.println(KEY_SUN_BRIGHTNESS + " = " + formatFloat(phylacterySunBrightness));
      writer.println();
      writer.println("[" + DRAGON_REALM_SECTION + "]");
      writer.println(KEY_VISIBILITY + " = " + formatFloat(dragonRealmVisibility));
      writer.println();
    } catch (IOException ignored) {
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  private static float getFloat(Map<String, String> values, String key, float fallback, float min, float max) {
    String value = values.get(normalizeKey(key));
    if (value == null) {
      return fallback;
    }

    try {
      float parsed = Float.parseFloat(value.trim());
      if (parsed < min) {
        return min;
      }
      if (parsed > max) {
        return max;
      }
      return parsed;
    } catch (NumberFormatException ignored) {}

    return fallback;
  }

  private static int getInt(Map<String, String> values, String key, int fallback, int min, int max) {
    String value = values.get(normalizeKey(key));
    if (value == null) {
      return fallback;
    }

    try {
      int parsed = Integer.parseInt(value.trim());
      if (parsed < min) {
        return min;
      }
      if (parsed > max) {
        return max;
      }
      return parsed;
    } catch (NumberFormatException ignored) {}

    return fallback;
  }

  private static String formatFloat(float value) {
    if (value == Math.rint(value)) {
      return String.format(Locale.ROOT, "%.1f", value);
    }
    return Float.toString(value);
  }

  private static boolean isPhylacterySection(String sectionName) {
    return normalizeKey(PHYLACTERY_SECTION).equals(normalizeKey(sectionName));
  }

  private static boolean isDragonRealmSection(String sectionName) {
    return normalizeKey(DRAGON_REALM_SECTION).equals(normalizeKey(sectionName));
  }

  private static String normalizeKey(String value) {
    return ConfigHandler.normalizeKey(value);
  }

  public static float getPhylacteryVisibility() {
    return phylacteryVisibility;
  }

  public static float getDragonRealmVisibility() {
    return dragonRealmVisibility;
  }

  public static void applyDimensionValues(float phylacteryVisibility, int fixedTime, float sunBrightness,
      float dragonRealmVisibility) {
    DimensionConfig.phylacteryVisibility = clampFloat(phylacteryVisibility, 0.0F, 1.0F);
    phylacteryFixedTime = clampInt(fixedTime, 0, 23999);
    phylacterySunBrightness = clampFloat(sunBrightness, 0.0F, 1.0F);
    DimensionConfig.dragonRealmVisibility = clampFloat(dragonRealmVisibility, 0.0F, 1.0F);
  }

  public static int getPhylacteryFixedTime() {
    return phylacteryFixedTime;
  }

  public static float getPhylacterySunBrightness() {
    return phylacterySunBrightness;
  }

  private static float clampFloat(float value, float min, float max) {
    if (value < min) {
      return min;
    }
    if (value > max) {
      return max;
    }
    return value;
  }

  private static int clampInt(int value, int min, int max) {
    if (value < min) {
      return min;
    }
    if (value > max) {
      return max;
    }
    return value;
  }
}
