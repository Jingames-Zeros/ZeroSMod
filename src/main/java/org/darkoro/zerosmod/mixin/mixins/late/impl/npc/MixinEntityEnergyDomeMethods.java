package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kamkeel.npcs.controllers.AbilityController;
import kamkeel.npcs.controllers.EnergyController;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.entity.EntityEnergyDome;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import noppes.npcs.NpcDamageSource;
import noppes.npcs.entity.EntityNPCInterface;
import org.darkoro.zerosmod.dome.EnergyDomeEventHooks;
import org.darkoro.zerosmod.dome.EnergyDomeExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityEnergyDome.class, remap = false)
public abstract class MixinEntityEnergyDomeMethods implements EnergyDomeExtension {

  @Unique private static final String NBT_PREFIX = "ZeroSDome";

  @Shadow protected float domeRadius;
  @Shadow protected boolean followCaster;
  @Shadow public abstract void setDomeRadius(float radius);
  @Shadow public abstract boolean isEntityInside(Entity entity);

  @Unique private float zerosmod$damage = 0.0F;
  @Unique private int zerosmod$damageInterval = 20;
  @Unique private int zerosmod$ticksSinceDamage = 20;
  @Unique private boolean zerosmod$affectOwner;
  @Unique private boolean zerosmod$followerOverridden;
  @Unique private int zerosmod$followerEntityId = -1;
  @Unique private UUID zerosmod$followerUuid;
  @Unique private final Map<Integer, Entity> zerosmod$insideEntities =
      new HashMap<Integer, Entity>();
  @Unique private float zerosmod$resizeStartRadius;
  @Unique private float zerosmod$resizeTargetRadius;
  @Unique private int zerosmod$resizeDuration;
  @Unique private int zerosmod$resizeElapsed;

  @Inject(method = "func_70071_h_", at = @At("TAIL"), require = 1, remap = false)
  private void zerosmod$afterDomeTick(CallbackInfo ci) {
    EntityEnergyDome dome = zerosmod$self();
    if (dome.worldObj == null || dome.isDead) {
      this.zerosmod$insideEntities.clear();
      return;
    }

    zerosmod$updateFollower();
    zerosmod$updateRadius();

    if (dome.worldObj.isRemote || dome.isCharging() || dome.getTicksAlive() < 1) {
      return;
    }

    Map<Integer, Entity> current = zerosmod$collectEntitiesInside();
    for (Map.Entry<Integer, Entity> entry : current.entrySet()) {
      if (!this.zerosmod$insideEntities.containsKey(entry.getKey())) {
        EnergyDomeEventHooks.fireEnter(dome, entry.getValue());
      }
    }
    for (Map.Entry<Integer, Entity> entry : this.zerosmod$insideEntities.entrySet()) {
      if (!current.containsKey(entry.getKey())) {
        EnergyDomeEventHooks.fireExit(dome, entry.getValue());
      }
    }

    this.zerosmod$insideEntities.clear();
    this.zerosmod$insideEntities.putAll(current);
    if (!dome.isDead) {
      zerosmod$damageEntities(current);
    }
  }

