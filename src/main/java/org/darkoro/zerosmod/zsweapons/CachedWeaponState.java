package org.darkoro.zerosmod.zsweapons;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.darkoro.zerosmod.config.ConfigHandler;
import org.darkoro.zerosmod.config.ServerWeaponConfig;
import org.darkoro.zerosmod.zsweapons.client.ClientWeaponHandler;

import java.util.Map;


public class CachedWeaponState {
    public ItemStack currentItem;
    public String type;
    public double remainingCooldown;
    public int cooldown;
    public float attackMultiplier;
    private float range;
    private float rangeSq;
    public float sweetSpot;

    public boolean canChargeKi;
    public float kiMultiplier;
    public int kiAdditive;

    public boolean canBlock;
    public float blockReduction;
    public float blockCostMultiplier;
    public int blockCooldown;

    public CachedWeaponState(String type) {
        applyBuiltInDefaults();
        this.type = type;
    }
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
            NBTTagCompound zsweaponNbt = item.getTagCompound().getCompoundTag("zsweapon");
            String type = ConfigHandler.normalizeKey(zsweaponNbt.getString("type"));
            Map<String, CachedWeaponState> loadedMap = (FMLCommonHandler.instance().getSide().isClient() ? ClientWeaponHandler.loadedWeaponStates : ServerWeaponConfig.loadedWeaponStates);
            if(loadedMap == null) {
                setToDefaultStats();
                return;
            }
            if(!type.isEmpty() && loadedMap.containsKey(type)) {
                copy(loadedMap.get(type));
            } else if(type.equals("special")) {
                readStatsFromCompound(zsweaponNbt);
            } else {
                setToDefaultStats();
            }
        } else {
            setToDefaultStats();
        }
    }

    /**
     * Copies states from an existing weapon state
     */
    public void copy(CachedWeaponState state) {
        if(state == null) return;
        this.type = state.type;
        this.cooldown = state.cooldown;
        setRange(state.getRange());
        this.attackMultiplier = state.attackMultiplier;
        this.sweetSpot = state.sweetSpot;
        this.canChargeKi = state.canChargeKi;
        this.kiMultiplier = state.kiMultiplier;
        this.kiAdditive = state.kiAdditive;
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
        sweetSpot = compound.hasKey("sweetspot") ? compound.getFloat("sweetspot") : 0.0F;
        canChargeKi = !compound.hasKey("cancharge") || compound.getBoolean("cancharge");
        kiMultiplier = compound.hasKey("kimultiplier") ? compound.getFloat("kimultiplier") : 1.0F;
        kiAdditive = compound.hasKey("kiadditive") ? compound.getInteger("kiadditive") : 0;
        canBlock = !compound.hasKey("canblock") || compound.getBoolean("canblock");
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
        applyBuiltInDefaults();
        if(FMLCommonHandler.instance().getSide().isClient()) {
            copy(ClientWeaponHandler.defaultWeaponState);
        } else {
            copy(ServerWeaponConfig.defaultWeaponState);
        }
    }

    private void applyBuiltInDefaults() {
        this.type = "default";
        this.cooldown = 20;
        this.attackMultiplier = 1.0F;
        setRange(3.0F);
        this.sweetSpot = 0.0F;
        this.canChargeKi = true;
        this.kiMultiplier = 1.0F;
        this.kiAdditive = 0;
        this.canBlock = true;
        this.blockReduction = 1.0F;
        this.blockCostMultiplier = 1.0F;
        this.blockCooldown = this.cooldown;
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
