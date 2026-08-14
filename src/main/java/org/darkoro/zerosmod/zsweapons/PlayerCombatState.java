package org.darkoro.zerosmod.zsweapons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.darkoro.zerosmod.zsweapons.cache.CachedWeaponStats;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponTypeId.DEFAULT;

public class PlayerCombatState {
    private ItemStack currentItem;
    private final CachedWeaponStats itemStats = new CachedWeaponStats( DEFAULT );
    private double remainingAttackCooldown = 0;

    public PlayerCombatState() {}

    /**
     * Updates stats and current item from new item and it's nbt
     * @param item new item
     */
    public void changeItem(ItemStack item) {
        this.currentItem = item;
        this.itemStats.changeItem(item);
    }

    /**
     * Sets item stats independent of item
     * @param itemStats Stats to copy
     * @param resetCooldown If item cooldown should be reset upon setting stats
     */
    public void setItemStats(CachedWeaponStats itemStats, boolean resetCooldown) {
        this.itemStats.copy(itemStats);
        if(resetCooldown) resetCooldown();
    }

    /**
     * Handles combat state ticks
     */
    public void tick(double tickRate) {
        if(remainingAttackCooldown > 0) {
            remainingAttackCooldown -= tickRate;
        }
    }

    /**
     * Handles combat state attacks
     */
    public void handleAttack() {
        resetCooldown();
    }

    /**
     * Resets cooldown after blocking
     */
    public void blockEvent() {
        remainingAttackCooldown = itemStats.getBlockCooldown();
    }

    /**
     * Triggers an attack cooldown
     */
    public void resetCooldown() {
        remainingAttackCooldown = itemStats.getCooldown();
    }

    // Getters
    public double getRemainingAttackCooldown() { return remainingAttackCooldown; }
    public CachedWeaponStats getItemStats() { return itemStats; }
    public ItemStack getCurrentItem() { return currentItem; }

    // Setters
    public void setRemainingAttackCooldown(double remainingAttackCooldown) { this.remainingAttackCooldown = remainingAttackCooldown; }
}
