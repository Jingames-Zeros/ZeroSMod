package org.darkoro.zerosmod;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.biome.BiomeGenBase;
import org.darkoro.zerosmod.blocks.ModBlocks;
import org.darkoro.zerosmod.guis.GUIHandler;
import org.darkoro.zerosmod.guis.GUIScheduler;
import org.darkoro.zerosmod.network.SyncGuiTitlePacket;
import org.darkoro.zerosmod.network.SyncGuiTitlePacketHandler;
import org.darkoro.zerosmod.proxy.CommonProxy;

@Mod(modid = ZeroSMod.MODID, version = ZeroSMod.VERSION, acceptableRemoteVersions = "*")
public class ZeroSMod {

	public static final String MODID = "zerosmod";
	public static final String VERSION = "1.0.0";
	public static SimpleNetworkWrapper network;

	@Instance(MODID)
	public static ZeroSMod instance;

	@SidedProxy(clientSide = "org.darkoro.zerosmod.proxy.CommonProxy", serverSide = "org.darkoro.zerosmod.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static int GENERIC_CHEST_GUI = 1;
	public static int GENERIC_ANVIL_GUI = 2;

	// Spirit Garden
	public static BiomeGenBase SPIRIT_GARDEN_BIOME;

	// Vakron
	public static BiomeGenBase VAKRON_BIOME;

    // Dragon Realm
    public static BiomeGenBase DRAGON_REALM;

	//Generic Biomes
	public static BiomeGenBase ZS_BIOME_2;
	public static BiomeGenBase ZS_BIOME_3;
	public static BiomeGenBase ZS_BIOME_4;

	// Creative Tab
	public static final CreativeTabs ZeroSModTab = new CreativeTabs("zerosmod") {
		@Override public Item getTabIconItem() {
			return Item.getItemFromBlock(Blocks.chest);
		}
	};

	@EventHandler public void fmlLifeCycleEvent(FMLPreInitializationEvent event) {
		network = NetworkRegistry.INSTANCE.newSimpleChannel("ZeroSMod_Chan");
		network.registerMessage(SyncGuiTitlePacketHandler.class, SyncGuiTitlePacket.class, 0, Side.CLIENT);
		proxy.preInit(event);
	}

	@EventHandler public void fmlLifeCycleEvent(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GUIHandler());
		FMLCommonHandler.instance().bus().register(GUIScheduler.INSTANCE);
		proxy.init(event);
	}

	@EventHandler public void serverStarting(FMLServerStartingEvent event) {
		proxy.serverStarting(event);
	}

	@EventHandler public void missingMappings(FMLMissingMappingsEvent event) {
		var remaps = Map.of(
			"zerosmod:spirit_water",    ModBlocks.SPIRIT_WATER_BLOCK,
			"zerosmod:colorless_water", ModBlocks.COLORLESS_WATER_BLOCK,
			"zerosmod:dragon_water",    ModBlocks.DRAGON_WATER_BLOCK
		);

		for (var mapping : event.getAll()) {
			Block block = remaps.get(mapping.name);
			if (block == null) continue;
			switch (mapping.type) {
				case BLOCK -> mapping.remap(block);
				case ITEM  -> mapping.remap(Item.getItemFromBlock(block));
			}
		}
	}
}