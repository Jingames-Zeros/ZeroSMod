package org.darkoro.zerosmod.mixin.mixins.late.impl.npc.client;

import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.form.Form;
import org.darkoro.zerosmod.network.BeginCustomTransformationPacket;
import org.darkoro.zerosmod.network.NetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TransformController.class, remap = false)
public abstract class MixinTransformControllerTransformationEvents {

  @Inject(
      method = "Ascend",
      at = @At(
          value = "INVOKE",
          target = "Lkamkeel/npcdbc/controllers/TransformController;setAscending(Z)V",
          shift = At.Shift.AFTER),
      remap = false)
  private static void zerosmod$beginCustomFormTransformation(Form form, CallbackInfo ci) {
    if (TransformController.time == 1) {
      NetworkHandler.INSTANCE.sendToServer(new BeginCustomTransformationPacket(form.getID()));
    }
  }
}
