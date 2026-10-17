package org.darkoro.zerosmod.api;

import noppes.npcs.api.entity.IEntity;

/**
 * Methods added to spawned CNPC+ energy dome wrappers.
 * <p>
 * Ability-created domes are available through CNPC+'s existing
 * {@code energyBarrierSpawned(event)} hook. Check that the barrier is a dome before calling these
 * methods. Changes apply to that spawned dome only. Damage defaults to zero, its interval defaults
 * to 20 ticks, and owner damage defaults to disabled.
 */
public interface ScriptEnergyDomeMethods {

  /**
   * @return damage dealt to each living entity inside per hit, or zero when damage is disabled
   */
  float getDamage();

  /**
   * Sets the damage dealt to living entities inside the dome. Zero disables damage. Negative or
   * non-finite values become zero.
   *
   * @param damage damage dealt per hit
   */
  void setDamage(float damage);

  /**
   * @return ticks between damage hits; always at least one
   */
  int getDamageInterval();

  /**
   * Sets the delay between damage hits. Values below one become one. A newly configured damaging
   * dome may hit on its next server tick, then waits this many ticks between later hits.
   *
   * @param ticks ticks between damage hits
   */
  void setDamageInterval(int ticks);

  /**
   * @return true when the dome can damage its owner
   */
  boolean affectOwner();

  /**
   * @param affects true to let the dome damage its owner; false to exclude the owner
   */
  void setAffectOwner(boolean affects);

  /**
   * Gets the entity currently followed by the dome. This includes the caster when CNPC+'s native
   * follow-caster setting is active.
   *
   * @return followed entity, or null when the dome stays in place or the entity is unavailable
   */
  IEntity getFollower();

  /**
   * Makes this spawned dome follow an entity in the same world. Passing null disables following
   * and leaves the dome at its current position. An entity from another world is ignored.
   *
   * @param entity entity to follow, or null to stay in place
   */
  void setFollower(IEntity entity);

  /**
   * Gets every loaded entity whose midpoint is currently inside the dome. The dome entity itself
   * is excluded; its owner is included when inside regardless of {@link #affectOwner()}.
   *
   * @return entities currently inside the dome; never null
   */
  IEntity[] getEntitiesInside();

  /**
   * Checks the entity's midpoint against the dome's current spherical radius.
   *
   * @param entity entity to check
   * @return true when the entity is currently inside this dome
   */
  boolean isInside(IEntity entity);

  /**
   * Linearly grows or shrinks this spawned dome from its current radius. The target radius is
   * clamped to at least {@code 0.1} blocks. Zero or fewer ticks applies it immediately. A
   * non-finite radius is ignored. The resize is synchronized to tracking clients.
   *
   * @param radius target radius in blocks
   * @param ticks resize duration in ticks
   */
  void setTargetRadius(float radius, int ticks);
}
