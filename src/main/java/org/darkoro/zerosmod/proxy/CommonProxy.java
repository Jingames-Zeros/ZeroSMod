package org.darkoro.zerosmod.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import noppes.npcs.scripted.NpcAPI;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.blocks.ModBlocks;
import org.darkoro.zerosmod.client.JRMCoreRacePatch;
import org.darkoro.zerosmod.command.CommandZSMod;
import org.darkoro.zerosmod.config.BiomeConfig;
import org.darkoro.zerosmod.event.SaiyanMasteryMergeEvent;
import org.darkoro.zerosmod.network.NetworkHandler;
import org.darkoro.zerosmod.zsweapons.server.ServerWeaponHandler;
import org.darkoro.zerosmod.world.GenericZSBiome;

public class CommonProxy {

  public void preInit(FMLPreInitializationEvent event) {
    ZeroSMod.LOGGER = event.getModLog();

    // Biomes
    ZeroSMod.SPIRIT_GARDEN_BIOME = new GenericZSBiome(80, BiomeConfig.SPIRIT_GARDEN);
    ZeroSMod.VAKRON_BIOME = new GenericZSBiome(81, BiomeConfig.VAKRON);
    ZeroSMod.DRAGON_REALM = new GenericZSBiome(82, BiomeConfig.DRAGON_REALM);
    ZeroSMod.ZS_BIOME_2 = new GenericZSBiome(83, BiomeConfig.ZS_BIOME_2);
    ZeroSMod.ZS_BIOME_3 = new GenericZSBiome(84, BiomeConfig.ZS_BIOME_3);
    ZeroSMod.ZS_BIOME_4 = new GenericZSBiome(85, BiomeConfig.ZS_BIOME_4);
    ZeroSMod.ZS_BIOME_5 = new GenericZSBiome(86, BiomeConfig.ZS_BIOME_5);
    ZeroSMod.ZS_BIOME_6 = new GenericZSBiome(87, BiomeConfig.ZS_BIOME_6);
    ZeroSMod.ZS_BIOME_7 = new GenericZSBiome(88, BiomeConfig.ZS_BIOME_7);
    ZeroSMod.ZS_BIOME_8 = new GenericZSBiome(89, BiomeConfig.ZS_BIOME_8);
    ZeroSMod.ZS_BIOME_9 = new GenericZSBiome(90, BiomeConfig.ZS_BIOME_9);
    ZeroSMod.ZS_BIOME_10 = new GenericZSBiome(91, BiomeConfig.ZS_BIOME_10);

    // Blocks & Fluids
    ModBlocks.registerAll();
    NetworkHandler.init();
  }

  public void init(FMLInitializationEvent event) {
    SaiyanMasteryMergeEvent saiyanMasteryMergeEvent = new SaiyanMasteryMergeEvent();
    FMLCommonHandler.instance().bus().register(saiyanMasteryMergeEvent);
    NpcAPI.EVENT_BUS.register(saiyanMasteryMergeEvent);
    if (ZeroSMod.LOGGER != null) {
      ZeroSMod.LOGGER.info("Registered Saiyan mastery merge handler on FML and CNPC event buses.");
    }

    FMLCommonHandler.instance().bus().register(ServerWeaponHandler.INSTANCE);
    MinecraftForge.EVENT_BUS.register(ServerWeaponHandler.INSTANCE);

    if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
      try {
        Class clazz = Class.forName("org.darkoro.zerosmod.client.BiomeFogHandler");
        Object handler = clazz.newInstance();
        MinecraftForge.EVENT_BUS.register(handler);
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
  }

  public void postInit(FMLPostInitializationEvent event) {
    JRMCoreRacePatch.apply();
  }

  public void serverStarting(FMLServerStartingEvent event) {
    event.registerServerCommand(new CommandZSMod());
  }
}