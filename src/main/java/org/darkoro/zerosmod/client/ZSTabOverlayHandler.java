package org.darkoro.zerosmod.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.network.RequestZSTabDataPacket;
import org.darkoro.zerosmod.network.SyncZSTabDataPacket;
import org.darkoro.zerosmod.tab.ClientZSTabDataCache;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ZSTabOverlayHandler extends Gui {

  private static final int ROW_HEIGHT = 9;
  private static final int MIN_PLAYER_ROWS = 15;
  private static final int MAX_PLAYER_ROWS = 40;
  private static final int HEADER_COLOR = 0xFFFFFF;
  private static final int TEXT_COLOR = 0xE8E8E8;
  private static final int SPC_COLOR = 0x00D8FF;
  private static final int SUPER_COLOR = 0xB64CFF;
  private static final int ULTIMATE_COLOR = 0xFFE600;
  private static final int SPC_GAUGE_SEGMENTS = 40;
  private static final int SPC_COLUMN_MIN_WIDTH = 122;

  private long lastRequestMillis;

  @SubscribeEvent
  public void onRenderPlayerList(RenderGameOverlayEvent.Pre event) {
    if (event.type != RenderGameOverlayEvent.ElementType.PLAYER_LIST) {
      return;
    }

    Minecraft mc = Minecraft.getMinecraft();
    if (mc == null || mc.thePlayer == null || mc.theWorld == null) {
      return;
    }

    event.setCanceled(true);
    requestFreshData();
    render(mc, event.resolution.getScaledWidth());
  }

  private void requestFreshData() {
    long now = System.currentTimeMillis();
    if (now - lastRequestMillis < 1000L) {
      return;
    }

    lastRequestMillis = now;
    ZeroSMod.network.sendToServer(new RequestZSTabDataPacket());
  }

  @SuppressWarnings("unchecked")
  private void render(Minecraft mc, int screenWidth) {
    NetHandlerPlayClient handler = mc.thePlayer.sendQueue;
    List<GuiPlayerInfo> players = (List<GuiPlayerInfo>) handler.playerInfoList;
    FontRenderer font = mc.fontRenderer;
    SyncZSTabDataPacket data = ClientZSTabDataCache.getLatest();
    boolean showSpc = data.spcUnlocked;

    int rows = Math.max(MIN_PLAYER_ROWS, Math.min(MAX_PLAYER_ROWS, (players.size() + 1) / 2));
    int[] widths = getColumnWidths(screenWidth, showSpc);
    int panelWidth = widths[0] + widths[1] + widths[2] + (showSpc ? widths[3] : 0);
    int left = (screenWidth - panelWidth) / 2;
    int top = 18;
    int height = (rows + 1) * ROW_HEIGHT;

    GL11.glPushMatrix();
    drawRect(left - 2, top - 2, left + panelWidth + 2, top + height + 2, 0xAA202020);
    drawBorder(left - 2, top - 2, panelWidth + 4, height + 4, 0x997A7A7A);

    int x0 = left;
    int x1 = x0 + widths[0];
    int x2 = x1 + widths[1];
    int x3 = x2 + widths[2];

    drawColumnGrid(x0, top, widths[0], rows);
    drawColumnGrid(x1, top, widths[1], rows);
    drawColumnGrid(x2, top, widths[2], rows);
    if (showSpc) {
      drawColumnGrid(x3, top, widths[3], rows);
    }

    drawCentered(font, EnumChatFormatting.BLUE + "" + EnumChatFormatting.BOLD + "Stats", x2, top + 1, widths[2], HEADER_COLOR);
    if (showSpc) {
      drawCentered(font, EnumChatFormatting.DARK_AQUA + "" + EnumChatFormatting.BOLD + "Spirit Control", x3, top + 1, widths[3], HEADER_COLOR);
    }

    drawPlayers(mc, font, players, x0, x1, top + ROW_HEIGHT, widths[0], widths[1], rows);
    drawStats(font, data, x2, top + ROW_HEIGHT, widths[2]);
    if (showSpc) {
      drawSpiritControl(font, data, x3, top + ROW_HEIGHT, widths[3]);
    }

    if (!ClientZSTabDataCache.hasFreshData()) {
      drawString(font, "Loading...", x2 + 3, top + height - ROW_HEIGHT + 1, 0xAAAAAA);
    }

    GL11.glPopMatrix();
  }

  private int[] getColumnWidths(int screenWidth, boolean showSpc) {
    int maxWidth = Math.min(screenWidth - 28, 468);
    int nameWidth = Math.max(110, Math.min(120, maxWidth / 4));
    int statsWidth = Math.max(101, Math.min(118, maxWidth / 4 + 6));
    if (!showSpc) {
      return new int[] {nameWidth, nameWidth, statsWidth, 0};
    }

    int spcWidth = maxWidth - nameWidth - nameWidth - statsWidth;
    if (spcWidth < SPC_COLUMN_MIN_WIDTH) {
      spcWidth = SPC_COLUMN_MIN_WIDTH;
      int remaining = maxWidth - statsWidth - spcWidth;
      nameWidth = Math.max(105, remaining / 2);
    }

    return new int[] {nameWidth, nameWidth, statsWidth, spcWidth};
  }

  private void drawPlayers(Minecraft mc, FontRenderer font, List<GuiPlayerInfo> players, int x0, int x1, int y,
      int w0, int w1, int rows) {
    int capacity = rows * 2;
    int overflow = Math.max(0, players.size() - capacity);
    int visiblePlayers = overflow > 0 ? capacity - 1 : players.size();

    for (int i = 0; i < visiblePlayers; i++) {
      int column = i < rows ? 0 : 1;
      int row = column == 0 ? i : i - rows;
      if (row >= rows) {
        break;
      }

      GuiPlayerInfo player = players.get(i);
      ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(player.name);
      String displayName = ScorePlayerTeam.formatPlayerName(team, player.name);
      int x = column == 0 ? x0 : x1;
      int width = column == 0 ? w0 : w1;
      drawString(font, trim(font, displayName, width - 6), x + 3, y + row * ROW_HEIGHT + 1, 0xFFFFFF);
    }

    if (overflow > 0) {
      String more = "+" + (overflow + 1) + " more";
      drawString(font, trim(font, more, w1 - 6), x1 + 3, y + (rows - 1) * ROW_HEIGHT + 1, 0x777777);
    }
  }

  private void drawStats(FontRenderer font, SyncZSTabDataPacket data, int x, int y, int width) {
    int row = 0;
    drawFormatted(font, raceText(data.raceName), x + 3, y + row++ * ROW_HEIGHT + 1, width - 6, TEXT_COLOR);
    drawFormatted(font, classText(data.className), x + 3, y + row++ * ROW_HEIGHT + 1, width - 6, TEXT_COLOR);
    row++;
    drawFormatted(font, formText(data.raceName, data.currentForm), x + 3, y + row++ * ROW_HEIGHT + 1, width - 6, TEXT_COLOR);
    row++;
    drawStatText(font, "TP", formatCompactNumber(data.tp), EnumChatFormatting.DARK_AQUA, x, y + row++ * ROW_HEIGHT, width);
    drawStat(font, "STR", data.str, EnumChatFormatting.RED, x, y + row++ * ROW_HEIGHT, width);
    drawStat(font, "DEX", data.dex, EnumChatFormatting.BLUE, x, y + row++ * ROW_HEIGHT, width);
    drawStat(font, "CON", data.con, EnumChatFormatting.GREEN, x, y + row++ * ROW_HEIGHT, width);
    drawStat(font, "WIL", data.wil, EnumChatFormatting.GOLD, x, y + row++ * ROW_HEIGHT, width);
    drawStat(font, "MND", data.mnd, EnumChatFormatting.LIGHT_PURPLE, x, y + row++ * ROW_HEIGHT, width);
    drawStat(font, "SPI", data.spi, EnumChatFormatting.AQUA, x, y + row++ * ROW_HEIGHT, width);
    row++;
    drawFormatted(font, EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "Level: "
        + EnumChatFormatting.RESET + EnumChatFormatting.WHITE + formatNumber(data.level),
        x + 3, y + row * ROW_HEIGHT + 1, width - 6, TEXT_COLOR);
  }

  private void drawSpiritControl(FontRenderer font, SyncZSTabDataPacket data, int x, int y, int width) {
    if (!data.spcArmed) {
      drawCentered(font, EnumChatFormatting.RED + "" + EnumChatFormatting.BOLD + "Disarmed",
          x, y + ROW_HEIGHT + 1, width, TEXT_COLOR);
      return;
    }

    drawSpcAbility(font, data.passive, x + 3, y + 1, width - 6, SPC_COLOR);
    drawSpcAbility(font, data.super1, x + 3, y + ROW_HEIGHT * 3 + 1, width - 6, SUPER_COLOR);
    drawSpcAbility(font, data.super2, x + 3, y + ROW_HEIGHT * 4 + 1, width - 6, ULTIMATE_COLOR);
    drawSpcAbility(font, data.ultimate, x + 3, y + ROW_HEIGHT * 7 + 1, width - 6, 0x009CFF);
    drawSpcGauge(font, data.spiritPercent, x + 3, y + ROW_HEIGHT * 9 + 1);
  }

  private void drawSpcAbility(FontRenderer font, String value, int x, int y, int width, int fallbackColor) {
    int color = "none".equalsIgnoreCase(clean(value)) ? 0x555555 : fallbackColor;
    drawString(font, trim(font, value, width), x, y, color);
  }

  private void drawSpcGauge(FontRenderer font, int percent, int x, int y) {
    drawString(font, buildSpcGauge(percent), x, y, TEXT_COLOR);
  }

  private String buildSpcGauge(int percent) {
    int clampedPercent = percent < 0 ? 0 : Math.max(0, Math.min(100, percent));
    int filledSegments = clampedPercent * SPC_GAUGE_SEGMENTS / 100;
    StringBuilder gauge = new StringBuilder();
    gauge.append(EnumChatFormatting.BLUE).append("[");
    for (int i = 0; i < SPC_GAUGE_SEGMENTS; i++) {
      gauge.append(i < filledSegments ? EnumChatFormatting.AQUA : EnumChatFormatting.GRAY).append("|");
    }
    gauge.append(EnumChatFormatting.BLUE).append("] ");
    gauge.append(formatSpcGaugePercent(percent, clampedPercent));
    return gauge.toString();
  }

  private String formatSpcGaugePercent(int percent, int clampedPercent) {
    if (percent < 0) {
      return EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + "--%";
    }

    if (clampedPercent == 0) {
      return EnumChatFormatting.RESET + "" + EnumChatFormatting.GRAY + "0%";
    }

    return EnumChatFormatting.RESET + "" + EnumChatFormatting.AQUA + clampedPercent + "%";
  }

  private void drawStat(FontRenderer font, String label, int value, EnumChatFormatting color, int x, int y, int width) {
    drawStatText(font, label, formatNumber(value), color, x, y, width);
  }

  private void drawStatText(FontRenderer font, String label, String value, EnumChatFormatting color, int x, int y, int width) {
    String statLabel = color + "" + EnumChatFormatting.BOLD + label + ": ";
    String statValue = EnumChatFormatting.RESET + "" + EnumChatFormatting.WHITE + value;
    drawFormatted(font, statLabel + statValue, x + 3, y + 1, width - 6, TEXT_COLOR);
  }

  private String raceText(String raceName) {
    EnumChatFormatting color = EnumChatFormatting.WHITE;
    String normalized = clean(raceName).toLowerCase();
    if ("saiyan".equals(normalized) || "half-saiyan".equals(normalized) || "half saiyan".equals(normalized)) {
      color = EnumChatFormatting.YELLOW;
    } else if ("arcosian".equals(normalized)) {
      color = EnumChatFormatting.DARK_PURPLE;
    } else if ("human".equals(normalized)) {
      color = EnumChatFormatting.AQUA;
    } else if ("namekian".equals(normalized)) {
      color = EnumChatFormatting.GREEN;
    } else if ("majin".equals(normalized)) {
      color = EnumChatFormatting.LIGHT_PURPLE;
    }

    return color + "" + EnumChatFormatting.BOLD + raceName;
  }

  private String classText(String className) {
    EnumChatFormatting color = EnumChatFormatting.WHITE;
    String normalized = clean(className).toLowerCase();
    String displayName = className;
    if ("warrior".equals(normalized)) {
      color = EnumChatFormatting.RED;
    } else if ("martialartist".equals(normalized) || "martial artist".equals(normalized)) {
      color = EnumChatFormatting.GOLD;
      displayName = "Martial Artist";
    } else if ("spiritualist".equals(normalized)) {
      color = EnumChatFormatting.AQUA;
    }

    return color + "" + EnumChatFormatting.BOLD + displayName;
  }

  private String formText(String raceName, String formName) {
    String clean = clean(formName);
    if ("arcosian".equalsIgnoreCase(clean(raceName)) && "form0".equalsIgnoreCase(clean)) {
      return EnumChatFormatting.AQUA + "Minimal";
    }

    if ("base".equalsIgnoreCase(clean)) {
      return EnumChatFormatting.AQUA + clean;
    }

    return formName;
  }

  private void drawColumnGrid(int x, int y, int width, int rows) {
    drawRect(x, y, x + width, y + (rows + 1) * ROW_HEIGHT, 0x44282828);
    drawRect(x, y, x + width, y + 1, 0x777A7A7A);
    drawRect(x, y + ROW_HEIGHT, x + width, y + ROW_HEIGHT + 1, 0x777A7A7A);
    drawRect(x, y, x + 1, y + (rows + 1) * ROW_HEIGHT, 0x777A7A7A);
    drawRect(x + width - 1, y, x + width, y + (rows + 1) * ROW_HEIGHT, 0x777A7A7A);

    for (int i = 2; i <= rows + 1; i++) {
      int lineY = y + i * ROW_HEIGHT;
      drawRect(x, lineY, x + width, lineY + 1, 0x335F5F5F);
    }
  }

  private void drawBorder(int x, int y, int width, int height, int color) {
    drawRect(x, y, x + width, y + 1, color);
    drawRect(x, y + height - 1, x + width, y + height, color);
    drawRect(x, y, x + 1, y + height, color);
    drawRect(x + width - 1, y, x + width, y + height, color);
  }

  private void drawCentered(FontRenderer font, String text, int x, int y, int width, int color) {
    drawString(font, text, x + (width - font.getStringWidth(text)) / 2, y, color);
  }

  private void drawFormatted(FontRenderer font, String text, int x, int y, int width, int color) {
    drawString(font, trimFormatted(font, text, width), x, y, color);
  }

  private String trim(FontRenderer font, String text, int width) {
    if (text == null) {
      return "";
    }

    String clean = EnumChatFormatting.getTextWithoutFormattingCodes(text);
    if (font.getStringWidth(text) <= width) {
      return text;
    }

    String suffix = "...";
    return font.trimStringToWidth(clean, Math.max(0, width - font.getStringWidth(suffix))) + suffix;
  }

  private String trimFormatted(FontRenderer font, String text, int width) {
    if (text == null) {
      return "";
    }

    if (font.getStringWidth(text) <= width) {
      return text;
    }

    String suffix = "...";
    String clean = clean(text);
    return font.trimStringToWidth(clean, Math.max(0, width - font.getStringWidth(suffix))) + suffix;
  }

  private String clean(String text) {
    String clean = EnumChatFormatting.getTextWithoutFormattingCodes(text == null ? "" : text);
    return clean == null ? "" : clean;
  }

  private String formatNumber(int value) {
    return String.format("%,d", value);
  }

  private String formatCompactNumber(int value) {
    long longValue = value;
    long abs = Math.abs(longValue);
    if (abs < 10000L) {
      return formatNumber(value);
    }

    long[] thresholds = {999500000000L, 999500000L, 999500L, 10000L};
    long[] units = {1000000000000L, 1000000000L, 1000000L, 1000L};
    String[] suffixes = {"T", "B", "M", "k"};
    for (int i = 0; i < units.length; i++) {
      if (abs >= thresholds[i]) {
        double scaled = longValue / (double) units[i];
        String pattern = Math.abs(scaled) < 10.0D && scaled != Math.rint(scaled) ? "%.1f" : "%.0f";
        String formatted = String.format(Locale.ROOT, pattern, scaled);
        if (formatted.endsWith(".0")) {
          formatted = formatted.substring(0, formatted.length() - 2);
        }
        return formatted + suffixes[i];
      }
    }

    return formatNumber(value);
  }

}
