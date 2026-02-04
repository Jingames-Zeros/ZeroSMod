package org.darkoro.zerosmod.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import org.darkoro.zerosmod.config.ModConfig;

public class VakronBiome extends BiomeGenBase {

  public VakronBiome(int biomeId) {
    super(biomeId);
    this.setBiomeName(ModConfig.VAKRON.biomeName);
    this.waterColorMultiplier = (ModConfig.VAKRON.waterColor & 0x960019);
    this.topBlock = Blocks.grass;
    this.fillerBlock = Blocks.obsidian;
    this.spawnableCreatureList.clear();
    this.spawnableMonsterList.clear();
  }

  @Override @SideOnly(Side.CLIENT) public int getSkyColorByTemp(float currentTemperature) { return (ModConfig.VAKRON.skyColor & 0x7C0A02); }
  @Override @SideOnly(Side.CLIENT) public int getBiomeGrassColor(int x, int y, int z) { return (ModConfig.VAKRON.grassColor & 0xCD1C18); }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeGrassColor(int original) { return (ModConfig.VAKRON.grassColor & 0xCD1C18); }
  @Override @SideOnly(Side.CLIENT) public int getBiomeFoliageColor(int x, int y, int z) { return (ModConfig.VAKRON.foliageColor & 0x960019); }
  @Override @SideOnly(Side.CLIENT) public int getModdedBiomeFoliageColor(int original) { return (ModConfig.VAKRON.foliageColor & 0x960019); }
}
