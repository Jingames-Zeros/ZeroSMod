package org.darkoro.zerosmod.zsweapons;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CachedWeaponState {
    public ItemStack currentItem;
    public double remainingCooldown;
    public int cooldown;
    public float attackMultiplier;
    private float range;
    private float rangeSq;

    public boolean canChargeKi;
    public boolean canBlock;
    public float blockReduction;
    public float blockCostMultiplier;
    public float blockCooldown;

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
            readStatsFromCompound(item.getTagCompound().getCompoundTag("zsweapon"));
        } else {
            setToDefaultStats();
        }
    }

    /**
     * Copies states from an existing weapon state
     */
    public void copy(CachedWeaponState state) {
        this.cooldown = state.cooldown;
        setRange(state.getRange());
        this.attackMultiplier = state.attackMultiplier;
        this.canChargeKi = state.canChargeKi;
        this.canBlock = state.canBlock;
        this.blockReduction = state.blockReduction;
        this.blockCostMultiplier = state.blockCostMultiplier;
        this.blockCooldown = state.blockCooldown;
    }

    /**
     * Reads weapon stats from zsweapon nbt compound
     * @param compound - zsweapon compound
     */
    public void readStatsFromCompound(NBTTagCompound compound) {
        cooldown = compound.hasKey("attackcooldown") ? compound.getInteger("attackcooldown") : 20;
        attackMultiplier = compound.hasKey("attackmultiplier") ? compound.getFloat("attackmultiplier") : 1.0F;
        canChargeKi = compound.getBoolean("cancharge");
        canBlock = compound.getBoolean("canblock");
        blockReduction = compound.hasKey("blockreduction") ? compound.getFloat("blockreduction") : 0.5F;
        blockCostMultiplier = compound.hasKey("blockcostmultiplier") ? compound.getFloat("blockcostmultiplier") : 1.0F;
        blockCooldown = compound.hasKey("blockcooldown") ? compound.getInteger("blockcooldown") : cooldown;

        setRange(compound.hasKey("range") ? compound.getFloat("range") : 3.0F);
    }

    /**
     *
     */
    public NBTTagCompound saveStatsToCompound() { return null; }

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
        blockCooldown = cooldown;
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
     * Resets cooldown after blocking
     */
    public void blockEvent() {
        remainingCooldown = blockCooldown;
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
    public void setRange(float range) {
        this.range = range;
        this.rangeSq = range * range;
    }
    public float getRange() { return range; }
    public float getRangeSq() { return rangeSq; }
}
