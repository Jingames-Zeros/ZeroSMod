package org.darkoro.zerosmod.dbchair.gui;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import JinRyuu.JRMCore.JRMCoreH;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/** Keeps editor presets as normal DBC hair codes without touching DBC's shared NBT file. */
final class HairPresetStore {
  private static final String FILE_NAME = "zerosmod_hair_presets.txt";
  private static final List<Preset> CUSTOM = new ArrayList<Preset>();
  private static boolean loaded;

  static final class Preset {
    final String name;
    final String code;
    Preset(String name, String code) { this.name = name; this.code = code; }
  }

  private HairPresetStore() { }

  static void ensureLoaded(File gameDir, String activeCode) {
    if (!loaded) {
      loaded = true;
      File file = new File(gameDir, FILE_NAME);
      if (file.isFile()) {
        if (!read(file)) {
          File backup = new File(gameDir, FILE_NAME + ".corrupt-" + System.currentTimeMillis());
          try { Files.copy(file.toPath(), backup.toPath()); } catch (Exception ignored) { }
          write(gameDir);
        }
      } else {
        importLegacyPresets();
        write(gameDir);
      }
    }
    int selected = findCode(activeCode);
    if (selected < 0) selected = JRMCoreGuiScreen.HairPrstsSlcted;
    refreshGlobalList(selected);
  }

  private static int findCode(String code) {
    if (!validCode(code)) return -1;
    ArrayList<String> list = new ArrayList<String>();
    for (String preset : JRMCoreH.defHairPrsts) list.add(preset);
    for (Preset preset : CUSTOM) list.add(preset.code);
    for (int i = list.size() - 1; i >= 0; i--) if (code.equals(list.get(i))) return i;
    return -1;
  }

  private static boolean read(File file) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(
        new FileInputStream(file), StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        int split = line.indexOf('\t');
        if (split < 0) continue;
        String name = new String(Base64.getDecoder().decode(line.substring(0, split)), StandardCharsets.UTF_8);
        String code = line.substring(split + 1);
        if (validCode(code)) CUSTOM.add(new Preset(name, code));
      }
      return true;
    } catch (Exception ignored) { return false; }
  }

  private static void importLegacyPresets() {
    List<?> old = JRMCoreGuiScreen.PresetList;
    int defaults = JRMCoreH.defHairPrsts.length;
    if (old == null) return;
    for (int i = defaults; i < old.size(); i++) {
      Object value = old.get(i);
      if (value instanceof String) {
        String code = normalizeLegacy((String) value);
        if (code != null) CUSTOM.add(new Preset("Custom Preset " + (CUSTOM.size() + 1), code));
      }
    }
  }

  static int customCount() { return CUSTOM.size(); }
  static Preset customAt(int index) { return index >= 0 && index < CUSTOM.size() ? CUSTOM.get(index) : null; }
  static boolean isCustom(int index) { return index >= JRMCoreH.defHairPrsts.length
      && index < JRMCoreH.defHairPrsts.length + CUSTOM.size(); }

  static int add(String name, String code, File gameDir) {
    Preset preset = new Preset(name.trim(), code);
    CUSTOM.add(preset);
    if (!write(gameDir)) {
      CUSTOM.remove(CUSTOM.size() - 1);
      return -1;
    }
    int selected = JRMCoreH.defHairPrsts.length + CUSTOM.size() - 1;
    refreshGlobalList(selected);
    return selected;
  }

  static boolean delete(int index, File gameDir) {
    int customIndex = index - JRMCoreH.defHairPrsts.length;
    if (customIndex < 0 || customIndex >= CUSTOM.size()) return false;
    Preset removed = CUSTOM.remove(customIndex);
    if (!write(gameDir)) {
      CUSTOM.add(customIndex, removed);
      return false;
    }
    int selected = Math.min(index - 1, JRMCoreH.defHairPrsts.length + CUSTOM.size() - 1);
    selected = Math.max(0, selected);
    refreshGlobalList(selected);
    return true;
  }

  static String nameAt(int index) {
    int customIndex = index - JRMCoreH.defHairPrsts.length;
    Preset preset = customAt(customIndex);
    return preset == null ? null : preset.name;
  }

  static void refreshGlobalList(int selected) {
    ArrayList<String> list = new ArrayList<String>();
    for (String preset : JRMCoreH.defHairPrsts) list.add(preset);
    for (Preset preset : CUSTOM) list.add(preset.code);
    JRMCoreGuiScreen.PresetList = list;
    JRMCoreGuiScreen.HairPrstsSlcted = Math.max(0, Math.min(selected, list.size() - 1));
  }

  private static boolean write(File gameDir) {
    File file = new File(gameDir, FILE_NAME);
    File temp = new File(gameDir, FILE_NAME + ".tmp");
    try (FileOutputStream stream = new FileOutputStream(temp);
         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8))) {
      for (Preset preset : CUSTOM) {
        writer.write(Base64.getEncoder().encodeToString(preset.name.getBytes(StandardCharsets.UTF_8)));
        writer.write('\t');
        writer.write(preset.code);
        writer.newLine();
      }
      writer.flush();
      stream.getFD().sync();
    } catch (Exception ignored) {
      return false;
    }
    try {
      try {
        Files.move(temp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (Exception atomicMoveUnavailable) {
        Files.move(temp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
      }
      return true;
    } catch (Exception ignored) {
      return false;
    }
  }

  private static boolean validCode(String code) {
    if (code == null || code.length() != 786) return false;
    for (int i = 0; i < code.length(); i++) if (code.charAt(i) < '0' || code.charAt(i) > '9') return false;
    return true;
  }

  private static String normalizeLegacy(String code) {
    if (code == null || (code.length() != 392 && code.length() != 784 && code.length() != 786)) return null;
    for (int i = 0; i < code.length(); i++) if (code.charAt(i) < '0' || code.charAt(i) > '9') return null;
    if (code.length() != 786) code = JRMCoreH.dnsHairG1toG2(code);
    return validCode(code) ? code : null;
  }
}
