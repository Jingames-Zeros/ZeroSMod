package org.darkoro.guiapi.guis.clientside.Spirit;

import static cpw.mods.fml.client.config.GuiUtils.drawTexturedModalRect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.darkoro.zerosmod.config.SpiritConfig;
import org.darkoro.zerosmod.proxy.ClientProxy;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

public class SpiritOverlay {
  private final Minecraft mc = Minecraft.getMinecraft();
  private final SpiritConfig config = ClientProxy.getSpiritSettings();

  public static Color hex2Rgb(String colorStr) {
    return new Color(
        Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
        Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
        Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
  }

  public void renderOverlay(EntityPlayer player) {
    ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
    int width = res.getScaledWidth();
    int height = res.getScaledHeight();

    GL11.glDisable(GL11.GL_BLEND);

    SpiritHudComponent c = config.get();
    Color color;
    if (c.getColor() != -1) {
       color = hex2Rgb(String.valueOf(c.getColor()));
    }
    else color = new Color();

    if(c == null) return;
    GL11.glColor4f(color.getRed()/255f ,color.getGreen() / 255f, color.getBlue() / 255f, 1.0f);

    GL11.glPushMatrix();
    GL11.glScalef(c.getScale(),c.getScale(),1f);
    GL11.glTranslatef(c.getOffsetX(),c.getOffsetY(),0f);


    mc.renderEngine.bindTexture(new ResourceLocation("zerosmod", "textures/gui/spirit.png"));
    int cap = this.mc.thePlayer.xpBarCap(); // max gauge
    int left = width / 2 - 91;
    short barWidth = 182;
    int top = height - 42;
    if (cap > 0)
    {

      int filled = (int)(mc.thePlayer.experience * (float)(barWidth + 1)); // % out of max

      drawTexturedModalRect(left, top, 0, 64, barWidth, 5, 1);

      if (filled > 0)
      {
        drawTexturedModalRect(left, top, 0, 69, filled, 5,1);
      }
    }
    GL11.glPopMatrix();

    FontRenderer fr = mc.fontRenderer;

    String text = ""+ mc.thePlayer.experienceLevel;
    int x = -261 +(width - fr.getStringWidth(text)) / 2;
    int y = top + 1;

    GL11.glPushMatrix();
    GL11.glTranslatef(c.getTextOffsetX(),c.getTextOffsetY(),0f);
    GL11.glScalef(c.getTextScale(),c.getTextScale(),1f);
    fr.drawString(text, x + 1 + barWidth, y , 0);

    fr.drawString(text, x - 1+ barWidth, y, 0);
    fr.drawString(text, x+barWidth, y + 1, 0);
    fr.drawString(text, x+barWidth, y - 1, 0);
    fr.drawString(text, x+barWidth, y, c.getColor());

    GL11.glPopMatrix();

    GL11.glEnable(GL11.GL_BLEND);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

    GL11.glPopMatrix();



  }

}
