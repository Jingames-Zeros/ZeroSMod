package org.darkoro.ZeroSMod.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public class SpiritGardenBiome extends BiomeGenBase {

  public SpiritGardenBiome(int biomeId) {
    super(biomeId);
    this.setBiomeName("Spirit Garden");
    this.waterColorMultiplier = 0xF56C62;
    this.topBlock = Blocks.grass;
    this.fillerBlock = Blocks.obsidian;
    this.spawnableCreatureList.clear();
    this.spawnableMonsterList.clear();
  }

  @Override @SideOnly(Side.CLIENT) public int getBiomeGrassColor(int x, int y, int z) { return 0x3BAD59; }
  @Override @SideOnly(Side.CLIENT) public int getBiomeFoliageColor(int x, int y, int z) { return 0x228F3F; }
  @Override @SideOnly(Side.CLIENT) public int getSkyColorByTemp(float currentTemperature) { return 0x5A30B8; }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeGrassColor(int original) { return 0x3BAD59; }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeFoliageColor(int original) { return 0x228f3f; }
}
