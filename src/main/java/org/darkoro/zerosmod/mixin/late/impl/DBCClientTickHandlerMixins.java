package org.darkoro.zerosmod.mixin.late.impl;

import JinRyuu.DragonBC.common.DBCClientTickHandler;
import JinRyuu.JRMCore.JRMCoreH;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.zsweapons.ZSWeaponUtils;
import org.darkoro.zerosmod.zsweapons.client.ClientWeaponHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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
        if(!ZSWeaponUtils.clientStatsAreNull() && ClientWeaponHandler.INSTANCE.clientCombatState.getItemStats().canChargeKi()) return null;
        return item;
    }

    @Redirect(
            method = "onTickInGame()V",
            at = @At(
                    value = "INVOKE",
                    target = "JinRyuu/JRMCore/JRMCoreH.techDBCkic ([Ljava/lang/String;I[B)I"
            )
    )
    private int updateKiCost(String[] listOfAttacks, int playerStat, byte[] kiAttackStats) {
        int baseline = JRMCoreH.techDBCkic(listOfAttacks, playerStat, kiAttackStats);
        if(ZSWeaponUtils.clientStatsAreNull()) return baseline;
        return (int) (baseline * ClientWeaponHandler.INSTANCE.clientCombatState.getItemStats().getKiCostMultiplier());
    }
}
