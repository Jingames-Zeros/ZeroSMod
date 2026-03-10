package org.darkoro.zerosmod.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.darkoro.zerosmod.event.BiomeVisualLoginSyncEvent;
import org.darkoro.zerosmod.network.ServerTaskScheduler;

public class ServerProxy extends CommonProxy {

  @Override
  public void preInit(FMLPreInitializationEvent event) {
    super.preInit(event);
  }

  @Override
  public void init(FMLInitializationEvent event) {
    super.init(event);
    FMLCommonHandler.instance().bus().register(ServerTaskScheduler.INSTANCE);
    FMLCommonHandler.instance().bus().register(new BiomeVisualLoginSyncEvent());
  }

}
