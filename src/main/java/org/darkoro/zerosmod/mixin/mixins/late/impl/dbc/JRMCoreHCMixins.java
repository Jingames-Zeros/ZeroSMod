package org.darkoro.zerosmod.mixin.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreHC;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import static org.darkoro.zerosmod.mixin.utils.WeaponHandlerMixins.getBlockItem;

@Mixin(
        value = {JRMCoreHC.class},
        remap = false
)
public class JRMCoreHCMixins {
    @Redirect(
            method = "Blocking()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/InventoryPlayer;getCurrentItem()Lnet/minecraft/item/ItemStack;"
            ),
            remap = true
    )
    private static ItemStack heldItemCanBlock(InventoryPlayer instance) {
        return getBlockItem(instance.getCurrentItem());
    }
}
