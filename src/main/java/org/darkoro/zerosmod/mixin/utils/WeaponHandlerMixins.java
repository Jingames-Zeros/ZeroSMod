package org.darkoro.zerosmod.mixin.utils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.CommonProxy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.darkoro.zerosmod.zsweapons.cache.CachedWeaponStats;
import org.darkoro.zerosmod.zsweapons.client.ClientWeaponHandler;
import org.darkoro.zerosmod.zsweapons.enums.DBCStatIds;
import org.darkoro.zerosmod.zsweapons.server.ServerWeaponHandler;

import static org.darkoro.zerosmod.zsweapons.ZSWeaponUtils.getWeaponStats;

public class WeaponHandlerMixins {
    /**
     * Calculates updated ki attack cost from weapon multiplier
     * @param original cost
     * @return updated cost
     */
    public static int calculateUpdatedKiCost(int original) {
        CachedWeaponStats stats = FMLCommonHandler.instance().getEffectiveSide().isServer() ? getWeaponStats(CommonProxy.getCurrentJRMCTickPlayer()) : ClientWeaponHandler.INSTANCE.clientCombatState.getItemStats();
        return (int) (original * (stats == null ? 1 : stats.getKiCostMultiplier()));
    }

    /**
     * Calculates updated stat for melee and ki power
     * @param player .
     * @param original original stat amount
     * @param statId 0 melee, 4 ki power
     * @return updated stat amount
     */
    public static int calculateUpdatedStat(Entity player, int original, int statId) {
        if(!(player instanceof EntityPlayer ep) || getWeaponStats(ep) == null) return original;
        CachedWeaponStats weaponStats = getWeaponStats(ep);
        DBCStatIds stat = DBCStatIds.values()[statId];

        return switch (stat) {
            case MELEE -> (int) (original * weaponStats.getAttackMultiplier());
            case ENERGY_POWER -> (int) (original * weaponStats.getKiMultiplier()) + weaponStats.getKiAdditive();
            default -> original;
        };
    }

    /**
     * Calculates updated block dex amount from weapon multiplier
     * @param player .
     * @param def amount
     * @return updated amount
     */
    public static float calculateUpdatedBlockDex(EntityPlayer player, float def) {
        CachedWeaponStats stats = getWeaponStats(player);
        if(stats == null) return def;
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            ServerWeaponHandler.INSTANCE.getPlayerState(player).blockEvent();
        } else {
            ClientWeaponHandler.INSTANCE.clientCombatState.blockEvent();
        }
        return def * stats.getBlockDexMultiplier();
    }

    /**
     * Calculates updated block stamina cost from weapon multiplier
     * @param player .
     * @param original cost
     * @return updated cost
     */
    public static float calculateUpdatedBlockCost(EntityPlayer player, float original) {
        if(player == null || getWeaponStats(player) == null) return original;
        return original * getWeaponStats(player).getBlockCostMultiplier();
    }

    /**
     * Checks if an item can dbc block
     * @param item .
     * @return null if the item is valid - returns item if not
     */
    public static ItemStack getBlockItem(ItemStack item) {
        if(item == null || ClientWeaponHandler.INSTANCE.clientCombatState.getItemStats().canBlock()) return null;
        return item;
    }

    /**
     * Checks if an item can charge ki
     * @param item .
     * @return null if the item is valid - returns item if not
     */
    public static ItemStack getChargeItem(ItemStack item) {
        if(item == null || ClientWeaponHandler.INSTANCE.clientCombatState.getItemStats().canChargeKi()) return null;
        return item;
    }
}
