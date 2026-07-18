package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.Side;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.darkoro.zerosmod.ZeroSMod;

public class ServerTaskScheduler {

  public static final ServerTaskScheduler INSTANCE = new ServerTaskScheduler();
  private static final List<Runnable> queue = Collections.synchronizedList(new ArrayList<>());

  public static void schedule(Runnable task) {
    queue.add(task);
  }

  @SubscribeEvent public void onServerTick(ServerTickEvent event) {
    if (event.side == Side.SERVER && event.phase == Phase.END) {
      if (queue.isEmpty()) return;
      List<Runnable> queued;
      synchronized (queue) {
        queued = new ArrayList<>(queue);
        queue.clear();
      }
      for (Runnable task : queued) {
        try {
          task.run();
        } catch (Exception e) {
          ZeroSMod.LOGGER.error("Exception in ServerTaskScheduler: {}", e.getMessage(), e);
        }
      }
    }
  }

}
