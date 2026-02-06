package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public final class BiomeConfig {

  private BiomeConfig() {}

  // Folder + files (server_root/config/zerosmod/*.cfg)
  private static File biomeConfigDir;
  private static Configuration biomesConfig;

  // Forge Config pisses me off ong
  private static final String HEADER_CAT = "_README".toUpperCase();

  // Biome visuals (IDs are NOT configurable, only visuals are.)
  public static final BiomeVisuals SPIRIT_GARDEN = new BiomeVisuals("Spirit Garden", 80);
  public static final BiomeVisuals VAKRON       = new BiomeVisuals("Vakron", 81);
  public static final BiomeVisuals DRAGON_REALM = new BiomeVisuals("Dragon Realm", 82);
  public static final BiomeVisuals ZS_BIOME_2 = new BiomeVisuals("Zs Biome 2", 83);
  public static final BiomeVisuals ZS_BIOME_3 = new BiomeVisuals("Zs Biome 3", 84);
  public static final BiomeVisuals ZS_BIOME_4 = new BiomeVisuals("Zs Biome 4", 85);
  public static final BiomeVisuals ZS_BIOME_5 = new BiomeVisuals("Zs Biome 5", 86);
  public static final BiomeVisuals ZS_BIOME_6 = new BiomeVisuals("Zs Biome 6", 87);
  public static final BiomeVisuals ZS_BIOME_7 = new BiomeVisuals("Zs Biome 7", 88);
  public static final BiomeVisuals ZS_BIOME_8 = new BiomeVisuals("Zs Biome 8", 89);
  public static final BiomeVisuals ZS_BIOME_9 = new BiomeVisuals("Zs Biome 9", 90);
  public static final BiomeVisuals ZS_BIOME_10 = new BiomeVisuals("Zs Biome 10", 91);

  // Call once in mod preInit BEFORE biomes are instantiated.
  public static void load(FMLPreInitializationEvent event) {
    // config/
    File rootConfigDir = event.getModConfigurationDirectory();

    // config/zerosmod/
    biomeConfigDir = new File(rootConfigDir, "zerosmod");
    if (!biomeConfigDir.exists()) {
      //noinspection ResultOfMethodCallIgnored
      biomeConfigDir.mkdirs();
    }

    // config/zerosmod/biomes.cfg
    File biomesFile = new File(biomeConfigDir, "biomes.cfg");
    biomesConfig = new Configuration(biomesFile);

    biomesConfig.load();

    biomesConfig.addCustomCategoryComment(HEADER_CAT,
        "====================================================\n" +
            " ZeroSMod - Biome Visuals Config (biomes.cfg)\n" +
            "====================================================\n" +
            "\n" +
            "This file controls biome VISUALS only (client-side rendering).\n" +
            "Changes take effect after a restart.\n" +
            "\n" +
            "IMPORTANT:\n" +
            "- Sky Color is the top half of the sky while Fog is the lower half.\n" +
            "- Fog Strength: -1 uses the mod default.\n" +
            "- High fog strength will drastically change the look of the sky.\n" +
            "\n" +
            "Hex format: 0xRRGGBB, #RRGGBB, or RRGGBB\n" +
            "====================================================\n"
    );

    // -----------------
    // Spirit Garden (80)
    // -----------------
    loadBiomeVisuals(
        SPIRIT_GARDEN,
        "Spirit Garden",
        80,
        0x5A30B8, // sky default
        0x6A44BF, // fog default
        -1.0F,    // fog strength default: -1 = use default
        0x3BAD59, // grass default
        0x228F3F, // foliage default
        0xF56C62  // water overlay default
    );

    // -----------------
    // Vakron (81)
    // -----------------
    loadBiomeVisuals(
        VAKRON,
        "Vakron",
        81,
        0x7C0A02, // sky default
        0x2A2A2A, // fog default
        -1.0F,    // fog strength default: -1 = use default
        0xCD1C18, // grass default
        0x960019, // foliage default
        0x960019  // water overlay default
    );

    // -----------------
    // Dragon Realm (82)
    // -----------------
    loadBiomeVisuals(
        DRAGON_REALM,
        "Dragon Realm",
        82,
        0xFFD235, // sky default
        0xFDDC5C, // fog default
        -1.0F,    // fog strength default: -1 = use default
        0xFDDC5C, // grass default
        0xD3AF37, // foliage default
        0xFFC300  // water overlay default
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

    // -----------------
    // ZS Biome 5 (86)
    // -----------------
    loadBiomeVisuals(
        ZS_BIOME_5,
        "ZS Biome 5",
        86,
        0xFFFFF9, // sky default
        0xFFFFF9, // fog default
        -1.0F,    // fog strength default: -1 = use default
        0xFFFFF9, // grass default
        0xFFFFF9, // foliage default
        0xFFFFF9  // water overlay default
    );

    // -----------------
    // ZS Biome 6 (87)
    // -----------------
    loadBiomeVisuals(
        ZS_BIOME_6,
        "ZS Biome 6",
        87,
        0xFFFFF9, // sky default
        0xFFFFF9, // fog default
        -1.0F,    // fog strength default: -1 = use default
        0xFFFFF9, // grass default
        0xFFFFF9, // foliage default
        0xFFFFF9  // water overlay default
    );

    // -----------------
    // ZS Biome 7 (88)
    // -----------------
    loadBiomeVisuals(
        ZS_BIOME_7,
        "ZS Biome 7",
        88,
        0xFFFFF9, // sky default
        0xFFFFF9, // fog default
        -1.0F,    // fog strength default: -1 = use default
        0xFFFFF9, // grass default
        0xFFFFF9, // foliage default
        0xFFFFF9  // water overlay default
    );

    // -----------------
    // ZS Biome 8 (89)
    // -----------------
    loadBiomeVisuals(
        ZS_BIOME_8,
        "ZS Biome 8",
        89,
        0xFFFFF9, // sky default
        0xFFFFF9, // fog default
        -1.0F,    // fog strength default: -1 = use default
        0xFFFFF9, // grass default
        0xFFFFF9, // foliage default
        0xFFFFF9  // water overlay default
    );

    // -----------------
    // ZS Biome 9 (90)
    // -----------------
    loadBiomeVisuals(
        ZS_BIOME_9,
        "ZS Biome 9",
        90,
        0xFFFFF9, // sky default
        0xFFFFF9, // fog default
        -1.0F,    // fog strength default: -1 = use default
        0xFFFFF9, // grass default
        0xFFFFF9, // foliage default
        0xFFFFF9  // water overlay default
    );

    // -----------------
    // ZS Biome 10 (91)
    // -----------------
    loadBiomeVisuals(
        ZS_BIOME_10,
        "ZS Biome 10",
        91,
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
    String cat = "Biome - " + out.getLabel().toUpperCase() + " (" + biomeId + ")";

    out.biomeName = biomesConfig.getString(
        "Biome Name",
        cat,
        displayName,
        "Biome display name"
    );

    out.skyColor = getHexInt(
        "Sky Color",
        cat,
        defaultSky,
        "Sky color hex"
    );

    out.fogColor = getHexInt(
        "Fog Color",
        cat,
        defaultFog,
        "Fog color hex"
    );

    out.fogMaxStrength = biomesConfig.getFloat(
        "Fog Strength",
        cat,
        defaultFogStrength,
        -1.0F,
        20.0F,
        "Fog maxStrength"
    );

    out.grassColor = getHexInt(
        "Grass Color",
        cat,
        defaultGrass,
        "Grass color hex"
    );

    out.foliageColor = getHexInt(
        "Foliage Color",
        cat,
        defaultFoliage,
        "Foliage color hex"
    );

    out.waterColor = getHexInt(
        "Water Color",
        cat,
        defaultWater,
        "Water overlay color hex. This is an overlay tint, not a real water texture."
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
