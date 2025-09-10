package org.darkoro.guiapi.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public class SpiritGardenBiome extends BiomeGenBase {

  public SpiritGardenBiome(int biomeId) {
    super(biomeId);
    this.setBiomeName("Spirit Garden");
    this.waterColorMultiplier = 0x48006E;
    this.topBlock = Blocks.grass;
    this.fillerBlock = Blocks.obsidian;
    this.spawnableCreatureList.clear();
    this.spawnableMonsterList.clear();
  }

  @Override @SideOnly(Side.CLIENT) public int getBiomeGrassColor(int x, int y, int z) { return 0xC71585; }
  @Override @SideOnly(Side.CLIENT) public int getBiomeFoliageColor(int x, int y, int z) { return 0xC71585; }
  @Override @SideOnly(Side.CLIENT) public int getSkyColorByTemp(float currentTemperature) { return 0xFF991C; }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeGrassColor(int original) { return 0xC71585; }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeFoliageColor(int original) { return 0xC71585; }
}
