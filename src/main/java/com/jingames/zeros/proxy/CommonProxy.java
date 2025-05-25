package com.jingames.zeros.proxy;

import com.jingames.zeros.guis.GUIHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

  public void preInit(FMLPreInitializationEvent event) {
    GUIHandler.LOGGER = event.getModLog();
  }

  public void init(FMLInitializationEvent event) {}

  public void serverStarting(FMLServerStartingEvent event) {}

}