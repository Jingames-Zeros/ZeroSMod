package org.darkoro.guiapi.guis.clientside.Spirit;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class SpiritHudComponent {
  private boolean enabled = true;
  private boolean showText = true;
  private boolean showIcon = true;
  private float scale = 1.0f;
  private int offsetX = 0;
  private int offsetY = 0;
  private int textOffsetX = 0;
  private int textOffsetY = 0;
  private float textScale = 1.0f;
  private int textColor = -1;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean b) {
    enabled = b;
  }

  public boolean isShowText() {
    return showText;
  }

  public void setShowText(boolean b) {
    showText = b;
  }

  public float getScale() {
    return scale;
  }

  public void setScale(float s) {
    scale = s;
  }

  public int getOffsetX() {
    return offsetX;
  }

  public void setOffsetX(int x) {
    offsetX = x;
  }

  public int getOffsetY() {
    return offsetY;
  }

  public void setOffsetY(int y) {
    offsetY = y;
  }

  public int getTextOffsetX() {
    return textOffsetX;
  }

  public void setTextOffsetX(int x) {
    textOffsetX = x;
  }

  public int getTextOffsetY() {
    return textOffsetY;
  }

  public void setTextOffsetY(int y) {
    textOffsetY = y;
  }

  public float getTextScale() {
    return textScale;
  }

  public void setTextScale(float s) {
    textScale = s;
  }

  public int getColor() {
    return textColor;
  }

  public void setColor(int c) {
    textColor = c;
  }

  public int[] getBounds(int width, int height, EntityPlayer player){
    int[] anchor = new int[]{width / 2 - 91,height - 42};
    int x = anchor[0] + offsetX;
    int y = anchor[1] + offsetY;

    int w = 182;
    int h = 5;

    w = (int) (w * scale);
    h = (int) (h * scale);

    return new int[]{x,y,w,h};

  }

  public int[] getTextBounds(int width, int height, EntityPlayer player, String text){
    int[] anchor = new int[]{width / 2 - 91,height - 42};
    int x = anchor[0] + textOffsetX;
    int y = anchor[1] + textOffsetY;

    int w = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
    int h = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;


    w = (int) (w * textScale);
    h = (int) (h * textScale);

    return new int[]{x,y,w,h};
  }

  public boolean containsText(int mouseX, int mouseY, int width, int height, net.minecraft.entity.player.EntityPlayer player, String text) {
    int[] b = getTextBounds(width, height, player, text);
    return mouseX >= b[0] && mouseX <= b[0] + b[2] && mouseY >= b[1] && mouseY <= b[1] + b[3];
  }

  public boolean contains(int mouseX, int mouseY, int width, int height, net.minecraft.entity.player.EntityPlayer player) {
    int[] b = getBounds(width, height, player);
    int x = b[0];
    int y = b[1];
    int w = b[2];
    int h = b[3];
    return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
  }


}
