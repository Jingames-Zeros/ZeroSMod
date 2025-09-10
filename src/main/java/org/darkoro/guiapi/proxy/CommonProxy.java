package org.darkoro.guiapi.proxy;

import org.darkoro.guiapi.GenericGuiApi;
import org.darkoro.guiapi.guis.GUIHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.darkoro.guiapi.world.SpiritGardenBiome;

public class CommonProxy {

  public void preInit(FMLPreInitializationEvent event) {
    GUIHandler.LOGGER = event.getModLog();
    GenericGuiApi.SPIRIT_GARDEN_BIOME = new SpiritGardenBiome(80);
  }

  public void init(FMLInitializationEvent event) {}

  public void serverStarting(FMLServerStartingEvent event) {}

}