package org.darkoro.guiapi.guis.clientside;

import static cpw.mods.fml.client.config.GuiUtils.drawTexturedModalRect;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

public class ClientEventHandler {
  private Minecraft mc = Minecraft.getMinecraft();
  public static final ResourceLocation Spirit_Bar = new ResourceLocation("genericguiapi","textures/gui/spirit.png");

  @SubscribeEvent
  public void onRenderPre(RenderGameOverlayEvent.Pre event){
    if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

    Minecraft mc = Minecraft.getMinecraft();
    Entity e = mc.renderViewEntity;
    if (e instanceof EntityPlayer) {
      ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
      int width = res.getScaledWidth();
      int height = res.getScaledHeight();
      EntityPlayer p = (EntityPlayer) e;

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(GL11.GL_BLEND);


      mc.renderEngine.bindTexture(new ResourceLocation("genericguiapi", "textures/gui/spirit.png"));
      int cap = this.mc.thePlayer.xpBarCap(); // max gauge
      int left = width / 2 - 91;

      if (cap > 0)
      {
        short barWidth = 182;
        int filled = (int)(mc.thePlayer.experience * (float)(barWidth + 1)); // % out of max
        int top = height - 32 - 10;
        drawTexturedModalRect(left, top, 0, 64, barWidth, 5, 1);

        if (filled > 0)
        {
          drawTexturedModalRect(left, top, 0, 69, filled, 5,1);
        }
      }
      FontRenderer fr = mc.fontRenderer;

      boolean flag1 = false;
      int color = flag1 ? 16777215 : 8453920;
      String text = "" + mc.thePlayer.experienceLevel;
      int x = (width - fr.getStringWidth(text)) / 2;
      int y = height - 31 - 4;
      fr.drawString(text, x + 1, y, 0);
      fr.drawString(text, x - 1, y, 0);
      fr.drawString(text, x, y + 1, 0);
      fr.drawString(text, x, y - 1, 0);
      fr.drawString(text, x, y, color);




      GL11.glEnable(GL11.GL_BLEND);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);


    }

  }
}
