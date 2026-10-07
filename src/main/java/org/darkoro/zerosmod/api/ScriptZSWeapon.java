package org.darkoro.zerosmod.api;

import noppes.npcs.api.item.IItemStack;

public interface ScriptZSWeapon {
    /**
     * Sets item weapon type
     * @param type Valid string weapon type
     */
    void setType(String type) throws UnknownWeaponTypeException;

    /**
     * Sets item to default stats
     */
    void setToDefaultStats();

    /**
     * Sets item to special allowing for stat editing
     */
    void setSpecial();

    // Getters
    int getLevelReq();
    float getRange();
    float getRangeSq();
    int getCooldown();
    IItemStack getItem();
    String getType();
    String getFormattedType();
    float getAttackPercent();
    int getAttackAdditive();
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
    void setLevelReq(int levelReq);
    void setKiAdditive(int kiAdditive);
    void setAttackAdditive(int attack);
    void setCooldown(int cooldown) throws ProtectedWeaponTypeException;
    void setAttackPercent(float attackPercent) throws ProtectedWeaponTypeException;
    void setSweetSpot(float sweetSpot) throws ProtectedWeaponTypeException;
    void setCanChargeKi(boolean canChargeKi) throws ProtectedWeaponTypeException;
    void setKiPercent(float kiPercent) throws ProtectedWeaponTypeException;
    void setKiCostPercent(float kiCostPercent) throws ProtectedWeaponTypeException;
    void setCanBlock(boolean canBlock) throws ProtectedWeaponTypeException;
    void setBlockDexPercent(float blockDexPercent) throws ProtectedWeaponTypeException;
    void setBlockCostPercent(float blockCostPercent) throws ProtectedWeaponTypeException;
    void setBlockCooldown(int blockCooldown) throws ProtectedWeaponTypeException;
    void setRange(float range) throws ProtectedWeaponTypeException;
    void setFormattedType(String formattedType) throws ProtectedWeaponTypeException;
}