  @Inject(method = "writeSpawnNBT", at = @At("RETURN"), require = 1, remap = false)
  private void zerosmod$writeState(NBTTagCompound nbt, CallbackInfo ci) {
    nbt.setFloat(NBT_PREFIX + "Damage", this.zerosmod$damage);
    nbt.setInteger(NBT_PREFIX + "DamageInterval", this.zerosmod$damageInterval);
    nbt.setInteger(NBT_PREFIX + "DamageTicks", this.zerosmod$ticksSinceDamage);
    nbt.setBoolean(NBT_PREFIX + "AffectOwner", this.zerosmod$affectOwner);
    nbt.setBoolean(NBT_PREFIX + "FollowerOverridden", this.zerosmod$followerOverridden);
    nbt.setInteger(NBT_PREFIX + "FollowerId", this.zerosmod$followerEntityId);
    if (this.zerosmod$followerUuid != null) {
      nbt.setLong(NBT_PREFIX + "FollowerUuidMost", this.zerosmod$followerUuid.getMostSignificantBits());
      nbt.setLong(NBT_PREFIX + "FollowerUuidLeast", this.zerosmod$followerUuid.getLeastSignificantBits());
    }
    nbt.setFloat(NBT_PREFIX + "ResizeStart", this.zerosmod$resizeStartRadius);
    nbt.setFloat(NBT_PREFIX + "ResizeTarget", this.zerosmod$resizeTargetRadius);
    nbt.setInteger(NBT_PREFIX + "ResizeDuration", this.zerosmod$resizeDuration);
    nbt.setInteger(NBT_PREFIX + "ResizeElapsed", this.zerosmod$resizeElapsed);
  }

  @Inject(method = "readSpawnNBT", at = @At("RETURN"), require = 1, remap = false)
  private void zerosmod$readState(NBTTagCompound nbt, CallbackInfo ci) {
    this.zerosmod$damage = zerosmod$finiteNonNegative(
        nbt.hasKey(NBT_PREFIX + "Damage") ? nbt.getFloat(NBT_PREFIX + "Damage") : 0.0F);
    this.zerosmod$damageInterval = Math.max(1,
        nbt.hasKey(NBT_PREFIX + "DamageInterval")
            ? nbt.getInteger(NBT_PREFIX + "DamageInterval") : 20);
    this.zerosmod$ticksSinceDamage = Math.max(0,
        nbt.hasKey(NBT_PREFIX + "DamageTicks")
            ? nbt.getInteger(NBT_PREFIX + "DamageTicks") : this.zerosmod$damageInterval);
    this.zerosmod$affectOwner = nbt.getBoolean(NBT_PREFIX + "AffectOwner");
    this.zerosmod$followerOverridden = nbt.getBoolean(NBT_PREFIX + "FollowerOverridden");
    this.zerosmod$followerEntityId = nbt.hasKey(NBT_PREFIX + "FollowerId")
        ? nbt.getInteger(NBT_PREFIX + "FollowerId") : -1;
    if (nbt.hasKey(NBT_PREFIX + "FollowerUuidMost")
        && nbt.hasKey(NBT_PREFIX + "FollowerUuidLeast")) {
      this.zerosmod$followerUuid = new UUID(
          nbt.getLong(NBT_PREFIX + "FollowerUuidMost"),
          nbt.getLong(NBT_PREFIX + "FollowerUuidLeast"));
    } else {
      this.zerosmod$followerUuid = null;
    }
    this.zerosmod$resizeStartRadius = nbt.getFloat(NBT_PREFIX + "ResizeStart");
    this.zerosmod$resizeTargetRadius = nbt.getFloat(NBT_PREFIX + "ResizeTarget");
    this.zerosmod$resizeDuration = Math.max(0, nbt.getInteger(NBT_PREFIX + "ResizeDuration"));
    this.zerosmod$resizeElapsed = Math.max(0, nbt.getInteger(NBT_PREFIX + "ResizeElapsed"));
    if (this.zerosmod$followerOverridden) {
      this.followCaster = false;
    }
  }

