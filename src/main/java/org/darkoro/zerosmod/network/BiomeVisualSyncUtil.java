package org.darkoro.zerosmod.network;

import org.darkoro.zerosmod.config.BiomeConfig;

public final class BiomeVisualSyncUtil {

  private BiomeVisualSyncUtil() {}

  public static SyncBiomeVisualsPacket buildFullPacket() {
    BiomeConfig.BiomeVisuals[] list = BiomeConfig.getAllVisuals();

    SyncBiomeVisualsPacket pkt = new SyncBiomeVisualsPacket();
    pkt.count = list.length;

    pkt.biomeIds = new int[pkt.count];
    pkt.skyColors = new int[pkt.count];
    pkt.fogColors = new int[pkt.count];
    pkt.fogStrengths = new float[pkt.count];
    pkt.grassColors = new int[pkt.count];
    pkt.foliageColors = new int[pkt.count];
    pkt.waterColors = new int[pkt.count];

    for (int i = 0; i < pkt.count; i++) {
      BiomeConfig.BiomeVisuals v = list[i];

      pkt.biomeIds[i] = v.getId();
      pkt.skyColors[i] = v.skyColor;
      pkt.fogColors[i] = v.fogColor;
      pkt.fogStrengths[i] = v.fogMaxStrength;
      pkt.grassColors[i] = v.grassColor;
      pkt.foliageColors[i] = v.foliageColor;
      pkt.waterColors[i] = v.waterColor;
    }

    return pkt;
  }
}
