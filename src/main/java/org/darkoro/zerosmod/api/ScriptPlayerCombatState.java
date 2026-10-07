package org.darkoro.zerosmod.api;

import noppes.npcs.api.item.IItemStack;

/**
 * A player's live Zero S combat state, from {@link ZSAPI#getPlayerCombatState}.
 */
public interface ScriptPlayerCombatState {

  /**
   * Switches the tracked weapon to the given item and reads its stats.
   *
   * @param item new item
   */
  void changeItem(IItemStack item);

  /**
   * Uses the given stats regardless of the held item.
   *
   * @param itemStats stats to copy
   * @param resetCooldown true to also start a new attack cooldown
   */
  void setCurrentZSWeapon(ScriptZSWeapon itemStats, boolean resetCooldown);

  /**
   * Re-reads the stats when the given item is the tracked weapon.
   *
   * @param item item to compare with the tracked weapon
   */
  void refreshItem(IItemStack item);

  /**
   * Starts a new attack cooldown.
   */
  void resetCooldown();

  /**
   * @return remaining attack cooldown in ticks
   */
  double getRemainingAttackCooldown();

  /**
   * @return stats of the tracked weapon
   */
  ScriptZSWeapon getCurrentZSWeapon();

  /**
   * @return the tracked weapon item
   */
  IItemStack getCurrentScriptItem();

  /**
   * @param remainingAttackCooldown remaining attack cooldown in ticks
   */
  void setRemainingAttackCooldown(double remainingAttackCooldown);
}
