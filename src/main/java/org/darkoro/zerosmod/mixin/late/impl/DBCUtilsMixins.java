package org.darkoro.zerosmod.mixin.late.impl;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import kamkeel.npcdbc.util.DBCUtils;
import net.minecraft.entity.player.EntityPlayer;
import org.darkoro.zerosmod.zsweapons.CachedWeaponState;
import org.darkoro.zerosmod.zsweapons.server.ServerWeaponHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(
        value = DBCUtils.class,
        remap = false
)

public class DBCUtilsMixins {
    @ModifyVariable(
            method = "calculateDBCDamageFromSource(Lnet/minecraft/entity/Entity;FLnet/minecraft/util/DamageSource;)Lkamkeel/npcdbc/data/DBCDamageCalc;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Math;random()D"
            ),
            index = 36
    )
    private static float updateBlockPercentSource(float def, @Local(name = "player") EntityPlayer player) {
        return (int) (def * getState(player).blockReduction);
    }

    @ModifyVariable(
            method = "calculateDBCStatDamage(Lnet/minecraft/entity/player/EntityPlayer;FLkamkeel/npcdbc/api/npc/IDBCStats;Lnet/minecraft/util/DamageSource;)Lkamkeel/npcdbc/data/DBCDamageCalc;",
            at = @At(
                    value = "INVOKE",
                    target = "Lkamkeel/npcdbc/data/DBCDamageCalc;setStaminaReduction(I)V"
            ),
            index = 32
    )
    private static float updateBlockPercentStatDamage(float def, @Local(name = "player") EntityPlayer player) {
        return (int) (def * getState(player).blockReduction);
    }

    @ModifyExpressionValue(
            method = "calculateDBCDamageFromSource(Lnet/minecraft/entity/Entity;FLnet/minecraft/util/DamageSource;)Lkamkeel/npcdbc/data/DBCDamageCalc;",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=0.05F")
    )
    private static float updateBlockStaminaCostSource(float original, @Local(name = "player") EntityPlayer player) {
        return original * getState(player).blockCostMultiplier;
    }

    @ModifyExpressionValue(
            method = "calculateDBCStatDamage(Lnet/minecraft/entity/player/EntityPlayer;FLkamkeel/npcdbc/api/npc/IDBCStats;Lnet/minecraft/util/DamageSource;)Lkamkeel/npcdbc/data/DBCDamageCalc;",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=0.05F")
    )
    private static float updateBlockStaminaCostStatDamage(float original, @Local(name = "player") EntityPlayer player) {
        CachedWeaponState state = getState(player);
        state.blockEvent();
        return original * state.blockCostMultiplier;
    }

    private static CachedWeaponState getState(EntityPlayer player) {
        return ServerWeaponHandler.INSTANCE.getPlayerState(player);
    }
}
