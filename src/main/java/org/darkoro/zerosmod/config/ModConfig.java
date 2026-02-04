package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public final class ModConfig {

  private ModConfig() {}

  // Folder + files (server_root/config/zerosmod/*.cfg)
  private static File modConfigDir;
  private static Configuration biomesConfig;

  // Biome visuals (IDs are NOT configurable, only visuals are.)
  public static final BiomeVisuals SPIRIT_GARDEN = new BiomeVisuals("Spirit Garden", 80);
  public static final BiomeVisuals VAKRON       = new BiomeVisuals("Vakron", 81);
  public static final BiomeVisuals DRAGON_REALM = new BiomeVisuals("Dragon Realm", 82);
  public static final BiomeVisuals ZS_BIOME_2 = new BiomeVisuals("Zs Biome 2", 83);
  public static final BiomeVisuals ZS_BIOME_3 = new BiomeVisuals("Zs Biome 3", 84);
  public static final BiomeVisuals ZS_BIOME_4 = new BiomeVisuals("Zs Biome 4", 85);

  // Call once in mod preInit BEFORE biomes are instantiated.
  public static void load(FMLPreInitializationEvent event) {
    // config/
    File rootConfigDir = event.getModConfigurationDirectory();

    // config/zerosmod/
    modConfigDir = new File(rootConfigDir, "zerosmod");
    if (!modConfigDir.exists()) {
      //noinspection ResultOfMethodCallIgnored
      modConfigDir.mkdirs();
    }

    // config/zerosmod/biomes.cfg
    File biomesFile = new File(modConfigDir, "biomes.cfg");
    biomesConfig = new Configuration(biomesFile);

    biomesConfig.load();

    // -----------------
    // Spirit Garden (80)
    // -----------------
    loadBiomeVisuals(
        SPIRIT_GARDEN,
        "Spirit Garden",
        80,
        0xFF991C, // sky default
        0x48006E, // fog default
        -1.0F,    // fog strength default: -1 = use default
        0xC71585, // grass default
        0xC71585, // foliage default
        0x48006E  // water overlay default
    );

    // -----------------
    // Vakron (81)
    // -----------------
    loadBiomeVisuals(
        VAKRON,
        "Vakron",
        81,
        0xFF991C, // sky default
        0x2A2A2A, // fog default
        -1.0F,    // fog strength default: -1 = use default
        0xC71585, // grass default
        0xC71585, // foliage default
        0x48006E  // water overlay default
    );

    // -----------------
    // Dragon Realm (82)
    // -----------------
    loadBiomeVisuals(
        DRAGON_REALM,
        "Dragon Realm",
        82,
        0xFF991C, // sky default
        0xFFFFF9, // fog default
        -1.0F,    // fog strength default: -1 = use default
        0xC71585, // grass default
        0xC71585, // foliage default
        0x48006E  // water overlay default
    );

    // -----------------
    // ZS Biome 2 (83)
    // -----------------
    loadBiomeVisuals(
        ZS_BIOME_2,
        "ZS Biome 2",
        83,
        0xFFFFF9, // sky default
        0xFFFFF9, // fog default
        -1.0F,    // fog strength default: -1 = use default
        0xFFFFF9, // grass default
        0xFFFFF9, // foliage default
        0xFFFFF9  // water overlay default
    );

    // -----------------
    // ZS Biome 3 (84)
    // -----------------
    loadBiomeVisuals(
        ZS_BIOME_3,
        "ZS Biome 3",
        84,
        0xFFFFF9, // sky default
        0xFFFFF9, // fog default
        -1.0F,    // fog strength default: -1 = use default
        0xFFFFF9, // grass default
        0xFFFFF9, // foliage default
        0xFFFFF9  // water overlay default
    );

    // -----------------
    // ZS Biome 4 (85)
    // -----------------
    loadBiomeVisuals(
        ZS_BIOME_4,
        "ZS Biome 4",
        85,
        0xFFFFF9, // sky default
        0xFFFFF9, // fog default
        -1.0F,    // fog strength default: -1 = use default
        0xFFFFF9, // grass default
        0xFFFFF9, // foliage default
        0xFFFFF9  // water overlay default
    );

    if (biomesConfig.hasChanged()) {
      biomesConfig.save();
    }
  }

  private static void loadBiomeVisuals(
      BiomeVisuals out,
      String displayName,
      int biomeId,
      int defaultSky,
      int defaultFog,
      float defaultFogStrength,
      int defaultGrass,
      int defaultFoliage,
      int defaultWater
  ) {
    String cat = "Biome - " + displayName + " (" + biomeId + ")";

    biomesConfig.addCustomCategoryComment(cat,
        "------\n" +
            displayName + " (" + biomeId + ")\n" +
            "------\n\n" +
            "Sky color adjusts slightly by time of day; the target color is for 12:00.\n\n" +
            "Fog color is the lower half of the horizon down into the void.\n" +
            "Fog Strength adjusts how the fog and horizon line blend.\n" +
            "NOTE: HIGH STRENGTH WILL DRASTICALLY AFFECT HOW THE ENTIRE SKY LOOKS.\n" +
            "Message Trent on Discord for any questions.\n\n" +
            "Set Fog Strength to -1 to use the code default.\n"
    );

    out.biomeName = biomesConfig.getString(
        "Biome Name",
        cat,
        displayName,
        "Biome display name."
    );

    out.skyColor = getHexInt(
        "Sky Color",
        cat,
        defaultSky,
        "Sky color hex (RRGGBB). Examples: 0xFF991C, #FF991C, FF991C"
    );

    out.fogColor = getHexInt(
        "Fog Color",
        cat,
        defaultFog,
        "Fog color hex (RRGGBB)."
    );

    out.fogMaxStrength = biomesConfig.getFloat(
        "Fog Strength",
        cat,
        defaultFogStrength,
        -1.0F,
        1.0F,
        "Fog maxStrength. -1 = use code default."
    );

    out.grassColor = getHexInt(
        "Grass Color",
        cat,
        defaultGrass,
        "Grass color hex (RRGGBB). Used for both getBiomeGrassColor and getModdedBiomeGrassColor."
    );

    out.foliageColor = getHexInt(
        "Foliage Color",
        cat,
        defaultFoliage,
        "Foliage color hex (RRGGBB). Used for both getBiomeFoliageColor and getModdedBiomeFoliageColor."
    );

    out.waterColor = getHexInt(
        "Water Color",
        cat,
        defaultWater,
        "Water overlay color hex (RRGGBB). This is an overlay tint, not a real water texture."
    );
  }

  private static int getHexInt(String key, String cat, int defaultRgb, String comment) {
    String defaultStr = toHex6(defaultRgb);
    String raw = biomesConfig.getString(key, cat, defaultStr, comment);
    Integer parsed = parseHexColor(raw);
    return parsed != null ? parsed : (defaultRgb & 0xFFFFFF);
  }

  private static String toHex6(int rgb) {
    return String.format("0x%06X", (rgb & 0xFFFFFF));
  }

  /** Accepts "0xRRGGBB", "#RRGGBB", or "RRGGBB". Returns null if invalid. */
  private static Integer parseHexColor(String raw) {
    if (raw == null) return null;
    String s = raw.trim();
    if (s.startsWith("0x") || s.startsWith("0X")) s = s.substring(2);
    if (s.startsWith("#")) s = s.substring(1);
    if (s.length() != 6) return null;

    try {
      return Integer.parseInt(s, 16) & 0xFFFFFF;
    } catch (NumberFormatException e) {
      return null;
    }
  }

  public static final class BiomeVisuals {
    public String biomeName;
    public int skyColor;
    public int fogColor;
    /** -1 means "use mod default". */
    public float fogMaxStrength;
    public int grassColor;
    public int foliageColor;
    public int waterColor;

    private final String label;
    private final int id;

    private BiomeVisuals(String label, int id) {
      this.label = label;
      this.id = id;
      this.biomeName = label;
    }

    public String getLabel() { return label; }
    public int getId() { return id; }
  }
}
