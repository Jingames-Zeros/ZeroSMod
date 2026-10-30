package org.darkoro.zerosmod.dbchair.gui;

import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/** Versioned per-strand color data appended to DBC's unchanged hair geometry. */
public final class HairStrandColors {
  private static final String MARKER = "~ZS1:";
  private static final int GEOMETRY_LENGTH = 786;
  private static final int STRAND_COUNT = 56;
  private static final int SLOT_LENGTH = 6;
  private static final int DATA_LENGTH = STRAND_COUNT * SLOT_LENGTH;
  public static final int MAJIN_RACE = 5;
  public static final int NO_COLOR = -1;
  private static final int[] NO_OVERRIDES = new int[0];
  private static final int PARSE_CACHE_SIZE = 64;
  private static final Map<String, int[]> PARSED_COLORS =
      new LinkedHashMap<String, int[]>(PARSE_CACHE_SIZE, 0.75F, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<String, int[]> eldest) {
          return size() > PARSE_CACHE_SIZE;
        }
      };
  private static String lastParsedCode;
  private static int[] lastParsedColors = NO_OVERRIDES;
  private static WeakReference<EntityPlayer> lastPlayer = new WeakReference<EntityPlayer>(null);
  private static int lastPlayerTick;
  private static String lastPlayerHair;
  private static boolean lastPlayerFormColorsHair;
  private static boolean addonLookupDone;
  private static Method addonGetData, addonGetForm, displayHasColor, displayHasHairColor;
  private static Field formDisplay, dataRace, dataHair;

  private HairStrandColors() { }

  public static String geometry(String code) {
    if (code == null) return "";
    int marker = code.indexOf(MARKER);
    return marker < 0 ? code : code.substring(0, marker);
  }

  public static boolean hasOverrides(String code) {
    return code != null && code.indexOf(MARKER) == GEOMETRY_LENGTH
        && isValid(code);
  }

  public static boolean isValid(String code) {
    if (code == null) return false;
    int marker = code.indexOf(MARKER);
    if (marker < 0) return code.length() == GEOMETRY_LENGTH && digits(code, 0, code.length());
    if (marker != GEOMETRY_LENGTH || code.indexOf(MARKER, marker + MARKER.length()) >= 0
        || code.length() != GEOMETRY_LENGTH + MARKER.length() + DATA_LENGTH
        || !digits(code, 0, GEOMETRY_LENGTH)) return false;
    int slots = marker + MARKER.length();
    for (int i = 0; i < STRAND_COUNT; i++) {
      for (int j = 0; j < SLOT_LENGTH; j++) {
        char c = code.charAt(slots + i * SLOT_LENGTH + j);
        if (!(isHex(c) || c == '-')) return false;
      }
      String slot = code.substring(slots + i * SLOT_LENGTH, slots + (i + 1) * SLOT_LENGTH);
      if (!"------".equals(slot) && !hex(slot)) return false;
    }
    return true;
  }

  public static String colorSlot(String code, int strand) {
    int rgb = strandColor(code, strand);
    return rgb == NO_COLOR ? null : String.format(java.util.Locale.ROOT, "%06X", rgb);
  }

  public static String withColor(String code, int strand, int rgb) {
    return setSlot(code, strand, String.format(java.util.Locale.ROOT, "%06X", rgb & 0xFFFFFF));
  }

  public static String withoutColor(String code, int strand) {
    return setSlot(code, strand, "------");
  }

  public static String ensureTrailer(String code) {
    String base = geometry(code);
    if (base.length() != GEOMETRY_LENGTH || !digits(base, 0, base.length())) return code;
    if (hasOverrides(code)) return code;
    StringBuilder result = new StringBuilder(base).append(MARKER);
    for (int i = 0; i < STRAND_COUNT; i++) result.append("------");
    return result.toString();
  }

  /** Applies a strand override unless DBC Addon explicitly colors this player's active form hair. */
  public static boolean applyRenderColor(String renderCode, int strand, EntityPlayer player) {
    if (player == null) return false;
    refreshPlayerState(player);
    int rgb = strandColor(renderCode, strand);
    if (rgb == NO_COLOR) rgb = strandColor(lastPlayerHair, strand);
    return !lastPlayerFormColorsHair && applyColor(rgb);
  }

  private static void refreshPlayerState(EntityPlayer player) {
    if (player == lastPlayer.get() && player.ticksExisted == lastPlayerTick) return;
    lastPlayer = new WeakReference<EntityPlayer>(player);
    lastPlayerTick = player.ticksExisted;
    Object dbcData = addonData(player);
    lastPlayerHair = dbcData == null ? null : readString(dataHair, dbcData);
    lastPlayerFormColorsHair = dbcData != null && formHasExplicitHairColor(dbcData, player);
  }

  public static boolean applyColor(int rgb) {
    if (rgb == NO_COLOR) return false;
    GL11.glColor4f(((rgb >> 16) & 255) / 255F, ((rgb >> 8) & 255) / 255F,
        (rgb & 255) / 255F, 1F);
    return true;
  }

  public static int strandColor(String code, int strand) {
    if (code == null || strand < 0 || strand >= STRAND_COUNT) return NO_COLOR;
    if (code != lastParsedCode) {
      int[] colors = PARSED_COLORS.get(code);
      if (colors == null) {
        colors = parseColors(code);
        PARSED_COLORS.put(code, colors);
      }
      lastParsedCode = code;
      lastParsedColors = colors;
    }
    return lastParsedColors.length == 0 ? NO_COLOR : lastParsedColors[strand];
  }

  private static int[] parseColors(String code) {
    if (!hasOverrides(code)) return NO_OVERRIDES;
    int[] colors = new int[STRAND_COUNT];
    int slots = GEOMETRY_LENGTH + MARKER.length();
    for (int i = 0; i < STRAND_COUNT; i++) {
      String slot = code.substring(slots + i * SLOT_LENGTH, slots + (i + 1) * SLOT_LENGTH);
      colors[i] = "------".equals(slot) ? NO_COLOR : Integer.parseInt(slot, 16);
    }
    return colors;
  }

  private static Object addonData(EntityPlayer player) {
    initAddonLookup();
    if (addonGetData == null) return null;
    try { return addonGetData.invoke(null, player); }
    catch (Exception ignored) { return null; }
  }

  private static boolean formHasExplicitHairColor(Object data, EntityPlayer player) {
    initAddonLookup();
    if (addonGetForm == null || displayHasColor == null || formDisplay == null) return false;
    try {
      Object form = addonGetForm.invoke(null, player);
      if (form == null) return false;
      Object display = formDisplay.get(form);
      if (display == null) return false;
      if (Boolean.TRUE.equals(displayHasColor.invoke(display, "hair"))) return true;
      if (Boolean.TRUE.equals(displayHasHairColor.invoke(display, data))) return true;
      return readInt(dataRace, data) == MAJIN_RACE
          && Boolean.TRUE.equals(displayHasColor.invoke(display, "bodycm"));
    } catch (Exception ignored) { return false; }
  }

  private static String readString(Field field, Object instance) {
    try { Object value = field == null ? null : field.get(instance); return value instanceof String ? (String) value : null; }
    catch (Exception ignored) { return null; }
  }

  private static int readInt(Field field, Object instance) {
    try { return field == null ? -1 : field.getInt(instance); }
    catch (Exception ignored) { return -1; }
  }

  private static synchronized void initAddonLookup() {
    if (addonLookupDone) return;
    addonLookupDone = true;
    try {
      Class<?> dataClass = Class.forName("kamkeel.npcdbc.data.dbcdata.DBCData");
      Class<?> formClass = Class.forName("kamkeel.npcdbc.data.form.Form");
      Class<?> displayClass = Class.forName("kamkeel.npcdbc.data.form.FormDisplay");
      addonGetData = dataClass.getMethod("get", EntityPlayer.class);
      addonGetForm = dataClass.getMethod("getForm", EntityPlayer.class);
      formDisplay = formClass.getField("display");
      dataRace = dataClass.getField("Race");
      dataHair = dataClass.getField("DNSHair");
      displayHasColor = displayClass.getMethod("hasColor", String.class);
      displayHasHairColor = displayClass.getMethod("hasHairCol", dataClass);
    } catch (Exception unavailable) {
      addonGetData = addonGetForm = displayHasColor = displayHasHairColor = null;
      formDisplay = dataRace = dataHair = null;
    }
  }

  private static String setSlot(String code, int strand, String value) {
    if (strand < 0 || strand >= STRAND_COUNT) return code;
    String extended = ensureTrailer(code);
    if (!hasOverrides(extended)) return code;
    int start = GEOMETRY_LENGTH + MARKER.length() + strand * SLOT_LENGTH;
    return extended.substring(0, start) + value + extended.substring(start + SLOT_LENGTH);
  }

  private static boolean digits(String value, int start, int end) {
    for (int i = start; i < end; i++) if (value.charAt(i) < '0' || value.charAt(i) > '9') return false;
    return true;
  }

  private static boolean hex(String value) {
    for (int i = 0; i < value.length(); i++) if (!isHex(value.charAt(i))) return false;
    return true;
  }

  private static boolean isHex(char c) {
    return c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f';
  }
}
