package org.darkoro.zerosmod.mixin.late.impl;

import JinRyuu.JRMCore.JRMCoreH;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.EntityPlayer;
import org.darkoro.zerosmod.zsweapons.CachedWeaponState;
import org.darkoro.zerosmod.zsweapons.server.ServerWeaponHandler;
import org.spongepowered.asm.mixin.Mixin;
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
        return original * getState(player).blockCostMultiplier;
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
        return (int) (def * getState(player).blockDexMultiplier);
    }

    private static CachedWeaponState getState(EntityPlayer player) {
        return ServerWeaponHandler.INSTANCE.getPlayerState(player);
    }
}
