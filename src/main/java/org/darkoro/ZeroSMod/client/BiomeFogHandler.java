package org.darkoro.ZeroSMod.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.darkoro.ZeroSMod.ZeroSMod;

@SideOnly(Side.CLIENT)
public class BiomeFogHandler {
    @SubscribeEvent
    public void onFogColors(EntityViewRenderEvent.FogColors event) {
        Minecraft mc = Minecraft.getMinecraft();
        if(mc == null || mc.theWorld == null || mc.thePlayer == null || event.entity == null) {
            return;
        }

        int x = (int) Math.floor(mc.thePlayer.posX);
        int z = (int) Math.floor(mc.thePlayer.posZ);

        BiomeGenBase biome = mc.theWorld.getBiomeGenForCoords(x, z);

        int rgb;

        if (biome == ZeroSMod.SPIRIT_GARDEN_BIOME) {
            rgb = 0x6A44BF;
        } else if (biome == ZeroSMod.VAKRON_BIOME) {
            rgb = 0x2A2A2A;
        } else if (biome == ZeroSMod.DRAGON_REALM) {
            rgb = 0xFDDC5C;
        } else {
            return;
        }

        float r = ((rgb >> 16) & 0xFF) / 255.0F;
        float g = ((rgb >> 8) & 0xFF) / 255.0F;
        float b = (rgb & 0xFF) / 255.0F;

        float maxStrength = 0.70F;

        float lookY = (float) Math.abs(event.entity.getLookVec().yCoord);
        float horizonFactor = 0.9F + (0.75F * lookY);

        float strength = maxStrength * horizonFactor;

        event.red   = (event.red   * (1.0F - strength)) + (r * strength);
        event.green = (event.green * (1.0F - strength)) + (g * strength);
        event.blue  = (event.blue  * (1.0F - strength)) + (b * strength);
    }
}