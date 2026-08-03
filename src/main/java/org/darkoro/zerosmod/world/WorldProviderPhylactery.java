package org.darkoro.zerosmod.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.config.BiomeConfig;
import org.darkoro.zerosmod.config.DimensionConfig;

public class WorldProviderPhylactery extends WorldProvider {

  @Override public void registerWorldChunkManager() {
    BiomeGenBase biome = BiomeConfig.PHYLACTERY.getBiome();
    if (biome == null) {
      biome = BiomeGenBase.sky;
    }

    this.worldChunkMgr = new WorldChunkManagerHell(biome, 0.0F);
    this.hasNoSky = false;
    ZeroSMod.proxy.configurePhylacterySky(this);
  }

  @Override public IChunkProvider createChunkGenerator() {
    return new ZSChunkProviderEnd(this.worldObj, this.worldObj.getSeed());
  }

  @Override public float calculateCelestialAngle(long worldTime, float partialTicks) {
    int fixedTime = DimensionConfig.getPhylacteryFixedTime();
    float angle = ((float)fixedTime + partialTicks) / 24000.0F - 0.25F;

    if (angle < 0.0F) {
      angle += 1.0F;
    }

    if (angle > 1.0F) {
      angle -= 1.0F;
    }

    float smoothedAngle = angle;
    angle = 1.0F - (float)((Math.cos((double)angle * Math.PI) + 1.0D) / 2.0D);
    return smoothedAngle + (angle - smoothedAngle) / 3.0F;
  }

  @Override @SideOnly(Side.CLIENT) public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
    return null;
  }

  @Override @SideOnly(Side.CLIENT) public Vec3 getFogColor(float celestialAngle, float partialTicks) {
    return rgbVector(BiomeConfig.PHYLACTERY.fogColor, 0.45F);
  }

  @Override @SideOnly(Side.CLIENT) public boolean isSkyColored() {
    return true;
  }

  @Override @SideOnly(Side.CLIENT) public Vec3 getSkyColor(Entity cameraEntity, float partialTicks) {
    return rgbVector(BiomeConfig.PHYLACTERY.skyColor, 1.0F);
  }

  @Override public float getSunBrightnessFactor(float partialTicks) {
    return DimensionConfig.getPhylacterySunBrightness();
  }

  @Override @SideOnly(Side.CLIENT) public float getSunBrightness(float partialTicks) {
    return DimensionConfig.getPhylacterySunBrightness();
  }

  @Override @SideOnly(Side.CLIENT) public float getStarBrightness(float partialTicks) {
    return 0.0F;
  }

  @Override public boolean canRespawnHere() {
    return false;
  }

  @Override public boolean isSurfaceWorld() {
    return false;
  }

  @Override @SideOnly(Side.CLIENT) public float getCloudHeight() {
    return 8.0F;
  }

  @Override public boolean canCoordinateBeSpawn(int x, int z) {
    return this.worldObj.getTopBlock(x, z).getMaterial().blocksMovement();
  }

  @Override public ChunkCoordinates getEntrancePortalLocation() {
    return new ChunkCoordinates(100, 50, 0);
  }

  @Override public int getAverageGroundLevel() {
    return 50;
  }

  @Override @SideOnly(Side.CLIENT) public boolean doesXZShowFog(int x, int z) {
    return false;
  }

  @Override public void setWorldTime(long time) {
    this.worldObj.getWorldInfo().setWorldTime(DimensionConfig.getPhylacteryFixedTime());
  }

  @Override public long getWorldTime() {
    return DimensionConfig.getPhylacteryFixedTime();
  }

  @Override public String getDimensionName() {
    return "Phylactery";
  }

  private Vec3 rgbVector(int rgb, float multiplier) {
    float red = (float)(rgb >> 16 & 255) / 255.0F * multiplier;
    float green = (float)(rgb >> 8 & 255) / 255.0F * multiplier;
    float blue = (float)(rgb & 255) / 255.0F * multiplier;
    return Vec3.createVectorHelper(red, green, blue);
  }
}
