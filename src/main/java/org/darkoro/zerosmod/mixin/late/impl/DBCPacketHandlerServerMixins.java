package org.darkoro.zerosmod.mixin.late.impl;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.p.DBC.DBCPacketHandlerServer;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.EntityPlayer;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.zsweapons.ZSWeaponUtils;
import org.darkoro.zerosmod.zsweapons.client.ClientWeaponHandler;
import org.darkoro.zerosmod.zsweapons.server.ServerWeaponHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
        value = DBCPacketHandlerServer.class,
        remap = false
)
public class DBCPacketHandlerServerMixins {

    @Redirect(
            method = "handleDBCenergy(BBLnet/minecraft/entity/player/EntityPlayer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "JinRyuu/JRMCore/JRMCoreH.techDBCkic ([Ljava/lang/String;I[B)I"
            )
    )
    private int updateKiCost(String[] listOfAttacks, int playerStat, byte[] kiAttackStats, @Local(name = "pl") EntityPlayer player) {
        int baseline = JRMCoreH.techDBCkic(listOfAttacks, playerStat, kiAttackStats);
        ZeroSMod.LOGGER.info("Player: " + ZSWeaponUtils.playerStatsAreNull(player));
        if(ZSWeaponUtils.playerStatsAreNull(player)) return baseline;
        ZeroSMod.LOGGER.info("Baseline : " + baseline);
        ZeroSMod.LOGGER.info("New : " + baseline * ServerWeaponHandler.INSTANCE.getPlayerState(player).getItemStats().getKiCostMultiplier());
        return (int) (baseline * ServerWeaponHandler.INSTANCE.getPlayerState(player).getItemStats().getKiCostMultiplier());
    }
}