  @Inject(method = "writeBarrierClientSyncData", at = @At("RETURN"), require = 1, remap = false)
  private void zerosmod$writeClientState(NBTTagCompound nbt, CallbackInfo ci) {
    nbt.setBoolean(NBT_PREFIX + "FollowerOverridden", this.zerosmod$followerOverridden);
    nbt.setInteger(NBT_PREFIX + "FollowerId", this.zerosmod$followerEntityId);
    if (this.zerosmod$followerUuid != null) {
      nbt.setLong(NBT_PREFIX + "FollowerUuidMost", this.zerosmod$followerUuid.getMostSignificantBits());
      nbt.setLong(NBT_PREFIX + "FollowerUuidLeast", this.zerosmod$followerUuid.getLeastSignificantBits());
    }
    nbt.setFloat(NBT_PREFIX + "ResizeStart", this.zerosmod$resizeStartRadius);
    nbt.setFloat(NBT_PREFIX + "ResizeTarget", this.zerosmod$resizeTargetRadius);
    nbt.setInteger(NBT_PREFIX + "ResizeDuration", this.zerosmod$resizeDuration);
    nbt.setInteger(NBT_PREFIX + "ResizeElapsed", this.zerosmod$resizeElapsed);
  }

  @Inject(method = "applyBarrierClientSyncData", at = @At("RETURN"), require = 1, remap = false)
  private void zerosmod$applyClientState(NBTTagCompound nbt, CallbackInfo ci) {
    this.zerosmod$followerOverridden = nbt.getBoolean(NBT_PREFIX + "FollowerOverridden");
    this.zerosmod$followerEntityId = nbt.hasKey(NBT_PREFIX + "FollowerId")
        ? nbt.getInteger(NBT_PREFIX + "FollowerId") : -1;
    if (nbt.hasKey(NBT_PREFIX + "FollowerUuidMost")
        && nbt.hasKey(NBT_PREFIX + "FollowerUuidLeast")) {
      this.zerosmod$followerUuid = new UUID(
          nbt.getLong(NBT_PREFIX + "FollowerUuidMost"),
          nbt.getLong(NBT_PREFIX + "FollowerUuidLeast"));
    } else {
      this.zerosmod$followerUuid = null;
    }
    this.zerosmod$resizeStartRadius = nbt.getFloat(NBT_PREFIX + "ResizeStart");
    this.zerosmod$resizeTargetRadius = nbt.getFloat(NBT_PREFIX + "ResizeTarget");
    this.zerosmod$resizeDuration = Math.max(0, nbt.getInteger(NBT_PREFIX + "ResizeDuration"));
    this.zerosmod$resizeElapsed = Math.max(0, nbt.getInteger(NBT_PREFIX + "ResizeElapsed"));
    if (this.zerosmod$followerOverridden) {
      this.followCaster = false;
    }
  }

  @Override public float zerosmod$getDamage() {
    return this.zerosmod$damage;
  }

  @Override public void zerosmod$setDamage(float damage) {
    this.zerosmod$damage = zerosmod$finiteNonNegative(damage);
    if (this.zerosmod$damage <= 0.0F) {
      this.zerosmod$ticksSinceDamage = this.zerosmod$damageInterval;
    }
  }

  @Override public int zerosmod$getDamageInterval() {
    return this.zerosmod$damageInterval;
  }

  @Override public void zerosmod$setDamageInterval(int ticks) {
    this.zerosmod$damageInterval = Math.max(1, ticks);
    this.zerosmod$ticksSinceDamage = Math.min(
        this.zerosmod$ticksSinceDamage, this.zerosmod$damageInterval);
  }

  @Override public boolean zerosmod$affectOwner() {
    return this.zerosmod$affectOwner;
  }

  @Override public void zerosmod$setAffectOwner(boolean affects) {
    this.zerosmod$affectOwner = affects;
  }

  @Override public Entity zerosmod$getFollower() {
    EntityEnergyDome dome = zerosmod$self();
    if (dome.worldObj == null) {
      return null;
    }
    if (this.zerosmod$followerOverridden) {
      return zerosmod$resolveCustomFollower();
    }
    return this.followCaster ? dome.getOwnerEntity() : null;
  }

  @Override public void zerosmod$setFollower(Entity entity) {
    EntityEnergyDome dome = zerosmod$self();
    if (entity != null && (entity == dome || entity.worldObj != dome.worldObj)) {
      return;
    }

    this.zerosmod$followerOverridden = true;
    this.followCaster = false;
    this.zerosmod$followerEntityId = entity == null ? -1 : entity.getEntityId();
    this.zerosmod$followerUuid = entity == null ? null : entity.getUniqueID();
    dome.sendClientSync();
  }

