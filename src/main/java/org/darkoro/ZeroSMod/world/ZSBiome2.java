package org.darkoro.ZeroSMod.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public class ZSBiome2 extends BiomeGenBase {

  public ZSBiome2(int biomeId) {
    super(biomeId);
    this.setBiomeName("ZS Biome 2");
    this.waterColorMultiplier = 0xFFFFF9;
    this.topBlock = Blocks.grass;
    this.fillerBlock = Blocks.obsidian;
    this.spawnableCreatureList.clear();
    this.spawnableMonsterList.clear();
  }

  @Override @SideOnly(Side.CLIENT) public int getBiomeGrassColor(int x, int y, int z) { return 0xFFFFF9; }
  @Override @SideOnly(Side.CLIENT) public int getBiomeFoliageColor(int x, int y, int z) { return 0xFFFFF9; }
  @Override @SideOnly(Side.CLIENT) public int getSkyColorByTemp(float currentTemperature) { return 0xFFFFF9; }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeGrassColor(int original) { return 0xFFFFF9; }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeFoliageColor(int original) { return 0xFFFFF9; }
}
