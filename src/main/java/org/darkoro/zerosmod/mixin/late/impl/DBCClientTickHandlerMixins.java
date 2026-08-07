package org.darkoro.zerosmod.mixin.late.impl;

import JinRyuu.DragonBC.common.DBCClientTickHandler;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.darkoro.zerosmod.zsweapons.client.ClientWeaponHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
        value = DBCClientTickHandler.class,
        remap = false
)
public class DBCClientTickHandlerMixins {

    @Redirect(
            method = "onTickInGame()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/InventoryPlayer;getCurrentItem()Lnet/minecraft/item/ItemStack;"
            ),
            remap = true
    )
    private ItemStack heldItemCanChargeKi(InventoryPlayer instance) {
        ItemStack item = instance.getCurrentItem();
        if(ClientWeaponHandler.INSTANCE.currentWeapon.canChargeKi) return null;
        return item;
    }
}
