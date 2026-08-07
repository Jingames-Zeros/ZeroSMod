package org.darkoro.zerosmod.mixin.late.impl.dbc;

import JinRyuu.DragonBC.common.Entitys.EntityInstantTransmission;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import org.darkoro.zerosmod.it.InstantTransmissionEventHooks;
import org.darkoro.zerosmod.it.InstantTransmissionScriptHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityInstantTransmission.class, remap = false)
public abstract class MixinEntityInstantTransmission {

  @Shadow public Entity shootingEntity;
  @Shadow private boolean teleported;
  @Shadow private byte skillLevel;

  @Unique private EntityLivingBase zerosmod$instantTransmissionTarget;
  @Unique private boolean zerosmod$firedInstantTransmissionEvent;

  @Inject(method = "onLand", at = @At("HEAD"), remap = false)
  private void zerosmod$captureInstantTransmissionTarget(CallbackInfo ci) {
    if (this.teleported || this.zerosmod$firedInstantTransmissionEvent() || !(this.shootingEntity instanceof EntityPlayerMP)) {
      return;
    }

    EntityInstantTransmission self = (EntityInstantTransmission)(Object)this;
    this.zerosmod$instantTransmissionTarget =
        InstantTransmissionScriptHelper.getInstantTransmissionProjectileTarget(
            (EntityPlayerMP)this.shootingEntity,
            self.posX,
            self.posY,
            self.posZ,
            self.motionX,
            self.motionY,
            self.motionZ,
            this.skillLevel);
  }

  @Inject(
      method = "onLand",
      at = @At(
          value = "INVOKE",
          target = "LJinRyuu/JRMCore/JRMCoreH;playerUsedInstantTransmission(Lnet/minecraft/entity/player/EntityPlayer;)V",
          ordinal = 1,
          shift = At.Shift.AFTER),
      remap = false)
  private void zerosmod$fireActivatedInstantTransmissionEvent(CallbackInfo ci) {
    if (this.zerosmod$firedInstantTransmissionEvent || !(this.shootingEntity instanceof EntityPlayerMP)) {
      return;
    }

    this.zerosmod$firedInstantTransmissionEvent = true;
    InstantTransmissionEventHooks.onActivated((EntityPlayerMP)this.shootingEntity, this.zerosmod$instantTransmissionTarget);
  }

  @Unique private boolean zerosmod$firedInstantTransmissionEvent() {
    return this.zerosmod$firedInstantTransmissionEvent;
  }
}
