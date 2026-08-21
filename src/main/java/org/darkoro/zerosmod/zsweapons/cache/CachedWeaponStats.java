package org.darkoro.zerosmod.zsweapons.cache;

import kamkeel.npcs.util.AttributeItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import noppes.npcs.api.INbt;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.scripted.ScriptNbt;
import noppes.npcs.scripted.item.ScriptItemStack;
import org.darkoro.zerosmod.api.ScriptZSWeapon;
import org.darkoro.zerosmod.config.ConfigHandler;
import org.darkoro.zerosmod.zsweapons.ZSWeaponUtils;
import org.darkoro.zerosmod.zsweapons.attributes.AttributeBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponNBTKey.*;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponTypeId.*;

public class CachedWeaponStats implements ScriptZSWeapon {
    private ItemStack item;
    private String type = DEFAULT;
    private final boolean isPrimitive;

    private int cooldown = 10;
    private float attackPercent = 1.0F;
    private float range = 3;
    private float rangeSq = 9;
    private float sweetSpot = 1.5F;
    private boolean canChargeKi = false;
    private float kiPercent = 1.0F;
    private int kiAdditive = 0;
    private float kiCostPercent = 1.0F;
    private boolean canBlock = false;
    private float blockDexPercent = 1.0F;
    private float blockCostPercent = 1.0F;
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
                copy(loadedMap.get(FIST), false);
            }
        } else if(ZSWeaponUtils.hasZSWeaponTag(item)) {
            NBTTagCompound zsweaponNbt = item.getTagCompound().getCompoundTag(ZSWEAPON.key);
            String type = ConfigHandler.normalizeKey(zsweaponNbt.getString(TYPE.key));
            Map<String, CachedWeaponStats> loadedMap = ZSWeaponUtils.getLoadedStats();
            if (loadedMap == null || type.isEmpty()) {
                setToDefaultStats();
            } else if (type.equals(SPECIAL)) {
                readStatsFromItem();
            } else if (loadedMap.containsKey(type)) {
                copy(loadedMap.get(type), false);
            } else {
                setToDefaultStats();
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
    public void setType(String type) throws UnknownWeaponTypeException {
        if(type.equals(SPECIAL)) {
            readStatsFromItem();
            this.type = type;
        } else {
            CachedWeaponStats stats = ZSWeaponUtils.getLoadedStats().get(type);
            if(stats == null) {
                throw new UnknownWeaponTypeException(type);
            } else {
                this.type = type;
                copy(stats, true);
            }
        }
    }

    /**
     * Sets an item type to special
     */
    public void setSpecial() {
        this.type = SPECIAL;
        readStatsFromItem();
        saveToItem();
    }

    /**
     * Copies states from an existing weapon stats
     */
    public void copy(CachedWeaponStats stats, boolean applyStats) {
        if (stats == null) return;

        // General
        if(!isPrimitive) this.type = stats.getType();
        this.cooldown = stats.getCooldown();
        this.range = stats.getRange();
        this.rangeSq = range * range;
        this.attackPercent = stats.getAttackPercent();
        this.sweetSpot = stats.getSweetSpot();

        // Ki
        this.canChargeKi = stats.canChargeKi();
        this.kiPercent = stats.getKiPercent();
        this.kiAdditive = stats.getKiAdditive();
        this.kiCostPercent = stats.getKiCostPercent();

        // Block
        this.canBlock = stats.canBlock();
        this.blockDexPercent = stats.getBlockDexPercent();
        this.blockCostPercent = stats.getBlockCostPercent();
        this.blockCooldown = stats.getBlockCooldown();

        // Only display custom stats if item is not default type
        if(applyStats) {
            AttributeItemUtil.applyAttribute(item, AttributeBuilder.ATTACK_COOLDOWN_KEY, stats.getCooldown());
            AttributeItemUtil.applyAttribute(item, AttributeBuilder.RANGE_KEY, stats.getRange());
            AttributeItemUtil.applyAttribute(item, AttributeBuilder.ATTACK_PERCENT_KEY, stats.getAttackPercent());
            AttributeItemUtil.applyAttribute(item, AttributeBuilder.SWEET_SPOT_KEY, stats.getSweetSpot());
            AttributeItemUtil.applyAttribute(item, AttributeBuilder.KI_PERCENT_KEY, stats.getKiPercent());
            AttributeItemUtil.applyAttribute(item, AttributeBuilder.KI_ADDITIVE_KEY, stats.getKiAdditive());
            AttributeItemUtil.applyAttribute(item, AttributeBuilder.KI_COST_PERCENT_KEY, stats.getKiCostPercent());
            AttributeItemUtil.applyAttribute(item, AttributeBuilder.BLOCK_DEX_PERCENT_KEY, stats.getBlockDexPercent());
            AttributeItemUtil.applyAttribute(item, AttributeBuilder.BLOCK_COST_PERCENT_KEY, stats.getBlockCostPercent());
            AttributeItemUtil.applyAttribute(item, AttributeBuilder.BLOCK_COOLDOWN_KEY, stats.getBlockCooldown());

            saveToItem();
            updateItemLore();
        }
    }

    private void updateItemLore() {
        List<String> loreList = new ArrayList<>();
        loreList.add(EnumChatFormatting.RESET + "Weapon Type: " + type);
        loreList.add("");
        if(canBlock) loreList.add(EnumChatFormatting.RESET + "Can Block");
        if(canChargeKi) loreList.add(EnumChatFormatting.RESET + "Can Charge Ki");
        String[] lore = loreList.toArray(new String[0]);

        // Lore adding code grabbed from CNPC+
        NBTTagCompound compound = this.item.getTagCompound();
        if (compound == null) {
            this.item.setTagCompound(compound = new NBTTagCompound());
        }

        NBTTagCompound display = compound.getCompoundTag("display");
        NBTTagList nbtlist = new NBTTagList();
        String[] var5 = lore;
        int var6 = lore.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            String s = var5[var7];
            nbtlist.appendTag(new NBTTagString(s));
        }

        display.setTag("Lore", nbtlist);
        compound.setTag("display", display);
    }

    /**
     * Reads weapon stats from zsweapon nbt compound
     */
    public void readStatsFromItem() {
        CachedWeaponStats defaultStats = ZSWeaponUtils.getDefaultStats();
        NBTTagCompound compound = ZSWeaponUtils.getZSWeaponTag(item);
        if(compound == null)  compound = new NBTTagCompound();
        Map<String, Float> attributes = AttributeItemUtil.readAttributes(item);
        if(attributes == null || defaultStats == null) return;

        // General
        this.type = SPECIAL;
        this.cooldown = Math.round(attributes.getOrDefault(AttributeBuilder.ATTACK_COOLDOWN_KEY, (float) defaultStats.getCooldown()));
        this.attackPercent = attributes.getOrDefault(AttributeBuilder.ATTACK_PERCENT_KEY, defaultStats.getAttackPercent());
        this.sweetSpot = attributes.getOrDefault(AttributeBuilder.SWEET_SPOT_KEY, defaultStats.getSweetSpot());
        this.range = attributes.getOrDefault(AttributeBuilder.RANGE_KEY, defaultStats.getRange());
        this.rangeSq = range * range;

        // Ki
        this.canChargeKi = compound.hasKey(CAN_CHARGE.key) ? compound.getBoolean(CAN_CHARGE.key) : defaultStats.canChargeKi();
        this.kiPercent = attributes.getOrDefault(AttributeBuilder.KI_PERCENT_KEY, defaultStats.getKiPercent());
        this.kiAdditive = Math.round(attributes.getOrDefault(AttributeBuilder.KI_ADDITIVE_KEY, (float) defaultStats.getKiAdditive()));
        this.kiCostPercent = attributes.getOrDefault(AttributeBuilder.KI_COST_PERCENT_KEY, defaultStats.getKiCostPercent());

        // Block
        this.canBlock = compound.hasKey(CAN_BLOCK.key) ? compound.getBoolean(CAN_BLOCK.key) : defaultStats.canBlock();
        this.blockDexPercent = attributes.getOrDefault(AttributeBuilder.BLOCK_DEX_PERCENT_KEY, defaultStats.getBlockDexPercent());
        this.blockCostPercent = attributes.getOrDefault(AttributeBuilder.BLOCK_COST_PERCENT_KEY, defaultStats.getBlockCostPercent());
        this.blockCooldown = Math.round(attributes.getOrDefault(AttributeBuilder.BLOCK_COOLDOWN_KEY, (float) defaultStats.getBlockCooldown()));
    }

    /**
     * Saves current cached stats to a new NBT compound
     * Will I ever need this? Probably not.
     */
    public NBTTagCompound saveStatsToCompound() {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setString(TYPE.key, getType());
        nbt.setBoolean(CAN_CHARGE.key, canChargeKi());
        nbt.setBoolean(CAN_BLOCK.key, canBlock());

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
        copy(ZSWeaponUtils.getDefaultStats(), false);
    }

    // Getters
    public float getRange() { return range; }
    public float getRangeSq() { return rangeSq; }
    public int getCooldown() { return cooldown; }
    public ItemStack getItemStack() { return item; }
    public IItemStack getItem() { return new ScriptItemStack(item); }
    public String getType() { return type; }
    public float getAttackPercent() { return attackPercent; }
    public float getSweetSpot() { return sweetSpot; }
    public boolean canChargeKi() { return canChargeKi; }
    public float getKiPercent() { return kiPercent; }
    public int getKiAdditive() { return kiAdditive; }
    public float getKiCostPercent() { return kiCostPercent; }
    public boolean canBlock() { return canBlock; }
    public float getBlockDexPercent() { return blockDexPercent; }
    public float getBlockCostPercent() { return blockCostPercent; }
    public int getBlockCooldown() { return blockCooldown; }

    // Setters
    public void setCooldown(int cooldown) throws ProtectedWeaponTypeException {
        checkMutable();
        this.cooldown = cooldown;
    }

    public void setAttackPercent(float attackPercent) throws ProtectedWeaponTypeException {
        checkMutable();
        this.attackPercent = attackPercent;
    }

    public void setSweetSpot(float sweetSpot) throws ProtectedWeaponTypeException {
        checkMutable();
        this.sweetSpot = sweetSpot;
    }

    public void setCanChargeKi(boolean canChargeKi) throws ProtectedWeaponTypeException {
        checkMutable();
        this.canChargeKi = canChargeKi;
    }

    public void setKiPercent(float kiPercent) throws ProtectedWeaponTypeException {
        checkMutable();
        this.kiPercent = kiPercent;
    }

    public void setKiAdditive(int kiAdditive) throws ProtectedWeaponTypeException {
        this.kiAdditive = kiAdditive;
    }

    public void setCanBlock(boolean canBlock) throws ProtectedWeaponTypeException {
        checkMutable();
        this.canBlock = canBlock;
    }

    public void setBlockDexPercent(float blockDexPercent) throws ProtectedWeaponTypeException {
        checkMutable();
        this.blockDexPercent = blockDexPercent;
    }

    public void setBlockCostPercent(float blockCostPercent) throws ProtectedWeaponTypeException {
        checkMutable();
        this.blockCostPercent = blockCostPercent;
    }

    public void setBlockCooldown(int blockCooldown) throws ProtectedWeaponTypeException {
        checkMutable();
        this.blockCooldown = blockCooldown;
    }

    public void setKiCostPercent(float kiCostPercent) throws ProtectedWeaponTypeException {
        checkMutable();
        this.kiCostPercent = kiCostPercent;
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
        if(!isPrimitive) {
            throw new ProtectedWeaponTypeException(type);
        }
    }

    public static class ProtectedWeaponTypeException extends Exception {
        public ProtectedWeaponTypeException(String type) { super("Protected weapon type: " + type); }
    }

    public static class UnknownWeaponTypeException extends Exception {
        public UnknownWeaponTypeException(String type) { super("Unknown weapon type: " + type); }
    }
}
