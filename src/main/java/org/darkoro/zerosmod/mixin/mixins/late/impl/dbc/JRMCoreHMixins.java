package org.darkoro.zerosmod.mixin.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreH;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.darkoro.zerosmod.mixin.utils.WeaponHandlerMixins;
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
        return WeaponHandlerMixins.calculateUpdatedBlockCost(player, original);
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
        return (int) WeaponHandlerMixins.calculateUpdatedBlockDex(player, def);
    }

    @ModifyReturnValue(
            method = "stat(Lnet/minecraft/entity/Entity;IIIIIIF)I",
            at = @At("RETURN")
    )
    private static int updateStatAmount(int original, Entity player, int attributeID, int powerType, int stat, int attribute, int race, int classID, float skillBonus) {
        return WeaponHandlerMixins.calculateUpdatedStat(player, original, stat);
    }

    @ModifyReturnValue(
            method = "techDBCkic([Ljava/lang/String;I[B)I",
            at = @At("RETURN")
    )
    private static int updateKiCost(int original) {
        return WeaponHandlerMixins.calculateUpdatedKiCost(original);
    }
}
