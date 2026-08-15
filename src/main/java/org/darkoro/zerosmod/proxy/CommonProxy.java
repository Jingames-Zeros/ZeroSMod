package org.darkoro.zerosmod.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;
import noppes.npcs.constants.ScriptContext;
import noppes.npcs.controllers.APIRegistry;
import noppes.npcs.controllers.HookDefinition;
import noppes.npcs.controllers.ScriptHookController;
import noppes.npcs.scripted.NpcAPI;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.api.AbstractZeroSAPI;
import org.darkoro.zerosmod.api.event.IZeroSEvent;
import org.darkoro.zerosmod.blocks.ModBlocks;
import org.darkoro.zerosmod.client.JRMCoreRacePatch;
import org.darkoro.zerosmod.command.CommandZSMod;
import org.darkoro.zerosmod.config.BiomeConfig;
import org.darkoro.zerosmod.config.ServerWeaponConfig;
import org.darkoro.zerosmod.event.KiAttackGuard;
import org.darkoro.zerosmod.event.SaiyanMasteryMergeEvent;
import org.darkoro.zerosmod.it.InstantTransmissionEventHooks;
import org.darkoro.zerosmod.network.NetworkHandler;
import org.darkoro.zerosmod.network.SyncKiAttackStatePacket;
import org.darkoro.zerosmod.world.GenericZSBiome;
import org.darkoro.zerosmod.world.ModDimensions;
import org.darkoro.zerosmod.world.WorldProviderPhylactery;
import org.darkoro.zerosmod.zsweapons.API.WeaponAPI;
import org.darkoro.zerosmod.zsweapons.server.ServerWeaponHandler;

public class CommonProxy {

  private boolean scriptHooksRegistered;

  public void preInit(FMLPreInitializationEvent event) {
    ZeroSMod.LOGGER = event.getModLog();

    // Biomes
    for (BiomeConfig.BiomeVisuals visuals : BiomeConfig.getAllVisuals()) {
      visuals.setBiome(new GenericZSBiome(visuals.getId(), visuals));
    }

    // Blocks & Fluids
    ModBlocks.registerAll();
    ModDimensions.registerAll();
    NetworkHandler.init();
  }

  public void init(FMLInitializationEvent event) {
    SaiyanMasteryMergeEvent saiyanMasteryMergeEvent = new SaiyanMasteryMergeEvent();
    FMLCommonHandler.instance().bus().register(saiyanMasteryMergeEvent);
    FMLCommonHandler.instance().bus().register(KiAttackGuard.INSTANCE);
    NpcAPI.EVENT_BUS.register(saiyanMasteryMergeEvent);
    NpcAPI.Instance().addGlobalObject("ZSAPI", AbstractZeroSAPI.Instance());
    NpcAPI.Instance().addGlobalObject("WeaponAPI", WeaponAPI.INSTANCE);
    registerScriptHooks();
    MinecraftForge.EVENT_BUS.register(KiAttackGuard.INSTANCE);
    if (ServerWeaponConfig.isEnabled()) {
      FMLCommonHandler.instance().bus().register(ServerWeaponHandler.INSTANCE);
      MinecraftForge.EVENT_BUS.register(ServerWeaponHandler.INSTANCE);
    }
    if (ZeroSMod.LOGGER != null) {
      ZeroSMod.LOGGER.info("Registered Saiyan mastery merge handler on FML and CNPC event busses.");
      ZeroSMod.LOGGER.info("Registered ZSAPI CNPC global script object.");
    }
  }

  public void postInit(FMLPostInitializationEvent event) {
    JRMCoreRacePatch.apply();
    registerScriptHooks();
  }

  public void serverStarting(FMLServerStartingEvent event) {
    event.registerServerCommand(new CommandZSMod());
  }

  public void configurePhylacterySky(WorldProviderPhylactery provider) {}

  public void applyKiAttackColorSync(int entityId, int color, int color2) {}

  public void applyKiAttackStateSync(SyncKiAttackStatePacket packet) {}

  private void registerScriptHooks() {
    if (this.scriptHooksRegistered || ScriptHookController.Instance == null) {
      return;
    }

    ScriptContext.PLAYER.addNamespace("IZeroSEvent");
    ScriptHookController.Instance.registerHook(
        ScriptContext.PLAYER,
        HookDefinition.builder(InstantTransmissionEventHooks.ACTIVATED_HOOK)
            .eventClass(IZeroSEvent.ActivatedInstantTransmissionEvent.class)
            .paramNames("event")
            .requiredImports("org.darkoro.zerosmod.api.event.IZeroSEvent")
            .build());
    APIRegistry.Instance.register("Zero S API", "https://jingames-zeros.github.io/ZeroSMod/");
    this.scriptHooksRegistered = true;

    if (ZeroSMod.LOGGER != null) {
      ZeroSMod.LOGGER.info("Registered Zero S CNPC script hooks.");
    }
  }
}
