package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.io.*;

public class ClientWeaponConfig {
    private static final String CONFIG_FILE_NAME = "weapon_client.cfg";
    private static hudConfig HUD_CONFIG = null;

    private static final String[] fileHeader = {
            "# ZeroSMod client Weapon System config",
            "#",
            "# This currently only controls the attack cooldown hud dimensions.",
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

    private static final String[] hudPositionConfig = {
            "# HUD Position - Percentage coordinates for each corner of the bar (0.0 - 1.0)",
            "[HUD Position]",
            "x1 = 0.47",
            "y1 =  0.55",
            "x2 = 0.53",
            "y2 = 0.56",
            ""
    };

    private static final String[] hudColourConfig = {
            "# HUD Colour - Hexadecimal ARBG Colours for hud main bar and shadow",
            "[HUD Colour]",
            "Progress Bar = AA00FFFF",
            "Shadow Bar =  AA000000",
            ""
    };

    private static File clientWeaponConfigFile;

    public static void load(FMLPreInitializationEvent event) {
        File configDir = new File(event.getModConfigurationDirectory(), "zerosmod");
        if (!configDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            configDir.mkdirs();
        }

        clientWeaponConfigFile = new File(configDir, CONFIG_FILE_NAME);
        reload();
    }

    public static void reload() {
        if (clientWeaponConfigFile == null) return;

        ensureDefaultFile();
        readPathFile();
    }

    private static void ensureDefaultFile() {
        if (clientWeaponConfigFile.exists()) return;
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(clientWeaponConfigFile));
            for(String line : fileHeader) {
                writer.println(line);
            }
            for(String line : hudPositionConfig) {
                writer.println(line);
            }
            for(String line : hudColourConfig) {
                writer.println(line);
            }
            writer.close();
        } catch (IOException ignored) {}
    }

    private static void readPathFile() {
        BufferedReader reader = null;
        float x1 = 0;
        float y1 = 0;
        float x2 = 0;
        float y2 = 0;
        int progressBarColour = 0;
        int progressBarShadow = 0;

        try {
            reader = new BufferedReader(new FileReader(clientWeaponConfigFile));
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
                if(section.equals("[HUD Position]")) {
                    switch(key) {
                        case "x1":
                            x1 = Float.parseFloat(value);
                            break;
                        case "y1":
                            y1 = Float.parseFloat(value);
                            break;
                        case "x2":
                            x2 = Float.parseFloat(value);
                            break;
                        case "y2":
                            y2 = Float.parseFloat(value);
                            break;
                    }
                }
                else if(section.equals("[HUD Colour]")) {
                    switch(key) {
                        case "Progress Bar":
                            progressBarColour = (int) Long.parseLong(value, 16);
                            break;

                        case "Shadow Bar":
                            progressBarShadow = (int) Long.parseLong(value, 16);
                            break;
                    }
                }
            }
        } catch (IOException ignored) {
        } finally {
            HUD_CONFIG = new hudConfig(x1, y1, x2, y2, progressBarColour, progressBarShadow);
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {}
            }
        }
    }

    public static hudConfig getHudConfig() { return HUD_CONFIG; }

    public static final class hudConfig {
        public final float x1;
        public final float y1;
        public final float x2;
        public final float y2;
        public final int progressBarColour;
        public final int progressBarShadowColour;

        private hudConfig(float x1, float y1, float x2, float y2, int progressBarColour, int progressBarShadowColour) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.progressBarColour = progressBarColour;
            this.progressBarShadowColour = progressBarShadowColour;
        }
    }
}
