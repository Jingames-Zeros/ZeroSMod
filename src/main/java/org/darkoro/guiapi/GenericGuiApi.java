package org.darkoro.guiapi;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import org.darkoro.guiapi.network.SyncGuiTitlePacket;
import org.darkoro.guiapi.network.SyncGuiTitlePacketHandler;
import org.darkoro.guiapi.proxy.CommonProxy;
import org.darkoro.guiapi.guis.GUIHandler;
import org.darkoro.guiapi.guis.GUIScheduler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = GenericGuiApi.MODID, version = GenericGuiApi.VERSION, acceptableRemoteVersions = "*")
public class GenericGuiApi {

	public static final String MODID = "genericguiapi";
	public static final String VERSION = "0.6.0";
	public static SimpleNetworkWrapper network;

	@Instance(MODID)
	public static GenericGuiApi instance;

	@SidedProxy(clientSide = "org.darkoro.guiapi.proxy.CommonProxy", serverSide = "org.darkoro.guiapi.proxy.CommonProxy")
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
	public static final CreativeTabs GENERIC_GUI_TAB = new CreativeTabs("genericguiapi") {
		@Override public Item getTabIconItem() {
			return Item.getItemFromBlock(Blocks.chest);
		}
	};

	@EventHandler
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event) {
		network = NetworkRegistry.INSTANCE.newSimpleChannel("GeGuiApi_Chan");
		network.registerMessage(SyncGuiTitlePacketHandler.class, SyncGuiTitlePacket.class, 0, Side.CLIENT);
		proxy.preInit(event);
	}

	@EventHandler
	public void fmlLifeCycleEvent(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GUIHandler());
		FMLCommonHandler.instance().bus().register(GUIScheduler.INSTANCE);
		proxy.init(event);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		proxy.serverStarting(event);
	}
}