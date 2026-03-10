package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public final class ConfigHandler {

  private ConfigHandler() {}

  public static void loadAll(FMLPreInitializationEvent event) {

    BiomeConfig.load(event);
  }
}
