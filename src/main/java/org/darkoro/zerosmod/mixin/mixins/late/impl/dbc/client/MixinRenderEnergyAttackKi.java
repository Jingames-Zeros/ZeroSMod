package org.darkoro.zerosmod.mixin.mixins.late.impl.dbc.client;

import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import JinRyuu.JRMCore.entity.RenderEnergyAttackKi;
import org.darkoro.zerosmod.ki.DestroyerKiColor;
import org.darkoro.zerosmod.ki.KiAttackSafety;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = RenderEnergyAttackKi.class, remap = false)
public abstract class MixinRenderEnergyAttackKi {

  @Inject(method = "renderEnergy", at = @At("HEAD"), remap = false)
  private void zerosmod$applyRedirectedTrailOrigin(EntityEnergyAtt entity, double par2, double par4, double par6,
      float par8, float par9, CallbackInfo ci) {
    if (entity instanceof KiAttackSafety.KiAttackInternals && (entity.isLaser() || entity.isSpiral())) {
      KiAttackSafety.applyRedirectedOrigin(entity, (KiAttackSafety.KiAttackInternals)entity);
    }
  }

  @ModifyArgs(
      method = "renderEnergy(LJinRyuu/JRMCore/entity/EntityEnergyAtt;DDDFF)V",
      at = @At(
          value = "INVOKE",
          target = "LJinRyuu/JRMCore/entity/RenderEnergyAttackKi;render_2d(LJinRyuu/JRMCore/entity/EntityEnergyAtt;DDDFFFDDDIIF)V"),
      remap = false)
  private void zerosmod$apply2dDestroyerColor(Args args) {
    EntityEnergyAtt attack = args.get(0);
    int rgb = DestroyerKiColor.get(attack);
    if (attack.destroyer && DestroyerKiColor.isValid(rgb)) {
      // In DBC's 2D renderer the dark destroyer accent is the primary color.
      args.set(10, rgb);
    }
  }

  @ModifyArgs(
      method = "renderEnergy(LJinRyuu/JRMCore/entity/EntityEnergyAtt;DDDFF)V",
      at = @At(
          value = "INVOKE",
          target = "LJinRyuu/JRMCore/entity/RenderEnergyAttackKi;render_3d(LJinRyuu/JRMCore/entity/EntityEnergyAtt;DDDFFFDDDIIF)V"),
      remap = false)
  private void zerosmod$apply3dDestroyerColor(Args args) {
    EntityEnergyAtt attack = args.get(0);
    int rgb = DestroyerKiColor.get(attack);
    if (attack.destroyer && DestroyerKiColor.isValid(rgb)) {
      // In DBC's 3D renderer the dark destroyer accent is the secondary color.
      args.set(11, rgb);
    }
  }
}
