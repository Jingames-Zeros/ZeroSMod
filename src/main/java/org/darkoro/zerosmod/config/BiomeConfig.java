package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraftforge.common.config.Configuration;

public final class BiomeConfig {

  private static final String CONFIG_FILE_NAME = "biomes.cfg";

  private static File biomeConfigDir;
  private static File biomesFile;

  public static final BiomeVisuals SPIRIT_GARDEN =
      new BiomeVisuals("Spirit Garden", 80, 0x5A30B8, 0x6A44BF, -1.0F, 0x3BAD59, 0x228F3F, 0xF56C62);
  public static final BiomeVisuals VAKRON =
      new BiomeVisuals("Vakron", 81, 0x7C0A02, 0x2A2A2A, -1.0F, 0xCD1C18, 0x960019, 0x960019);
  public static final BiomeVisuals DRAGON_REALM =
      new BiomeVisuals("Dragon Realm", 82, 0xFFD235, 0xFDDC5C, -1.0F, 0xFDDC5C, 0xD3AF37, 0xFFC300);
  public static final BiomeVisuals ZS_BIOME_2 =
      new BiomeVisuals("ZS Biome 2", 83, 0xFFFFF9, 0xFFFFF9, -1.0F, 0xFFFFF9, 0xFFFFF9, 0xFFFFF9);
  public static final BiomeVisuals ZS_BIOME_3 =
      new BiomeVisuals("ZS Biome 3", 84, 0xFFFFF9, 0xFFFFF9, -1.0F, 0xFFFFF9, 0xFFFFF9, 0xFFFFF9);
  public static final BiomeVisuals ZS_BIOME_4 =
      new BiomeVisuals("ZS Biome 4", 85, 0xFFFFF9, 0xFFFFF9, -1.0F, 0xFFFFF9, 0xFFFFF9, 0xFFFFF9);
  public static final BiomeVisuals ZS_BIOME_5 =
      new BiomeVisuals("ZS Biome 5", 86, 0xFFFFF9, 0xFFFFF9, -1.0F, 0xFFFFF9, 0xFFFFF9, 0xFFFFF9);
  public static final BiomeVisuals ZS_BIOME_6 =
      new BiomeVisuals("ZS Biome 6", 87, 0xFFFFF9, 0xFFFFF9, -1.0F, 0xFFFFF9, 0xFFFFF9, 0xFFFFF9);
  public static final BiomeVisuals ZS_BIOME_7 =
      new BiomeVisuals("ZS Biome 7", 88, 0xFFFFF9, 0xFFFFF9, -1.0F, 0xFFFFF9, 0xFFFFF9, 0xFFFFF9);
  public static final BiomeVisuals ZS_BIOME_8 =
      new BiomeVisuals("ZS Biome 8", 89, 0xFFFFF9, 0xFFFFF9, -1.0F, 0xFFFFF9, 0xFFFFF9, 0xFFFFF9);
  public static final BiomeVisuals ZS_BIOME_9 =
      new BiomeVisuals("ZS Biome 9", 90, 0xFFFFF9, 0xFFFFF9, -1.0F, 0xFFFFF9, 0xFFFFF9, 0xFFFFF9);
  public static final BiomeVisuals ZS_BIOME_10 =
      new BiomeVisuals("ZS Biome 10", 91, 0xFFFFF9, 0xFFFFF9, -1.0F, 0xFFFFF9, 0xFFFFF9, 0xFFFFF9);

  private BiomeConfig() {}

  public static BiomeVisuals getVisualsById(int biomeId) {
    for (BiomeVisuals visuals : getAllVisuals()) {
      if (biomeId == visuals.getId()) {
        return visuals;
      }
    }

    return null;
  }

  public static void load(FMLPreInitializationEvent event) {
    biomeConfigDir = new File(event.getModConfigurationDirectory(), "zerosmod");
    if (!biomeConfigDir.exists()) {
      //noinspection ResultOfMethodCallIgnored
      biomeConfigDir.mkdirs();
    }

    biomesFile = new File(biomeConfigDir, CONFIG_FILE_NAME);
    reload();
  }

