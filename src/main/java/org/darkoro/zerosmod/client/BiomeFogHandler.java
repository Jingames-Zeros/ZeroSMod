package org.darkoro.zerosmod.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.config.ModConfig;

@SideOnly(Side.CLIENT) public class BiomeFogHandler {

  // Your current defaults. These are used when config Fog Strength = -1.
  private static final float DEFAULT_SPIRIT_MAX_STRENGTH = 0.7F;
  private static final float DEFAULT_VAKRON_MAX_STRENGTH = 0.7F;
  private static final float DEFAULT_DRAGON_MAX_STRENGTH = 0.7F;
  private static final float DEFAULT_ZS_BIOME_2_STRENGTH = 0.0F;
  private static final float DEFAULT_ZS_BIOME_3_STRENGTH = 0.0F;
  private static final float DEFAULT_ZS_BIOME_4_STRENGTH = 0.0F;

  @SubscribeEvent
  public void onFogColors(EntityViewRenderEvent.FogColors event) {
    Minecraft mc = Minecraft.getMinecraft();
    if (mc == null || mc.theWorld == null || mc.thePlayer == null || event.entity == null) return;

    int x = (int) Math.floor(mc.thePlayer.posX);
    int z = (int) Math.floor(mc.thePlayer.posZ);

    BiomeGenBase biome = mc.theWorld.getBiomeGenForCoords(x, z);

    int rgb;
    float maxStrength;

    if (biome == ZeroSMod.SPIRIT_GARDEN_BIOME) {
      rgb = ModConfig.SPIRIT_GARDEN.fogColor;
      maxStrength = (ModConfig.SPIRIT_GARDEN.fogMaxStrength >= 0.0F)
          ? ModConfig.SPIRIT_GARDEN.fogMaxStrength
          : DEFAULT_SPIRIT_MAX_STRENGTH;

    } else if (biome == ZeroSMod.VAKRON_BIOME) {
      rgb = ModConfig.VAKRON.fogColor;
      maxStrength = (ModConfig.VAKRON.fogMaxStrength >= 0.0F)
          ? ModConfig.VAKRON.fogMaxStrength
          : DEFAULT_VAKRON_MAX_STRENGTH;

    } else if (biome == ZeroSMod.DRAGON_REALM) {
      rgb = ModConfig.DRAGON_REALM.fogColor;
      maxStrength = (ModConfig.DRAGON_REALM.fogMaxStrength >= 0.0F)
          ? ModConfig.DRAGON_REALM.fogMaxStrength
          : DEFAULT_DRAGON_MAX_STRENGTH;

    } else if (biome == ZeroSMod.ZS_BIOME_2) {
      rgb = ModConfig.ZS_BIOME_2.fogColor;
      maxStrength = (ModConfig.ZS_BIOME_2.fogMaxStrength >= 0.0F)
          ? ModConfig.DRAGON_REALM.fogMaxStrength
          : DEFAULT_ZS_BIOME_2_STRENGTH;

    }  else if (biome == ZeroSMod.ZS_BIOME_3) {
      rgb = ModConfig.ZS_BIOME_3.fogColor;
      maxStrength = (ModConfig.ZS_BIOME_3.fogMaxStrength >= 0.0F)
          ? ModConfig.ZS_BIOME_3.fogMaxStrength
          : DEFAULT_ZS_BIOME_3_STRENGTH;

    } else if (biome == ZeroSMod.ZS_BIOME_4) {
      rgb = ModConfig.ZS_BIOME_4.fogColor;
      maxStrength = (ModConfig.ZS_BIOME_4.fogMaxStrength >= 0.0F)
          ? ModConfig.ZS_BIOME_4.fogMaxStrength
          : DEFAULT_ZS_BIOME_4_STRENGTH;
    }
    else {
      return;
    }

    float r = ((rgb >> 16) & 0xFF) / 255.0F;
    float g = ((rgb >> 8) & 0xFF) / 255.0F;
    float b = (rgb & 0xFF) / 255.0F;

    // Keep your current math exactly
    float lookY = (float) Math.abs(event.entity.getLookVec().yCoord);
    float horizonFactor = 0.9F + (0.75F * lookY);

    float strength = maxStrength * horizonFactor;
    if (strength < 0.0F) strength = 0.0F;
    if (strength > 1.0F) strength = 1.0F;

    event.red   = (event.red   * (1.0F - strength)) + (r * strength);
    event.green = (event.green * (1.0F - strength)) + (g * strength);
    event.blue  = (event.blue  * (1.0F - strength)) + (b * strength);
  }
}
