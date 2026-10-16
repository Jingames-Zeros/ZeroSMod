package org.darkoro.zerosmod.ki;

import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import net.minecraft.entity.Entity;

/** Internal storage and validation for per-attack destroyer color overrides. */
public final class DestroyerKiColor {

  public static final int DEFAULT = -1;
  public static final int MAX_RGB = 0xFFFFFF;

  private DestroyerKiColor() {}

  public static boolean isValid(int rgb) {
    return rgb >= 0 && rgb <= MAX_RGB;
  }

  public static int get(Object value) {
    return value instanceof Holder ? ((Holder)value).zerosmod$getDestroyerColor() : DEFAULT;
  }

  public static boolean set(Object value, int rgb) {
    if (!(value instanceof Holder) || !isValid(rgb)) {
      return false;
    }

    ((Holder)value).zerosmod$setDestroyerColor(rgb);
    return true;
  }

  public static boolean clear(Object value) {
    if (!(value instanceof Holder)) {
      return false;
    }

    ((Holder)value).zerosmod$setDestroyerColor(DEFAULT);
    return true;
  }

  /** Copies a builder override after NPCDBC has assigned the spawned attack's destroyer flag. */
  public static void applyToSpawnedAttack(Object configuredAttack, Entity spawnedEntity) {
    if (!(spawnedEntity instanceof EntityEnergyAtt)) {
      return;
    }

    EntityEnergyAtt spawnedAttack = (EntityEnergyAtt)spawnedEntity;
    int rgb = get(configuredAttack);
    if (spawnedAttack.destroyer && isValid(rgb)) {
      set(spawnedAttack, rgb);
    }
  }

  public interface Holder {
    int zerosmod$getDestroyerColor();
    void zerosmod$setDestroyerColor(int rgb);
  }
}
