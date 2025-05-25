package com.jingames.zeros;

import com.jingames.zeros.network.SyncGuiTitlePacket;
import com.jingames.zeros.network.SyncGuiTitlePacketHandler;
import com.jingames.zeros.proxy.CommonProxy;
import com.jingames.zeros.guis.GUIHandler;
import com.jingames.zeros.guis.GUIScheduler;
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

	public static final String MODID = "Generic GUI API";
	public static final String VERSION = "0.0.1";
	public static SimpleNetworkWrapper network;

	@Instance(MODID)
	public static GenericGuiApi instance;

	@SidedProxy(clientSide = "com.jingames.zeros.proxy.CommonProxy", serverSide = "com.jingames.zeros.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static int GENERIC_CHEST_GUI = 1;
	public static int GENERIC_ANVIL_GUI = 2;

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
