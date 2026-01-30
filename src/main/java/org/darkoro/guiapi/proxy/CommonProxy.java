package org.darkoro.guiapi.proxy;

import org.darkoro.guiapi.GenericGuiApi;
import org.darkoro.guiapi.guis.GUIHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import org.darkoro.guiapi.world.*;
import org.darkoro.guiapi.blocks.ModBlocks;

public class CommonProxy {

  public void preInit(FMLPreInitializationEvent event) {
    GUIHandler.LOGGER = event.getModLog();

    // Biomes
    GenericGuiApi.SPIRIT_GARDEN_BIOME = new SpiritGardenBiome(80);
    GenericGuiApi.VAKRON_BIOME = new VakronBiome(81);
    GenericGuiApi.DRAGON_REALM = new DragonRealm(82);
    GenericGuiApi.ZS_BIOME_2 = new ZSBiome2(83);
    GenericGuiApi.ZS_BIOME_3 = new ZSBiome3(84);
    GenericGuiApi.ZS_BIOME_4 = new ZSBiome4(85);

    // Blocks & Fluids
    ModBlocks.registerAll();
  }

  public void init(FMLInitializationEvent event) {
    if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
      try {
        Class clazz = Class.forName("org.darkoro.guiapi.client.BiomeFogHandler");
        Object handler = clazz.newInstance();
        MinecraftForge.EVENT_BUS.register(handler);
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
  }

  public void serverStarting(FMLServerStartingEvent event) {}

}