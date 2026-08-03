package org.darkoro.zerosmod.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import org.darkoro.zerosmod.blocks.ModBlocks;
import org.darkoro.zerosmod.config.BiomeConfig;

public class WorldProviderDragonRealm extends WorldProvider {

  private static final float MIN_NIGHT_COLOR_FACTOR = 0.28F;

  @Override public void registerWorldChunkManager() {
    BiomeGenBase biome = BiomeConfig.DRAGON_REALM.getBiome();
    if (biome == null) {
      biome = BiomeGenBase.plains;
    }

    this.worldChunkMgr = new WorldChunkManagerHell(biome, 0.0F);
    this.hasNoSky = false;
  }

  @Override public IChunkProvider createChunkGenerator() {
    return new ChunkProviderFlat(this.worldObj, this.worldObj.getSeed(), false, getFlatPreset());
  }

  @Override public boolean isSurfaceWorld() {
    return true;
  }

  @Override public boolean canRespawnHere() {
    return true;
  }

  @Override public int getAverageGroundLevel() {
    return 4;
  }

  @Override public double getHorizon() {
    return 4.0D;
  }

  @Override @SideOnly(Side.CLIENT) public Vec3 getSkyColor(Entity cameraEntity, float partialTicks) {
    return daylightScaledColor(BiomeConfig.DRAGON_REALM.skyColor, partialTicks, 1.0F);
  }

  @Override @SideOnly(Side.CLIENT) public Vec3 getFogColor(float celestialAngle, float partialTicks) {
    return daylightScaledColor(BiomeConfig.DRAGON_REALM.fogColor, partialTicks, 1.0F);
  }

  @Override @SideOnly(Side.CLIENT) public boolean doesXZShowFog(int x, int z) {
    return false;
  }

  @Override public String getDimensionName() {
    return "Dragon Realm";
  }

  private String getFlatPreset() {
    int biomeId = getDragonRealmBiomeId();
    int dragonCloudsId = Block.getIdFromBlock(ModBlocks.DRAGON_CLOUDS);
    return "2;7,2x3," + dragonCloudsId + ";" + biomeId;
  }

  private int getDragonRealmBiomeId() {
    BiomeGenBase biome = BiomeConfig.DRAGON_REALM.getBiome();
    return biome == null ? BiomeConfig.DRAGON_REALM.getId() : biome.biomeID;
  }

  private Vec3 daylightScaledColor(int rgb, float partialTicks, float multiplier) {
    float daylight = getDaylightFactor(partialTicks);
    float red = (float)(rgb >> 16 & 255) / 255.0F * daylight * multiplier;
    float green = (float)(rgb >> 8 & 255) / 255.0F * daylight * multiplier;
    float blue = (float)(rgb & 255) / 255.0F * daylight * multiplier;
    return Vec3.createVectorHelper(red, green, blue);
  }

  private float getDaylightFactor(float partialTicks) {
    float angle = this.worldObj.getCelestialAngle(partialTicks);
    float daylight = MathHelper.cos(angle * (float)Math.PI * 2.0F) * 2.0F + 0.5F;

    if (daylight < MIN_NIGHT_COLOR_FACTOR) {
      return MIN_NIGHT_COLOR_FACTOR;
    }

    if (daylight > 1.0F) {
      return 1.0F;
    }

    return daylight;
  }
}
