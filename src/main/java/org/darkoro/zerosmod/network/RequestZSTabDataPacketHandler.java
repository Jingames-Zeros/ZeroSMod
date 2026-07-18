package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.tab.ZSTabDataProvider;

public class RequestZSTabDataPacketHandler implements IMessageHandler<RequestZSTabDataPacket, IMessage> {

  // DROP requests <900ms, avoid modified client packet spam
  private static final long MIN_REQUEST_INTERVAL_MS = 900L;
  private static final Map<UUID, Long> lastRequestMillis = new HashMap<UUID, Long>();

  @Override
  public IMessage onMessage(RequestZSTabDataPacket message, final MessageContext ctx) {
    ServerTaskScheduler.schedule(new Runnable() {
      @Override
      public void run() {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null || isThrottled(player)) {
          return;
        }

        ZeroSMod.network.sendTo(ZSTabDataProvider.buildPacket(player), player);
      }
    });
    return null;
  }

  private static boolean isThrottled(EntityPlayerMP player) {
    long now = System.currentTimeMillis();
    UUID playerId = player.getUniqueID();
    Long last = lastRequestMillis.get(playerId);
    if (last != null && now - last < MIN_REQUEST_INTERVAL_MS) return true;
    if (lastRequestMillis.size() > 128) {
      Iterator<Long> iterator = lastRequestMillis.values().iterator();
      while (iterator.hasNext()) {
        if (now - iterator.next() > 60000L) {
          iterator.remove();
        }
      }
    }

    lastRequestMillis.put(playerId, now);
    return false;
  }
}
