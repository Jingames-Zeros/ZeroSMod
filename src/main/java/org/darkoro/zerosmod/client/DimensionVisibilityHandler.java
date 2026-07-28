package org.darkoro.zerosmod.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.darkoro.zerosmod.config.DimensionConfig;
import org.darkoro.zerosmod.world.WorldProviderDragonRealm;
import org.darkoro.zerosmod.world.WorldProviderPhylactery;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

@SideOnly(Side.CLIENT) public class DimensionVisibilityHandler {

  @SubscribeEvent
  public void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
    Minecraft mc = Minecraft.getMinecraft();
    if (mc == null || mc.theWorld == null) {
      return;
    }

    Float visibility = getVisibility(mc.theWorld.provider);
    if (visibility == null) {
      return;
    }

    float farPlane = event.farPlaneDistance;
    float normalStart = event.fogMode < 0 ? 0.0F : farPlane * 0.75F;
    float normalEnd = farPlane;
    float hazeStart = farPlane * 0.05F;
    float hazeEnd = Math.min(farPlane, 192.0F) * 0.5F;

    float fogStart = hazeStart + ((normalStart - hazeStart) * visibility.floatValue());
    float fogEnd = hazeEnd + ((normalEnd - hazeEnd) * visibility.floatValue());

    GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
    GL11.glFogf(GL11.GL_FOG_START, fogStart);
    GL11.glFogf(GL11.GL_FOG_END, fogEnd);

    if (GLContext.getCapabilities().GL_NV_fog_distance) {
      GL11.glFogi(34138, 34139);
    }
  }

  private Float getVisibility(WorldProvider provider) {
    if (provider instanceof WorldProviderPhylactery) {
      return Float.valueOf(DimensionConfig.getPhylacteryVisibility());
    }

    if (provider instanceof WorldProviderDragonRealm) {
      return Float.valueOf(DimensionConfig.getDragonRealmVisibility());
    }

    return null;
  }
}
