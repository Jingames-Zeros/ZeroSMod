package org.darkoro.zerosmod.api;

import noppes.npcs.api.ability.IAbility;

/**
 * Typed settings added to CNPC+ dome abilities before they are cast.
 * <p>
 * These methods are called directly on a dome ability returned by the CNPC ability API.
 * They change later casts and do not modify a dome that has already spawned.
 */
public interface ScriptAbilityDomeMethods extends IAbility {

  /**
   * @return configured dome radius in blocks
   */
  float getDomeRadius();

  /**
   * Sets the radius used by later casts. CNPC+ clamps values below one block to one.
   *
   * @param radius dome radius in blocks
   */
  void setDomeRadius(float radius);

  /**
   * Gets the configured lifetime. CNPC+ only expires the dome after this many ticks when the
   * ability's duration setting is enabled.
   *
   * @return configured dome lifetime in ticks
   */
  int getBarrierDuration();

  /**
   * Sets the lifetime used by later casts. CNPC+ clamps values below one tick to one.
   *
   * @param ticks dome lifetime in ticks
   */
  void setBarrierDuration(int ticks);
}
