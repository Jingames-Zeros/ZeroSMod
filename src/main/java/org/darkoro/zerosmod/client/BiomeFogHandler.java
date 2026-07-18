package org.darkoro.zerosmod.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.darkoro.zerosmod.config.BiomeConfig;
import org.darkoro.zerosmod.world.GenericZSBiome;

@SideOnly(Side.CLIENT) public class BiomeFogHandler {

  // Used when config Fog Strength = -1.
  private static final float DEFAULT_MAX_STRENGTH = 0.7F;

  @SubscribeEvent
  public void onFogColors(EntityViewRenderEvent.FogColors event) {
    Minecraft mc = Minecraft.getMinecraft();
    if (mc == null || mc.theWorld == null || mc.thePlayer == null || event.entity == null) return;

    int x = (int) Math.floor(mc.thePlayer.posX);
    int z = (int) Math.floor(mc.thePlayer.posZ);

    BiomeGenBase biome = mc.theWorld.getBiomeGenForCoords(x, z);
    if (!(biome instanceof GenericZSBiome)) return;

    BiomeConfig.BiomeVisuals visuals = ((GenericZSBiome) biome).getVisuals();
    int rgb = visuals.fogColor;
    float maxStrength = (visuals.fogMaxStrength >= 0.0F) ? visuals.fogMaxStrength : DEFAULT_MAX_STRENGTH;

    float r = ((rgb >> 16) & 0xFF) / 255.0F;
    float g = ((rgb >> 8) & 0xFF) / 255.0F;
    float b = (rgb & 0xFF) / 255.0F;

    // Same value getLookVec().yCoord produces, without allocating a Vec3 per frame.
    float lookY = Math.abs(MathHelper.sin(-event.entity.rotationPitch * (float) Math.PI / 180.0F));
    float horizonFactor = 0.9F + (0.75F * lookY);

    float strength = maxStrength * horizonFactor;
    if (strength < 0.0F) strength = 0.0F;
    if (strength > 20.0F) strength = 20.0F;

    event.red   = (event.red   * (1.0F - strength)) + (r * strength);
    event.green = (event.green * (1.0F - strength)) + (g * strength);
    event.blue  = (event.blue  * (1.0F - strength)) + (b * strength);
  }
}
