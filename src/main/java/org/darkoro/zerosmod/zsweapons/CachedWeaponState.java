package org.darkoro.zerosmod.zsweapons;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.darkoro.zerosmod.config.ConfigHandler;
import org.darkoro.zerosmod.config.ServerWeaponConfig;
import org.darkoro.zerosmod.zsweapons.client.ClientWeaponHandler;

import java.util.Map;

import static org.darkoro.zerosmod.zsweapons.WeaponNBTKey.*;
import static org.darkoro.zerosmod.zsweapons.WeaponTypeId.*;


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
        if(item != null && item.getTagCompound() != null && item.getTagCompound().hasKey(ZSWEAPON.key)) {
            NBTTagCompound zsweaponNbt = item.getTagCompound().getCompoundTag(ZSWEAPON.key);
            String type = ConfigHandler.normalizeKey(zsweaponNbt.getString(TYPE.key));
            Map<String, CachedWeaponState> loadedMap = (FMLCommonHandler.instance().getSide().isClient() ? ClientWeaponHandler.loadedWeaponStates : ServerWeaponConfig.loadedWeaponStates);
            if(loadedMap == null) {
                setToDefaultStats();
                return;
            }
            if(!type.isEmpty() && loadedMap.containsKey(type)) {
                copy(loadedMap.get(type));
            } else if(type.equals(SPECIAL)) {
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
        cooldown = compound.hasKey(ATTACK_COOLDOWN.key) ? compound.getInteger(ATTACK_COOLDOWN.key) : 20;
        attackMultiplier = compound.hasKey(ATTACK_MULTIPLIER.key) ? compound.getFloat(ATTACK_MULTIPLIER.key) : 1.0F;
        sweetSpot = compound.hasKey(SWEET_SPOT.key) ? compound.getFloat(SWEET_SPOT.key) : 0.0F;
        canChargeKi = !compound.hasKey(CAN_CHARGE.key) || compound.getBoolean(CAN_CHARGE.key);
        kiMultiplier = compound.hasKey(KI_MULTIPLIER.key) ? compound.getFloat(KI_MULTIPLIER.key) : 1.0F;
        kiAdditive = compound.hasKey(KI_ADDITIVE.key) ? compound.getInteger(KI_ADDITIVE.key) : 0;
        canBlock = !compound.hasKey(CAN_BLOCK.key) || compound.getBoolean(CAN_BLOCK.key);
        blockReduction = compound.hasKey(BLOCK_REDUCTION.key) ? compound.getFloat(BLOCK_REDUCTION.key) : 0.5F;
        blockCostMultiplier = compound.hasKey(BLOCK_COST_MULTIPLIER.key) ? compound.getFloat(BLOCK_COST_MULTIPLIER.key) : 1.0F;
        blockCooldown = compound.hasKey(BLOCK_COOLDOWN.key) ? compound.getInteger(BLOCK_COOLDOWN.key) : cooldown;

        setRange(compound.hasKey(RANGE.key) ? compound.getFloat(RANGE.key) : 3.0F);
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
        this.type = DEFAULT;
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
