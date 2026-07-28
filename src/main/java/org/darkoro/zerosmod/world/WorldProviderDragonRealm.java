package org.darkoro.zerosmod.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import org.darkoro.zerosmod.config.BiomeConfig;

public class WorldProviderDragonRealm extends WorldProvider {

  @Override public void registerWorldChunkManager() {
    BiomeGenBase biome = BiomeConfig.DRAGON_REALM.getBiome();
    if (biome == null) {
      biome = BiomeGenBase.plains;
    }

    this.worldChunkMgr = new WorldChunkManagerHell(biome, 0.0F);
    this.hasNoSky = false;
  }

  @Override public IChunkProvider createChunkGenerator() {
    return new ChunkProviderDragonRealm(this.worldObj, this.worldObj.getSeed());
  }

  @Override @SideOnly(Side.CLIENT) public Vec3 getFogColor(float celestialAngle, float partialTicks) {
    return rgbVector(BiomeConfig.DRAGON_REALM.fogColor, 0.45F);
  }

  @Override @SideOnly(Side.CLIENT) public Vec3 getSkyColor(Entity cameraEntity, float partialTicks) {
    return rgbVector(BiomeConfig.DRAGON_REALM.skyColor, 1.0F);
  }

  @Override public String getDimensionName() {
    return "Dragon Realm";
  }

  @Override public String getWelcomeMessage() {
    return "Entering Dragon Realm";
  }

  @Override public String getDepartMessage() {
    return "Leaving Dragon Realm";
  }

  private Vec3 rgbVector(int rgb, float multiplier) {
    float red = (float)(rgb >> 16 & 255) / 255.0F * multiplier;
    float green = (float)(rgb >> 8 & 255) / 255.0F * multiplier;
    float blue = (float)(rgb & 255) / 255.0F * multiplier;
    return Vec3.createVectorHelper(red, green, blue);
  }
}
