package org.darkoro.ZeroSMod.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public class DragonRealm extends BiomeGenBase {

  public DragonRealm(int biomeId) {
    super(biomeId);
    this.setBiomeName("Dragon Realm");
    this.waterColorMultiplier = 0xFFC300;
    this.topBlock = Blocks.grass;
    this.fillerBlock = Blocks.obsidian;
    this.spawnableCreatureList.clear();
    this.spawnableMonsterList.clear();
  }

  @Override @SideOnly(Side.CLIENT) public int getBiomeGrassColor(int x, int y, int z) { return 0xFDDC5C; }
  @Override @SideOnly(Side.CLIENT) public int getBiomeFoliageColor(int x, int y, int z) { return 0xD3AF37; }
  @Override @SideOnly(Side.CLIENT) public int getSkyColorByTemp(float currentTemperature) { return 0xFFD235; }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeGrassColor(int original) { return 0xFDDC5C; }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeFoliageColor(int original) { return 0xD3AF37; }
}
