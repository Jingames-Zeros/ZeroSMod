package org.darkoro.zerosmod.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import noppes.npcs.scripted.NpcAPI;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.blocks.ModBlocks;
import org.darkoro.zerosmod.client.JRMCoreRacePatch;
import org.darkoro.zerosmod.command.CommandZSMod;
import org.darkoro.zerosmod.config.BiomeConfig;
import org.darkoro.zerosmod.event.SaiyanMasteryMergeEvent;
import org.darkoro.zerosmod.network.NetworkHandler;
import org.darkoro.zerosmod.world.GenericZSBiome;
import org.darkoro.zerosmod.zsweapons.API.WeaponAPI;

public class CommonProxy {

  public void preInit(FMLPreInitializationEvent event) {
    ZeroSMod.LOGGER = event.getModLog();

    // Biomes
    for (BiomeConfig.BiomeVisuals visuals : BiomeConfig.getAllVisuals()) {
      visuals.setBiome(new GenericZSBiome(visuals.getId(), visuals));
    }

    // Blocks & Fluids
    ModBlocks.registerAll();
    NetworkHandler.init();
  }

  public void init(FMLInitializationEvent event) {
    SaiyanMasteryMergeEvent saiyanMasteryMergeEvent = new SaiyanMasteryMergeEvent();
    FMLCommonHandler.instance().bus().register(saiyanMasteryMergeEvent);
    NpcAPI.EVENT_BUS.register(saiyanMasteryMergeEvent);
    NpcAPI.engineObjects.put("WeaponAPI", new WeaponAPI());
    if (ZeroSMod.LOGGER != null) {
      ZeroSMod.LOGGER.info("Registered Saiyan mastery merge handler on FML and CNPC event busses.");
    }
  }

  public void postInit(FMLPostInitializationEvent event) {
    JRMCoreRacePatch.apply();
  }

  public void serverStarting(FMLServerStartingEvent event) {
    event.registerServerCommand(new CommandZSMod());
  }
}