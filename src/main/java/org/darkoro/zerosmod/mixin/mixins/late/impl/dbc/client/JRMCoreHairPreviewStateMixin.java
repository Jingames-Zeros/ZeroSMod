package org.darkoro.zerosmod.mixin.mixins.late.impl.dbc.client;

import JinRyuu.JRMCore.JRMCoreH;
import net.minecraft.nbt.NBTTagCompound;
import org.darkoro.zerosmod.dbchair.gui.HairEditorScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = JRMCoreH.class, remap = false)
public abstract class JRMCoreHairPreviewStateMixin {
  @Inject(method = "state(Lnet/minecraft/nbt/NBTTagCompound;I)Z", at = @At("HEAD"),
      cancellable = true, remap = false)
  private static void zerosmod$useEditorFormForPreview(NBTTagCompound data, int state,
      CallbackInfoReturnable<Boolean> cir) {
    if (HairEditorScreen.zerosmod$isPreviewDrawActive()) {
      cir.setReturnValue(HairEditorScreen.zerosmod$getPreviewFormState() == state);
    }
  }

  @Inject(method = "isInState(I)Z", at = @At("HEAD"), cancellable = true, remap = false)
  private static void zerosmod$useEditorFormForCurrentState(int state, CallbackInfoReturnable<Boolean> cir) {
    if (HairEditorScreen.zerosmod$isPreviewDrawActive()) {
      cir.setReturnValue(HairEditorScreen.zerosmod$getPreviewFormState() == state);
    }
  }
}
