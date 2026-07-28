package org.darkoro.zerosmod.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;
import org.darkoro.zerosmod.config.BiomeConfig;
import org.darkoro.zerosmod.config.DimensionConfig;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT) public class PhylacterySkyRenderer extends IRenderHandler {

  private static final ResourceLocation SUN_TEXTURE = new ResourceLocation("textures/environment/sun.png");

  @Override public void render(float partialTicks, WorldClient world, Minecraft mc) {
    GL11.glDisable(GL11.GL_FOG);
    GL11.glDisable(GL11.GL_ALPHA_TEST);
    GL11.glEnable(GL11.GL_BLEND);
    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
    RenderHelper.disableStandardItemLighting();
    GL11.glDepthMask(false);

    drawSkyBox();
    drawSun(mc, world, partialTicks);

    GL11.glDepthMask(true);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glEnable(GL11.GL_ALPHA_TEST);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glEnable(GL11.GL_FOG);
  }

  private void drawSkyBox() {
    GL11.glDisable(GL11.GL_TEXTURE_2D);

    int skyColor = BiomeConfig.PHYLACTERY.skyColor;
    float red = ((skyColor >> 16) & 0xFF) / 255.0F * 0.45F;
    float green = ((skyColor >> 8) & 0xFF) / 255.0F * 0.45F;
    float blue = (skyColor & 0xFF) / 255.0F * 0.55F;
    Tessellator tessellator = Tessellator.instance;

    for (int face = 0; face < 6; face++) {
      GL11.glPushMatrix();

      if (face == 1) {
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      } else if (face == 2) {
        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
      } else if (face == 3) {
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      } else if (face == 4) {
        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
      } else if (face == 5) {
        GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
      }

      tessellator.startDrawingQuads();
      tessellator.setColorRGBA_F(red, green, blue, 1.0F);
      tessellator.addVertex(-100.0D, -100.0D, -100.0D);
      tessellator.addVertex(-100.0D, -100.0D, 100.0D);
      tessellator.addVertex(100.0D, -100.0D, 100.0D);
      tessellator.addVertex(100.0D, -100.0D, -100.0D);
      tessellator.draw();
      GL11.glPopMatrix();
    }
  }

  private void drawSun(Minecraft mc, WorldClient world, float partialTicks) {
    float brightness = DimensionConfig.getPhylacterySunBrightness();
    if (brightness <= 0.0F) {
      return;
    }

    GL11.glPushMatrix();
    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
    GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);

    drawSunTexture(mc, brightness);
    drawSunOverlay(brightness);

    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
    GL11.glPopMatrix();
  }

  private void drawSunTexture(Minecraft mc, float brightness) {
    Tessellator tessellator = Tessellator.instance;
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    OpenGlHelper.glBlendFunc(770, 1, 1, 0);
    GL11.glColor4f(brightness, brightness, brightness, brightness);
    mc.getTextureManager().bindTexture(SUN_TEXTURE);

    float sunSize = 34.0F;
    tessellator.startDrawingQuads();
    tessellator.addVertexWithUV(-sunSize, 100.0D, -sunSize, 0.0D, 0.0D);
    tessellator.addVertexWithUV(sunSize, 100.0D, -sunSize, 1.0D, 0.0D);
    tessellator.addVertexWithUV(sunSize, 100.0D, sunSize, 1.0D, 1.0D);
    tessellator.addVertexWithUV(-sunSize, 100.0D, sunSize, 0.0D, 1.0D);
    tessellator.draw();
  }

  private void drawSunOverlay(float brightness) {
    int overlayColor = BiomeConfig.PHYLACTERY.sunOverlayColor;
    if (overlayColor < 0) {
      return;
    }

    Tessellator tessellator = Tessellator.instance;
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
    setColor(overlayColor, 0.58F * brightness);

    float overlaySize = 38.0F;
    tessellator.startDrawingQuads();
    tessellator.addVertex(-overlaySize, 99.0D, -overlaySize);
    tessellator.addVertex(overlaySize, 99.0D, -overlaySize);
    tessellator.addVertex(overlaySize, 99.0D, overlaySize);
    tessellator.addVertex(-overlaySize, 99.0D, overlaySize);
    tessellator.draw();
  }

  private void setColor(int rgb, float alpha) {
    float red = ((rgb >> 16) & 0xFF) / 255.0F;
    float green = ((rgb >> 8) & 0xFF) / 255.0F;
    float blue = (rgb & 0xFF) / 255.0F;
    GL11.glColor4f(red, green, blue, alpha);
  }
}