  @Override public Entity[] zerosmod$getEntitiesInside() {
    Map<Integer, Entity> entities = zerosmod$collectEntitiesInside();
    return entities.values().toArray(new Entity[entities.size()]);
  }

  @Override public boolean zerosmod$isInside(Entity entity) {
    EntityEnergyDome dome = zerosmod$self();
    return entity != null && entity != dome && entity.worldObj == dome.worldObj
        && !entity.isDead && this.isEntityInside(entity);
  }

  @Override public void zerosmod$setTargetRadius(float radius, int ticks) {
    if (!zerosmod$isFinite(radius)) {
      return;
    }

    EntityEnergyDome dome = zerosmod$self();
    float target = Math.max(0.1F, radius);
    if (ticks <= 0 || target == this.domeRadius) {
      this.setDomeRadius(target);
      this.zerosmod$resizeDuration = 0;
      this.zerosmod$resizeElapsed = 0;
    } else {
      this.zerosmod$resizeStartRadius = this.domeRadius;
      this.zerosmod$resizeTargetRadius = target;
      this.zerosmod$resizeDuration = ticks;
      this.zerosmod$resizeElapsed = 0;
    }
    dome.sendClientSync();
  }

  @Unique private void zerosmod$updateFollower() {
    if (!this.zerosmod$followerOverridden) {
      return;
    }
    Entity follower = zerosmod$resolveCustomFollower();
    if (follower != null && !follower.isDead) {
      zerosmod$self().setPosition(follower.posX, follower.posY, follower.posZ);
    }
  }

  @Unique private Entity zerosmod$resolveCustomFollower() {
    EntityEnergyDome dome = zerosmod$self();
    if (this.zerosmod$followerEntityId < 0 || dome.worldObj == null) {
      return null;
    }

    Entity follower = dome.worldObj.getEntityByID(this.zerosmod$followerEntityId);
    if (zerosmod$isExpectedFollower(follower)) {
      return follower;
    }
    if (this.zerosmod$followerUuid == null) {
      return null;
    }

    for (Object loaded : dome.worldObj.loadedEntityList) {
      if (loaded instanceof Entity && zerosmod$isExpectedFollower((Entity)loaded)) {
        follower = (Entity)loaded;
        this.zerosmod$followerEntityId = follower.getEntityId();
        return follower;
      }
    }
    return null;
  }

  @Unique private boolean zerosmod$isExpectedFollower(Entity entity) {
    return entity != null && !entity.isDead && entity != zerosmod$self()
        && (this.zerosmod$followerUuid == null
            || this.zerosmod$followerUuid.equals(entity.getUniqueID()));
  }

  @Unique private void zerosmod$updateRadius() {
    EntityEnergyDome dome = zerosmod$self();
    if (this.zerosmod$resizeDuration <= 0 || dome.isCharging()) {
      return;
    }

    this.zerosmod$resizeElapsed = Math.min(
        this.zerosmod$resizeDuration, this.zerosmod$resizeElapsed + 1);
    float progress = (float)this.zerosmod$resizeElapsed / (float)this.zerosmod$resizeDuration;
    this.setDomeRadius(this.zerosmod$resizeStartRadius
        + (this.zerosmod$resizeTargetRadius - this.zerosmod$resizeStartRadius) * progress);
    if (this.zerosmod$resizeElapsed >= this.zerosmod$resizeDuration) {
      this.zerosmod$resizeDuration = 0;
      this.zerosmod$resizeElapsed = 0;
    }
  }

