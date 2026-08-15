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
import org.darkoro.zerosmod.zsweapons.cache.CachedWeaponStats;
import org.darkoro.zerosmod.zsweapons.client.ClientWeaponHandler;
import org.darkoro.zerosmod.zsweapons.server.ServerWeaponHandler;

import static org.darkoro.zerosmod.zsweapons.enums.WeaponTypeId.*;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponNBTKey.*;
import java.util.Map;

public class ZSWeaponUtils {
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
    public static boolean isValidAttack(PlayerCombatState state, EntityPlayer player, EntityLivingBase target) {
        return (
                player != null &&
                target != null &&
                state != null &&
                state.getRemainingAttackCooldown() <= 0 &&
                distanceSqToHitBox(player, target) < state.getItemStats().getRangeSq()) &&
                itemsAreEqual(state.getCurrentItem(), player.getHeldItem()
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
    public static CachedWeaponStats getDefaultStats() {
        if(FMLCommonHandler.instance().getSide().isClient()) {
            if(ClientWeaponHandler.loadedWeaponStats == null) return null;
            return ClientWeaponHandler.loadedWeaponStats.get(DEFAULT);
        } else {
            if(ServerWeaponConfig.loadedWeaponStats == null) return null;
            return ServerWeaponConfig.loadedWeaponStats.get(DEFAULT);
        }
    }

    /**
     * Returns loaded states for appropriate side
     */
    public static Map<String, CachedWeaponStats> getLoadedStats() {
        if(FMLCommonHandler.instance().getSide().isClient()) {
            return ClientWeaponHandler.loadedWeaponStats;
        } else {
            return ServerWeaponConfig.loadedWeaponStats;
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

    /**
     * Returns player stats depending on effective side
     * @param player .
     * @return player stats
     */
    public static CachedWeaponStats getWeaponStats(EntityPlayer player) {
        if(FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            if(playerStatsAreNull(player)) return null;
            return ServerWeaponHandler.INSTANCE.getPlayerState(player).getItemStats();
        } else {
            if(clientStatsAreNull()) return null;
            return ClientWeaponHandler.INSTANCE.clientCombatState.getItemStats();
        }
    }

    /**
     * Client combat null checks
     */
    public static boolean clientStateIsNull() { return ClientWeaponHandler.INSTANCE.clientCombatState == null; }
    public static boolean clientStatsAreNull() { return clientStateIsNull() && ClientWeaponHandler.INSTANCE.clientCombatState.getItemStats() == null; }

    /**
     * Server combat null checks
     */
    public static boolean playerStateIsNull(EntityPlayer player) { return ServerWeaponHandler.INSTANCE.getPlayerState(player) == null; }
    public static boolean playerStatsAreNull(EntityPlayer player) { return playerStateIsNull(player) && ServerWeaponHandler.INSTANCE.getPlayerState(player).getItemStats() == null; }
}
