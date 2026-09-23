package org.darkoro.zerosmod.rebirth.gui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import org.darkoro.zerosmod.rebirth.network.RebirthSnapshot;

public final class RebirthMenuClient {
  public static final RebirthMenuClient INSTANCE = new RebirthMenuClient();
  private final Queue<Delivery> responses = new ConcurrentLinkedQueue<>();

  public void receive(RebirthSnapshot snapshot, INetHandler connection) {
    responses.add(new Delivery(snapshot, connection));
  }

  @SubscribeEvent public void disconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
    responses.clear();
  }

  @SubscribeEvent public void tick(TickEvent.ClientTickEvent event) {
    if (event.phase != TickEvent.Phase.END) {
      return;
    }
    Minecraft mc = Minecraft.getMinecraft();
    Delivery delivery;
    while ((delivery = responses.poll()) != null) {
      if (mc.theWorld == null || mc.thePlayer == null || delivery.connection != mc.getNetHandler()) {
        continue;
      }
      if (delivery.snapshot.open) {
        mc.displayGuiScreen(new RebirthScreen(delivery.snapshot));
      } else if (mc.currentScreen instanceof RebirthScreen) {
        ((RebirthScreen) mc.currentScreen).receive(delivery.snapshot);
      }
    }
  }

  private static final class Delivery {
    final RebirthSnapshot snapshot;
    final INetHandler connection;

    Delivery(RebirthSnapshot snapshot, INetHandler connection) {
      this.snapshot = snapshot;
      this.connection = connection;
    }
  }
}
