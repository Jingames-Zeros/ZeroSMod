package org.darkoro.zerosmod.mixin.mixins.late.impl.dbc.client;

import JinRyuu.DragonBC.common.DBCClientTickHandler;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import static org.darkoro.zerosmod.mixin.utils.WeaponHandlerMixins.getChargeItem;

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
        return getChargeItem(instance.getCurrentItem());
    }
}
