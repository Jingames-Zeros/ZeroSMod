package org.darkoro.zerosmod.dbcarmor.editor;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public final class DBCAEditorNetwork {
  public static final String CHANNEL = "dbca_editor_v1";
  public static SimpleNetworkWrapper channel;

  private DBCAEditorNetwork() {}

  public static void initialize() {
    if (channel != null) {
      return;
    }
    channel = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);
    channel.registerMessage(DBCAEditorRequest.Handler.class, DBCAEditorRequest.class, 0,
        Side.SERVER);
    channel.registerMessage(DBCAEditorSnapshot.Handler.class, DBCAEditorSnapshot.class, 1,
        Side.CLIENT);
  }
}
