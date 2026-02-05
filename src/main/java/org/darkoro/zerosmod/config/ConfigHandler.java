package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
public final class ConfigHandler {

  private ConfigManager() {}

  public static void loadAll(FMLPreInitializationEvent event) {

    BiomeConfig.load(event);
  }
}
