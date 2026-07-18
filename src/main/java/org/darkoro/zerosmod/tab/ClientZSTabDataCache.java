package org.darkoro.zerosmod.tab;

import org.darkoro.zerosmod.network.SyncZSTabDataPacket;

public final class ClientZSTabDataCache {

  // Read/Write on different threads
  private static volatile SyncZSTabDataPacket latest = new SyncZSTabDataPacket();
  private static volatile long lastUpdateMillis;

  private ClientZSTabDataCache() {}

  public static void update(SyncZSTabDataPacket packet) {
    latest = packet;
    lastUpdateMillis = System.currentTimeMillis();
  }

  public static SyncZSTabDataPacket getLatest() {
    return latest;
  }

  public static boolean hasFreshData() {
    return System.currentTimeMillis() - lastUpdateMillis < 5000L;
  }
}
