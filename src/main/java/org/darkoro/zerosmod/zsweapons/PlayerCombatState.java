package org.darkoro.zerosmod.zsweapons;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerCombatState {
    public ItemStack currentItem;
    public int remainingCooldown = 0;
    public int cooldown = 20;
    public int range = 3;

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
            range = weaponCompound.hasKey("range") ? weaponCompound.getInteger("range") : 3;
        } else {
            cooldown = 20;
            range = 3;
        }
        resetCooldown();
    }

    /**
     * Handles combat state ticks
     */
    public void tick() {
        if(remainingCooldown > 0) remainingCooldown --;
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
}
