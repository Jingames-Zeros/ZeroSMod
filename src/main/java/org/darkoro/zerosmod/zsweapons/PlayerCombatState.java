package org.darkoro.zerosmod.zsweapons;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerCombatState {
    public ItemStack currentItem;
    public int remainingCooldown = 0;
    public int cooldown = 20;
    public float attackMultiplier = 1.0F;
    private int range = 3;
    private int rangeSq = 9;

    public PlayerCombatState() {}

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
        } else {
            cooldown = 20;
            setRange(3);
            attackMultiplier = 1.0F;
        }
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
