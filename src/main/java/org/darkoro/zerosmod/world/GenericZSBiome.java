package org.darkoro.zerosmod.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import org.darkoro.zerosmod.config.BiomeConfig;

public class GenericZSBiome extends BiomeGenBase {

  private final BiomeConfig.BiomeVisuals config;

  public GenericZSBiome(int biomeId, BiomeConfig.BiomeVisuals config) {
    super(biomeId);
    this.config = config;
    this.setBiomeName(config.biomeName);
    this.waterColorMultiplier = config.waterColor;
    this.topBlock = Blocks.grass;
    this.fillerBlock = Blocks.obsidian;
    this.spawnableCreatureList.clear();
    this.spawnableMonsterList.clear();
  }

  @Override @SideOnly(Side.CLIENT) public int getSkyColorByTemp(float currentTemperature) { return config.skyColor; }
  @Override @SideOnly(Side.CLIENT) public int getBiomeGrassColor(int x, int y, int z) { return config.grassColor; }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeGrassColor(int original) { return config.grassColor; }
  @Override @SideOnly(Side.CLIENT) public int getBiomeFoliageColor(int x, int y, int z) { return config.foliageColor; }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeFoliageColor(int original) { return config.foliageColor; }
}
