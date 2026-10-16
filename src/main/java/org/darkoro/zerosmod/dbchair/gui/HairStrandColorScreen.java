package org.darkoro.zerosmod.dbchair.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

/** Color chooser for one selected strand; values are saved in the hair-code trailer. */
public final class HairStrandColorScreen extends GuiScreen {
  private static final int W = 800, H = 470;
  private static final int GREEN = 0x90E080, PURPLE = 0xB050D0, TEXT = 0xF3F8F2, MUTED = 0xB2C0B1;
  private static final int PANEL_X = 40, PANEL_Y = 17, PANEL_W = 720, PANEL_H = 436;
  private static final int GRID_X = 155, GRID_Y = 78, GRID_COLS = 12, GRID_ROWS = 10;
  private static final int CELL_STEP_X = 41, CELL_STEP_Y = 30, CELL_W = 38, CELL_H = 27;
  private static final int[] SWATCHES = makeSwatches();
  private final HairEditorScreen editor;
  private GuiTextField hex;
  private int chosenColor = 0xFFFFFF;
  private float scale, ox, oy;
  private String errorText;

  public HairStrandColorScreen(HairEditorScreen editor) { this.editor = editor; }

  private static int[] makeSwatches() {
    int[] colors = new int[GRID_COLS * GRID_ROWS];
    for (int col = 0; col < GRID_COLS; col++) {
      int gray = 255 - col * 23;
      colors[col] = gray << 16 | gray << 8 | gray;
    }
    float[] brightness = {0.20F, 0.31F, 0.43F, 0.56F, 0.69F, 0.81F, 0.91F, 0.97F, 1F};
    float[] saturation = {1F, 1F, 1F, 1F, 1F, 1F, 0.92F, 0.68F, 0.36F};
    for (int row = 1; row < GRID_ROWS; row++) {
      for (int col = 0; col < GRID_COLS; col++) {
        float hue = (0.5F + col / (float) GRID_COLS) % 1F;
        colors[row * GRID_COLS + col] = java.awt.Color.HSBtoRGB(hue, saturation[row - 1], brightness[row - 1]) & 0xFFFFFF;
      }
    }
    return colors;
  }

  @Override public void initGui() {
    scale = Math.min(1F, Math.min((width - 12F) / W, (height - 12F) / H));
    ox = (width - W * scale) / 2F;
    oy = (height - H * scale) / 2F;
    buttonList.clear();
    buttonList.add(new HairEditorButton(1, 183, 423, 144, 24, "APPLY TO STRAND", GREEN));
    buttonList.add(new HairEditorButton(2, 340, 423, 185, 24, "USE CHARACTER COLOR", PURPLE));
    buttonList.add(new HairEditorButton(3, 538, 423, 80, 24, "BACK", MUTED));
    hex = new GuiTextField(fontRendererObj, 325, 389, 100, 20);
    hex.setMaxStringLength(6);
    hex.setTextColor(TEXT);
    String existing = editor.selectedStrandColor();
    if (existing != null) hex.setText(existing);
    else hex.setText("FFFFFF");
    try { chosenColor = Integer.parseInt(hex.getText(), 16); } catch (Exception ignored) { chosenColor = 0xFFFFFF; }
  }

  @Override protected void actionPerformed(GuiButton button) {
    if (button.id == 1) {
      if (editor.setSelectedStrandColor(hex.getText())) editor.returnFromColorPicker();
      else errorText = "Enter exactly six hexadecimal digits.";
    } else if (button.id == 2) {
      editor.setSelectedStrandColor(null);
      editor.returnFromColorPicker();
    } else if (button.id == 3) {
      editor.returnFromColorPicker();
    }
  }

  private int toGuiX(int x) { return (int) ((x - ox) / scale); }
  private int toGuiY(int y) { return (int) ((y - oy) / scale); }

  @Override protected void mouseClicked(int mouseX, int mouseY, int button) {
    int x = toGuiX(mouseX), y = toGuiY(mouseY);
    super.mouseClicked(x, y, button);
    hex.mouseClicked(x, y, button);
    if (button == 0 && x >= GRID_X && x < GRID_X + GRID_COLS * CELL_STEP_X
        && y >= GRID_Y && y < GRID_Y + GRID_ROWS * CELL_STEP_Y) {
      int col = (x - GRID_X) / CELL_STEP_X, row = (y - GRID_Y) / CELL_STEP_Y;
      if (col < GRID_COLS && row < GRID_ROWS) {
        chosenColor = SWATCHES[row * GRID_COLS + col];
        hex.setText(String.format(java.util.Locale.ROOT, "%06X", chosenColor));
        errorText = null;
      }
    }
  }

  @Override protected void keyTyped(char c, int key) {
    if (key == Keyboard.KEY_ESCAPE) { editor.returnFromColorPicker(); return; }
    if (hex.textboxKeyTyped(c, key)) {
      try { chosenColor = Integer.parseInt(hex.getText(), 16); errorText = null; }
      catch (Exception ignored) { }
    } else super.keyTyped(c, key);
  }

  @Override public void updateScreen() { hex.updateCursorCounter(); }

  @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    int x = toGuiX(mouseX), y = toGuiY(mouseY);
    org.lwjgl.opengl.GL11.glPushMatrix();
    org.lwjgl.opengl.GL11.glTranslatef(ox, oy, 0);
    org.lwjgl.opengl.GL11.glScalef(scale, scale, 1F);
    drawRect(PANEL_X, PANEL_Y, PANEL_X + PANEL_W, PANEL_Y + PANEL_H, 0xE0111813);
    drawRect(PANEL_X, PANEL_Y, PANEL_X + PANEL_W, PANEL_Y + 2, 0xFF000000 | GREEN);
    drawCenteredString(fontRendererObj, "COLOR STRAND " + (editor.selectedStrand() + 1), 400, 30, GREEN);
    drawCenteredString(fontRendererObj, "Choose from 120 colors or enter any RGB hex value.", 400, 48, MUTED);
    for (int i = 0; i < SWATCHES.length; i++) {
      int col = i % GRID_COLS, row = i / GRID_COLS;
      int sx = GRID_X + col * CELL_STEP_X, sy = GRID_Y + row * CELL_STEP_Y;
      drawRect(sx, sy, sx + CELL_W, sy + CELL_H, 0xFF000000 | SWATCHES[i]);
      if (chosenColor == SWATCHES[i]) {
        drawRect(sx - 2, sy - 2, sx + CELL_W + 2, sy, 0xFF000000 | GREEN);
        drawRect(sx - 2, sy + CELL_H, sx + CELL_W + 2, sy + CELL_H + 2, 0xFF000000 | GREEN);
        drawRect(sx - 2, sy, sx, sy + CELL_H, 0xFF000000 | GREEN);
        drawRect(sx + CELL_W, sy, sx + CELL_W + 2, sy + CELL_H, 0xFF000000 | GREEN);
      }
    }
    drawString(fontRendererObj, "RGB HEX", 258, 394, TEXT);
    drawRect(440, 386, 470, 412, 0xFF000000 | chosenColor);
    hex.drawTextBox();
    if (errorText != null) drawCenteredString(fontRendererObj, errorText, 400, 414, 0xFFFF8080);
    super.drawScreen(x, y, partialTicks);
    org.lwjgl.opengl.GL11.glPopMatrix();
  }

  @Override public boolean doesGuiPauseGame() { return false; }
}
