package org.darkoro.zerosmod.mixin.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.EntityEnAttacks;
import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import cpw.mods.fml.common.registry.IThrowableEntity;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.darkoro.zerosmod.config.KiAttackConfig;
import org.darkoro.zerosmod.ki.KiAttackSafety;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityEnergyAtt.class, remap = false)
public abstract class MixinEntityEnergyAtt extends EntityEnAttacks implements IThrowableEntity, KiAttackSafety.KiAttackInternals {

  @Shadow private byte type;
  @Shadow private byte speed;
  @Shadow private byte perc;
  @Shadow private byte density;
  @Shadow private double damage;
  @Shadow private double damageOriginal;
  @Shadow private float size;
  @Shadow private float explevel;
  @Shadow private boolean shrink;
  @Shadow private float strtX;
  @Shadow private float strtY;
  @Shadow private float strtZ;
  @Shadow private float trgtX;
  @Shadow private float trgtY;
  @Shadow private float trgtZ;
  @Shadow private int color;
  @Shadow private int color2;
  @Shadow private Entity target;
  @Shadow public boolean shooterHolds;

  public MixinEntityEnergyAtt(World world) {
    super(world);
  }

  @Shadow public abstract boolean isBarrage();
  @Shadow public abstract boolean isShield();
  @Shadow public abstract boolean isExplosion();
  @Shadow public abstract boolean isContinuesWave();

