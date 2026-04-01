package org.darkoro.zerosmod.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.io.File;
import org.darkoro.zerosmod.client.ClientCache;

public final class ConfigHandler {

  private ConfigHandler() {}

  public static void loadAll(FMLPreInitializationEvent event) {

    BiomeConfig.load(event);
    String configPath = event.getModConfigurationDirectory() + File.separator + "zerosmod" + File.separator;
    ClientCache.spiritConfig =  new SpiritConfig(configPath + "spirit_hud.cfg");
  }
}
