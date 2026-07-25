package org.darkoro.zerosmod.mixin.late;

import JinRyuu.DragonBC.common.DBCClientTickHandler;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.darkoro.zerosmod.ZeroSMod;
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
                    target = "net/minecraft/entity/player/InventoryPlayer.getCurrentItem ()Lnet/minecraft/item/ItemStack;"
            )
    )
    private ItemStack redirectGetHeldItem(InventoryPlayer instance) {
        ItemStack item = instance.getCurrentItem();
        if(item == null) return null;
        if(item.hasTagCompound()) {
            NBTTagCompound nbt = item.getTagCompound();
            if(nbt.hasKey("zsweapon") && nbt.getCompoundTag("zsweapon").getBoolean("cancharge")) {
                return null;
            }
        }
        return item;
    }
}
