package org.darkoro.zerosmod.mixin.late.impl;

import JinRyuu.JRMCore.JRMCoreGuiBars;
import JinRyuu.JRMCore.JRMCoreH;
import org.darkoro.zerosmod.zsweapons.ZSWeaponUtils;
import org.darkoro.zerosmod.zsweapons.client.ClientWeaponHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
        value = JRMCoreGuiBars.class,
        remap = false
)
public class JRMCoreGuiBarsMixins {
    @Redirect(
            method = "renderEnChrgBar()V",
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