  public static void reload() {
    if (biomesFile == null) {
      return;
    }

    resetDefaults();
    if (!biomesFile.exists()) {
      writeCleanConfig();
      return;
    }

    if (isLegacyForgeConfig()) {
      readLegacyForgeConfig();
      backupLegacyConfig();
      writeCleanConfig();
      return;
    }

    readCleanConfig();
  }

  private static void resetDefaults() {
    for (BiomeVisuals visuals : getAllVisuals()) {
      visuals.reset();
    }
  }

  private static void readCleanConfig() {
    BufferedReader reader = null;
    String currentSection = null;
    Map<String, String> values = new LinkedHashMap<String, String>();

    try {
      reader = new BufferedReader(new FileReader(biomesFile));
      String line;
      while ((line = reader.readLine()) != null) {
        String cleanLine = line.trim();
        if (cleanLine.length() == 0 || cleanLine.startsWith("#") || cleanLine.startsWith(";")) {
          continue;
        }

        if (cleanLine.startsWith("[") && cleanLine.endsWith("]")) {
          applySection(currentSection, values);
          currentSection = cleanLine.substring(1, cleanLine.length() - 1).trim();
          values = new LinkedHashMap<String, String>();
          continue;
        }

        int separator = cleanLine.indexOf('=');
        if (separator < 0) {
          separator = cleanLine.indexOf(':');
        }

        if (separator <= 0 || separator >= cleanLine.length() - 1) {
          continue;
        }

        values.put(normalizeKey(cleanLine.substring(0, separator)), cleanLine.substring(separator + 1).trim());
      }
    } catch (IOException ignored) {
    } finally {
      applySection(currentSection, values);
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ignored) {}
      }
    }
  }

  private static void applySection(String sectionName, Map<String, String> values) {
    if (sectionName == null || values == null || values.isEmpty()) {
      return;
    }

    BiomeVisuals visuals = findVisuals(sectionName, values.get("id"));
    if (visuals == null) {
      return;
    }

    visuals.biomeName = getString(values, visuals.defaultName, "name", "biomename", "displayname");
    visuals.skyColor = getHex(values, visuals.defaultSkyColor, "sky", "skycolor");
    visuals.fogColor = getHex(values, visuals.defaultFogColor, "fog", "fogcolor");
    visuals.fogMaxStrength = getFloat(values, visuals.defaultFogMaxStrength, -1.0F, 20.0F,
        "fogstrength", "fog_strength", "fogmaxstrength");
    visuals.grassColor = getHex(values, visuals.defaultGrassColor, "grass", "grasscolor");
    visuals.foliageColor = getHex(values, visuals.defaultFoliageColor, "foliage", "foliagecolor");
    visuals.waterColor = getHex(values, visuals.defaultWaterColor, "water", "watercolor");
  }

  private static BiomeVisuals findVisuals(String sectionName, String idValue) {
    Integer id = parseInteger(idValue);
    if (id != null) {
      BiomeVisuals visuals = getVisualsById(id);
      if (visuals != null) {
        return visuals;
      }
    }

    String normalizedSection = normalizeName(sectionName);
    for (BiomeVisuals visuals : getAllVisuals()) {
      if (normalizeName(visuals.getLabel()).equals(normalizedSection)
          || normalizeName(visuals.defaultName).equals(normalizedSection)) {
        return visuals;
      }
    }

    return null;
  }

  private static boolean isLegacyForgeConfig() {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(biomesFile));
      for (int i = 0; i < 20; i++) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }
        String cleanLine = line.trim();
        if ("# Configuration file".equalsIgnoreCase(cleanLine) || cleanLine.endsWith("{")) {
          return true;
        }
      }
    } catch (IOException ignored) {
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ignored) {}
      }
    }

    return false;
  }

  private static void readLegacyForgeConfig() {
    Configuration config = new Configuration(biomesFile);
    config.load();

    for (BiomeVisuals visuals : getAllVisuals()) {
      String category = "Biome - " + visuals.getLabel().toUpperCase(Locale.ROOT) + " (" + visuals.getId() + ")";
      visuals.biomeName = config.getString("Biome Name", category, visuals.defaultName, "Biome display name");
      visuals.skyColor = getLegacyHex(config, "Sky Color", category, visuals.defaultSkyColor, "Sky color hex");
      visuals.fogColor = getLegacyHex(config, "Fog Color", category, visuals.defaultFogColor, "Fog color hex");
      visuals.fogMaxStrength = config.getFloat("Fog Strength", category, visuals.defaultFogMaxStrength,
          -1.0F, 20.0F, "Fog maxStrength");
      visuals.grassColor = getLegacyHex(config, "Grass Color", category, visuals.defaultGrassColor, "Grass color hex");
      visuals.foliageColor = getLegacyHex(config, "Foliage Color", category, visuals.defaultFoliageColor,
          "Foliage color hex");
      visuals.waterColor = getLegacyHex(config, "Water Color", category, visuals.defaultWaterColor,
          "Water overlay color hex. This is an overlay tint, not a real water texture.");
    }
  }

  private static int getLegacyHex(Configuration config, String key, String category, int defaultRgb, String comment) {
    String raw = config.getString(key, category, toHex6(defaultRgb), comment);
    Integer parsed = parseHexColor(raw);
    return parsed != null ? parsed : defaultRgb;
  }

  private static void backupLegacyConfig() {
    File backup = new File(biomeConfigDir, CONFIG_FILE_NAME + ".legacy.bak");
    if (backup.exists()) {
      return;
    }

    try {
      Files.copy(biomesFile.toPath(), backup.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
    } catch (IOException ignored) {}
  }

  private static void writeCleanConfig() {
    PrintWriter writer = null;
    try {
      writer = new PrintWriter(new FileWriter(biomesFile));
      writer.println("# ZeroSMod biome visuals config");
      writer.println("#");
      writer.println("# This controls visuals only. It does not change biome IDs or world generation.");
      writer.println("# Use /zsmod reload to reload this file and sync visuals to online clients.");
      writer.println("#");
      writer.println("# Fields:");
      writer.println("# id = fixed biome ID, used only so the section can be renamed safely");
      writer.println("# name = display name for the biome");
      writer.println("# sky = top half of the sky");
      writer.println("# fog = lower sky/fog color");
      writer.println("# fog_strength = -1 uses the mod default; valid range is -1 to 20");
      writer.println("# grass = grass tint");
      writer.println("# foliage = leaf/foliage tint");
      writer.println("# water = water overlay tint, not a water texture");
      writer.println("#");
      writer.println("# Colors accept 0xRRGGBB, #RRGGBB, or RRGGBB.");
      writer.println();

      for (BiomeVisuals visuals : getAllVisuals()) {
        writeVisuals(writer, visuals);
      }
    } catch (IOException ignored) {
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  private static void writeVisuals(PrintWriter writer, BiomeVisuals visuals) {
    writer.println("[" + visuals.getLabel() + "]");
    writer.println("id = " + visuals.getId());
    writer.println("name = " + visuals.biomeName);
    writer.println("sky = " + toHex6(visuals.skyColor));
    writer.println("fog = " + toHex6(visuals.fogColor));
    writer.println("fog_strength = " + formatFloat(visuals.fogMaxStrength));
    writer.println("grass = " + toHex6(visuals.grassColor));
    writer.println("foliage = " + toHex6(visuals.foliageColor));
    writer.println("water = " + toHex6(visuals.waterColor));
    writer.println();
  }

  private static String getString(Map<String, String> values, String fallback, String... keys) {
    for (String key : keys) {
      String value = values.get(normalizeKey(key));
      if (value != null && value.trim().length() > 0) {
        return value.trim();
      }
    }

    return fallback;
  }

  private static int getHex(Map<String, String> values, int fallback, String... keys) {
    for (String key : keys) {
      Integer parsed = parseHexColor(values.get(normalizeKey(key)));
      if (parsed != null) {
        return parsed;
      }
    }

    return fallback;
  }

  private static float getFloat(Map<String, String> values, float fallback, float min, float max, String... keys) {
    for (String key : keys) {
      String value = values.get(normalizeKey(key));
      if (value == null) {
        continue;
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
    }

    return fallback;
  }

  private static Integer parseInteger(String raw) {
    if (raw == null) {
      return null;
    }

    try {
      return Integer.valueOf(raw.trim());
    } catch (NumberFormatException ignored) {
      return null;
    }
  }

  private static String toHex6(int rgb) {
    return String.format(Locale.ROOT, "0x%06X", (rgb & 0xFFFFFF));
  }

  private static String formatFloat(float value) {
    if (value == Math.rint(value)) {
      return String.format(Locale.ROOT, "%.0f", value);
    }
    return Float.toString(value);
  }

  private static Integer parseHexColor(String raw) {
    if (raw == null) {
      return null;
    }

    String clean = raw.trim();
    if (clean.startsWith("0x") || clean.startsWith("0X")) {
      clean = clean.substring(2);
    }
    if (clean.startsWith("#")) {
      clean = clean.substring(1);
    }
    if (clean.length() != 6) {
      return null;
    }

    try {
      return Integer.valueOf(Integer.parseInt(clean, 16) & 0xFFFFFF);
    } catch (NumberFormatException ignored) {
      return null;
    }
  }

  private static String normalizeKey(String value) {
    return value == null ? "" : value.trim().replace(" ", "").replace("_", "").replace("-", "").toLowerCase(Locale.ROOT);
  }

  private static String normalizeName(String value) {
    return value == null ? "" : value.trim().replace(" ", "").replace("_", "").replace("-", "").toLowerCase(Locale.ROOT);
  }

  private static BiomeVisuals[] getAllVisuals() {
    return new BiomeVisuals[] {
        SPIRIT_GARDEN,
        VAKRON,
        DRAGON_REALM,
        ZS_BIOME_2,
        ZS_BIOME_3,
        ZS_BIOME_4,
        ZS_BIOME_5,
        ZS_BIOME_6,
        ZS_BIOME_7,
        ZS_BIOME_8,
        ZS_BIOME_9,
        ZS_BIOME_10
    };
  }

  public static final class BiomeVisuals {
    public String biomeName;
    public int skyColor;
    public int fogColor;
    public float fogMaxStrength;
    public int grassColor;
    public int foliageColor;
    public int waterColor;

    private final String label;
    private final int id;
    private final String defaultName;
    private final int defaultSkyColor;
    private final int defaultFogColor;
    private final float defaultFogMaxStrength;
    private final int defaultGrassColor;
    private final int defaultFoliageColor;
    private final int defaultWaterColor;

    private BiomeVisuals(String label, int id, int defaultSkyColor, int defaultFogColor,
        float defaultFogMaxStrength, int defaultGrassColor, int defaultFoliageColor, int defaultWaterColor) {
      this.label = label;
      this.id = id;
      this.defaultName = label;
      this.defaultSkyColor = defaultSkyColor;
      this.defaultFogColor = defaultFogColor;
      this.defaultFogMaxStrength = defaultFogMaxStrength;
      this.defaultGrassColor = defaultGrassColor;
      this.defaultFoliageColor = defaultFoliageColor;
      this.defaultWaterColor = defaultWaterColor;
      reset();
    }

    private void reset() {
      this.biomeName = defaultName;
      this.skyColor = defaultSkyColor;
      this.fogColor = defaultFogColor;
      this.fogMaxStrength = defaultFogMaxStrength;
      this.grassColor = defaultGrassColor;
      this.foliageColor = defaultFoliageColor;
      this.waterColor = defaultWaterColor;
    }

    public String getLabel() {
      return label;
    }

    public int getId() {
      return id;
    }
  }
}
