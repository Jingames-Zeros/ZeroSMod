package org.darkoro.zerosmod.dbchair.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

final class HairEditorButton extends GuiButton {
  private final int accent;

  HairEditorButton(int id, int x, int y, int width, int height, String label, int accent) {
    super(id, x, y, width, height, label);
    this.accent = accent;
  }

  @Override public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    if (!visible) return;
    boolean hover = mouseX >= xPosition && mouseY >= yPosition
        && mouseX < xPosition + width && mouseY < yPosition + height;
    int bg = !enabled ? 0xB0171D18 : hover ? 0xD02B4A2F : 0xB0203322;
    drawRect(xPosition, yPosition, xPosition + width, yPosition + height, bg);
    drawRect(xPosition, yPosition, xPosition + width, yPosition + 1,
        enabled ? 0xFF000000 | accent : 0xFF4A564B);
    drawRect(xPosition, yPosition + height - 1, xPosition + width, yPosition + height,
        0xFF101610);
    drawCenteredString(mc.fontRenderer, displayString, xPosition + width / 2,
        yPosition + (height - 8) / 2, !enabled ? 0xFF879489 : hover ? 0xFFFFFFFF : 0xFFEDF8EE);
  }
}
