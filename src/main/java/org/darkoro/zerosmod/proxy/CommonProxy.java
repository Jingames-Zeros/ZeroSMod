package org.darkoro.zerosmod.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.network.INetHandler;
import net.minecraftforge.common.MinecraftForge;
import noppes.npcs.constants.ScriptContext;
import noppes.npcs.controllers.APIRegistry;
import noppes.npcs.controllers.HookDefinition;
import noppes.npcs.controllers.ScriptHookController;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.CustomNpcs;
import noppes.npcs.scripted.NpcAPI;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.api.AbstractZeroSAPI;
import org.darkoro.zerosmod.api.event.IZeroSEvent;
import org.darkoro.zerosmod.blocks.ModBlocks;
import org.darkoro.zerosmod.client.JRMCoreRacePatch;
import org.darkoro.zerosmod.command.CommandZSMod;
import org.darkoro.zerosmod.config.BiomeConfig;
import org.darkoro.zerosmod.config.ServerWeaponConfig;
import org.darkoro.zerosmod.dome.EnergyDomeEventHooks;
import org.darkoro.zerosmod.event.KiAttackGuard;
import org.darkoro.zerosmod.event.SaiyanMasteryMergeEvent;
import org.darkoro.zerosmod.finisher.FinisherCamera;
import org.darkoro.zerosmod.finisher.FinisherCameraPacket;
import org.darkoro.zerosmod.finisher.FinisherCameraPathPacket;
import org.darkoro.zerosmod.formdrain.CustomFormDrainEventHooks;
import org.darkoro.zerosmod.it.InstantTransmissionEventHooks;
import org.darkoro.zerosmod.transformation.CustomFormTransformationEventHooks;
import org.darkoro.zerosmod.network.NetworkHandler;
import org.darkoro.zerosmod.network.SyncKiAttackStatePacket;
import org.darkoro.zerosmod.network.RaceStatResponse;
import org.darkoro.zerosmod.network.ServerTaskScheduler;
import org.darkoro.zerosmod.dbcarmor.editor.DBCAEditorSnapshot;
import org.darkoro.zerosmod.rebirth.network.RebirthSnapshot;
import org.darkoro.zerosmod.world.GenericZSBiome;
import org.darkoro.zerosmod.world.ModDimensions;
import org.darkoro.zerosmod.world.WorldProviderPhylactery;
import org.darkoro.zerosmod.api.WeaponAPI;
import org.darkoro.zerosmod.zsweapons.attributes.AttributeBuilder;
import org.darkoro.zerosmod.zsweapons.event.WeaponSwingEventHooks;
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
    FMLCommonHandler.instance().bus().register(ServerTaskScheduler.INSTANCE);
    FMLCommonHandler.instance().bus().register(FinisherCamera.INSTANCE);
    SaiyanMasteryMergeEvent saiyanMasteryMergeEvent = new SaiyanMasteryMergeEvent();
    FMLCommonHandler.instance().bus().register(saiyanMasteryMergeEvent);
    FMLCommonHandler.instance().bus().register(KiAttackGuard.INSTANCE);
    if (Loader.isModLoaded("npcdbc")) {
      FMLCommonHandler.instance().bus().register(CustomFormTransformationEventHooks.INSTANCE);
    }
    NpcAPI.EVENT_BUS.register(saiyanMasteryMergeEvent);
    NpcAPI.Instance().addGlobalObject("ZSAPI", AbstractZeroSAPI.Instance());
    NpcAPI.Instance().addGlobalObject("WeaponAPI", WeaponAPI.INSTANCE);
    registerScriptHooks();
    MinecraftForge.EVENT_BUS.register(KiAttackGuard.INSTANCE);
    if (ServerWeaponConfig.isEnabled()) {
      FMLCommonHandler.instance().bus().register(ServerWeaponHandler.INSTANCE);
      MinecraftForge.EVENT_BUS.register(ServerWeaponHandler.INSTANCE);
    }
    new AttributeBuilder();
    if (ZeroSMod.LOGGER != null) {
      ZeroSMod.LOGGER.info("Registered Saiyan mastery merge handler on FML and CNPC event busses.");
      ZeroSMod.LOGGER.info("Registered ZSAPI CNPC global script object.");
    }
  }

  public void postInit(FMLPostInitializationEvent event) {
    JRMCoreRacePatch.apply();
    ScriptController scripts = ScriptController.Instance;
    if (scripts != null && CustomNpcs.proxy.isScriptingEnabled()
        && scripts.getEngineByName("ECMAScript") != null) {
      scripts.languages.put("ECMAScript", ".js");
    }
    registerScriptHooks();
  }

  public void serverStarting(FMLServerStartingEvent event) {
    event.registerServerCommand(new CommandZSMod());
  }

  public void configurePhylacterySky(WorldProviderPhylactery provider) {}

  public void applyKiAttackColorSync(int entityId, int color, int color2) {}

  public void applyKiAttackDestroyerColorSync(int entityId, int rgb) {}

  public void applyKiAttackStateSync(SyncKiAttackStatePacket packet) {}

  public void receiveRaceStats(RaceStatResponse packet) {}

  public void receiveRebirthSnapshot(RebirthSnapshot packet, INetHandler connection) {}

  public void receiveDBCAEditorSnapshot(DBCAEditorSnapshot packet, INetHandler connection) {}

  public void receiveFinisherCamera(FinisherCameraPacket packet) {}

  public void receiveFinisherCameraPath(FinisherCameraPathPacket packet) {}

  private void registerScriptHooks() {
    if (this.scriptHooksRegistered || ScriptHookController.Instance == null) {
      return;
    }

    ScriptContext.NPC.addNamespace("IZeroSEvent");
    ScriptContext.PLAYER.addNamespace("IZeroSEvent");
    registerEnergyDomeHook(
        ScriptContext.NPC,
        EnergyDomeEventHooks.ENTER_HOOK,
        IZeroSEvent.EnergyDomeEnterEvent.class);
    registerEnergyDomeHook(
        ScriptContext.NPC,
        EnergyDomeEventHooks.EXIT_HOOK,
        IZeroSEvent.EnergyDomeExitEvent.class);
    registerEnergyDomeHook(
        ScriptContext.PLAYER,
        EnergyDomeEventHooks.ENTER_HOOK,
        IZeroSEvent.EnergyDomeEnterEvent.class);
    registerEnergyDomeHook(
        ScriptContext.PLAYER,
        EnergyDomeEventHooks.EXIT_HOOK,
        IZeroSEvent.EnergyDomeExitEvent.class);
    ScriptHookController.Instance.registerHook(
        ScriptContext.PLAYER,
        HookDefinition.builder(InstantTransmissionEventHooks.ACTIVATED_HOOK)
            .eventClass(IZeroSEvent.ActivatedInstantTransmissionEvent.class)
            .paramNames("event")
            .requiredImports("org.darkoro.zerosmod.api.event.IZeroSEvent")
            .build());
    ScriptHookController.Instance.registerHook(
        ScriptContext.PLAYER,
        HookDefinition.builder(WeaponSwingEventHooks.WEAPON_SWING_HOOK)
            .eventClass(IZeroSEvent.WeaponSwingEvent.class)
            .paramNames("event")
            .requiredImports(
                "org.darkoro.zerosmod.api.ScriptZSWeapon",
                "org.darkoro.zerosmod.api.event.IZeroSEvent")
            .build());
    if (Loader.isModLoaded("npcdbc")) {
      ScriptHookController.Instance.registerHook(
          ScriptContext.PLAYER,
          HookDefinition.builder(CustomFormDrainEventHooks.FORM_DRAIN_HOOK)
              .eventClass(IZeroSEvent.FormDrainEvent.class)
              .paramNames("event")
              .requiredImports("org.darkoro.zerosmod.api.event.IZeroSEvent")
              .build());
      ScriptHookController.Instance.registerHook(
          ScriptContext.PLAYER,
          HookDefinition.builder(CustomFormTransformationEventHooks.BEGINS_TRANSFORMING_HOOK)
              .eventClass(IZeroSEvent.BeginsTransformingEvent.class)
              .paramNames("event")
              .requiredImports("org.darkoro.zerosmod.api.event.IZeroSEvent")
              .build());
    }
    APIRegistry.Instance.register("Zero S API", "https://jingames-zeros.github.io/ZeroSMod/");
    this.scriptHooksRegistered = true;

    if (ZeroSMod.LOGGER != null) {
      ZeroSMod.LOGGER.info("Registered Zero S CNPC script hooks.");
    }
  }

  private static void registerEnergyDomeHook(
      ScriptContext context, String hookName, Class<?> eventClass) {
    ScriptHookController.Instance.registerHook(
        context,
        HookDefinition.builder(hookName)
            .eventClass(eventClass)
            .paramNames("event")
            .requiredImports(
                "noppes.npcs.api.entity.IEnergyDome",
                "noppes.npcs.api.entity.IEntity",
                "org.darkoro.zerosmod.api.event.IZeroSEvent")
            .build());
  }
}
