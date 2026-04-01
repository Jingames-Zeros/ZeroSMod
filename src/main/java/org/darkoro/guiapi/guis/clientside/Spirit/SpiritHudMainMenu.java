package org.darkoro.guiapi.guis.clientside.Spirit;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import org.darkoro.zerosmod.config.SpiritConfig;
import org.lwjgl.opengl.GL11;

public class SpiritHudMainMenu extends GuiScreen {
  private final SpiritConfig settings;
  private SpiritHudComponent selected;
  private SpiritHudComponent selectedText;
  private int lastX, lastY;
  private boolean dragging;
  private boolean draggingText;

  public SpiritHudMainMenu(SpiritConfig s){this.settings = s;}

  @Override
  public void initGui(){
    this.buttonList.add(new GuiButton(0,getCenter()-100 , getRowPos(3), "Edit Components"));
    this.buttonList.add(new GuiButton(1,getCenter()-100, getRowPos(4), "HUD Enabled"));
    this.buttonList.add(new GuiButton(2,getCenter()-100, getRowPos(5), "Done"));


  }
  private int getRowPos(int row) {
    return this.height / 4 + (24 * row - 24) - 16;
  }

  private int getCenter() {
    return this.width / 2;
  }

  @Override
  protected void actionPerformed(GuiButton button){
    switch (button.id) {
      case 0:
        this.mc.displayGuiScreen(new SpiritHudEditorMenu(settings));
        break;

      case 1:
        settings.get().setEnabled(!settings.get().isEnabled());
        button.displayString = String.valueOf(settings.get().isEnabled());
      break;

      case 2:
        this.mc.displayGuiScreen(null);
        break;
    }


  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    super.drawDefaultBackground();

    EntityPlayer player = mc.thePlayer;
    int[] b = settings.get().getBounds(this.width,this.height,player);
    int x1 = b[0];
    int y1 = b[1];
    int x2 = x1 + b[2];
    int y2 = y1 + b[3];
    int col = settings.get() == selected ? 0xFFFF0000 : 0xFF00FF00;

    GL11.glPushMatrix();
    GL11.glScalef(settings.get().getScale(),settings.get().getScale(),1f);

    drawHorizontalLine(x1, x2, y1, col);
    drawHorizontalLine(x1, x2, y2, col);
    drawVerticalLine(x1, y1, y2, col);
    drawVerticalLine(x2, y1, y2, col);
    GL11.glPopMatrix();

    int[] tb = settings.get().getTextBounds(this.width,this.height,player,"1000");
    int tx1 = tb[0];
    int ty1 = tb[1];
    int tx2 = tx1 + tb[2];
    int ty2 = ty1 + tb[3];
    int ccol = settings.get() == selectedText ? 0xFFFF0000 : 0xFF00FF00;

    GL11.glPushMatrix();
    GL11.glScalef(settings.get().getTextScale(),settings.get().getTextScale(),1f);
    drawHorizontalLine(tx1, tx2, ty1, ccol);
    drawHorizontalLine(tx1, tx2, ty2, ccol);
    drawVerticalLine(tx1, ty1, ty2, ccol);
    drawVerticalLine(tx2, ty1, ty2, ccol);
    GL11.glPopMatrix();

    GL11.glPushMatrix();
    GL11.glTranslatef(tx1, ty1, 0);
    float s = settings.get().getTextScale();
    GL11.glScalef(s, s, 1f);
    int colText = settings.get().getColor() < 0 ? 0xFFFFFF : settings.get().getColor();
    this.mc.fontRenderer.drawStringWithShadow("1000", 1, 1, colText);
    GL11.glPopMatrix();

    if (dragging && selected != null) {
      selected.setOffsetX(selected.getOffsetX() + (mouseX - lastX));
      selected.setOffsetY(selected.getOffsetY() + (mouseY - lastY));
    }
    if (draggingText && selectedText != null) {
      selectedText.setTextOffsetX(selectedText.getTextOffsetX() + (mouseX - lastX));
      selectedText.setTextOffsetY(selectedText.getTextOffsetY() + (mouseY - lastY));
    }
    lastX = mouseX;
    lastY = mouseY;

    GL11.glPushMatrix();
    GL11.glScaled(2.0, 2.0, 2.0);
    this.drawCenteredString(this.mc.fontRenderer, "Spirit HUD", getCenter() / 2, 10, -1);
    GL11.glPopMatrix();

    super.drawScreen(mouseX, mouseY, partialTicks);

  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX,mouseY,mouseButton);
    if (!dragging && !draggingText){
      if(settings.get().isShowText()){
        if(settings.get().containsText(mouseX,mouseY,this.width,this.height,mc.thePlayer,"1000")){
          selectedText = settings.get();
          draggingText = true;
          return;
        }
      }
      if(settings.get().contains(mouseX,mouseY,this.width,this.height,mc.thePlayer)){
        selected = settings.get();
        dragging = true;
        return;
      }

    }

  }

  @Override
  protected void mouseMovedOrUp(int mouseX, int mouseY, int state) {
    super.mouseMovedOrUp(mouseX, mouseY, state);
    dragging = false;
    selected = null;
    draggingText = false;
    selectedText = null;
  }

  @Override
  public void onGuiClosed() {
    settings.saveConfig();
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

}
