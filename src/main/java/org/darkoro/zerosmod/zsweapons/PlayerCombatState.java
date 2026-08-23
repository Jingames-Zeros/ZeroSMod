package org.darkoro.zerosmod.zsweapons;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.scripted.item.ScriptItemStack;
import org.darkoro.zerosmod.api.ScriptPlayerCombatState;
import org.darkoro.zerosmod.api.ScriptZSWeapon;
import org.darkoro.zerosmod.config.ServerWeaponConfig;
import org.darkoro.zerosmod.zsweapons.cache.CachedWeaponStats;

public class PlayerCombatState implements ScriptPlayerCombatState {
    private ItemStack currentItem;
    private final CachedWeaponStats itemStats = new CachedWeaponStats();
    private double remainingAttackCooldown = 0;
    private boolean waitingToAttack = false;
    private float distanceToTarget = 0.0F;

    public PlayerCombatState() {}

    /**
     * Updates stats and current item from new item and it's nbt
     * @param item new item
     */
    public void changeItem(ItemStack item) {
        this.currentItem = item;
        this.itemStats.changeItem(item);
    }
    public void changeItem(IItemStack item) { changeItem(item.getMCItemStack()); }

    /**
     * Sets item stats independent of item
     * @param itemStats Stats to copy
     * @param resetCooldown If item cooldown should be reset upon setting stats
     */
    public void setItemStats(CachedWeaponStats itemStats, boolean resetCooldown) {
        this.itemStats.copy(itemStats, false);
        if(resetCooldown) resetCooldown();
    }
    public void setCurrentZSWeapon(ScriptZSWeapon itemStats, boolean resetCooldown) { setItemStats((CachedWeaponStats) itemStats, resetCooldown); }

    /**
     * Refreshes the player's current item if it is the same as the given item
     * @param item Item to compare to current
     */
    public void refreshItem(ItemStack item) {
        NBTTagCompound newNbt = item.getTagCompound();
        NBTTagCompound curNbt = currentItem.getTagCompound();
        // Why and how are you running this on a non-linked item
        if(
                newNbt == null ||
                !newNbt.hasKey("ItemData") ||
                !newNbt.getCompoundTag("ItemData").hasKey("Id") ||
                curNbt == null ||
                !curNbt.hasKey("ItemData") ||
                !curNbt.getCompoundTag("ItemData").hasKey("Id")
        ) return;
        if(newNbt.getCompoundTag("ItemData").getInteger("Id") == curNbt.getCompoundTag("ItemData").getInteger("Id")) {
            changeItem(item);
            resetCooldown();
        }
    }
    public void refreshItem(IItemStack item) { refreshItem(item.getMCItemStack()); }

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
    public void handleAttack(float distanceToTarget) {
        resetCooldown();
        this.distanceToTarget = distanceToTarget;
        waitingToAttack = true;
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

    /**
     * Resolves sweet spot of an attack returning the extra sweet spot multiplier
     * @return Sweet spot multiplier
     */
    public float resolveAttack() {
        if(!waitingToAttack) return 1.0F;
        waitingToAttack = false;
        return ZSWeaponUtils.calculateSweetSpotMulti(distanceToTarget, itemStats.getSweetSpot());
    }

    // Getters
    public double getRemainingAttackCooldown() { return remainingAttackCooldown; }
    public ScriptZSWeapon getCurrentZSWeapon() { return itemStats; }
    public CachedWeaponStats getItemStats() { return itemStats; }
    public ItemStack getCurrentItem() { return currentItem; }
    public IItemStack getCurrentScriptItem() { return new ScriptItemStack(currentItem); }


    // Setters
    public void setRemainingAttackCooldown(double remainingAttackCooldown) { this.remainingAttackCooldown = remainingAttackCooldown; }
}
