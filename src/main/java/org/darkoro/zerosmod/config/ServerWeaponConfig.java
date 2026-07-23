package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.io.*;

public class ServerWeaponConfig {
    private static final String CONFIG_FILE_NAME = "weapon_server.cfg";
    private static boolean ENABLED;

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
            "Disable All Modules = 1",
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
            writer.close();
        } catch (IOException ignored) {}
    }

    private static void readPathFile() {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(ServerWeaponConfigFile));
            String line;
            String section = "";
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


                String key = line.substring(0, separator).trim();
                String value = line.substring(separator + 1).trim();
                if(section.equals("[Combat System]") && key.equals("Disable All Modules")) {
                    ENABLED = !value.equals("1");
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

    public static boolean isEnabled() { return ENABLED; }
}
