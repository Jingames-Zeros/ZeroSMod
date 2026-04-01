package org.darkoro.zerosmod.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import java.util.Arrays;
import java.util.List;
import net.minecraftforge.common.MinecraftForge;
import org.darkoro.guiapi.guis.clientside.ClientEventHandler;
import org.darkoro.guiapi.guis.clientside.Spirit.SpiritOverlay;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.client.ClientCache;
import org.darkoro.zerosmod.config.SpiritConfig;
import org.darkoro.zerosmod.input.KeyInputHandler;
import org.darkoro.zerosmod.input.KeybindHandler;

public class ClientProxy extends CommonProxy {

  public static void eventsInit(){
    ClientEventHandler handler = new ClientEventHandler();
    FMLCommonHandler.instance().bus().register(handler);
    MinecraftForge.EVENT_BUS.register(handler);

  }

  @Override
  public void preInit(FMLPreInitializationEvent event) {
    super.preInit(event);
  }

  @Override
  public void init(FMLInitializationEvent event) {
    super.init(event);
    KeybindHandler.init();
    FMLCommonHandler.instance().bus().register(new KeyInputHandler());

    eventsInit();
  }

  @Override
  public void postInit(FMLPostInitializationEvent event) {
    String defaultFile = "/assets/zerosmod/lang/%s.lang";
    List<String> langs = Arrays.asList(
        "de_DE", "en_AU", "en_CA", "en_GB", "en_US", "es_AR", "es_ES", "es_MX",
        "fr_FR", "it_IT", "pl_PL", "pt_BR");
    for (String lang : langs) {
      LanguageRegistry.instance().loadLocalization(String.format(defaultFile, lang), lang, false);
      ZeroSMod.LOGGER.info("Loaded language {}", lang);
    }
    ClientCache.spiritbar = new SpiritOverlay();

  }

  public static SpiritConfig getSpiritSettings (){
    return ClientCache.spiritConfig;
  }
  public static void openHealthMenu(){
    ClientCache.isSpiritHudOpen = true;
  }
}
