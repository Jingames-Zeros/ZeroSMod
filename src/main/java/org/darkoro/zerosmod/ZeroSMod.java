package org.darkoro.zerosmod;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;
import org.darkoro.zerosmod.config.ConfigHandler;
import org.darkoro.zerosmod.guis.GUIHandler;
import org.darkoro.zerosmod.guis.GUIScheduler;
import org.darkoro.zerosmod.guis.GuiTextureRegistry;
import org.darkoro.zerosmod.network.SyncBiomeVisualsPacket;
import org.darkoro.zerosmod.network.SyncBiomeVisualsPacketHandler;
import org.darkoro.zerosmod.network.SyncDimensionConfigPacket;
import org.darkoro.zerosmod.network.SyncDimensionConfigPacketHandler;
import org.darkoro.zerosmod.network.SyncGuiTitlePacket;
import org.darkoro.zerosmod.network.SyncGuiTitlePacketHandler;
import org.darkoro.zerosmod.network.SyncKiAttackColorPacket;
import org.darkoro.zerosmod.network.SyncKiAttackColorPacketHandler;
import org.darkoro.zerosmod.network.SyncKiAttackStatePacket;
import org.darkoro.zerosmod.network.SyncKiAttackStatePacketHandler;
import org.darkoro.zerosmod.network.RequestZSTabDataPacket;
import org.darkoro.zerosmod.network.RequestZSTabDataPacketHandler;
import org.darkoro.zerosmod.network.SyncZSTabDataPacket;
import org.darkoro.zerosmod.network.SyncZSTabDataPacketHandler;
import org.darkoro.zerosmod.proxy.CommonProxy;
import org.darkoro.zerosmod.zsweapons.network.WeaponSystemPacketHandler;

@Mod(modid = ZeroSMod.MODID, version = ZeroSMod.VERSION, acceptableRemoteVersions = ZeroSMod.ACCEPTABLE_REMOTE_VERSIONS)
public class ZeroSMod {

	public static Logger LOGGER;

	public static final String MODID = "zerosmod";
	// Bump VERSION only when clients must update. Forge uses this for the client/server mod handshake.
	public static final String VERSION = "2.0.0-beta37";
	public static final String ACCEPTABLE_REMOTE_VERSIONS = "[" + VERSION + "]";
	// Bump this for server-only emergency builds that should still allow clients on VERSION.
	public static final String SERVER_BUILD_VERSION = "2.0.0-beta37";
	public static SimpleNetworkWrapper network;

	@Instance(MODID)
	public static ZeroSMod instance;

	@SidedProxy(clientSide = "org.darkoro.zerosmod.proxy.ClientProxy", serverSide = "org.darkoro.zerosmod.proxy.ServerProxy")
	public static CommonProxy proxy;

	public static int GENERIC_CHEST_GUI = 1;
	public static int GENERIC_ANVIL_GUI = 2;

	// Creative Tab
	public static final CreativeTabs ZeroSModTab = new CreativeTabs("zerosmod") {
		@Override public Item getTabIconItem() {
			return Item.getItemFromBlock(Blocks.chest);
		}
	};

	@EventHandler public void fmlLifeCycleEvent(FMLPreInitializationEvent event) {
		ConfigHandler.loadAll(event);
		network = NetworkRegistry.INSTANCE.newSimpleChannel("ZeroSMod_Chan");
		network.registerMessage(SyncGuiTitlePacketHandler.class, SyncGuiTitlePacket.class, 0, Side.CLIENT);
		network.registerMessage(SyncBiomeVisualsPacketHandler.class, SyncBiomeVisualsPacket.class, 1, Side.CLIENT);
		network.registerMessage(RequestZSTabDataPacketHandler.class, RequestZSTabDataPacket.class, 2, Side.SERVER);
		network.registerMessage(SyncZSTabDataPacketHandler.class, SyncZSTabDataPacket.class, 3, Side.CLIENT);
		WeaponSystemPacketHandler.registerPackets(network, 4, 5, 9);
		network.registerMessage(SyncDimensionConfigPacketHandler.class, SyncDimensionConfigPacket.class, 6, Side.CLIENT);
		network.registerMessage(SyncKiAttackColorPacketHandler.class, SyncKiAttackColorPacket.class, 7, Side.CLIENT);
		network.registerMessage(SyncKiAttackStatePacketHandler.class, SyncKiAttackStatePacket.class, 8, Side.CLIENT);
		proxy.preInit(event);
	}

	@EventHandler public void fmlLifeCycleEvent(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GUIHandler());
		GuiTextureRegistry.registerChestTexture(5000000, new ResourceLocation("zerosmod", "textures/gui/container/spcGUI.png"));
		FMLCommonHandler.instance().bus().register(GUIScheduler.INSTANCE);
		proxy.init(event);
	}

	@EventHandler public void serverStarting(FMLServerStartingEvent event) {
		proxy.serverStarting(event);
	}

	@EventHandler public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
