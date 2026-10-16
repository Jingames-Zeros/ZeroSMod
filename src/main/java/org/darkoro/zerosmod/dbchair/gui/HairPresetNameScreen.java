package org.darkoro.zerosmod.dbchair.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

/** Name entry shown when saving a new custom preset. */
final class HairPresetNameScreen extends GuiScreen {
  private final HairPresetScreen parent;
  private GuiTextField name;

  HairPresetNameScreen(HairPresetScreen parent) { this.parent = parent; }

  @Override public void initGui() {
    Keyboard.enableRepeatEvents(true);
    int cx = width / 2;
    name = new GuiTextField(fontRendererObj, cx - 130, height / 2 - 12, 260, 20);
    name.setMaxStringLength(40);
    name.setText("Custom Preset");
    name.setFocused(true);
    buttonList.clear();
    buttonList.add(new HairEditorButton(1, cx - 130, height / 2 + 26, 124, 22, "SAVE NEW", 0x90E080));
    buttonList.add(new HairEditorButton(2, cx + 6, height / 2 + 26, 124, 22, "CANCEL", 0xB2C0B1));
  }

  @Override protected void actionPerformed(GuiButton button) {
    if (button.id == 1 && !name.getText().trim().isEmpty()) parent.saveNamedPreset(name.getText());
    else if (button.id == 2) parent.cancelNameEntry();
  }

  @Override protected void keyTyped(char c, int key) {
    if (key == Keyboard.KEY_ESCAPE) parent.cancelNameEntry();
    else if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) {
      if (!name.getText().trim().isEmpty()) parent.saveNamedPreset(name.getText());
    } else name.textboxKeyTyped(c, key);
  }

  @Override protected void mouseClicked(int x, int y, int button) {
    super.mouseClicked(x, y, button);
    name.mouseClicked(x, y, button);
  }

  @Override public void updateScreen() { name.updateCursorCounter(); }

  @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    drawRect(width / 2 - 170, height / 2 - 84, width / 2 + 170, height / 2 + 72, 0xE0111813);
    drawRect(width / 2 - 170, height / 2 - 84, width / 2 + 170, height / 2 - 82, 0xFF90E080);
    drawCenteredString(fontRendererObj, "NAME NEW PRESET", width / 2, height / 2 - 62, 0xFF90E080);
    drawCenteredString(fontRendererObj, "This saves a separate copy; existing presets stay unchanged.",
        width / 2, height / 2 - 38, 0xFFB2C0B1);
    name.drawTextBox();
    super.drawScreen(mouseX, mouseY, partialTicks);
  }

  @Override public void onGuiClosed() { Keyboard.enableRepeatEvents(false); }
  @Override public boolean doesGuiPauseGame() { return false; }
}
