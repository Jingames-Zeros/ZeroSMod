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
    // NPCDBC passes the drain negated. A form configured with a negative drain restores Ki
    // instead, which is not a drain, so it runs unchanged without firing the hook.
    if (negativeDrain >= 0.0D) {
      return Math.floor(negativeDrain);
    }
    double replacementDrain = CustomFormDrainEventHooks.fire(player, -negativeDrain);
    return Math.floor(-replacementDrain);
  }
}
