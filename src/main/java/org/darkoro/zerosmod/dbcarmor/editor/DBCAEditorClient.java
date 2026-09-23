package org.darkoro.zerosmod.dbcarmor.editor;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;

public final class DBCAEditorClient {
  public static final DBCAEditorClient INSTANCE = new DBCAEditorClient();
  private final Queue<Delivery> responses = new ConcurrentLinkedQueue<>();

  private DBCAEditorClient() {}

  public void receive(DBCAEditorSnapshot snapshot, INetHandler connection) {
    responses.add(new Delivery(snapshot, connection));
  }

  @SubscribeEvent
  public void disconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
    responses.clear();
  }

  @SubscribeEvent
  public void tick(TickEvent.ClientTickEvent event) {
    if (event.phase != TickEvent.Phase.END) return;
    Minecraft mc = Minecraft.getMinecraft();
    Delivery delivery;
    while ((delivery = responses.poll()) != null) {
      if (mc.theWorld == null || mc.thePlayer == null || delivery.connection != mc.getNetHandler()) {
        continue;
      }
      if (delivery.snapshot.open || !(mc.currentScreen instanceof DBCAEditorScreen)) {
        mc.displayGuiScreen(new DBCAEditorScreen(delivery.snapshot));
      } else {
        ((DBCAEditorScreen) mc.currentScreen).receive(delivery.snapshot);
      }
    }
  }

  private static final class Delivery {
    final DBCAEditorSnapshot snapshot;
    final INetHandler connection;

    Delivery(DBCAEditorSnapshot snapshot, INetHandler connection) {
      this.snapshot = snapshot;
      this.connection = connection;
    }
  }
}
