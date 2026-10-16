package org.darkoro.zerosmod.dbchair.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/** Separate preset browser for the hair editor. */
public final class HairPresetScreen extends GuiScreen {
  private static final int W = 800, H = 470;
  private static final int GREEN = 0x90E080, PURPLE = 0xB050D0, TEXT = 0xF3F8F2, MUTED = 0xB2C0B1;
  private final HairEditorScreen editor;
  private int scrollOffset;
  private float scale, ox, oy;
  private String status = "Choose a preset to preview it, or save your current hair as a preset.";

  public HairPresetScreen(HairEditorScreen editor) { this.editor = editor; }

  @Override public void initGui() {
    scale = Math.min(1F, Math.min((width - 12F) / W, (height - 12F) / H));
    ox = (width - W * scale) / 2F;
    oy = (height - H * scale) / 2F;
    buttonList.clear();
    int count = editor.presetCount();
    int rows = 9;
    scrollOffset = Math.max(0, Math.min(scrollOffset, Math.max(0, count - rows)));
    for (int row = 0; row < rows && scrollOffset + row < count; row++) {
      int i = scrollOffset + row;
      String label = editor.presetDescription(i);
      buttonList.add(new HairEditorButton(300 + row, 32, 91 + row * 31, 422, 27, label,
          i == editor.selectedPreset() ? PURPLE : MUTED));
    }
    buttonList.add(new HairEditorButton(3, 500, 92, 240, 24, "SAVE AS NEW PRESET", GREEN));
    GuiButton delete = new HairEditorButton(4, 500, 124, 240, 24, "DELETE SELECTED CUSTOM", PURPLE);
    delete.enabled = editor.presetIsCustom(editor.selectedPreset());
    buttonList.add(delete);
    buttonList.add(new HairEditorButton(5, 500, 426, 110, 24, "BACK", MUTED));
  }

  @Override protected void actionPerformed(GuiButton button) {
    if (!button.enabled) return;
    if (button.id >= 300 && button.id < 309) {
      int index = scrollOffset + button.id - 300;
      editor.loadPreset(index);
      status = "Previewing " + editor.presetDescription(index) + ".";
      initGui();
    } else if (button.id == 3) {
      mc.displayGuiScreen(new HairPresetNameScreen(this));
    } else if (button.id == 4) {
      boolean deleted = editor.deleteSelectedPreset();
      scrollOffset = Math.min(scrollOffset, Math.max(0, editor.presetCount() - 9));
      status = deleted ? "Deleted the selected custom preset." : "Default presets cannot be deleted.";
      initGui();
    } else if (button.id == 5) {
      editor.closePresets();
    }
  }

  @Override protected void keyTyped(char c, int key) {
    if (key == Keyboard.KEY_ESCAPE) editor.closePresets();
    else super.keyTyped(c, key);
  }

  void saveNamedPreset(String name) {
    if (editor.saveNamedPreset(name)) {
      scrollOffset = Math.max(0, editor.presetCount() - 9);
      status = "Saved “" + name.trim() + "” as a new preset. It is selected in the list.";
    } else {
      status = "Could not save preset. Enter a name and make sure the hair is editable.";
    }
    mc.displayGuiScreen(this);
    initGui();
  }

  void cancelNameEntry() { mc.displayGuiScreen(this); }

  @Override protected void mouseClicked(int mouseX, int mouseY, int button) {
    super.mouseClicked(toGuiX(mouseX), toGuiY(mouseY), button);
  }

  @Override public void handleMouseInput() {
    super.handleMouseInput();
    int wheel = Mouse.getEventDWheel();
    if (wheel == 0) return;
    int mx = Mouse.getEventX() * width / mc.displayWidth;
    int my = height - Mouse.getEventY() * height / mc.displayHeight - 1;
    int x = toGuiX(mx), y = toGuiY(my);
    if (x >= 32 && x < 462 && y >= 91 && y < 372) {
      scrollOffset = Math.max(0, Math.min(scrollOffset + (wheel < 0 ? 2 : -2),
          Math.max(0, editor.presetCount() - 9)));
      initGui();
    }
  }

  private int toGuiX(int x) { return (int) ((x - ox) / scale); }
  private int toGuiY(int y) { return (int) ((y - oy) / scale); }

  @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    int x = (int) ((mouseX - ox) / scale), y = (int) ((mouseY - oy) / scale);
    drawRect(0, 0, width, height, 0x80000000);
    GL11.glPushMatrix();
    GL11.glTranslatef(ox, oy, 0);
    GL11.glScalef(scale, scale, 1);
    drawRect(0, 0, W, H, 0xC0111813);
    drawRect(0, 0, W, 2, 0xFF000000 | GREEN);
    fontRendererObj.drawString("DBC HAIR WORKSHOP", 32, 20, GREEN);
    fontRendererObj.drawString("SAVED PRESETS", 32, 38, TEXT);
    fontRendererObj.drawString("Select a preset to load it into the editor.", 32, 60, MUTED);
    drawRect(32, 82, 462, 83, 0xB0314033);
    drawRect(490, 82, 740, 83, 0xB0314033);
    drawRect(490, 82, 492, 405, 0xC0000000 | PURPLE);
    fontRendererObj.drawString("PRESET ACTIONS", 500, 72, MUTED);
    super.drawScreen(x, y, partialTicks);
    drawScrollBar(460, 91, 9 * 31 - 4, editor.presetCount(), 9, scrollOffset);
    fontRendererObj.drawString("Mouse wheel scrolls presets", 32, 389, MUTED);
    fontRendererObj.drawString("Selected: " + editor.presetDescription(editor.selectedPreset()), 500, 211, TEXT);
    fontRendererObj.drawSplitString(status, 500, 246, 230, 0xFFB2C0B1);
    GL11.glPopMatrix();
  }

  private void drawScrollBar(int x, int y, int height, int total, int visible, int scroll) {
    drawRect(x, y, x + 2, y + height, 0xFF243326);
    if (total <= visible) return;
    int knobHeight = Math.max(18, height * visible / total);
    int travel = height - knobHeight;
    int knobY = y + travel * scroll / Math.max(1, total - visible);
    drawRect(x, knobY, x + 2, knobY + knobHeight, 0xFF000000 | GREEN);
  }

  @Override public boolean doesGuiPauseGame() { return false; }
}
