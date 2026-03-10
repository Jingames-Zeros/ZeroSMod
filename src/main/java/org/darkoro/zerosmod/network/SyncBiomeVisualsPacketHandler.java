package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.world.biome.BiomeGenBase;
import org.darkoro.zerosmod.ZeroSMod;
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
      BiomeGenBase biome = getBiomeInstanceById(id);
      if (biome != null) {
        biome.waterColorMultiplier = v.waterColor;
      }
    }

    return null;
  }

  private BiomeGenBase getBiomeInstanceById(int id) {
    if (id == BiomeConfig.SPIRIT_GARDEN.getId()) return ZeroSMod.SPIRIT_GARDEN_BIOME;
    if (id == BiomeConfig.VAKRON.getId()) return ZeroSMod.VAKRON_BIOME;
    if (id == BiomeConfig.DRAGON_REALM.getId()) return ZeroSMod.DRAGON_REALM;
    if (id == BiomeConfig.ZS_BIOME_2.getId()) return ZeroSMod.ZS_BIOME_2;
    if (id == BiomeConfig.ZS_BIOME_3.getId()) return ZeroSMod.ZS_BIOME_3;
    if (id == BiomeConfig.ZS_BIOME_4.getId()) return ZeroSMod.ZS_BIOME_4;
    if (id == BiomeConfig.ZS_BIOME_5.getId()) return ZeroSMod.ZS_BIOME_5;
    if (id == BiomeConfig.ZS_BIOME_6.getId()) return ZeroSMod.ZS_BIOME_6;
    if (id == BiomeConfig.ZS_BIOME_7.getId()) return ZeroSMod.ZS_BIOME_7;
    if (id == BiomeConfig.ZS_BIOME_8.getId()) return ZeroSMod.ZS_BIOME_8;
    if (id == BiomeConfig.ZS_BIOME_9.getId()) return ZeroSMod.ZS_BIOME_9;
    if (id == BiomeConfig.ZS_BIOME_10.getId()) return ZeroSMod.ZS_BIOME_10;

    return null;
  }
}
