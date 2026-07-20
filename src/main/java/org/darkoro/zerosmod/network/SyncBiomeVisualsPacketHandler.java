package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.world.biome.BiomeGenBase;
import org.darkoro.zerosmod.config.BiomeConfig;

public class SyncBiomeVisualsPacketHandler implements IMessageHandler<SyncBiomeVisualsPacket, IMessage> {

  @Override
  public IMessage onMessage(SyncBiomeVisualsPacket msg, MessageContext ctx) {

    for (int i = 0; i < msg.count; i++) {
      int id = msg.biomeIds[i];

      BiomeConfig.BiomeVisuals v = BiomeConfig.getVisualsById(id);
      if (v == null) continue;

      v.skyColor = msg.skyColors[i];
      v.fogColor = msg.fogColors[i];
      v.fogMaxStrength = msg.fogStrengths[i];
      v.grassColor = msg.grassColors[i];
      v.foliageColor = msg.foliageColors[i];
      v.waterColor = msg.waterColors[i];

      // Water tint is baked into the BiomeGenBase instance as waterColorMultiplier.
      // Biomes set it once in the constructor, update it live.
      BiomeGenBase biome = v.getBiome();
      if (biome != null) {
        biome.waterColorMultiplier = v.waterColor;
      }
    }

    return null;
  }
}
