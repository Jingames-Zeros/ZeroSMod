package org.darkoro.guiapi.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import org.darkoro.guiapi.guis.clientside.ClientEventHandler;

public class ClientProxy extends CommonProxy{

  public static void eventsInit(){
    ClientEventHandler handler = new ClientEventHandler();
    FMLCommonHandler.instance().bus().register(handler);
    MinecraftForge.EVENT_BUS.register(handler);

  }

  public void init(FMLInitializationEvent event) {

    eventsInit();

  }

}
