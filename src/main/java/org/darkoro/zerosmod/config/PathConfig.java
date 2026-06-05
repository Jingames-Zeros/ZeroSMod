package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.util.EnumChatFormatting;

public final class PathConfig {

  private static final String CONFIG_FILE_NAME = "tab_paths.cfg";
  private static final List<PathEntry> paths = new ArrayList<PathEntry>();

  private static File pathConfigFile;

  private PathConfig() {}

  public static void load(FMLPreInitializationEvent event) {
    File configDir = new File(event.getModConfigurationDirectory(), "zerosmod");
    if (!configDir.exists()) {
      //noinspection ResultOfMethodCallIgnored
      configDir.mkdirs();
    }

    pathConfigFile = new File(configDir, CONFIG_FILE_NAME);
    reload();
  }

  public static void reload() {
    if (pathConfigFile == null) {
      return;
    }

    ensureDefaultFile();
    paths.clear();
    readPathFile();
  }

  public static String getPathForForms(Collection<String> formNames) {
    if (formNames == null || formNames.isEmpty()) {
      return "";
    }

    Set<String> unlockedForms = new LinkedHashSet<String>();
    for (String formName : formNames) {
      String normalized = normalize(formName);
      if (normalized.length() > 0) {
        unlockedForms.add(normalized);
      }
    }

    for (PathEntry path : paths) {
      for (String formName : path.forms) {
        if (unlockedForms.contains(formName)) {
          return path.getFormattedDisplayName();
        }
      }
    }

    return "";
  }

  private static void ensureDefaultFile() {
    if (pathConfigFile.exists()) {
      return;
    }

    try {
      PrintWriter writer = new PrintWriter(new FileWriter(pathConfigFile));
      writer.println("# ZeroSMod tab path display config");
      writer.println("#");
      writer.println("# This only controls the path name shown in tab.");
      writer.println("# It does not lock, unlock, grant, or remove forms.");
      writer.println("#");
      writer.println("# Format:");
      writer.println("# [Display Path]");
      writer.println("# color = white");
      writer.println("# bold = false");
      writer.println("# forms = Form Name 1, Form Name 2, Form Name 3");
      writer.println("#");
      writer.println("# color accepts names like white, gray, aqua, dark_red, or codes like f, 7, &b, §b.");
      writer.println("# bold is optional and defaults to false.");
      writer.println("#");
      writer.println("# The first matching path in this file wins.");
      writer.println("# Matching ignores case, extra spaces, and Minecraft color codes.");
      writer.println();
      writer.println("[Angel]");
      writer.println("color = white");
      writer.println("bold = false");
      writer.println("forms = A-Ultra Instinct Omen, A-Ultra Instinct");
      writer.println();
      writer.println("[Destroyer]");
      writer.println("color = white");
      writer.println("bold = false");
      writer.println("forms = A-God of Destruction, A-Ultra Ego, SAI-Blue Evolution, NAM-Orange, MAJ-Pure Chaos, HUM-Indomitable Spirit, ARC-Black");
      writer.println();
      writer.println("[Godform]");
      writer.println("color = white");
      writer.println("bold = false");
      writer.println("forms = SAI-SSG, SAI-SSB, SAI-Blue Evolution, SAI-SSG (GS), SAI-SSB (GS), ARC-God, NAM-God, HUM-God, MAJ-God");
      writer.println();
      writer.println("[SSJ4]");
      writer.println("color = white");
      writer.println("bold = false");
      writer.println("forms = SAI-SSJ4, SAI-FPSSJ4, SAI-LBSSJ4");
      writer.println();
      writer.println("[SSJ1]");
      writer.println("color = white");
      writer.println("bold = false");
      writer.println("forms = SAI-SSJ1, SAI-SS Green, SAI-SSMaxPower");
      writer.println();
      writer.println("[Fifth Form]");
      writer.println("color = white");
      writer.println("bold = false");
      writer.println("forms = ARC-Full Power 5th, ARC-Golden 5th, ARC-Black 5th");
      writer.println();
      writer.println("[Evil]");
      writer.println("color = white");
      writer.println("bold = false");
      writer.println("forms = MAJ-Evil, MAJ-Full Power, MAJ-Pure, MAJ-God, MAJ-Pure Chaos");
      writer.println();
      writer.println("[Mimicry]");
      writer.println("color = white");
      writer.println("bold = false");
      writer.println("forms = MAJ-Mimicry, MAJ-Ultimate Absorbtion");
      writer.println();
      writer.println("[Good]");
      writer.println("color = white");
      writer.println("bold = false");
      writer.println("forms = MAJ-Good, MAJ-Serene, MAJ-Saint");
      writer.close();
    } catch (IOException ignored) {}
  }

