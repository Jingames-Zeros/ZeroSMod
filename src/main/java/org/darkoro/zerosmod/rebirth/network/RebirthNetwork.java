package org.darkoro.zerosmod.rebirth.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public final class RebirthNetwork {
  public static final String CHANNEL = "rebirth_gui_v1";
  public static SimpleNetworkWrapper channel;

  private RebirthNetwork() {}

  public static void initialize() {
    if (channel != null) {
      return;
    }
    channel = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);
    channel.registerMessage(RebirthRequest.Handler.class, RebirthRequest.class, 0, Side.SERVER);
    channel.registerMessage(RebirthSnapshot.Handler.class, RebirthSnapshot.class, 1, Side.CLIENT);
  }
}
