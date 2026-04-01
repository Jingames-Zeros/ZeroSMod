package org.darkoro.guiapi.guis.clientside.Spirit;

import cpw.mods.fml.client.config.GuiSlider;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.darkoro.zerosmod.config.SpiritConfig;
import org.lwjgl.opengl.GL11;

public class SpiritHudEditorMenu extends GuiScreen {

  private final SpiritConfig settings;
  private GuiSlider barScale;
  private GuiSlider textScale;
  private GuiTextField color;

  public SpiritHudEditorMenu(SpiritConfig s) {
    this.settings = s;
  }

  @Override
  public void initGui() {
    this.buttonList.add(barScale = new GuiSlider(0,getCenter()-100,getRowPos(2),200,20,"Bar Scale: ","",0.1,3.0,settings.get().getScale(),false,true));
    this.buttonList.add(barScale = new GuiSlider(1,getCenter()-100,getRowPos(4),200,20,"Text Scale: ","",0.1,3.0,settings.get().getTextScale(),false,true));
    this.buttonList.add(new GuiButton(2, getCenter()-100, getRowPos(3), "Reset Bar Scale"));
    this.buttonList.add(new GuiButton(3, getCenter()-100, getRowPos(5), "Reset Text Scale"));
    this.color = new GuiTextField(this.mc.fontRenderer, getCenter() -40, getRowPos(6), 80, 20);


  }

  private int getCenter() {
    return this.width / 2;
  }

  private int getRowPos(int row) {
    return this.height / 4 + (24 * row - 24) - 16;
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    super.drawDefaultBackground();
    color.drawTextBox();
    super.drawScreen(mouseX, mouseY, partialTicks);

    GL11.glPushMatrix();
    GL11.glScaled(2.0, 2.0, 2.0);
    this.drawCenteredString(this.mc.fontRenderer, "Spirit HUD", getCenter() / 2, 10, -1);
    GL11.glPopMatrix();
  }

  @Override
  public void onGuiClosed() {
    settings.saveConfig();
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  protected void mouseMovedOrUp(int mouseX, int mouseY, int state) {
    super.mouseMovedOrUp(mouseX, mouseY, state);
  }

  @Override
  protected void actionPerformed(GuiButton button){
    if(settings.get() == null)return;

    switch (button.id){
      case 0:
        GuiSlider slider = (GuiSlider) button;
        settings.get().setScale((float) slider.getValue());
        break;
      case 1:
        GuiSlider slider2 = (GuiSlider) button;
        settings.get().setScale((float) slider2.getValue());
        break;
      case 2:
        settings.get().setScale(1.0f);
        break;
      case 3:
        settings.get().setScale(1.0f);
        break;

    }
  }

}