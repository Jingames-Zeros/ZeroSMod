package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.darkoro.zerosmod.config.defaults.WeaponTypesDefaults;
import org.darkoro.zerosmod.zsweapons.CachedWeaponState;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

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
                String value = line.substring(separator + 1).trim().toLowerCase();
                if(section.equals(enableConfig[2]) && key.equals("all combat modules")) {
                    ENABLED = value.equalsIgnoreCase("1");
                }
                else if(section.equalsIgnoreCase(WeaponTypesDefaults.header[0])) {
                    if(key.equals("type")) {
                        currentWeaponType = value;
                        if(value.equals("default")) {
                            defaultWeaponState = new CachedWeaponState("default");
                        } else {
                            loadedWeaponStates.put(value, new CachedWeaponState());
                        }
                    }
                    if(!currentWeaponType.isEmpty()) {
                        CachedWeaponState state = currentWeaponType.equals("default") ?
                                defaultWeaponState : loadedWeaponStates.get(currentWeaponType);
                        loadWeaponState(state, key.toLowerCase(), value);
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
        switch(key) {
            case "type":
                state.type = value;
                break;
            case "attack cooldown":
                state.blockCooldown = state.cooldown = Integer.parseInt(value);
                break;
            case "melee multiplier":
                state.attackMultiplier = Float.parseFloat(value);
                break;
            case "melee range":
                state.setRange(Float.parseFloat(value));
                break;
            case "can charge ki":
                state.canChargeKi = Boolean.parseBoolean(value);
                break;
            case "ki additive damage":
                // TODO
                break;
            case "ki multiplier":
                // TODO
                break;
            case "can block":
                state.canBlock = Boolean.parseBoolean(value);
                break;
            case "block dex percent":
                state.blockReduction = Float.parseFloat(value);
                break;
            case "block cost multiplier":
                state.blockCostMultiplier = Float.parseFloat(value);
                break;
            case "block cooldown":
                state.blockCooldown = Integer.parseInt(value);
                break;
        }
    }

    public static boolean isEnabled() { return ENABLED; }
}
