package org.darkoro.zerosmod.network;

import java.util.ArrayList;
import java.util.List;
import org.darkoro.zerosmod.config.BiomeConfig;

public final class BiomeVisualSyncUtil {

  private BiomeVisualSyncUtil() {}

  public static SyncBiomeVisualsPacket buildFullPacket() {
    List<BiomeConfig.BiomeVisuals> list = new ArrayList<BiomeConfig.BiomeVisuals>();

    list.add(BiomeConfig.SPIRIT_GARDEN);
    list.add(BiomeConfig.VAKRON);
    list.add(BiomeConfig.DRAGON_REALM);
    list.add(BiomeConfig.ZS_BIOME_2);
    list.add(BiomeConfig.ZS_BIOME_3);
    list.add(BiomeConfig.ZS_BIOME_4);
    list.add(BiomeConfig.ZS_BIOME_5);
    list.add(BiomeConfig.ZS_BIOME_6);
    list.add(BiomeConfig.ZS_BIOME_7);
    list.add(BiomeConfig.ZS_BIOME_8);
    list.add(BiomeConfig.ZS_BIOME_9);
    list.add(BiomeConfig.ZS_BIOME_10);

    SyncBiomeVisualsPacket pkt = new SyncBiomeVisualsPacket();
    pkt.count = list.size();

    pkt.biomeIds = new int[pkt.count];
    pkt.skyColors = new int[pkt.count];
    pkt.fogColors = new int[pkt.count];
    pkt.fogStrengths = new float[pkt.count];
    pkt.grassColors = new int[pkt.count];
    pkt.foliageColors = new int[pkt.count];
    pkt.waterColors = new int[pkt.count];

    for (int i = 0; i < pkt.count; i++) {
      BiomeConfig.BiomeVisuals v = list.get(i);

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
