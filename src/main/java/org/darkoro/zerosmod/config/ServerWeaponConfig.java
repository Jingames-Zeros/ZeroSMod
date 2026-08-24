package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.darkoro.zerosmod.config.defaults.GeneralWeaponSettings;
import org.darkoro.zerosmod.config.defaults.WeaponTypesDefaults;
import org.darkoro.zerosmod.zsweapons.cache.CachedWeaponStats;
import org.darkoro.zerosmod.zsweapons.enums.WeaponConfigKey;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.darkoro.zerosmod.zsweapons.enums.WeaponConfigKey.FORMATTED_TYPE;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponTypeId.*;

public class ServerWeaponConfig {
    private static final String CONFIG_FILE_NAME = "weapon_server.cfg";
    private static boolean ENABLED;
    public static Map<String, CachedWeaponStats> loadedWeaponStats = new HashMap<>();
    public static GeneralSettings generalSettings = new GeneralSettings();

    private static final String[] fileHeader = {
            "# ZeroSMod server Weapon System config",
            "#",
            "# This currently contains general weapon settings and predefined weapon types.",
            "#",
            "# Format:",
            "# [FEATURE]",
            "# setting = value",
            "#",
            "# Matching ignores case and extra spaces.",
            ""
    };

    private static final String[] enableConfig = {
            "# Combat System toggles - 1 = Enable, 0 = Disable",
            "# Disabling all modules overwrites all",
            "[Combat System]",
            "All Combat Modules = 1",
            ""
    };

    private static final String[][] essentialDefaults = {fileHeader, enableConfig};

    private static File ServerWeaponConfigFile;

    public static void load(FMLPreInitializationEvent event) {
        File configDir = new File(event.getModConfigurationDirectory(), "zerosmod");
        if (!configDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            configDir.mkdirs();
        }

        ServerWeaponConfigFile = new File(configDir, CONFIG_FILE_NAME);
        reload();
    }

    public static void reload() {
        if (ServerWeaponConfigFile == null) return;

        ensureDefaultFile();
        readPathFile();
    }

    private static void ensureDefaultFile() {
        if (ServerWeaponConfigFile.exists()) return;
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(ServerWeaponConfigFile));
            writeSection(essentialDefaults, writer);
            writeSection(GeneralWeaponSettings.values, writer);
            writeSection(WeaponTypesDefaults.values, writer);
            writer.close();
        } catch (IOException ignored) {}
    }

    /**
     * Writes defaults from a defaults string array
     * @param defaults String array containing an array of defaults arrays
     * @param writer .
     */
    private static void writeSection(String[][] defaults, PrintWriter writer) {
        for(String[] section : defaults) {
            for(String line : section) {
                writer.println(line);
            }
        }
    }

    private static void readPathFile() {
        BufferedReader reader = null;
        ENABLED = true;
        loadedWeaponStats.clear();

        try {
            reader = new BufferedReader(new FileReader(ServerWeaponConfigFile));
            String line;
            String section = "";
            String currentWeaponType = "";
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith(";")) continue;

                if (line.startsWith("[")) {
                    section = line;
                    continue;
                }

                int separator = line.indexOf('=');
                if (separator < 0) {
                    separator = line.indexOf(':');
                }
                if (separator <= 0 || separator >= line.length() - 1) continue;


                String key = line.substring(0, separator).trim().toLowerCase();
                String normalizedKey = ConfigHandler.normalizeKey(key);
                String value = line.substring(separator + 1).trim().toLowerCase();
                String unnormalisedValue = line.substring(separator + 1).trim();
                if(section.equals(enableConfig[2]) && normalizedKey.equals("allcombatmodules")) {
                    ENABLED = value.equalsIgnoreCase("1");
                }
                else if(section.equals(GeneralWeaponSettings.generalSettings[0])) {
                    switch(normalizedKey) {
                        case "sweetspotfalloffspeed":
                            generalSettings.sweetSpotFalloff = Float.parseFloat(value);
                            break;
                        case "sweetspotmaxdamageincrease":
                            generalSettings.sweetSpotDamage = Float.parseFloat(value);
                            break;
                    }
                }

                else if(section.equalsIgnoreCase(WeaponTypesDefaults.header[0])) {
                    if(normalizedKey.equals(WeaponConfigKey.TYPE.key)) {
                        currentWeaponType = ConfigHandler.normalizeKey(value);
                        CachedWeaponStats stats = new CachedWeaponStats(currentWeaponType);
                        stats.copy(loadedWeaponStats.get(DEFAULT), false);
                        loadedWeaponStats.put(currentWeaponType, stats);
                    }
                    if(!currentWeaponType.isEmpty()) {
                        CachedWeaponStats stats = loadedWeaponStats.get(currentWeaponType);
                        if(normalizedKey.equals(FORMATTED_TYPE.key)) {
                            loadWeaponState(stats, normalizedKey, unnormalisedValue);
                        } else {
                            loadWeaponState(stats, normalizedKey, value);
                        }
                    }
                }
            }
        } catch (IOException | CachedWeaponStats.ProtectedWeaponTypeException ignored) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {}
            }
        }
    }

    public static void loadWeaponState(CachedWeaponStats stats, String key, String value) throws CachedWeaponStats.ProtectedWeaponTypeException {
        if (stats == null) return;
        WeaponConfigKey configKey = WeaponConfigKey.fromKey(key);
        if (configKey == null) return;
        switch(configKey) {
            case ATTACK_COOLDOWN:
                stats.setCooldown(Integer.parseInt(value));
                stats.setBlockCooldown(Integer.parseInt(value));
                break;

            case MELEE_PERCENT:
                stats.setAttackPercent(Float.parseFloat(value));
                break;

            case MELEE_RANGE:
                stats.setRange(Float.parseFloat(value));
                break;

            case CAN_CHARGE_KI:
                stats.setCanChargeKi(Boolean.parseBoolean(value));
                break;

            case KI_PERCENT:
                stats.setKiPercent(Float.parseFloat(value));
                break;

            case KI_COST_PERCENT:
                stats.setKiCostPercent(Float.parseFloat(value));
                break;

            case SWEET_SPOT:
                stats.setSweetSpot(Float.parseFloat(value));
                break;

            case CAN_BLOCK:
                stats.setCanBlock(Boolean.parseBoolean(value));
                break;

            case BLOCK_DEX_PERCENT:
                stats.setBlockDexPercent(Float.parseFloat(value));
                break;

            case BLOCK_COST_PERCENT:
                stats.setBlockCostPercent(Float.parseFloat(value));
                break;

            case BLOCK_COOLDOWN:
                stats.setBlockCooldown(Integer.parseInt(value));
                break;

            case FORMATTED_TYPE:
                stats.setFormattedType(value);
                break;
        }
    }

    /**
     * @return If weapon system is enabled
     */
    public static boolean isEnabled() { return ENABLED; }

    /**
     * General combat settings used for sweet spot etc
     */
    public static class GeneralSettings {
        private float sweetSpotFalloff = 1.0F;
        private float sweetSpotDamage = 0.25F;

        public float getSweetSpotDamage() { return sweetSpotDamage; }
        public float getSweetSpotFalloff() { return sweetSpotFalloff; }
    }
}
