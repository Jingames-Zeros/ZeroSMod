package org.darkoro.zerosmod.api;

import noppes.npcs.api.item.IItemStack;

public interface ScriptPlayerCombatState {
    /**
     * Updates stats and current item from new item and it's nbt
     * @param item new item
     */
    void changeItem(IItemStack item);

    /**
     * Sets item stats independent of item
     * @param itemStats Stats to copy
     * @param resetCooldown If item cooldown should be reset upon setting stats
     */
    void setCurrentZSWeapon(ScriptZSWeapon itemStats, boolean resetCooldown);

    /**
     * Refreshes the player's current item if it is the same as the given item
     * @param item Item to compare to current
     */
    void refreshItem(IItemStack item);

    /**
     * Triggers an attack cooldown
     */
    void resetCooldown();

    // Getters
    double getRemainingAttackCooldown();
    ScriptZSWeapon getCurrentZSWeapon();
    IItemStack getCurrentScriptItem();

    // Setters
    void setRemainingAttackCooldown(double remainingAttackCooldown);
}
