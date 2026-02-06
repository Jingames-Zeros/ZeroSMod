package org.darkoro.zerosmod.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import org.darkoro.zerosmod.config.BiomeConfig;

public class ZSBiome8 extends BiomeGenBase {

  public ZSBiome8(int biomeId) {
    super(biomeId);
    this.setBiomeName(BiomeConfig.ZS_BIOME_8.biomeName);
    this.waterColorMultiplier = (BiomeConfig.ZS_BIOME_8.waterColor);
    this.topBlock = Blocks.grass;
    this.fillerBlock = Blocks.obsidian;
    this.spawnableCreatureList.clear();
    this.spawnableMonsterList.clear();
  }

  @Override @SideOnly(Side.CLIENT) public int getSkyColorByTemp(float currentTemperature) { return (BiomeConfig.ZS_BIOME_8.skyColor); }
  @Override @SideOnly(Side.CLIENT) public int getBiomeGrassColor(int x, int y, int z) { return (BiomeConfig.ZS_BIOME_8.grassColor); }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeGrassColor(int original) { return (BiomeConfig.ZS_BIOME_8.grassColor); }
  @Override @SideOnly(Side.CLIENT) public int getBiomeFoliageColor(int x, int y, int z) { return (BiomeConfig.ZS_BIOME_8.foliageColor); }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeFoliageColor(int original) { return (BiomeConfig.ZS_BIOME_8.foliageColor); }
}
