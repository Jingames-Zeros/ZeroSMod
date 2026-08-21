package org.darkoro.zerosmod.api;

import net.minecraft.item.ItemStack;
import noppes.npcs.api.INbt;
import noppes.npcs.api.item.IItemStack;
import org.darkoro.zerosmod.zsweapons.cache.CachedWeaponStats;

public interface ScriptZSWeapon {
    /**
     * Sets item weapon type
     * @param type Valid string weapon type
     */
    void setType(String type) throws CachedWeaponStats.UnknownWeaponTypeException;

    /**
     * Saves item stats to an INbt
     * @return INbt
     */
    INbt saveStatsToNbt();

    /**
     * Sets item to default stats
     */
    void setToDefaultStats();

    /**
     * Saves item stats to item's nbt
     */
    void saveToItem();

    /**
     * Sets item to special allowing for stat editing
     */
    void setSpecial();

    // Getters
    float getRange();
    float getRangeSq();
    int getCooldown();
    IItemStack getItem();
    String getType();
    float getAttackPercent();
    float getSweetSpot();
    boolean canChargeKi();
    float getKiPercent();
    int getKiAdditive();
    float getKiCostPercent();
    boolean canBlock();
    float getBlockDexPercent();
    float getBlockCostPercent();
    int getBlockCooldown();

    // Setters
    void setCooldown(int cooldown) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setAttackPercent(float attackPercent) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setSweetSpot(float sweetSpot) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setCanChargeKi(boolean canChargeKi) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setKiPercent(float kiPercent) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setKiAdditive(int kiAdditive) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setKiCostPercent(float kiCostPercent) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setCanBlock(boolean canBlock) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setBlockDexPercent(float blockDexPercent) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setBlockCostPercent(float blockCostPercent) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setBlockCooldown(int blockCooldown) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setRange(float range) throws CachedWeaponStats.ProtectedWeaponTypeException;
}
