package org.darkoro.zerosmod.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import org.darkoro.zerosmod.config.ModConfig;

public class ZSBiome3 extends BiomeGenBase {

  public ZSBiome3(int biomeId) {
    super(biomeId);
    this.setBiomeName(ModConfig.ZS_BIOME_3.biomeName);
    this.waterColorMultiplier = (ModConfig.ZS_BIOME_3.waterColor & 0xFFFFF9);
    this.topBlock = Blocks.grass;
    this.fillerBlock = Blocks.obsidian;
    this.spawnableCreatureList.clear();
    this.spawnableMonsterList.clear();
  }

  @Override @SideOnly(Side.CLIENT) public int getSkyColorByTemp(float currentTemperature) { return (ModConfig.ZS_BIOME_3.skyColor & 0xFFFFF9); }
  @Override @SideOnly(Side.CLIENT) public int getBiomeGrassColor(int x, int y, int z) { return (ModConfig.ZS_BIOME_3.grassColor & 0xFFFFF9); }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeGrassColor(int original) { return (ModConfig.ZS_BIOME_3.grassColor & 0xFFFFF9); }
  @Override @SideOnly(Side.CLIENT) public int getBiomeFoliageColor(int x, int y, int z) { return (ModConfig.ZS_BIOME_3.foliageColor & 0xFFFFF9); }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeFoliageColor(int original) { return (ModConfig.ZS_BIOME_3.foliageColor & 0xFFFFF9); }
}