  @Unique private Map<Integer, Entity> zerosmod$collectEntitiesInside() {
    Map<Integer, Entity> inside = new HashMap<Integer, Entity>();
    EntityEnergyDome dome = zerosmod$self();
    if (dome.worldObj == null || dome.isDead) {
      return inside;
    }

    double radius = this.domeRadius;
    AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(
        dome.posX - radius, dome.posY - radius, dome.posZ - radius,
        dome.posX + radius, dome.posY + radius, dome.posZ + radius);
    List entities = dome.worldObj.getEntitiesWithinAABBExcludingEntity(dome, bounds);
    for (Object value : entities) {
      if (value instanceof Entity) {
        Entity entity = (Entity)value;
        if (!entity.isDead && this.isEntityInside(entity)) {
          inside.put(entity.getEntityId(), entity);
        }
      }
    }
    return inside;
  }

  @Unique private void zerosmod$damageEntities(Map<Integer, Entity> inside) {
    if (this.zerosmod$damage <= 0.0F) {
      this.zerosmod$ticksSinceDamage = this.zerosmod$damageInterval;
      return;
    }

    if (this.zerosmod$ticksSinceDamage < this.zerosmod$damageInterval) {
      ++this.zerosmod$ticksSinceDamage;
    }
    if (this.zerosmod$ticksSinceDamage < this.zerosmod$damageInterval) {
      return;
    }
    this.zerosmod$ticksSinceDamage = 0;

    Entity owner = zerosmod$self().getOwnerEntity();
    for (Entity entity : inside.values()) {
      if (entity instanceof EntityLivingBase
          && (entity != owner || this.zerosmod$affectOwner)) {
        zerosmod$applyDamage((EntityLivingBase)entity, owner);
      }
    }
  }

  @Unique private void zerosmod$applyDamage(EntityLivingBase target, Entity owner) {
    EntityEnergyDome dome = zerosmod$self();
    boolean ignoreIFrames = dome.isIgnoreIFrames();
    int previousHurtResistance = Ability.clearHurtResistanceIfNeeded(target, ignoreIFrames);
    try {
      boolean handled = false;
      double dx = target.posX - dome.posX;
      double dz = target.posZ - dome.posZ;
      if (dome.getSourceAbility() != null && owner instanceof EntityLivingBase) {
        handled = AbilityController.Instance.fireOnAbilityDamage(
            dome.getSourceAbility(), (EntityLivingBase)owner, target, this.zerosmod$damage,
            0.0F, 0.0F, dx, dz, 1.0F);
      }
      if (!handled && dome.getCustomDamageData() != null && owner instanceof EntityLivingBase) {
        handled = EnergyController.Instance.fireOnEnergyDamage(
            dome, (EntityLivingBase)owner, target, this.zerosmod$damage,
            0.0F, 0.0F, dx, dz, 1.0F, dome.getCustomDamageData());
      }
      if (!handled) {
        if (owner instanceof EntityNPCInterface) {
          target.attackEntityFrom(
              new NpcDamageSource("npc_ability", (EntityNPCInterface)owner),
              this.zerosmod$damage);
        } else if (owner instanceof EntityPlayer) {
          target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)owner),
              this.zerosmod$damage);
        } else if (owner instanceof EntityLivingBase) {
          target.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)owner),
              this.zerosmod$damage);
        } else {
          target.attackEntityFrom(
              new NpcDamageSource("npc_ability", (Entity)null), this.zerosmod$damage);
        }
      }
    } finally {
      Ability.restoreHurtResistanceIfNeeded(target, ignoreIFrames, previousHurtResistance);
    }
  }

  @Unique private EntityEnergyDome zerosmod$self() {
    return (EntityEnergyDome)(Object)this;
  }

  @Unique private static boolean zerosmod$isFinite(float value) {
    return !Float.isNaN(value) && !Float.isInfinite(value);
  }

  @Unique private static float zerosmod$finiteNonNegative(float value) {
    return zerosmod$isFinite(value) ? Math.max(0.0F, value) : 0.0F;
  }
}
