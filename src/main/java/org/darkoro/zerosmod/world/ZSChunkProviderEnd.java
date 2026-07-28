package org.darkoro.zerosmod.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.ChunkProviderEnd;
import org.darkoro.zerosmod.blocks.ModBlocks;

public class ZSChunkProviderEnd extends ChunkProviderEnd {

  private static final int VANILLA_END_CHUNK_BLOCKS = 16 * 16 * 128;

  public ZSChunkProviderEnd(World world, long seed) {
    super(world, seed);
  }

  @Override public void func_147420_a(int chunkX, int chunkZ, Block[] blocks, BiomeGenBase[] biomes) {
    super.func_147420_a(chunkX, chunkZ, blocks, biomes);
    replaceEndStone(blocks);
  }

  private void replaceEndStone(Block[] blocks) {
    if (blocks.length != VANILLA_END_CHUNK_BLOCKS) {
      return;
    }

    Block terrainBlock = ModBlocks.PHYLACTERY_STONE == null ? Blocks.end_stone : ModBlocks.PHYLACTERY_STONE;
    for (int i = 0; i < blocks.length; i++) {
      if (blocks[i] == Blocks.end_stone) {
        blocks[i] = terrainBlock;
      }
    }
  }
}
