package org.darkoro.zerosmod.zsweapons.cache;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.INbt;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.scripted.ScriptNbt;
import noppes.npcs.scripted.item.ScriptItemStack;
import org.darkoro.zerosmod.api.ScriptZSWeapon;
import org.darkoro.zerosmod.config.ConfigHandler;
import org.darkoro.zerosmod.zsweapons.ZSWeaponUtils;
import java.util.Map;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponNBTKey.*;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponNBTKey.KI_ADDITIVE;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponTypeId.*;

public class CachedWeaponStats implements ScriptZSWeapon {
    private ItemStack item;
    private String type = DEFAULT;
    private final boolean isPrimitive;
    private boolean isCustom;

    private int cooldown = 10;
    private float attackMultiplier = 1.0F;
    private float range = 3;
    private float rangeSq = 9;
    private float sweetSpot = 1.5F;
    private boolean canChargeKi = false;
    private float kiMultiplier = 1.0F;
    private int kiAdditive = 0;
    private float kiCostMultiplier = 1.0F;
    private boolean canBlock = false;
    private float blockDexMultiplier = 1.0F;
    private float blockCostMultiplier = 1.0F;
    private int blockCooldown = cooldown;

    /**
     * Primitive constructor
     * @param type weapon type
     */
    public CachedWeaponStats(String type) {
        this.type = type;
        this.isPrimitive = true;
    }

    /**
     * Regular constructor
     */
    public CachedWeaponStats() {
        this.isPrimitive = false;
        setToDefaultStats();
    }

