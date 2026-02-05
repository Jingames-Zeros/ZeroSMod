package org.darkoro.zerosmod.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import org.darkoro.zerosmod.config.BiomeConfig;

public class SpiritGardenBiome extends BiomeGenBase {

  public SpiritGardenBiome(int biomeId) {
    super(biomeId);
    this.setBiomeName(BiomeConfig.SPIRIT_GARDEN.biomeName);
    this.waterColorMultiplier = (BiomeConfig.SPIRIT_GARDEN.waterColor);
    this.topBlock = Blocks.grass;
    this.fillerBlock = Blocks.obsidian;
    this.spawnableCreatureList.clear();
    this.spawnableMonsterList.clear();
  }

  @Override @SideOnly(Side.CLIENT) public int getSkyColorByTemp(float currentTemperature) { return (BiomeConfig.SPIRIT_GARDEN.skyColor); }
  @Override @SideOnly(Side.CLIENT) public int getBiomeGrassColor(int x, int y, int z) { return (BiomeConfig.SPIRIT_GARDEN.grassColor); }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeGrassColor(int original) { return (BiomeConfig.SPIRIT_GARDEN.grassColor); }
  @Override @SideOnly(Side.CLIENT) public int getBiomeFoliageColor(int x, int y, int z) { return (BiomeConfig.SPIRIT_GARDEN.foliageColor); }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeFoliageColor(int original) { return (BiomeConfig.SPIRIT_GARDEN.foliageColor); }
}
