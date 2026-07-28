package org.darkoro.zerosmod.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.init.Blocks;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import org.darkoro.zerosmod.blocks.ModBlocks;

import java.util.Random;

import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ANIMALS;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.DUNGEON;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ICE;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAKE;

public class ChunkProviderDragonRealm extends ChunkProviderGenerate {

  private static final int CHUNK_SIZE = 16;
  private static final int POPULATION_SIZE = 32;
  private static final int WORLD_HEIGHT = 256;
  private static final int DRAGON_LAKE_CHANCE = 10;
  private static final int DRAGON_SPRING_ATTEMPTS = 12;

  private final World world;
  private final Random populateRandom = new Random();

  public ChunkProviderDragonRealm(World world, long seed) {
    super(world, seed, false);
    this.world = world;
  }

  @Override public void func_147424_a(int chunkX, int chunkZ, Block[] blocks) {
    super.func_147424_a(chunkX, chunkZ, blocks);
    replaceWater(blocks);
  }

  @Override public void populate(IChunkProvider provider, int chunkX, int chunkZ) {
    BlockFalling.fallInstantly = true;

    try {
      int minX = chunkX * CHUNK_SIZE;
      int minZ = chunkZ * CHUNK_SIZE;
      BiomeGenBase biome = this.world.getBiomeGenForCoords(minX + CHUNK_SIZE, minZ + CHUNK_SIZE);
      seedPopulateRandom(chunkX, chunkZ);
      boolean structureGenerated = false;

      MinecraftForge.EVENT_BUS.post(
          new PopulateChunkEvent.Pre(provider, this.world, this.populateRandom, chunkX, chunkZ, structureGenerated));

      populateDragonLake(provider, chunkX, chunkZ, minX, minZ, structureGenerated);
      populateDungeons(provider, chunkX, chunkZ, minX, minZ, structureGenerated);
      biome.decorate(this.world, this.populateRandom, minX, minZ);
      populateDragonSprings(minX, minZ);
      populateAnimals(provider, chunkX, chunkZ, minX, minZ, biome, structureGenerated);
      populateIce(provider, chunkX, chunkZ, minX, minZ, structureGenerated);

      MinecraftForge.EVENT_BUS.post(
          new PopulateChunkEvent.Post(provider, this.world, this.populateRandom, chunkX, chunkZ, structureGenerated));

      replaceWaterInPopulationArea(chunkX, chunkZ);
      relightChunk(chunkX, chunkZ);
    } finally {
      BlockFalling.fallInstantly = false;
    }
  }

  private void replaceWater(Block[] blocks) {
    Block dragonWater = getDragonWaterBlock();
    if (dragonWater == Blocks.water) {
      return;
    }

    for (int i = 0; i < blocks.length; i++) {
      if (isVanillaWater(blocks[i])) {
        blocks[i] = dragonWater;
      }
    }
  }

  private void populateDragonLake(IChunkProvider provider, int chunkX, int chunkZ, int minX, int minZ,
      boolean structureGenerated) {
    Block dragonWater = getDragonWaterBlock();
    if (dragonWater == Blocks.water) {
      return;
    }

    if (this.populateRandom.nextInt(DRAGON_LAKE_CHANCE) == 0
        && TerrainGen.populate(provider, this.world, this.populateRandom, chunkX, chunkZ, structureGenerated, LAKE)) {
      int worldX = minX + this.populateRandom.nextInt(CHUNK_SIZE) + 8;
      int worldY = this.populateRandom.nextInt(128);
      int worldZ = minZ + this.populateRandom.nextInt(CHUNK_SIZE) + 8;
      new WorldGenLakes(dragonWater).generate(this.world, this.populateRandom, worldX, worldY, worldZ);
    }
  }

  private void populateDungeons(IChunkProvider provider, int chunkX, int chunkZ, int minX, int minZ,
      boolean structureGenerated) {
    boolean doGen =
        TerrainGen.populate(provider, this.world, this.populateRandom, chunkX, chunkZ, structureGenerated, DUNGEON);
    for (int i = 0; doGen && i < 8; i++) {
      int worldX = minX + this.populateRandom.nextInt(CHUNK_SIZE) + 8;
      int worldY = this.populateRandom.nextInt(WORLD_HEIGHT);
      int worldZ = minZ + this.populateRandom.nextInt(CHUNK_SIZE) + 8;
      new WorldGenDungeons().generate(this.world, this.populateRandom, worldX, worldY, worldZ);
    }
  }

