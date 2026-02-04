package org.darkoro.zerosmod.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import org.darkoro.zerosmod.config.ModConfig;

public class SpiritGardenBiome extends BiomeGenBase {

  public SpiritGardenBiome(int biomeId) {
    super(biomeId);
    this.setBiomeName(ModConfig.SPIRIT_GARDEN.biomeName);
    this.waterColorMultiplier = (ModConfig.SPIRIT_GARDEN.waterColor);
    this.topBlock = Blocks.grass;
    this.fillerBlock = Blocks.obsidian;
    this.spawnableCreatureList.clear();
    this.spawnableMonsterList.clear();
  }

  @Override @SideOnly(Side.CLIENT) public int getSkyColorByTemp(float currentTemperature) { return (ModConfig.SPIRIT_GARDEN.skyColor); }
  @Override @SideOnly(Side.CLIENT) public int getBiomeGrassColor(int x, int y, int z) { return (ModConfig.SPIRIT_GARDEN.grassColor); }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeGrassColor(int original) { return (ModConfig.SPIRIT_GARDEN.grassColor); }
  @Override @SideOnly(Side.CLIENT) public int getBiomeFoliageColor(int x, int y, int z) { return (ModConfig.SPIRIT_GARDEN.foliageColor); }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeFoliageColor(int original) { return (ModConfig.SPIRIT_GARDEN.foliageColor); }
}
