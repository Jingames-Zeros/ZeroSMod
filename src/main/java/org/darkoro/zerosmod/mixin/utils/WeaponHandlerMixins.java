package org.darkoro.zerosmod.mixin.utils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.CommonProxy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import noppes.npcs.entity.EntityNPCInterface;
import org.darkoro.zerosmod.zsweapons.ZSWeaponUtils;
import org.darkoro.zerosmod.zsweapons.cache.CachedWeaponStats;
import org.darkoro.zerosmod.zsweapons.client.ClientWeaponHandler;
import org.darkoro.zerosmod.zsweapons.enums.DBCStatIds;
import org.darkoro.zerosmod.zsweapons.server.ServerWeaponHandler;

import static org.darkoro.zerosmod.zsweapons.ZSWeaponUtils.calculateSweetSpotMulti;
import static org.darkoro.zerosmod.zsweapons.ZSWeaponUtils.getWeaponStats;

public class WeaponHandlerMixins {
    /**
     * Calculates updated ki attack cost from weapon multiplier
     * @param original cost
     * @return updated cost
     */
    public static int calculateUpdatedKiCost(int original) {
        CachedWeaponStats stats = FMLCommonHandler.instance().getEffectiveSide().isServer() ? getWeaponStats(CommonProxy.getCurrentJRMCTickPlayer()) : ClientWeaponHandler.INSTANCE.clientCombatState.getItemStats();
        return (int) (original * (stats == null ? 1 : stats.getKiCostPercent() / 100));
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

        switch (stat) {
            case MELEE:
                float sweetSpotMulti = 1.0F;
                if(isServerSide()) {
                    sweetSpotMulti = ServerWeaponHandler.INSTANCE.getPlayerState(ep).resolveAttack();
                }
                return (int) (original * weaponStats.getAttackPercent() / 100 * sweetSpotMulti);
            case ENERGY_POWER:
                return (int) (original * weaponStats.getKiPercent() / 100) + weaponStats.getKiAdditive();
            default:
                return original;
        }
    }

    /**
     * Calculates sweet spot damage for weapon damage specifically
     * @param original original weapon damage
     * @param player attacking player
     * @param npc target
     * @return New weapon damage
     */
    public static float calculateSweetSpotWeaponDamage(float original, EntityPlayer player, EntityLivingBase target) {
        CachedWeaponStats stats = ZSWeaponUtils.getWeaponStats(player);
        if(stats == null) return original;
        return original * calculateSweetSpotMulti(player.getDistanceToEntity(target), stats.getSweetSpot());
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
        if(isServerSide()) {
            ServerWeaponHandler.INSTANCE.getPlayerState(player).blockEvent();
        } else {
            ClientWeaponHandler.INSTANCE.clientCombatState.blockEvent();
        }
        return def * stats.getBlockDexPercent() / 100;
    }

    /**
     * Calculates updated block stamina cost from weapon multiplier
     * @param player .
     * @param original cost
     * @return updated cost
     */
    public static float calculateUpdatedBlockCost(EntityPlayer player, float original) {
        if(player == null || getWeaponStats(player) == null) return original;
        return original * getWeaponStats(player).getBlockCostPercent() / 100;
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

    /**
     * Util function used to check effective side
     */
    private static boolean isServerSide() {
        return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
    }
}
