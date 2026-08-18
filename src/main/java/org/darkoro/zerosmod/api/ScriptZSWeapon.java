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
    void setType(String type);

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
    float getAttackMultiplier();
    float getSweetSpot();
    boolean canChargeKi();
    float getKiMultiplier();
    int getKiAdditive();
    float getKiCostMultiplier();
    boolean canBlock();
    float getBlockDexMultiplier();
    float getBlockCostMultiplier();
    int getBlockCooldown();

    // Setters
    void setCooldown(int cooldown) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setAttackMultiplier(float attackMultiplier) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setSweetSpot(float sweetSpot) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setCanChargeKi(boolean canChargeKi) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setKiMultiplier(float kiMultiplier) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setKiAdditive(int kiAdditive) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setKiCostMultiplier(float kiCostMultiplier) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setCanBlock(boolean canBlock) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setBlockDexMultiplier(float blockDexMultiplier) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setBlockCostMultiplier(float blockCostMultiplier) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setBlockCooldown(int blockCooldown) throws CachedWeaponStats.ProtectedWeaponTypeException;
    void setRange(float range) throws CachedWeaponStats.ProtectedWeaponTypeException;
}
