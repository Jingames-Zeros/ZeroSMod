package org.darkoro.zerosmod.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import org.darkoro.zerosmod.config.BiomeConfig;

public class DragonRealm extends BiomeGenBase {

  public DragonRealm(int biomeId) {
    super(biomeId);
    this.setBiomeName(BiomeConfig.DRAGON_REALM.biomeName);
    this.waterColorMultiplier = (BiomeConfig.DRAGON_REALM.waterColor);
    this.topBlock = Blocks.grass;
    this.fillerBlock = Blocks.obsidian;
    this.spawnableCreatureList.clear();
    this.spawnableMonsterList.clear();
  }

  @Override @SideOnly(Side.CLIENT) public int getSkyColorByTemp(float currentTemperature) { return (BiomeConfig.DRAGON_REALM.skyColor); }
  @Override @SideOnly(Side.CLIENT) public int getBiomeGrassColor(int x, int y, int z) { return (BiomeConfig.DRAGON_REALM.grassColor); }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeGrassColor(int original) { return (BiomeConfig.DRAGON_REALM.grassColor); }
  @Override @SideOnly(Side.CLIENT) public int getBiomeFoliageColor(int x, int y, int z) { return (BiomeConfig.DRAGON_REALM.foliageColor); }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeFoliageColor(int original) { return (BiomeConfig.DRAGON_REALM.foliageColor); }
}
