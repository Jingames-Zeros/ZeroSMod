package org.darkoro.zerosmod.mixin.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCorePacHanS;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Keeps DBC's server-side DNS save from replacing a Majin's hair color with body color. */
@Mixin(value = JRMCorePacHanS.class, remap = false)
public abstract class JRMCoreMajinHairDataMixin {
  @Inject(method = "handleData(ILjava/lang/String;Lnet/minecraft/entity/player/EntityPlayer;)V",
      at = @At("HEAD"), cancellable = true, remap = false)
  private void zerosmod$saveMajinHairColor(int dataType, String dns, EntityPlayer player,
      CallbackInfo ci) {
    if (player == null
        || !JRMCoreH.isRaceMajin(JRMCoreH.getByte(player, JRMCorePacHanS.R))) {
      return;
    }

    byte accept = JRMCoreH.getByte(player, JRMCorePacHanS.Acc);
    if (dataType == 0) {
      String currentDns = JRMCoreH.getString(player, "jrmcDNS");
      if (accept == 0) {
        // DBC permits the player's full DNS edit here; retain that behavior but do not
        // force a Majin's independent hair color back to its body color.
        JRMCoreH.setString(dns, player, "jrmcDNS");
      } else {
        // With accept enabled DBC freezes every hair field in the incoming DNS. Majin hair
        // coloring only needs the hair color, so merge that one field into the saved DNS and
        // keep every other value, including the other hair fields, as DBC would.
        String updatedDns = JRMCoreH.dnsHairCSet(currentDns, JRMCoreH.dnsHairC(dns));
        JRMCoreH.setString(updatedDns, player, "jrmcDNS");
      }
      ci.cancel();
    }
  }
}
