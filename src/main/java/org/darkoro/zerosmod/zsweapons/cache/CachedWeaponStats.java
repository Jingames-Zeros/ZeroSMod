package org.darkoro.zerosmod.zsweapons.cache;

import kamkeel.npcs.CustomAttributes;
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
import java.util.*;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponNBTKey.*;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponTypeId.*;

public class CachedWeaponStats implements ScriptZSWeapon {
    private static final String WEAPON_TYPE_PREFIX = EnumChatFormatting.RESET + "Weapon Type: ";
    private static final String LEVEL_REQ_PREFIX = EnumChatFormatting.RESET + "Level Req: ";

    // General
    private ItemStack item;
    private String type = DEFAULT;
    private int levelReq = 0;
    private String formattedType = EnumChatFormatting.RESET.toString() + EnumChatFormatting.WHITE;
    private final boolean isPrimitive;

    // Melee
    private int cooldown = 10;
    private float attackPercent = 1.0F;
    private int attackAdditive = 0;

    // Range
    private float range = 3;
    private float rangeSq = 9;
    private float sweetSpot = 1.5F;

    // Ki
    private boolean canChargeKi = false;
    private float kiPercent = 1.0F;
    private int kiAdditive = 0;
    private float kiCostPercent = 1.0F;

    // Block
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
            return;
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
        Map<String, Float> stats = AttributeItemUtil.readAttributes(item);
        this.attackAdditive = Math.round(stats.getOrDefault(CustomAttributes.MAIN_ATTACK_KEY, 0.0F));
        this.kiAdditive = Math.round(stats.getOrDefault(AttributeBuilder.KI_ADDITIVE_KEY, 0.0F));
    }

    /**
     * Sets item type from preset types
     * @param type Type name
     */
    public void setType(String type) throws UnknownWeaponTypeException {
        type = type.toLowerCase().trim();
        if(type.equals(SPECIAL)) {
            setSpecial();
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
        updateItemLore();
    }

    /**
     * Copies states from an existing weapon stats
     */
    public void copy(CachedWeaponStats stats, boolean applyStats) {
        if (stats == null) return;

        // General
        if(!isPrimitive) this.type = stats.getType();
        if(!isPrimitive) this.formattedType = stats.getFormattedType();
        this.cooldown = stats.getCooldown();
        this.range = stats.getRange();
        this.rangeSq = range * range;
        this.attackPercent = stats.getAttackPercent();
        this.sweetSpot = stats.getSweetSpot();

        // Ki
        this.canChargeKi = stats.canChargeKi();
        this.kiPercent = stats.getKiPercent();
        this.kiCostPercent = stats.getKiCostPercent();

        // Block
        this.canBlock = stats.canBlock();
        this.blockDexPercent = stats.getBlockDexPercent();
        this.blockCostPercent = stats.getBlockCostPercent();
        this.blockCooldown = stats.getBlockCooldown();

        // Only display custom stats if item is not default type

        if (applyStats) {
            CachedWeaponStats defaultStats = ZSWeaponUtils.getDefaultStats();
            if (defaultStats == null) return;

            saveToItem();
            updateItemLore();
        }
    }

    /**
     * Helper to apply attributes
     * @param key Attribute string key
     * @param value Value to set attribute to
     * @param defaultValue Default stat value
     */
    private void applyIfDifferent(String key, float value, float defaultValue) {
        if (value != defaultValue) {
            AttributeItemUtil.applyAttribute(item, key, value);
        } else {
            AttributeItemUtil.removeAttribute(item, key);
        }
    }

    /**
     * Updates item lore to include the new weapon type and block/ki stats
     */
    private void updateItemLore() {
        // Grab existing lore
        NBTTagCompound compound = item.getTagCompound();
        if (compound == null) {
            item.setTagCompound(compound = new NBTTagCompound());
        }

        NBTTagCompound display = compound.getCompoundTag("display");
        NBTTagList oldLore = display.hasKey("Lore") ? display.getTagList("Lore", 8) : new NBTTagList();

        // Remove existing Weapon type lines
        NBTTagList newLore = new NBTTagList();
        for(int i = 0; i < oldLore.tagCount(); i++) {
            String line = oldLore.getStringTagAt(i);
            if(
                    line.startsWith(WEAPON_TYPE_PREFIX) ||
                    line.startsWith(LEVEL_REQ_PREFIX)
               ) {
                continue;
            }
            newLore.appendTag(new NBTTagString(line));
        }

        // Add new weapon type lines
        newLore.appendTag(new NBTTagString(WEAPON_TYPE_PREFIX + formattedType));
        newLore.appendTag(new NBTTagString(LEVEL_REQ_PREFIX + levelReq));

        display.setTag("Lore", newLore);
        compound.setTag("display", display);
    }

    /**
     * Reads weapon stats from zsweapon nbt compound
     */
    public void readStatsFromItem() {
        CachedWeaponStats defaultStats = ZSWeaponUtils.getDefaultStats();
        Map<String, Float> attributes = AttributeItemUtil.readAttributes(item);
        if(defaultStats == null) return;

        // General
        this.type = SPECIAL;
        this.cooldown = Math.round(attributes.getOrDefault(AttributeBuilder.ATTACK_COOLDOWN_KEY, (float) defaultStats.getCooldown()));
        this.attackPercent = attributes.getOrDefault(AttributeBuilder.ATTACK_PERCENT_KEY, defaultStats.getAttackPercent());
        this.attackAdditive = Math.round(attributes.getOrDefault(CustomAttributes.MAIN_ATTACK_KEY, 0.0F));
        this.sweetSpot = attributes.getOrDefault(AttributeBuilder.SWEET_SPOT_KEY, defaultStats.getSweetSpot());
        this.range = attributes.getOrDefault(AttributeBuilder.RANGE_KEY, defaultStats.getRange());
        this.rangeSq = range * range;

        // Ki
        this.canChargeKi = attributes.containsKey(AttributeBuilder.CAN_CHARGE_KI_KEY);
        this.kiAdditive = Math.round(attributes.getOrDefault(AttributeBuilder.KI_ADDITIVE_KEY, (float) defaultStats.getKiAdditive()));
        this.kiPercent = attributes.getOrDefault(AttributeBuilder.KI_PERCENT_KEY, defaultStats.getKiPercent());
        this.kiCostPercent = attributes.getOrDefault(AttributeBuilder.KI_COST_PERCENT_KEY, defaultStats.getKiCostPercent());

        // Block
        this.canBlock = attributes.containsKey(AttributeBuilder.CAN_BLOCK_KEY);
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
        nbt.setInteger("power", levelReq);
        item.setTagCompound(nbt);

        CachedWeaponStats defaultStats = ZSWeaponUtils.getDefaultStats();
        if (defaultStats == null) return;

        applyIfDifferent(CustomAttributes.MAIN_ATTACK_KEY, getAttackAdditive(), 0);
        applyIfDifferent(AttributeBuilder.KI_ADDITIVE_KEY, getKiAdditive(), 0);
        AttributeItemUtil.applyAttribute(item, AttributeBuilder.ATTACK_COOLDOWN_KEY, getCooldown());
        AttributeItemUtil.applyAttribute(item, AttributeBuilder.RANGE_KEY, getRange());
        AttributeItemUtil.applyAttribute(item, AttributeBuilder.SWEET_SPOT_KEY, getSweetSpot());
        if(canBlock()) AttributeItemUtil.applyAttribute(item, AttributeBuilder.CAN_BLOCK_KEY, 1);
        else AttributeItemUtil.removeAttribute(item, AttributeBuilder.CAN_BLOCK_KEY);
        if(canChargeKi()) AttributeItemUtil.applyAttribute(item, AttributeBuilder.CAN_CHARGE_KI_KEY, 1);
        else AttributeItemUtil.removeAttribute(item, AttributeBuilder.CAN_CHARGE_KI_KEY);
        applyIfDifferent(AttributeBuilder.ATTACK_PERCENT_KEY, getAttackPercent(), defaultStats.getAttackPercent());
        applyIfDifferent(AttributeBuilder.KI_PERCENT_KEY, getKiPercent(), defaultStats.getKiPercent());
        applyIfDifferent(AttributeBuilder.KI_COST_PERCENT_KEY, getKiCostPercent(), defaultStats.getKiCostPercent());
        applyIfDifferent(AttributeBuilder.BLOCK_DEX_PERCENT_KEY, getBlockDexPercent(), defaultStats.getBlockDexPercent());
        applyIfDifferent(AttributeBuilder.BLOCK_COST_PERCENT_KEY, getBlockCostPercent(), defaultStats.getBlockCostPercent());
        applyIfDifferent(AttributeBuilder.BLOCK_COOLDOWN_KEY, getBlockCooldown(), cooldown);
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
    public int getLevelReq() { return levelReq; }
    public float getRange() { return range; }
    public float getRangeSq() { return rangeSq; }
    public int getCooldown() { return cooldown; }
    public ItemStack getItemStack() { return item; }
    public IItemStack getItem() { return new ScriptItemStack(item); }
    public String getType() { return type; }
    public String getFormattedType() { return formattedType; }
    public float getAttackPercent() { return attackPercent; }
    public int getAttackAdditive() { return attackAdditive; }
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
    public void setKiAdditive(int kiAdditive) {
        this.kiAdditive = kiAdditive;
        AttributeItemUtil.applyAttribute(item, AttributeBuilder.KI_ADDITIVE_KEY, kiAdditive);
    }

    public void setAttackAdditive(int attack) {
        this.attackAdditive = attack;
        AttributeItemUtil.applyAttribute(item, CustomAttributes.MAIN_ATTACK_KEY, attack);
    }

    public void setLevelReq(int levelReq) {
        this.levelReq = levelReq;
        updateItemLore();
    }

    public void setCooldown(int cooldown) throws ProtectedWeaponTypeException {
        checkMutable();
        this.cooldown = cooldown;
        AttributeItemUtil.applyAttribute(item, AttributeBuilder.ATTACK_COOLDOWN_KEY, cooldown);
    }

    public void setAttackPercent(float attackPercent) throws ProtectedWeaponTypeException {
        checkMutable();
        this.attackPercent = attackPercent;
        AttributeItemUtil.applyAttribute(item, AttributeBuilder.ATTACK_PERCENT_KEY, attackPercent);
    }

    public void setSweetSpot(float sweetSpot) throws ProtectedWeaponTypeException {
        checkMutable();
        this.sweetSpot = sweetSpot;
        AttributeItemUtil.applyAttribute(item, AttributeBuilder.SWEET_SPOT_KEY, sweetSpot);
    }

    public void setCanChargeKi(boolean canChargeKi) throws ProtectedWeaponTypeException {
        checkMutable();
        this.canChargeKi = canChargeKi;
        if(canChargeKi) AttributeItemUtil.applyAttribute(item, AttributeBuilder.CAN_CHARGE_KI_KEY, 1);
        else AttributeItemUtil.removeAttribute(item, AttributeBuilder.CAN_CHARGE_KI_KEY);
    }

    public void setKiPercent(float kiPercent) throws ProtectedWeaponTypeException {
        checkMutable();
        this.kiPercent = kiPercent;
        AttributeItemUtil.applyAttribute(item, AttributeBuilder.KI_PERCENT_KEY, kiPercent);
    }

    public void setCanBlock(boolean canBlock) throws ProtectedWeaponTypeException {
        checkMutable();
        this.canBlock = canBlock;
        if(canBlock) AttributeItemUtil.applyAttribute(item, AttributeBuilder.CAN_BLOCK_KEY, 1);
        else AttributeItemUtil.removeAttribute(item, AttributeBuilder.CAN_BLOCK_KEY);
    }

    public void setBlockDexPercent(float blockDexPercent) throws ProtectedWeaponTypeException {
        checkMutable();
        this.blockDexPercent = blockDexPercent;
        AttributeItemUtil.applyAttribute(item, AttributeBuilder.BLOCK_DEX_PERCENT_KEY, blockDexPercent);
    }

    public void setBlockCostPercent(float blockCostPercent) throws ProtectedWeaponTypeException {
        checkMutable();
        this.blockCostPercent = blockCostPercent;
        AttributeItemUtil.applyAttribute(item, AttributeBuilder.BLOCK_COST_PERCENT_KEY, blockCostPercent);
    }

    public void setBlockCooldown(int blockCooldown) throws ProtectedWeaponTypeException {
        checkMutable();
        this.blockCooldown = blockCooldown;
        AttributeItemUtil.applyAttribute(item, AttributeBuilder.BLOCK_COOLDOWN_KEY, blockCooldown);
    }

    public void setKiCostPercent(float kiCostPercent) throws ProtectedWeaponTypeException {
        checkMutable();
        this.kiCostPercent = kiCostPercent;
        AttributeItemUtil.applyAttribute(item, AttributeBuilder.KI_COST_PERCENT_KEY, kiCostPercent);
    }

    public void setRange(float range) throws ProtectedWeaponTypeException {
        checkMutable();
        this.range = range;
        this.rangeSq = range * range;
        AttributeItemUtil.applyAttribute(item, AttributeBuilder.RANGE_KEY, range);
    }

    public void setFormattedType(String format) throws ProtectedWeaponTypeException {
        checkMutable();
        this.formattedType = format;
    }

    /**
     * Throws an error if a weapon is not custom or primitive
     * @throws ProtectedWeaponTypeException .
     */
    private void checkMutable() throws ProtectedWeaponTypeException {
        if(!isPrimitive && !getType().equals(SPECIAL)) {
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