  @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true, remap = true)
  private void zerosmod$guardOnUpdate(CallbackInfo ci) {
    if (KiAttackSafety.beforeUpdate((EntityEnergyAtt)(Object)this, this)) {
      ci.cancel();
    }
  }

  @Inject(method = "getPower", at = @At("HEAD"), cancellable = true, remap = false)
  private void zerosmod$safeGetPower(Entity entity, CallbackInfoReturnable<Long> cir) {
    if (KiAttackConfig.useSafePowerFormula()) {
      cir.setReturnValue(KiAttackSafety.safePower(entity));
    }
  }

  @Inject(method = "checkForEntitiesInside", at = @At("HEAD"), cancellable = true, remap = false)
  private void zerosmod$guardEntityScan(CallbackInfoReturnable<List> cir) {
    AxisAlignedBB box = this.boundingBox;
    if (KiAttackConfig.isEnabled() && KiAttackSafety.hasInvalidBox(box)) {
      cir.setReturnValue(Collections.emptyList());
    }
  }

  @Inject(method = "checkForEntitiesInside", at = @At("RETURN"), cancellable = true, remap = false)
  private void zerosmod$capEntityScan(CallbackInfoReturnable<List> cir) {
    if (KiAttackConfig.isEnabled()) {
      cir.setReturnValue(KiAttackSafety.capCollisionList(cir.getReturnValue()));
    }
  }

  @Inject(method = "createExplosion", at = @At("HEAD"), cancellable = true, remap = false)
  private void zerosmod$guardExplosion(int type, CallbackInfo ci) {
    if (KiAttackConfig.isEnabled() && KiAttackSafety.isBadExplosion((EntityEnergyAtt)(Object)this, this)) {
      KiAttackSafety.kill((Entity)(Object)this);
      ci.cancel();
    }
  }

  @Inject(method = "writeEntityToNBT", at = @At("HEAD"), remap = true)
  private void zerosmod$markKiNbt(NBTTagCompound nbt, CallbackInfo ci) {
    KiAttackSafety.markNonPersistent(nbt);
  }

  @Inject(method = "readEntityFromNBT", at = @At("RETURN"), remap = true)
  private void zerosmod$killLoadedKi(NBTTagCompound nbt, CallbackInfo ci) {
    KiAttackSafety.killLoadedAttack((EntityEnergyAtt)(Object)this);
  }

  @Redirect(
      method = "readSpawnData",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/World;getEntityByID(I)Lnet/minecraft/entity/Entity;",
          ordinal = 1),
      remap = true)
  private Entity zerosmod$fixMissingSpawnTarget(World world, int entityId) {
    return entityId == 0 ? null : world.getEntityByID(entityId);
  }

  @Inject(method = "setDead", at = @At("HEAD"), remap = true)
  private void zerosmod$clearShooterState(CallbackInfo ci) {
    if (this.worldObj != null && !this.worldObj.isRemote && this.shootingEntity instanceof EntityPlayer && this.shooterHolds) {
      JRMCoreH.setByte(0, (EntityPlayer)this.shootingEntity, "jrmcFrng");
    }
  }

  @Inject(method = "shrink", at = @At("HEAD"), cancellable = true, remap = false)
  private void zerosmod$skipRedirectedShrink(CallbackInfo ci) {
    EntityEnergyAtt attack = (EntityEnergyAtt)(Object)this;
    if (KiAttackSafety.isRedirected(attack)) {
      KiAttackSafety.clearRedirectedShrinkState(attack);
      ci.cancel();
    }
  }

  @Inject(method = "getThrower", at = @At("HEAD"), cancellable = true, remap = false)
  private void zerosmod$getThrower(CallbackInfoReturnable<Entity> cir) {
    cir.setReturnValue(this.shootingEntity);
  }

  @Inject(method = "setThrower", at = @At("HEAD"), cancellable = true, remap = false)
  private void zerosmod$setThrower(Entity entity, CallbackInfo ci) {
    this.shootingEntity = entity;
    ci.cancel();
  }

  @Unique @Override public int zerosmod$getType() {
    return this.type;
  }

  @Unique @Override public int zerosmod$getSpeed() {
    return this.speed;
  }

  @Unique @Override public void zerosmod$setSpeed(byte speed) {
    this.speed = speed;
  }

  @Unique @Override public int zerosmod$getPerc() {
    return this.perc;
  }

  @Unique @Override public void zerosmod$setPerc(byte perc) {
    this.perc = perc;
  }

  @Unique @Override public int zerosmod$getDensity() {
    return this.density;
  }

  @Unique @Override public void zerosmod$setDensity(byte density) {
    this.density = density;
  }

  @Unique @Override public double zerosmod$getDamage() {
    return this.damage;
  }

  @Unique @Override public void zerosmod$setDamage(double damage) {
    this.damage = damage;
  }

  @Unique @Override public double zerosmod$getDamageOriginal() {
    return this.damageOriginal;
  }

  @Unique @Override public void zerosmod$setDamageOriginal(double damageOriginal) {
    this.damageOriginal = damageOriginal;
  }

  @Unique @Override public float zerosmod$getSize() {
    return this.size;
  }

  @Unique @Override public void zerosmod$setSize(float size) {
    this.size = size;
  }

  @Unique @Override public float zerosmod$getExplosionLevel() {
    return this.explevel;
  }

  @Unique @Override public void zerosmod$setExplosionLevel(float explosionLevel) {
    this.explevel = explosionLevel;
  }

  @Unique @Override public int zerosmod$getColor() {
    return this.color;
  }

  @Unique @Override public void zerosmod$setColor(int color) {
    this.color = color;
  }

  @Unique @Override public int zerosmod$getColor2() {
    return this.color2;
  }

  @Unique @Override public void zerosmod$setColor2(int color2) {
    this.color2 = color2;
  }

  @Unique @Override public Entity zerosmod$getOwner() {
    return this.shootingEntity;
  }

  @Unique @Override public void zerosmod$setOwner(Entity owner) {
    this.shootingEntity = owner;
  }

  @Unique @Override public Entity zerosmod$getTarget() {
    return this.target;
  }

  @Unique @Override public void zerosmod$setTarget(Entity target) {
    this.target = target;
  }

  @Unique @Override public void zerosmod$setStartPosition(float x, float y, float z) {
    this.strtX = x;
    this.strtY = y;
    this.strtZ = z;
  }

  @Unique @Override public void zerosmod$setTargetPosition(float x, float y, float z) {
    this.trgtX = x;
    this.trgtY = y;
    this.trgtZ = z;
  }

  @Unique @Override public void zerosmod$setShrink(boolean shrink) {
    this.shrink = shrink;
  }
}
