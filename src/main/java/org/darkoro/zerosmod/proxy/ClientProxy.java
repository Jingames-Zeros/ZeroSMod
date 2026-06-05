package org.darkoro.zerosmod.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import java.util.Arrays;
import java.util.List;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.client.JRMCoreRacePatch;
import org.darkoro.zerosmod.client.ZSTabOverlayHandler;
import org.darkoro.zerosmod.input.KeyInputHandler;
import org.darkoro.zerosmod.input.KeybindHandler;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

  @Override
  public void preInit(FMLPreInitializationEvent event) {
    super.preInit(event);
  }

  @Override
  public void init(FMLInitializationEvent event) {
    super.init(event);
    KeybindHandler.init();
    FMLCommonHandler.instance().bus().register(new KeyInputHandler());
    MinecraftForge.EVENT_BUS.register(new ZSTabOverlayHandler());
  }

  @Override
  public void postInit(FMLPostInitializationEvent event) {
    JRMCoreRacePatch.apply();
    String defaultFile = "/assets/zerosmod/lang/%s.lang";
    List<String> langs = Arrays.asList(
        "de_DE", "en_AU", "en_CA", "en_GB", "en_US", "es_AR", "es_ES", "es_MX",
        "fr_FR", "it_IT", "pl_PL", "pt_BR");
    for (String lang : langs) {
      LanguageRegistry.instance().loadLocalization(String.format(defaultFile, lang), lang, false);
      ZeroSMod.LOGGER.info("Loaded language {}", lang);
    }
  }
}
