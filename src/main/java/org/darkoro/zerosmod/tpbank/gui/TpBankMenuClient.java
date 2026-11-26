package org.darkoro.zerosmod.tpbank.gui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import org.darkoro.zerosmod.tpbank.network.TpBankAdminSnapshot;
import org.darkoro.zerosmod.tpbank.network.TpBankSnapshot;

public final class TpBankMenuClient {
  public static final TpBankMenuClient INSTANCE = new TpBankMenuClient();
  private final Queue<Delivery> responses = new ConcurrentLinkedQueue<>();

  public void receive(TpBankSnapshot snapshot, INetHandler connection) {
    responses.add(new Delivery(snapshot, null, connection));
  }

  public void receive(TpBankAdminSnapshot snapshot, INetHandler connection) {
    responses.add(new Delivery(null, snapshot, connection));
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
      if (delivery.admin != null) {
        if (delivery.admin.open) {
          mc.displayGuiScreen(new TpBankAdminScreen(delivery.admin));
        } else if (mc.currentScreen instanceof TpBankAdminScreen) {
          ((TpBankAdminScreen) mc.currentScreen).receive(delivery.admin);
        }
      } else if (delivery.snapshot.open) {
        mc.displayGuiScreen(new TpBankScreen(delivery.snapshot));
      } else if (mc.currentScreen instanceof TpBankScreen) {
        ((TpBankScreen) mc.currentScreen).receive(delivery.snapshot);
      }
    }
  }

  private static final class Delivery {
    final TpBankSnapshot snapshot;
    final TpBankAdminSnapshot admin;
    final INetHandler connection;

    Delivery(TpBankSnapshot snapshot, TpBankAdminSnapshot admin, INetHandler connection) {
      this.admin = admin;
      this.snapshot = snapshot;
      this.connection = connection;
    }
  }
}