    /**
     * Updates stats and current item from new item and it's nbt
     * @param item new item
     */
    public void changeItem(ItemStack item) {
        this.item = item;
        // Read type from item tag
        if(item == null) {
            Map<String, CachedWeaponStats> loadedMap = ZSWeaponUtils.getLoadedStats();
            if(loadedMap != null) {
                copy(loadedMap.get(FIST));
            }
        } else if(ZSWeaponUtils.hasZSWeaponTag(item)) {
            NBTTagCompound zsweaponNbt = item.getTagCompound().getCompoundTag(ZSWEAPON.key);
            String type = ConfigHandler.normalizeKey(zsweaponNbt.getString(TYPE.key));
            Map<String, CachedWeaponStats> loadedMap = ZSWeaponUtils.getLoadedStats();
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
     * Sets item type from preset types
     * @param type Type name
     */
    public void setType(String type) {
        this.type = type;
        if(type.equals(SPECIAL)) {
            readStatsFromCompound(ZSWeaponUtils.getZSWeaponTag(item));
        } else {
            copy(ZSWeaponUtils.getLoadedStats().get(type));
        }
    }

    /**
     * Sets an item type to special
     */
    public void setSpecial() {
        this.type = SPECIAL;
        readStatsFromCompound(ZSWeaponUtils.getZSWeaponTag(item));
        saveToItem();
    }

    /**
     * Copies states from an existing weapon stats
     */
    public void copy(CachedWeaponStats stats) {
        if (stats == null) return;

        // General
        if(!isPrimitive) this.type = stats.getType();
        this.cooldown = stats.getCooldown();
        this.range = stats.getRange();
        this.rangeSq = range * range;
        this.attackMultiplier = stats.getAttackMultiplier();
        this.sweetSpot = stats.getSweetSpot();

        // Ki
        this.canChargeKi = stats.canChargeKi();
        this.kiMultiplier = stats.getKiMultiplier();
        this.kiAdditive = stats.getKiAdditive();
        this.kiCostMultiplier = stats.getKiCostMultiplier();

        // Block
        this.canBlock = stats.canBlock();
        this.blockDexMultiplier = stats.getBlockDexMultiplier();
        this.blockCostMultiplier = stats.getBlockCostMultiplier();
        this.blockCooldown = stats.getBlockCooldown();
    }

    /**
     * Reads weapon stats from zsweapon nbt compound
     */
    public void readStatsFromCompound(NBTTagCompound compound) {
        CachedWeaponStats defaultStats = ZSWeaponUtils.getDefaultStats();
        if(compound == null || defaultStats == null) return;

        // General
        this.type = compound.hasKey(TYPE.key) ? compound.getString(TYPE.key) : defaultStats.getType();
        this.cooldown = compound.hasKey(ATTACK_COOLDOWN.key) ? compound.getInteger(ATTACK_COOLDOWN.key) : defaultStats.getCooldown();
        this.attackMultiplier = compound.hasKey(ATTACK_MULTIPLIER.key) ? compound.getFloat(ATTACK_MULTIPLIER.key) : defaultStats.getAttackMultiplier();
        this.sweetSpot = compound.hasKey(SWEET_SPOT.key) ? compound.getFloat(SWEET_SPOT.key) : defaultStats.getSweetSpot();
        this.range = compound.hasKey(RANGE.key) ? compound.getFloat(RANGE.key) : 3.0F;
        this.rangeSq = range * range;

        // Ki
        this.canChargeKi = compound.hasKey(CAN_CHARGE.key) ? compound.getBoolean(CAN_CHARGE.key) : defaultStats.canChargeKi();
        this.kiMultiplier = compound.hasKey(KI_MULTIPLIER.key) ? compound.getFloat(KI_MULTIPLIER.key) : defaultStats.getKiMultiplier();
        this.kiAdditive = compound.hasKey(KI_ADDITIVE.key) ? compound.getInteger(KI_ADDITIVE.key) : defaultStats.getKiAdditive();
        this.kiCostMultiplier = compound.hasKey(KI_COST_MULTIPLIER.key) ? compound.getFloat(KI_COST_MULTIPLIER.key) : defaultStats.getKiCostMultiplier();

        // Block
        this.canBlock = compound.hasKey(CAN_BLOCK.key) ? compound.getBoolean(CAN_BLOCK.key) : defaultStats.canBlock();
        this.blockDexMultiplier = compound.hasKey(BLOCK_DEX_MULTIPLIER.key) ? compound.getFloat(BLOCK_DEX_MULTIPLIER.key) : defaultStats.getBlockDexMultiplier();
        this.blockCostMultiplier = compound.hasKey(BLOCK_COST_MULTIPLIER.key) ? compound.getFloat(BLOCK_COST_MULTIPLIER.key) : defaultStats.getBlockCostMultiplier();
        this.blockCooldown = compound.hasKey(BLOCK_COOLDOWN.key) ? compound.getInteger(BLOCK_COOLDOWN.key) : defaultStats.getBlockCooldown();
    }

    /**
     * Saves current cached stats to a new NBT compound
     * Will I ever need this? Probably not.
     */
    public NBTTagCompound saveStatsToCompound() {
        NBTTagCompound nbt = new NBTTagCompound();

        // General
        nbt.setString(TYPE.key, getType());
        nbt.setInteger(ATTACK_COOLDOWN.key, getCooldown());
        nbt.setFloat(ATTACK_MULTIPLIER.key, getAttackMultiplier());
        nbt.setFloat(SWEET_SPOT.key, getSweetSpot());
        nbt.setFloat(RANGE.key, getRange());

        // Ki
        nbt.setBoolean(CAN_CHARGE.key, canChargeKi());
        nbt.setFloat(KI_MULTIPLIER.key, getKiMultiplier());
        nbt.setFloat(KI_ADDITIVE.key, getKiAdditive());
        nbt.setFloat(KI_COST_MULTIPLIER.key, getKiCostMultiplier());

        // Block
        nbt.setBoolean(CAN_BLOCK.key, canBlock());
        nbt.setFloat(BLOCK_DEX_MULTIPLIER.key, getBlockDexMultiplier());
        nbt.setFloat(BLOCK_COST_MULTIPLIER.key, getBlockCostMultiplier());
        nbt.setInteger(BLOCK_COOLDOWN.key, getBlockCooldown());

        return nbt;
    }

    /**
     * Saves item stats to item's nbt
     */
    public void saveToItem() {
        NBTTagCompound nbt = item.getTagCompound();
        if(nbt == null) {
            nbt = new NBTTagCompound();
        }
        nbt.setTag(ZSWEAPON.key, saveStatsToCompound());
        item.setTagCompound(nbt);
    }

    /**
     * Saves current cached stats to a new NBT compound
     * Will I ever need this? Probably not.
     */
    public INbt saveStatsToNbt() {
        return new ScriptNbt(saveStatsToCompound());
    }

    /**
     * Sets stats to default values
     */
    public void setToDefaultStats() {
        copy(ZSWeaponUtils.getDefaultStats());
    }

    // Getters
    public float getRange() { return range; }
    public float getRangeSq() { return rangeSq; }
    public int getCooldown() { return cooldown; }
    public ItemStack getItemStack() { return item; }
    public IItemStack getItem() { return new ScriptItemStack(item); }
    public String getType() { return type; }
    public float getAttackMultiplier() { return attackMultiplier; }
    public float getSweetSpot() { return sweetSpot; }
    public boolean canChargeKi() { return canChargeKi; }
    public float getKiMultiplier() { return kiMultiplier; }
    public int getKiAdditive() { return kiAdditive; }
    public float getKiCostMultiplier() { return kiCostMultiplier; }
    public boolean canBlock() { return canBlock; }
    public float getBlockDexMultiplier() { return blockDexMultiplier; }
    public float getBlockCostMultiplier() { return blockCostMultiplier; }
    public int getBlockCooldown() { return blockCooldown; }

    // Setters
    public void setCooldown(int cooldown) throws ProtectedWeaponTypeException {
        checkMutable();
        this.cooldown = cooldown;
    }

    public void setAttackMultiplier(float attackMultiplier) throws ProtectedWeaponTypeException {
        checkMutable();
        this.attackMultiplier = attackMultiplier;
    }

    public void setSweetSpot(float sweetSpot) throws ProtectedWeaponTypeException {
        checkMutable();
        this.sweetSpot = sweetSpot;
    }

    public void setCanChargeKi(boolean canChargeKi) throws ProtectedWeaponTypeException {
        checkMutable();
        this.canChargeKi = canChargeKi;
    }

    public void setKiMultiplier(float kiMultiplier) throws ProtectedWeaponTypeException {
        checkMutable();
        this.kiMultiplier = kiMultiplier;
    }

    public void setKiAdditive(int kiAdditive) throws ProtectedWeaponTypeException {
        this.kiAdditive = kiAdditive;
    }

    public void setCanBlock(boolean canBlock) throws ProtectedWeaponTypeException {
        checkMutable();
        this.canBlock = canBlock;
    }

    public void setBlockDexMultiplier(float blockDexMultiplier) throws ProtectedWeaponTypeException {
        checkMutable();
        this.blockDexMultiplier = blockDexMultiplier;
    }

    public void setBlockCostMultiplier(float blockCostMultiplier) throws ProtectedWeaponTypeException {
        checkMutable();
        this.blockCostMultiplier = blockCostMultiplier;
    }

    public void setBlockCooldown(int blockCooldown) throws ProtectedWeaponTypeException {
        checkMutable();
        this.blockCooldown = blockCooldown;
    }

    public void setKiCostMultiplier(float kiCostMultiplier) throws ProtectedWeaponTypeException {
        checkMutable();
        this.kiCostMultiplier = kiCostMultiplier;
    }

    public void setRange(float range) throws ProtectedWeaponTypeException {
        checkMutable();
        this.range = range;
        this.rangeSq = range * range;
    }

    /**
     * Throws an error if a weapon is not custom or primitive
     * @throws ProtectedWeaponTypeException .
     */
    private void checkMutable() throws ProtectedWeaponTypeException {
        if(!isCustom && !isPrimitive) {
            throw new ProtectedWeaponTypeException(type);
        }
    }

    public static class ProtectedWeaponTypeException extends Exception {
        public ProtectedWeaponTypeException(String type) { super("Protected weapon type: " + type); }
    }
}