  private static void readPathFile() {
    BufferedReader reader = null;
    String currentPath = null;
    String currentColor = "white";
    boolean currentBold = false;
    List<String> currentForms = new ArrayList<String>();

    try {
      reader = new BufferedReader(new FileReader(pathConfigFile));
      String line;
      while ((line = reader.readLine()) != null) {
        String cleanLine = line.trim();
        if (cleanLine.length() == 0 || cleanLine.startsWith("#") || cleanLine.startsWith(";")) {
          continue;
        }

        if (cleanLine.startsWith("[") && cleanLine.endsWith("]")) {
          addEntry(currentPath, currentColor, currentBold, currentForms);
          currentPath = cleanLine.substring(1, cleanLine.length() - 1).trim();
          currentColor = "white";
          currentBold = false;
          currentForms = new ArrayList<String>();
          continue;
        }

        int separator = cleanLine.indexOf('=');
        if (separator < 0) {
          separator = cleanLine.indexOf(':');
        }

        if (separator <= 0 || separator >= cleanLine.length() - 1) {
          continue;
        }

        String key = cleanLine.substring(0, separator).trim();
        String value = cleanLine.substring(separator + 1).trim();
        if ("forms".equalsIgnoreCase(key)) {
          addForms(currentForms, value);
        } else if ("color".equalsIgnoreCase(key)) {
          currentColor = value;
        } else if ("bold".equalsIgnoreCase(key)) {
          currentBold = Boolean.parseBoolean(value);
        } else if (currentPath == null) {
          addLegacyEntry(key, value);
        }
      }
    } catch (IOException ignored) {
    } finally {
      addEntry(currentPath, currentColor, currentBold, currentForms);
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ignored) {}
      }
    }
  }

  private static void addLegacyEntry(String displayPath, String formsPart) {
    List<String> forms = new ArrayList<String>();
    addForms(forms, formsPart);
    addEntry(displayPath, "white", false, forms);
  }

  private static void addEntry(String displayPath, String colorName, boolean bold, List<String> forms) {
    if (displayPath == null || forms == null) {
      return;
    }

    String cleanPath = displayPath.trim();
    if (cleanPath.length() == 0) {
      return;
    }

    Set<String> normalizedForms = new LinkedHashSet<String>();
    for (String form : forms) {
      String normalizedForm = normalize(form);
      if (normalizedForm.length() > 0) {
        normalizedForms.add(normalizedForm);
      }
    }

    if (!normalizedForms.isEmpty()) {
      paths.add(new PathEntry(cleanPath, parseColor(colorName), bold, normalizedForms));
    }
  }

  private static void addForms(List<String> forms, String formsPart) {
    if (formsPart == null) {
      return;
    }

    String[] splitForms = formsPart.split(",");
    for (String form : splitForms) {
      String cleanForm = form.trim();
      if (cleanForm.length() > 0) {
        forms.add(cleanForm);
      }
    }
  }

  private static String normalize(String value) {
    if (value == null) {
      return "";
    }

    StringBuilder builder = new StringBuilder();
    boolean skipNext = false;
    for (int i = 0; i < value.length(); i++) {
      char current = value.charAt(i);
      if (skipNext) {
        skipNext = false;
        continue;
      }
      if (current == '\u00a7') {
        skipNext = true;
        continue;
      }
      if (current == '\u00c2') {
        continue;
      }
      builder.append(current);
    }

    return builder.toString().trim().toLowerCase(Locale.ROOT);
  }

  private static EnumChatFormatting parseColor(String value) {
    if (value == null) {
      return EnumChatFormatting.WHITE;
    }

    String clean = value.trim();
    if (clean.length() == 0) {
      return EnumChatFormatting.WHITE;
    }

    if (clean.startsWith("&") || clean.startsWith("\u00a7")) {
      clean = clean.substring(1);
    }

    if (clean.length() == 1) {
      char colorCode = Character.toLowerCase(clean.charAt(0));
      for (EnumChatFormatting formatting : EnumChatFormatting.values()) {
        if (!formatting.isFancyStyling() && formatting.getFormattingCode() == colorCode) {
          return formatting;
        }
      }
    }

    String normalized = clean.replace(' ', '_').replace('-', '_').toUpperCase(Locale.ROOT);
    try {
      EnumChatFormatting formatting = EnumChatFormatting.valueOf(normalized);
      return formatting.isFancyStyling() ? EnumChatFormatting.WHITE : formatting;
    } catch (IllegalArgumentException ignored) {
      return EnumChatFormatting.WHITE;
    }
  }

  private static final class PathEntry {
    private final String displayName;
    private final EnumChatFormatting color;
    private final boolean bold;
    private final Set<String> forms;

    private PathEntry(String displayName, EnumChatFormatting color, boolean bold, Set<String> forms) {
      this.displayName = displayName;
      this.color = color;
      this.bold = bold;
      this.forms = forms;
    }

    private String getFormattedDisplayName() {
      return color + "" + (bold ? EnumChatFormatting.BOLD : "") + displayName;
    }
  }
}
