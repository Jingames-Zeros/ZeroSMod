package org.darkoro.zerosmod.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Minecraft;
import org.darkoro.zerosmod.guis.clientside.RaceStatEditorGui;
import org.darkoro.zerosmod.network.RaceStatResponse;

/** Deliver GUI updates on the client tick, never on Netty's thread. */
public final class RaceStatEditorClient {
  public static final RaceStatEditorClient INSTANCE = new RaceStatEditorClient();
  private final Queue<RaceStatResponse> responses = new ConcurrentLinkedQueue<>();

  public void receive(RaceStatResponse response) { responses.add(response); }

  @SubscribeEvent public void tick(TickEvent.ClientTickEvent event) {
    if (event.phase != TickEvent.Phase.END) return;
    Minecraft mc = Minecraft.getMinecraft();
    RaceStatResponse response;
    while ((response = responses.poll()) != null) {
      if (mc.theWorld == null || mc.thePlayer == null) continue;
      if (response.open) {
        mc.displayGuiScreen(new RaceStatEditorGui(response));
      } else if (mc.currentScreen instanceof RaceStatEditorGui) {
        ((RaceStatEditorGui) mc.currentScreen).receive(response);
      }
    }
  }
}
