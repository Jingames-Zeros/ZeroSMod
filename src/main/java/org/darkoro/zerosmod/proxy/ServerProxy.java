package org.darkoro.zerosmod.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import noppes.npcs.scripted.NpcAPI;
import org.darkoro.zerosmod.config.ServerWeaponConfig;
import org.darkoro.zerosmod.event.BiomeVisualLoginSyncEvent;
import org.darkoro.zerosmod.network.ServerTaskScheduler;
import org.darkoro.zerosmod.zsweapons.server.ServerWeaponHandler;

public class ServerProxy extends CommonProxy {

  @Override
  public void init(FMLInitializationEvent event) {
    super.init(event);
    FMLCommonHandler.instance().bus().register(ServerTaskScheduler.INSTANCE);
    FMLCommonHandler.instance().bus().register(new BiomeVisualLoginSyncEvent());

    if (ServerWeaponConfig.isEnabled()) {
      FMLCommonHandler.instance().bus().register(ServerWeaponHandler.INSTANCE);
      MinecraftForge.EVENT_BUS.register(ServerWeaponHandler.INSTANCE);
      NpcAPI.EVENT_BUS.register(ServerWeaponHandler.INSTANCE);
    }
  }

}
