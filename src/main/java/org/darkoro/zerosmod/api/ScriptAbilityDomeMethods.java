package org.darkoro.zerosmod.api;

import noppes.npcs.api.ability.IAbility;

/**
 * Settings on CNPC+ dome abilities. Changes apply to later casts, not to spawned domes.
 */
public interface ScriptAbilityDomeMethods extends IAbility {

  /**
   * @return dome radius in blocks
   */
  float getDomeRadius();

  /**
   * @param radius dome radius in blocks, at least 1
   */
  void setDomeRadius(float radius);

  /**
   * @return dome lifetime in ticks, used only when the ability's duration is enabled
   */
  int getBarrierDuration();

  /**
   * @param ticks dome lifetime in ticks, at least 1
   */
  void setBarrierDuration(int ticks);
}
