package org.darkoro.zerosmod.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import org.darkoro.zerosmod.config.BiomeConfig;

public class ZSBiome7 extends BiomeGenBase {

  public ZSBiome7(int biomeId) {
    super(biomeId);
    this.setBiomeName(BiomeConfig.ZS_BIOME_7.biomeName);
    this.waterColorMultiplier = (BiomeConfig.ZS_BIOME_7.waterColor);
    this.topBlock = Blocks.grass;
    this.fillerBlock = Blocks.obsidian;
    this.spawnableCreatureList.clear();
    this.spawnableMonsterList.clear();
  }

  @Override @SideOnly(Side.CLIENT) public int getSkyColorByTemp(float currentTemperature) { return (BiomeConfig.ZS_BIOME_7.skyColor); }
  @Override @SideOnly(Side.CLIENT) public int getBiomeGrassColor(int x, int y, int z) { return (BiomeConfig.ZS_BIOME_7.grassColor); }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeGrassColor(int original) { return (BiomeConfig.ZS_BIOME_7.grassColor); }
  @Override @SideOnly(Side.CLIENT) public int getBiomeFoliageColor(int x, int y, int z) { return (BiomeConfig.ZS_BIOME_7.foliageColor); }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeFoliageColor(int original) { return (BiomeConfig.ZS_BIOME_7.foliageColor); }
}
