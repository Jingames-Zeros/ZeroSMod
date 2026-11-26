package org.darkoro.zerosmod.tpbank.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public final class TpBankNetwork {
  public static final String CHANNEL = "zs_tp_bank_v1";
  public static SimpleNetworkWrapper channel;

  private TpBankNetwork() {}

  public static void initialize() {
    if (channel != null) {
      return;
    }
    channel = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);
    channel.registerMessage(TpBankRequest.Handler.class, TpBankRequest.class, 0, Side.SERVER);
    channel.registerMessage(TpBankSnapshot.Handler.class, TpBankSnapshot.class, 1, Side.CLIENT);
    channel.registerMessage(TpBankAdminRequest.Handler.class, TpBankAdminRequest.class, 2, Side.SERVER);
    channel.registerMessage(TpBankAdminSnapshot.Handler.class, TpBankAdminSnapshot.class, 3, Side.CLIENT);
  }
}
