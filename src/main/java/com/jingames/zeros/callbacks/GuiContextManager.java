package com.jingames.zeros.callbacks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.entity.player.EntityPlayer;

public class GuiContextManager {

  private static final Map<UUID, Map<Integer, IGuiContextProvider>> playerContexts = new ConcurrentHashMap<>();
  private static final AtomicInteger nextContextId = new AtomicInteger(1000);

  public static int registerContext(EntityPlayer ply, IGuiContextProvider provider) {
    int id = nextContextId.getAndIncrement();
    playerContexts.computeIfAbsent(ply.getUniqueID(), k -> new ConcurrentHashMap<>()).put(id, provider);
    return id;
  }

  public static IGuiContextProvider getContext(EntityPlayer ply, int id) {
    Map<Integer, IGuiContextProvider> contexts = playerContexts.get(ply.getUniqueID());
    return contexts != null ? contexts.get(id) : null;
  }

  public static void clearContext(EntityPlayer ply, IGuiContextProvider provider) {
    Map<Integer, IGuiContextProvider> contexts = playerContexts.get(ply.getUniqueID());
    if (contexts != null) {
      contexts.values().remove(provider);
      if (contexts.isEmpty()) {
        playerContexts.remove(ply.getUniqueID());
      }
    }
  }

}
