package org.darkoro.zerosmod.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;

public class WorldProviderPvP extends WorldProvider {

  @Override public void registerWorldChunkManager() {
    this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.plains, 0.0F);
    this.hasNoSky = false;
  }

  @Override public IChunkProvider createChunkGenerator() {
    return new ChunkProviderFlat(this.worldObj, this.worldObj.getSeed(), false, getFlatPreset());
  }

  @Override public boolean isSurfaceWorld() {
    return true;
  }

  @Override public boolean canRespawnHere() {
    return true;
  }

  @Override public int getAverageGroundLevel() {
    return 4;
  }

  @Override public double getHorizon() {
    return 4.0D;
  }

  @Override public String getDimensionName() {
    return "PvP";
  }

  private String getFlatPreset() {
    int bedrockId = Block.getIdFromBlock(Blocks.bedrock);
    int dirtId = Block.getIdFromBlock(Blocks.dirt);
    int grassId = Block.getIdFromBlock(Blocks.grass);
    int biomeId = BiomeGenBase.plains.biomeID;
    return "2;" + bedrockId + ",2x" + dirtId + "," + grassId + ";" + biomeId + ";";
  }
}
