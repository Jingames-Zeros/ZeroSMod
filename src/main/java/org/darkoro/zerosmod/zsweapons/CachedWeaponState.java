package org.darkoro.zerosmod.zsweapons;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.darkoro.zerosmod.config.ConfigHandler;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponNBTKey.*;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponTypeId.*;
import java.util.Map;

public class CachedWeaponState {
    public ItemStack currentItem;
    public String type = DEFAULT;
    public double remainingCooldown = 10;
    public int cooldown = 10;
    public float attackMultiplier = 1.0F;
    private float range = 3;
    private float rangeSq = 9;
    public float sweetSpot = 1.5F;

    public boolean canChargeKi = false;
    public float kiMultiplier = 1.0F;
    public int kiAdditive = 0;

    public boolean canBlock = false;
    public float blockDexMultiplier = 1.0F;
    public float blockCostMultiplier = 1.0F;
    public int blockCooldown = cooldown;

    public CachedWeaponState(String type) { this.type = type; }
    public CachedWeaponState() { setToDefaultStats(); }

    /**
     * Updates stats and current item from new item and it's nbt
     * @param item new item
     */
    public void changeItem(ItemStack item) {
        currentItem = item;
        // Read type from item tag
        if(ZSWeaponUtils.hasZSWeaponTag(item)) {
            NBTTagCompound zsweaponNbt = item.getTagCompound().getCompoundTag(ZSWEAPON.key);
            String type = ConfigHandler.normalizeKey(zsweaponNbt.getString(TYPE.key));
            Map<String, CachedWeaponState> loadedMap = ZSWeaponUtils.getLoadedStates();
            if (loadedMap == null || type.isEmpty()) {
                setToDefaultStats();
            } else if (type.equals(SPECIAL)) {
                readStatsFromCompound(zsweaponNbt);
            } else if (loadedMap.containsKey(type)) {
                copy(loadedMap.get(type));
            }
        }
        // Item doesn't have tag
        else {
            setToDefaultStats();
        }
    }

    /**
     * Copies states from an existing weapon state
     */
    public void copy(CachedWeaponState state) {
        if(state == null) return;

        // General
        this.type = state.type;
        this.cooldown = state.cooldown;
        setRange(state.getRange());
        this.attackMultiplier = state.attackMultiplier;
        this.sweetSpot = state.sweetSpot;

        // Ki
        this.canChargeKi = state.canChargeKi;
        this.kiMultiplier = state.kiMultiplier;
        this.kiAdditive = state.kiAdditive;

        // Block
        this.canBlock = state.canBlock;
        this.blockDexMultiplier = state.blockDexMultiplier;
        this.blockCostMultiplier = state.blockCostMultiplier;
        this.blockCooldown = state.blockCooldown;
    }

    /**
     * Reads weapon stats from zsweapon nbt compound
     */
    public void readStatsFromCompound(NBTTagCompound compound) {
        CachedWeaponState defaultStats = ZSWeaponUtils.getDefaultState();
        if(defaultStats == null) return;

        // General
        type = compound.hasKey(TYPE.key) ? compound.getString(TYPE.key) : defaultStats.type;
        cooldown = compound.hasKey(ATTACK_COOLDOWN.key) ? compound.getInteger(ATTACK_COOLDOWN.key) : defaultStats.cooldown;
        attackMultiplier = compound.hasKey(ATTACK_MULTIPLIER.key) ? compound.getFloat(ATTACK_MULTIPLIER.key) : defaultStats.attackMultiplier;
        sweetSpot = compound.hasKey(SWEET_SPOT.key) ? compound.getFloat(SWEET_SPOT.key) : defaultStats.sweetSpot;
        setRange(compound.hasKey(RANGE.key) ? compound.getFloat(RANGE.key) : 3.0F);

        // Ki
        canChargeKi = compound.hasKey(CAN_CHARGE.key) ? compound.getBoolean(CAN_CHARGE.key) : defaultStats.canChargeKi;
        kiMultiplier = compound.hasKey(KI_MULTIPLIER.key) ? compound.getFloat(KI_MULTIPLIER.key) : defaultStats.kiMultiplier;
        kiAdditive = compound.hasKey(KI_ADDITIVE.key) ? compound.getInteger(KI_ADDITIVE.key) : defaultStats.kiAdditive;

        // Block
        canBlock = compound.hasKey(CAN_BLOCK.key) ? compound.getBoolean(CAN_BLOCK.key) : defaultStats.canBlock;
        blockDexMultiplier = compound.hasKey(BLOCK_DEX_MULTIPLIER.key) ? compound.getFloat(BLOCK_DEX_MULTIPLIER.key) : defaultStats.blockDexMultiplier;
        blockCostMultiplier = compound.hasKey(BLOCK_COST_MULTIPLIER.key) ? compound.getFloat(BLOCK_COST_MULTIPLIER.key) : defaultStats.blockCostMultiplier;
        blockCooldown = compound.hasKey(BLOCK_COOLDOWN.key) ? compound.getInteger(BLOCK_COOLDOWN.key) : defaultStats.blockCooldown;
    }

    /**
     * Saves current cached stats to a new NBT compound
     * Will I ever need this? Probably not.
     */
    public NBTTagCompound saveStatsToCompound() { 
        NBTTagCompound nbt = new NBTTagCompound();
        // General
        nbt.setString(TYPE.key, type);
        nbt.setInteger(ATTACK_COOLDOWN.key, cooldown);
        nbt.setFloat(ATTACK_MULTIPLIER.key, attackMultiplier);
        nbt.setFloat(SWEET_SPOT.key, sweetSpot);
        nbt.setFloat(RANGE.key, range);
        
        // Ki
        nbt.setBoolean(CAN_CHARGE.key, canChargeKi);
        nbt.setFloat(KI_MULTIPLIER.key, kiMultiplier);
        nbt.setFloat(KI_ADDITIVE.key, kiAdditive);
        
        // Block
        nbt.setBoolean(CAN_BLOCK.key, canBlock);
        nbt.setFloat(BLOCK_DEX_MULTIPLIER.key, blockDexMultiplier);
        nbt.setFloat(BLOCK_COST_MULTIPLIER.key, blockCostMultiplier);
        nbt.setInteger(BLOCK_COOLDOWN.key, blockCooldown);
        
        return nbt;
    }

    /**D
     * Sets stats to default values
     */
    public void setToDefaultStats() {
        copy(ZSWeaponUtils.getDefaultState());
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
