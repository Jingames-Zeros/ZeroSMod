package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import com.llamalad7.mixinextras.sugar.Local;
import kamkeel.npcdbc.ServerEventHandler;
import net.minecraft.entity.player.EntityPlayer;
import org.darkoro.zerosmod.formdrain.CustomFormDrainEventHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ServerEventHandler.class, remap = false)
public abstract class MixinServerEventHandlerFormDrain {

  @Redirect(
      method = "handleFormProcesses(Lnet/minecraft/entity/player/EntityPlayer;)V",
      at = @At(value = "INVOKE", target = "Ljava/lang/Math;floor(D)D", ordinal = 0),
      require = 1,
      remap = false)
  private double zerosmod$fireFormDrain(double negativeDrain,
      @Local(argsOnly = true) EntityPlayer player) {
    double replacementDrain = CustomFormDrainEventHooks.fire(player, Math.max(0.0D, -negativeDrain));
    return Math.floor(-replacementDrain);
  }
}
