package org.darkoro.zerosmod.mixin.late.impl;

import JinRyuu.JRMCore.JRMCoreH;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.darkoro.zerosmod.zsweapons.cache.CachedWeaponStats;
import static org.darkoro.zerosmod.zsweapons.enums.DBCStatIds.*;

import org.darkoro.zerosmod.zsweapons.enums.DBCStatIds;
import org.darkoro.zerosmod.zsweapons.server.ServerWeaponHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(
        value = JRMCoreH.class,
        remap = false
)
public class JRMCoreHMixins {
    @ModifyExpressionValue(
            method = "jrmcDam(Lnet/minecraft/entity/Entity;ILnet/minecraft/util/DamageSource;)I",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=0.05F")
    )
    private static float updateBlockStaminaCost(float original, @Local(name = "player") EntityPlayer player) {
        return original * getWeaponStats(player).getBlockCostMultiplier();
    }


    @ModifyVariable(
            method = "jrmcDam(Lnet/minecraft/entity/Entity;ILnet/minecraft/util/DamageSource;)I",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Math;random()D"
            ),
            index = 34
    )
    private static int updateBlockPercent(int def, @Local(name = "player") EntityPlayer player) {
        return (int) (def * getWeaponStats(player).getBlockCostMultiplier());
    }


    @ModifyReturnValue(
            method = "stat(Lnet/minecraft/entity/Entity;IIIIIIF)I",
            at = @At("RETURN")
    )
    private static int updateStatAmount(int original, Entity player, int attributeID, int powerType, int stat, int attribute, int race, int classID, float skillBonus) {
        if(!(player instanceof EntityPlayer ep) || getWeaponStats(ep) == null) return original;
        CachedWeaponStats weaponStats = getWeaponStats(ep);
        DBCStatIds statId = DBCStatIds.values()[stat];

        return switch (statId) {
            case MELEE -> (int) (original * weaponStats.getAttackMultiplier());
            case ENERGY_POWER -> (int) (original * weaponStats.getKiMultiplier()) + weaponStats.getKiAdditive();
            default -> original;
        };
    }

    @Unique
    private static CachedWeaponStats getWeaponStats(EntityPlayer player) {
        return ServerWeaponHandler.INSTANCE.getPlayerState(player).getItemStats();
    }
}
