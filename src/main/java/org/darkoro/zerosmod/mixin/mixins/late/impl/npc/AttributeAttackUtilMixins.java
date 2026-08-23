package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import com.llamalad7.mixinextras.sugar.Local;
import kamkeel.npcs.util.AttributeAttackUtil;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.darkoro.zerosmod.mixin.utils.WeaponHandlerMixins;

@Mixin(
        value = AttributeAttackUtil.class,
        remap = false
)
public class AttributeAttackUtilMixins {
    @ModifyVariable(
            method = "calculateDamagePlayerToNPC(Lnet/minecraft/entity/player/EntityPlayer;Lnoppes/npcs/entity/EntityNPCInterface;F)F",
            at = @At("STORE"),
            index = 5
    )
    private static float addSweetSpotWeaponDamagePVE(float original, @Local(argsOnly = true) EntityPlayer player, @Local(argsOnly = true) EntityNPCInterface npc) {
        return WeaponHandlerMixins.calculateSweetSpotWeaponDamage(original, player, npc);
    }

    @ModifyVariable(
            method = "calculateDamagePlayerToPlayer(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/player/EntityPlayer;F)F",
            at = @At("STORE"),
            index = 7
    )
    private static float addSweetSpotWeaponDamagePVP(float original, @Local(argsOnly = true, index = 1) EntityPlayer attackPlayer, @Local(argsOnly = true, index = 1) EntityPlayer defendPlayer) {
        return WeaponHandlerMixins.calculateSweetSpotWeaponDamage(original, attackPlayer, defendPlayer);
    }
}
