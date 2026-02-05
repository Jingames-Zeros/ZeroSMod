package org.darkoro.zerosmod.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import org.darkoro.zerosmod.config.BiomeConfig;

public class VakronBiome extends BiomeGenBase {

  public VakronBiome(int biomeId) {
    super(biomeId);
    this.setBiomeName(BiomeConfig.VAKRON.biomeName);
    this.waterColorMultiplier = (BiomeConfig.VAKRON.waterColor);
    this.topBlock = Blocks.grass;
    this.fillerBlock = Blocks.obsidian;
    this.spawnableCreatureList.clear();
    this.spawnableMonsterList.clear();
  }

  @Override @SideOnly(Side.CLIENT) public int getSkyColorByTemp(float currentTemperature) { return (BiomeConfig.VAKRON.skyColor); }
  @Override @SideOnly(Side.CLIENT) public int getBiomeGrassColor(int x, int y, int z) { return (BiomeConfig.VAKRON.grassColor); }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeGrassColor(int original) { return (BiomeConfig.VAKRON.grassColor); }
  @Override @SideOnly(Side.CLIENT) public int getBiomeFoliageColor(int x, int y, int z) { return (BiomeConfig.VAKRON.foliageColor); }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeFoliageColor(int original) { return (BiomeConfig.VAKRON.foliageColor); }
}
