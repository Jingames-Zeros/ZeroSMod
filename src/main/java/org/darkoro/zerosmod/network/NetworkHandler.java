package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class NetworkHandler {

  public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("zskeybinds");

  public static void init() {
    INSTANCE.registerMessage(OpenSpcGuiPacketHandler.class, OpenSpcGuiPacket.class, 0, Side.SERVER);
    INSTANCE.registerMessage(ChargeKeyPacketHandler.class, ChargeKeyPacket.class, 1, Side.SERVER);
    INSTANCE.registerMessage(OpenTournamentGuiPacketHandler.class, OpenTournamentGuiPacket.class, 2, Side.SERVER);
  }

}
