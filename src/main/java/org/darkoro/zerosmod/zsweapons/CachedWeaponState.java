package org.darkoro.zerosmod.zsweapons;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CachedWeaponState {
    public ItemStack currentItem;
    public double remainingCooldown;
    public int cooldown;
    public float attackMultiplier;
    private int range;
    private int rangeSq;

    public boolean canChargeKi;
    public boolean canBlock;
    public float blockReduction;
    public float blockCostMultiplier;

    public CachedWeaponState() {
        setToDefaultStats();
    }

    /**
     * Updates stats and current item from new item and it's nbt
     * @param item new item
     */
    public void changeItem(ItemStack item) {
        currentItem = item;
        if(item != null && item.getTagCompound() != null && item.getTagCompound().hasKey("zsweapon")) {
            NBTTagCompound weaponCompound = item.getTagCompound().getCompoundTag("zsweapon");

            cooldown = weaponCompound.hasKey("attackcooldown") ? weaponCompound.getInteger("attackcooldown") : 20;
            setRange(weaponCompound.hasKey("range") ? weaponCompound.getInteger("range") : 3);
            attackMultiplier = weaponCompound.hasKey("attackmultiplier") ? weaponCompound.getFloat("attackmultiplier") : 1.0F;
            canChargeKi = weaponCompound.getBoolean("cancharge");
            canBlock = weaponCompound.getBoolean("canblock");
            blockReduction = weaponCompound.hasKey("blockreduction") ? weaponCompound.getFloat("blockreduction") : 0.5F;
            blockCostMultiplier = weaponCompound.hasKey("blockcostmultiplier") ? weaponCompound.getFloat("blockcostmultiplier") : 1.0F;

        } else {
            setToDefaultStats();
        }
    }

    public void blockEvent() {}

    /**
     * Sets stats to default values
     */
    public void setToDefaultStats() {
        cooldown = 20;
        setRange(3);
        attackMultiplier = 1.0F;
        canChargeKi = false;
        canBlock = false;
        blockReduction = 0.5F;
        blockCostMultiplier = 1.0F;
    }

    /**
     * Handles combat state ticks
     */
    public void tick() {
        if(remainingCooldown > 0) {
            remainingCooldown --;
        }
    }

    /**
     * Handles combat state attacks
     */
    public void handleAttack() {
        resetCooldown();
    }

    /**
     * Triggers an attack cooldown
     */
    public void resetCooldown() {
        remainingCooldown = cooldown;
    }

    /**
     * Updates range and rangeSq
     */
    public void setRange(int range) {
        this.range = range;
        this.rangeSq = range * range;
    }
    public int getRange() { return range; }
    public int getRangeSq() { return rangeSq; }
}
