package org.darkoro.zerosmod.mixin.late;

import JinRyuu.JRMCore.JRMCoreHC;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.darkoro.zerosmod.zsweapons.client.ClientWeaponHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
        value = {JRMCoreHC.class},
        remap = false
)
public class JRMCoreHCMixins {

    @Redirect(
            method = "Blocking()V",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/entity/player/InventoryPlayer.getCurrentItem ()Lnet/minecraft/item/ItemStack;"
            ),
            remap = true
    )
    private static ItemStack heldItemCanBlock(InventoryPlayer instance) {
        ItemStack item = instance.getCurrentItem();
        if(item == null || ClientWeaponHandler.INSTANCE.currentWeapon.canBlock) return null;
        return item;
    }
}
