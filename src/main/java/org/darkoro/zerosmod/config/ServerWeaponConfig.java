package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.darkoro.zerosmod.config.defaults.WeaponTypesDefaults;
import org.darkoro.zerosmod.zsweapons.CachedWeaponState;
import org.darkoro.zerosmod.zsweapons.WeaponConfigKey;
import org.darkoro.zerosmod.zsweapons.WeaponTypeId;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.darkoro.zerosmod.zsweapons.WeaponTypeId.*;

public class ServerWeaponConfig {
    private static final String CONFIG_FILE_NAME = "weapon_server.cfg";
    private static boolean ENABLED;
    public static CachedWeaponState defaultWeaponState;
    public static Map<String, CachedWeaponState> loadedWeaponStates = new HashMap<>();

    private static final String[] fileHeader = {
            "# ZeroSMod server Weapon System config",
            "#",
            "# This currently .",
            "#",
            "# Format:",
            "# [FEATURE]",
            "# setting = value",
            "#",
            "# colour accepts ARGB values between FFFFFFFF and 00000000.",
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
            for(String line : fileHeader) {
                writer.println(line);
            }
            for(String line : enableConfig) {
                writer.println(line);
            }
            for(String[] section : WeaponTypesDefaults.values) {
                for(String line : section) {
                    writer.println(line);
                }
            }

            writer.close();
        } catch (IOException ignored) {}
    }

    private static void readPathFile() {
        BufferedReader reader = null;
        ENABLED = true;
        defaultWeaponState = new CachedWeaponState(DEFAULT);
        loadedWeaponStates.clear();

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
                if(section.equals(enableConfig[2]) && normalizedKey.equals("allcombatmodules")) {
                    ENABLED = value.equalsIgnoreCase("1");
                } else if(section.equals(enableConfig[2]) && normalizedKey.equals("disableallmodules")) {
                    ENABLED = !value.equalsIgnoreCase("1");
                }
                else if(section.equalsIgnoreCase(WeaponTypesDefaults.header[0])) {
                    if(normalizedKey.equals(WeaponConfigKey.TYPE.key)) {
                        currentWeaponType = ConfigHandler.normalizeKey(value);
                        if(currentWeaponType.equals(DEFAULT)) {
                            defaultWeaponState = new CachedWeaponState(DEFAULT);
                        } else {
                            CachedWeaponState weaponState = new CachedWeaponState(currentWeaponType);
                            weaponState.copy(defaultWeaponState);
                            loadedWeaponStates.put(currentWeaponType, weaponState);
                        }
                    }
                    if(!currentWeaponType.isEmpty()) {
                        CachedWeaponState state = currentWeaponType.equals(DEFAULT) ?
                                defaultWeaponState : loadedWeaponStates.get(currentWeaponType);
                        loadWeaponState(state, normalizedKey, value);
                    }
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
    }

    public static void loadWeaponState(CachedWeaponState state, String key, String value) {
        if (state == null) return;
        WeaponConfigKey configKey = WeaponConfigKey.fromKey(key);
        if (configKey == null) return;
        switch(configKey) {
            case TYPE:
                state.type = value;
                break;
            case ATTACK_COOLDOWN:
                state.blockCooldown = state.cooldown = Integer.parseInt(value);
                break;
            case MELEE_MULTIPLIER:
                state.attackMultiplier = Float.parseFloat(value);
                break;
            case MELEE_RANGE:
                state.setRange(Float.parseFloat(value));
                break;
            case CAN_CHARGE_KI:
                state.canChargeKi = Boolean.parseBoolean(value);
                break;
            case KI_ADDITIVE_DAMAGE:
                state.kiAdditive = Integer.parseInt(value);
                break;
            case KI_MULTIPLIER:
                state.kiMultiplier = Float.parseFloat(value);
                break;
            case SWEET_SPOT:
                state.sweetSpot = Float.parseFloat(value);
                break;
            case CAN_BLOCK:
                state.canBlock = Boolean.parseBoolean(value);
                break;
            case BLOCK_DEX_PERCENT:
                state.blockReduction = Float.parseFloat(value);
                break;
            case BLOCK_COST_MULTIPLIER:
                state.blockCostMultiplier = Float.parseFloat(value);
                break;
            case BLOCK_COOLDOWN:
                state.blockCooldown = Integer.parseInt(value);
                break;
        }
    }

    public static boolean isEnabled() { return ENABLED; }
}
