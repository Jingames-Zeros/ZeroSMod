package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import com.llamalad7.mixinextras.sugar.Local;
import kamkeel.npcs.controllers.data.attribute.tracker.PlayerAttributeTracker;
import kamkeel.npcs.util.AttributeAttackUtil;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.darkoro.zerosmod.mixin.utils.WeaponHandlerMixins;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
        value = AttributeAttackUtil.class,
        remap = false
)
public class AttributeAttackUtilMixins {
    @Redirect(
            method = "calculateDamagePlayerToNPC(Lnet/minecraft/entity/player/EntityPlayer;Lnoppes/npcs/entity/EntityNPCInterface;F)F",
            at = @At(
                    value = "INVOKE",
                    target = "kamkeel/npcs/util/AttributeAttackUtil.applyMainAttack (FLkamkeel/npcs/controllers/data/attribute/tracker/PlayerAttributeTracker;)F"
            )
    )
    private static float addSweetSpotWeaponDamagePVE(float damage, PlayerAttributeTracker tracker, @Local(argsOnly = true) EntityPlayer player, @Local(argsOnly = true) EntityNPCInterface npc) {
        return WeaponHandlerMixins.calculateModifiedMainAttack(damage, player.getDistanceToEntity(npc), player, tracker);
    }

    @Redirect(
            method = "calculateDamagePlayerToPlayer(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/player/EntityPlayer;F)F",
            at = @At(
                    value = "INVOKE",
                    target = "kamkeel/npcs/util/AttributeAttackUtil.applyMainAttack (FLkamkeel/npcs/controllers/data/attribute/tracker/PlayerAttributeTracker;)F"
            )
    )
    private static float addSweetSpotWeaponDamagePVP(float damage, PlayerAttributeTracker tracker, @Local(argsOnly = true, index = 1) EntityPlayer attackPlayer, @Local(argsOnly = true, index = 1) EntityPlayer defendPlayer) {
        return WeaponHandlerMixins.calculateModifiedMainAttack(damage, attackPlayer.getDistanceToEntity(defendPlayer), attackPlayer, tracker);
    }
}
