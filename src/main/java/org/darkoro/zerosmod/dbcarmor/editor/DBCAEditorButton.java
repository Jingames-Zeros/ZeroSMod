package org.darkoro.zerosmod.dbcarmor.editor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

final class DBCAEditorButton extends GuiButton {
  private final int accent;
  private final boolean dropdown;

  DBCAEditorButton(int id, int x, int y, int width, int height, String label, int accent) {
    this(id, x, y, width, height, label, accent, false);
  }

  DBCAEditorButton(int id, int x, int y, int width, int height, String label, int accent,
      boolean dropdown) {
    super(id, x, y, width, height, label);
    this.accent = accent;
    this.dropdown = dropdown;
  }

  @Override
  public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    if (!visible) return;
    boolean hover = mouseX >= xPosition && mouseY >= yPosition
        && mouseX < xPosition + width && mouseY < yPosition + height;
    drawRect(xPosition, yPosition, xPosition + width, yPosition + height,
        !enabled ? 0xFF1E261F : hover ? 0xFF365C3A : 0xFF28422C);
    int arrowWidth = dropdown ? 18 : 0;
    if (dropdown) {
      int arrowX = xPosition + width - arrowWidth;
      drawRect(arrowX, yPosition, xPosition + width, yPosition + height,
          !enabled ? 0xFF171D18 : hover ? 0xFF2B4A2F : 0xFF203322);
      drawRect(arrowX, yPosition + 1, arrowX + 1, yPosition + height - 2, 0xFF4A564B);
      int arrowColor = !enabled ? 0xFF879489 : hover ? 0xFFFFFFFF : 0xFFEDF8EE;
      int centerX = arrowX + arrowWidth / 2;
      int centerY = yPosition + height / 2;
      drawRect(centerX - 4, centerY - 2, centerX + 5, centerY - 1, arrowColor);
      drawRect(centerX - 3, centerY - 1, centerX + 4, centerY, arrowColor);
      drawRect(centerX - 2, centerY, centerX + 3, centerY + 1, arrowColor);
      drawRect(centerX - 1, centerY + 1, centerX + 2, centerY + 2, arrowColor);
      drawRect(centerX, centerY + 2, centerX + 1, centerY + 3, arrowColor);
    }
    drawRect(xPosition, yPosition + height - 2, xPosition + width, yPosition + height,
        enabled ? 0xFF000000 | accent : 0xFF4A564B);
    int labelWidth = width - arrowWidth;
    drawCenteredString(mc.fontRenderer, displayString, xPosition + labelWidth / 2, yPosition + 6,
        !enabled ? 0x879489 : hover ? 0xFFFFFF : 0xEDF8EE);
  }
}
