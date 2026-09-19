package org.darkoro.zerosmod.mixin.late.impl.dbc.client;

import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import JinRyuu.JRMCore.entity.RenderEnergyAttackKi;
import org.darkoro.zerosmod.ki.KiAttackSafety;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderEnergyAttackKi.class, remap = false)
public abstract class MixinRenderEnergyAttackKi {

  @Inject(method = "renderEnergy", at = @At("HEAD"), remap = false)
  private void zerosmod$applyRedirectedTrailOrigin(EntityEnergyAtt entity, double par2, double par4, double par6,
      float par8, float par9, CallbackInfo ci) {
    if (entity instanceof KiAttackSafety.KiAttackInternals && (entity.isLaser() || entity.isSpiral())) {
      KiAttackSafety.applyRedirectedOrigin(entity, (KiAttackSafety.KiAttackInternals)entity);
    }
  }
}