  private void populateDragonSprings(int minX, int minZ) {
    Block dragonWater = getDragonWaterBlock();
    if (dragonWater == Blocks.water) {
      return;
    }

    WorldGenLiquids springGenerator = new WorldGenLiquids(dragonWater);
    for (int i = 0; i < DRAGON_SPRING_ATTEMPTS; i++) {
      int worldX = minX + this.populateRandom.nextInt(CHUNK_SIZE) + 8;
      int worldY = this.populateRandom.nextInt(this.populateRandom.nextInt(248) + 8);
      int worldZ = minZ + this.populateRandom.nextInt(CHUNK_SIZE) + 8;
      springGenerator.generate(this.world, this.populateRandom, worldX, worldY, worldZ);
    }
  }

  private void populateAnimals(IChunkProvider provider, int chunkX, int chunkZ, int minX, int minZ, BiomeGenBase biome,
      boolean structureGenerated) {
    if (TerrainGen.populate(provider, this.world, this.populateRandom, chunkX, chunkZ, structureGenerated, ANIMALS)) {
      SpawnerAnimals.performWorldGenSpawning(this.world, biome, minX + 8, minZ + 8, CHUNK_SIZE, CHUNK_SIZE,
          this.populateRandom);
    }
  }

  private void populateIce(IChunkProvider provider, int chunkX, int chunkZ, int minX, int minZ,
      boolean structureGenerated) {
    boolean doGen =
        TerrainGen.populate(provider, this.world, this.populateRandom, chunkX, chunkZ, structureGenerated, ICE);
    for (int localX = 0; doGen && localX < CHUNK_SIZE; localX++) {
      for (int localZ = 0; localZ < CHUNK_SIZE; localZ++) {
        int worldX = minX + 8 + localX;
        int worldZ = minZ + 8 + localZ;
        int precipitationY = this.world.getPrecipitationHeight(worldX, worldZ);

        if (this.world.isBlockFreezable(worldX, precipitationY - 1, worldZ)) {
          this.world.setBlock(worldX, precipitationY - 1, worldZ, Blocks.ice, 0, 2);
        }

        if (this.world.func_147478_e(worldX, precipitationY, worldZ, true)) {
          this.world.setBlock(worldX, precipitationY, worldZ, Blocks.snow_layer, 0, 2);
        }
      }
    }
  }

  private void replaceWaterInPopulationArea(int chunkX, int chunkZ) {
    Block dragonWater = getDragonWaterBlock();
    if (dragonWater == Blocks.water) {
      return;
    }

    int minX = chunkX * CHUNK_SIZE;
    int minZ = chunkZ * CHUNK_SIZE;
    for (int localX = 0; localX < POPULATION_SIZE; localX++) {
      int worldX = minX + localX;
      for (int localZ = 0; localZ < POPULATION_SIZE; localZ++) {
        int worldZ = minZ + localZ;
        if (!this.world.blockExists(worldX, 0, worldZ)) {
          continue;
        }

        for (int y = 0; y < WORLD_HEIGHT; y++) {
          Block block = this.world.getBlock(worldX, y, worldZ);
          if (isVanillaWater(block)) {
            this.world.setBlock(worldX, y, worldZ, dragonWater, this.world.getBlockMetadata(worldX, y, worldZ), 3);
            this.world.scheduleBlockUpdate(worldX, y, worldZ, dragonWater, dragonWater.tickRate(this.world));
          }
        }
      }
    }
  }

  private void seedPopulateRandom(int chunkX, int chunkZ) {
    this.populateRandom.setSeed(this.world.getSeed());
    long xSeed = this.populateRandom.nextLong() / 2L * 2L + 1L;
    long zSeed = this.populateRandom.nextLong() / 2L * 2L + 1L;
    this.populateRandom.setSeed((long)chunkX * xSeed + (long)chunkZ * zSeed ^ this.world.getSeed());
  }

  private void relightChunk(int chunkX, int chunkZ) {
    if (!this.world.blockExists(chunkX * CHUNK_SIZE, 0, chunkZ * CHUNK_SIZE)) {
      return;
    }

    Chunk chunk = this.world.getChunkFromChunkCoords(chunkX, chunkZ);
    chunk.generateSkylightMap();
    chunk.enqueueRelightChecks();
    this.world.markBlockRangeForRenderUpdate(
        chunkX * CHUNK_SIZE, 0, chunkZ * CHUNK_SIZE,
        chunkX * CHUNK_SIZE + 15, WORLD_HEIGHT - 1, chunkZ * CHUNK_SIZE + 15);
  }

  private Block getDragonWaterBlock() {
    Block block = ModBlocks.getLiquidBlock("dragon_water");
    return block == null ? Blocks.water : block;
  }

  private boolean isVanillaWater(Block block) {
    return block == Blocks.water || block == Blocks.flowing_water;
  }
}
