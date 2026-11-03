package org.darkoro.zerosmod.api;

import noppes.npcs.api.entity.IEntity;

/**
 * Methods on spawned CNPC+ energy domes, for example from {@code energyBarrierSpawned}.
 */
public interface ScriptEnergyDomeMethods {

  /**
   * @return damage per hit to living entities inside, 0 when disabled
   */
  float getDamage();

  /**
   * @param damage damage per hit; 0 disables it
   */
  void setDamage(float damage);

  /**
   * @return ticks between hits
   */
  int getDamageInterval();

  /**
   * @param ticks ticks between hits, at least 1
   */
  void setDamageInterval(int ticks);

  /**
   * @return true when the dome also damages its owner
   */
  boolean affectOwner();

  /**
   * @param affects true to also damage the owner
   */
  void setAffectOwner(boolean affects);

  /**
   * @return followed entity, including a followed caster, or null
   */
  IEntity getFollower();

  /**
   * @param entity entity in the same world to follow, or null to stay in place
   */
  void setFollower(IEntity entity);

  /**
   * @return entities inside the dome, including the owner
   */
  IEntity[] getEntitiesInside();

  /**
   * @param entity entity to check
   * @return true when the entity is inside the dome
   */
  boolean isInside(IEntity entity);

  /**
   * Resizes the dome over time. A charging dome finishes its charge first; with 0 ticks it
   * charges to the new radius.
   *
   * @param radius target radius in blocks, at least 0.1
   * @param ticks duration; 0 resizes immediately
   */
  void setTargetRadius(float radius, int ticks);
}
