package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public final class MusicPackConfig {

  private static final String CONFIG_FILE_NAME = "music_pack.cfg";

  private static File configFile;
  private static String url = "";

  private MusicPackConfig() {}

  public static void load(FMLPreInitializationEvent event) {
    configFile = new File(ConfigHandler.getConfigDir(event), CONFIG_FILE_NAME);
    reload();
  }

  public static void reload() {
    if (configFile == null) {
      return;
    }

    url = "";
    if (!configFile.exists()) {
      writeDefaultConfig();
      return;
    }

    ConfigHandler.parseIni(configFile, new ConfigHandler.IniVisitor() {
      @Override public void onSection(String name) {}

      @Override public void onKeyValue(String key, String value) {
        if ("url".equalsIgnoreCase(key)) {
          url = value.trim();
        }
      }
    });
  }

  private static void writeDefaultConfig() {
    try (PrintWriter writer = new PrintWriter(new FileWriter(configFile))) {
      writer.println("# ZeroSMod music pack hint");
      writer.println("#");
      writer.println("# Players without a CustomNPC sounds.json get a chat message with this link on login.");
      writer.println("# Leave url empty to turn the message off. Use /zsmod reload after changing it.");
      writer.println();
      writer.println("[MusicPack]");
      writer.println("url = ");
    } catch (IOException ignored) {
    }
  }

  public static String getUrl() {
    return url;
  }
}
