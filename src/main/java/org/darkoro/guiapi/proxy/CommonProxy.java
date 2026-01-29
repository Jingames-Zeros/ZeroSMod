package org.darkoro.guiapi.proxy;

import org.darkoro.guiapi.GenericGuiApi;
import org.darkoro.guiapi.guis.GUIHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.darkoro.guiapi.world.*;

public class CommonProxy {

  public void preInit(FMLPreInitializationEvent event) {
    GUIHandler.LOGGER = event.getModLog();
    GenericGuiApi.SPIRIT_GARDEN_BIOME = new SpiritGardenBiome(80);
    GenericGuiApi.VAKRON_BIOME = new VakronBiome(81);
    GenericGuiApi.ZS_BIOME_1 = new ZSBiome1(82);
    GenericGuiApi.ZS_BIOME_2 = new ZSBiome2(83);
    GenericGuiApi.ZS_BIOME_3 = new ZSBiome3(84);
    GenericGuiApi.ZS_BIOME_4 = new ZSBiome4(85);
  }

  public void init(FMLInitializationEvent event) {}

  public void serverStarting(FMLServerStartingEvent event) {}

}