package org.darkoro.zerosmod.zsweapons;

import cpw.mods.fml.common.FMLCommonHandler;
import kamkeel.npcdbc.util.DBCUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import org.darkoro.zerosmod.config.ServerWeaponConfig;
import org.darkoro.zerosmod.zsweapons.client.ClientWeaponHandler;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponTypeId.*;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponNBTKey.*;
import java.util.Map;

public class ZSWeaponUtils {
    /**
     * Calculates additional damage from multiplier
     * @param player .
     * @param multiplier float multiplier
     * @return float
     */
    public static float getMultiplierBonusDamage(EntityPlayer player, float multiplier) {
        float meleeDamage = DBCUtils.calculateAttackStat(player, 0, DamageSource.causePlayerDamage(player));
        return meleeDamage * multiplier - meleeDamage;
    }

    /**
     * Compares two item stacks and their NBTs
     * @param item1 ItemStack
     * @param item2 ItemStack
     * @return if items are equal
     */
    public static boolean itemsAreEqual(ItemStack item1, ItemStack item2) {
        return ItemStack.areItemStacksEqual(item1, item2) && ItemStack.areItemStackTagsEqual(item1, item2);
    }

    /**
     * Checks attack conditions to determine if an attack is valid
     * @param state CachedWeaponState
     * @param player Player doing the attack
     * @param target Target of the attack
     * @return Boolean
     */
    public static boolean isValidAttack(CachedWeaponState state, EntityPlayer player, EntityLivingBase target) {
        return (
                player != null &&
                target != null &&
                state != null &&
                state.remainingCooldown <= 0 &&
                distanceSqToHitBox(player, target) < state.getRangeSq() &&
                itemsAreEqual(state.currentItem, player.getHeldItem())
        );
    }

    /**
     * Gets an estimated distance to target hitbox from the player's eye height
     * @param attacker attacking player
     * @param target .
     * @return double
     */
    public static double distanceSqToHitBox(EntityPlayer attacker, EntityLivingBase target) {
        AxisAlignedBB hitbox = target.boundingBox;
        double x = Math.max(hitbox.minX, Math.min(attacker.posX, hitbox.maxX));
        double y = Math.max(hitbox.minY, Math.min(attacker.posY + attacker.getEyeHeight(), hitbox.maxY));
        double z = Math.max(hitbox.minZ, Math.min(attacker.posZ, hitbox.maxZ));

        double dx = attacker.posX - x;
        double dy = attacker.posY + attacker.getEyeHeight() - y;
        double dz = attacker.posZ - z;

        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Returns default state for appropriate side
     */
    public static CachedWeaponState getDefaultState() {
        if(FMLCommonHandler.instance().getSide().isClient()) {
            return ClientWeaponHandler.defaultWeaponState;
        } else {
            return ServerWeaponConfig.defaultWeaponState;
        }
    }

    /**
     * Returns loaded states for appropriate side
     */
    public static Map<String, CachedWeaponState> getLoadedStates() {
        if(FMLCommonHandler.instance().getSide().isClient()) {
            return ClientWeaponHandler.loadedWeaponStates;
        } else {
            return ServerWeaponConfig.loadedWeaponStates;
        }
    }

    /**
     * Checks if an item has a ZSWEAPON key
     */
    public static boolean hasZSWeaponTag(ItemStack item) {
        return item != null && item.hasTagCompound() && item.getTagCompound().hasKey(ZSWEAPON.key);
    }

    /**
     * Returns existing ZSWeapon
     * @param item .
     * @return Item's ZSWeapon Compound or default
     */
    public static NBTTagCompound getZSWeaponTag(ItemStack item) {
        if(hasZSWeaponTag(item)) {
            return item.getTagCompound().getCompoundTag(ZSWEAPON.key);
        } else {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setString(TYPE.key, DEFAULT);
            return nbt;
        }
    }
}
