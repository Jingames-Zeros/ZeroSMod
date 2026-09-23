package org.darkoro.zerosmod.rebirth.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

/** Flat controls drawn with Minecraft primitives; no texture pack dependency. */
final class RebirthButton extends GuiButton {
  private final int accent;

  RebirthButton(int id, int x, int y, int width, String label, int accent) {
    super(id, x, y, width, 20, label);
    this.accent = accent;
  }

  @Override public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    if (!visible) {
      return;
    }
    boolean hover = mouseX >= xPosition && mouseY >= yPosition
        && mouseX < xPosition + width && mouseY < yPosition + height;
    drawRect(xPosition, yPosition, xPosition + width, yPosition + height,
        !enabled ? 0xFF1E261F : hover ? 0xFF365C3A : 0xFF28422C);
    drawRect(xPosition, yPosition + height - 2, xPosition + width, yPosition + height,
        enabled ? 0xFF000000 | accent : 0xFF4A564B);
    drawCenteredString(mc.fontRenderer, displayString, xPosition + width / 2, yPosition + 6,
        !enabled ? 0x879489 : hover ? 0xFFFFFF : 0xEDF8EE);
  }
}
