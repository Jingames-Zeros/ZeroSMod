package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

public final class ConfigHandler {

  private ConfigHandler() {}

  public static void loadAll(FMLPreInitializationEvent event) {
    BiomeConfig.load(event);
    DimensionConfig.load(event);
    PathConfig.load(event);
    ClientWeaponConfig.load(event);
    ServerWeaponConfig.load(event);
  }

  public static File getConfigDir(FMLPreInitializationEvent event) {
    File configDir = new File(event.getModConfigurationDirectory(), "zerosmod");
    if (!configDir.exists()) {
      //noinspection ResultOfMethodCallIgnored
      configDir.mkdirs();
    }
    return configDir;
  }

  public interface IniVisitor {
    void onSection(String name);
    void onKeyValue(String key, String value);
  }

  public static void parseIni(File file, IniVisitor visitor) {
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String cleanLine = line.trim();
        if (cleanLine.isEmpty() || cleanLine.startsWith("#") || cleanLine.startsWith(";")) {
          continue;
        }

        if (cleanLine.startsWith("[") && cleanLine.endsWith("]")) {
          visitor.onSection(cleanLine.substring(1, cleanLine.length() - 1).trim());
          continue;
        }

        int separator = cleanLine.indexOf('=');
        if (separator < 0) {
          separator = cleanLine.indexOf(':');
        }

        if (separator <= 0 || separator >= cleanLine.length() - 1) {
          continue;
        }

        visitor.onKeyValue(cleanLine.substring(0, separator).trim(),
            cleanLine.substring(separator + 1).trim());
      }
    } catch (IOException ignored) {
    }
  }

  public static String normalizeKey(String value) {
    return value == null ? "" : value.trim().replace(" ", "").replace("_", "").replace("-", "").toLowerCase(Locale.ROOT);
  }
}
