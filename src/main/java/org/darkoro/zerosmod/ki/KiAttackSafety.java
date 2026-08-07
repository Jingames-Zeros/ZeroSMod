package org.darkoro.zerosmod.ki;

import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.darkoro.zerosmod.config.KiAttackConfig;

public final class KiAttackSafety {

  public static final String TAG_IS_KI_ATTACK = "ZeroS_KiAttack";
  public static final String TAG_REMOVED_BY_ZEROS = "ZeroS_KiRemoved";
  private static final String TAG_OWNER_NAME = "ZeroS_KiOwner";
  private static final String TAG_OWNER_ID = "ZeroS_KiOwnerId";
  private static final String TAG_TYPE = "ZeroS_KiType";
  private static final String TAG_SPEED = "ZeroS_KiSpeed";
  private static final String TAG_DENSITY = "ZeroS_KiDensity";
  private static final String TAG_DAMAGE = "ZeroS_KiDamage";
  private static final String TAG_PERC = "ZeroS_KiPerc";
  private static final String TAG_AGE = "ZeroS_KiAge";
  private static final String TAG_STOPPED_HAS_MOTION = "ZeroS_KiStoppedHasMotion";
  private static final String TAG_STOPPED_MOTION_X = "ZeroS_KiStoppedMotionX";
  private static final String TAG_STOPPED_MOTION_Y = "ZeroS_KiStoppedMotionY";
  private static final String TAG_STOPPED_MOTION_Z = "ZeroS_KiStoppedMotionZ";
  private static final String TAG_REDIRECTED = "ZeroS_KiRedirected";
  private static final String TAG_REDIRECT_ORIGIN_X = "ZeroS_KiRedirectOriginX";
  private static final String TAG_REDIRECT_ORIGIN_Y = "ZeroS_KiRedirectOriginY";
  private static final String TAG_REDIRECT_ORIGIN_Z = "ZeroS_KiRedirectOriginZ";
  private static final Map<StopZoneKey, StopZone> STOP_ZONES = new HashMap<StopZoneKey, StopZone>();
  private static final int STOP_ZONE_LOOKAHEAD_TICKS = 1;

  private KiAttackSafety() {}

  public static boolean beforeUpdate(EntityEnergyAtt attack, KiAttackInternals internals) {
    if (!KiAttackConfig.isEnabled()) {
      return false;
    }

    exposeScriptData(attack, internals);

    if (attack.worldObj == null || hasInvalidEntityNumbers(attack) || hasInvalidBox(attack.boundingBox)
        || isOutOfWorldBounds(attack)) {
      kill(attack);
      return true;
    }

    if (!attack.worldObj.isRemote && stopForActiveZone(attack)) {
      return true;
    }

    if (pauseStoppedAttack(attack)) {
      return true;
    }

    if (attack.getEntityData().getBoolean(TAG_REDIRECTED)) {
      clearTargetingState(attack);
      applyRedirectedOrigin(attack, internals);
    }

    sanitizeInternals(attack, internals);
    if (attack.isDead) {
      return true;
    }

    if (!attack.worldObj.isRemote && (shouldKillForLifetime(attack) || shouldKillForOwner(attack)
        || shouldKillForUnloadedPosition(attack))) {
      kill(attack);
      return true;
    }

    return false;
  }

  public static long safePower(Entity entity) {
    if (!(entity instanceof EntityEnergyAtt)) {
      return 1L;
    }

    EntityEnergyAtt attack = (EntityEnergyAtt)entity;
    double damage = finitePositive(attack.getDamage(), 1.0D);
    double speed = Math.max(1.0D, attack.getSpe());
    double density = Math.max(1.0D, attack.getDen());
    double power = damage / 2.0D + speed * 2.0D + density * 10.0D + 1.0D;

    if (!isFinite(power) || power < 1.0D) {
      return 1L;
    }
    if (power > Long.MAX_VALUE) {
      return Long.MAX_VALUE;
    }
    return (long)power;
  }

  public static List capCollisionList(List original) {
    if (original == null || original.size() <= KiAttackConfig.getMaxCollisionListSize()) {
      return original;
    }

    return new ArrayList(original.subList(0, KiAttackConfig.getMaxCollisionListSize()));
  }

  public static boolean hasInvalidBox(AxisAlignedBB box) {
    if (box == null) {
      return true;
    }

    return !isFinite(box.minX) || !isFinite(box.minY) || !isFinite(box.minZ)
        || !isFinite(box.maxX) || !isFinite(box.maxY) || !isFinite(box.maxZ)
        || box.maxX < box.minX || box.maxY < box.minY || box.maxZ < box.minZ
        || box.maxX - box.minX > KiAttackConfig.getMaxSize() * 4.0F
        || box.maxY - box.minY > KiAttackConfig.getMaxSize() * 4.0F
        || box.maxZ - box.minZ > KiAttackConfig.getMaxSize() * 4.0F;
  }

  public static boolean isBadExplosion(EntityEnergyAtt attack, KiAttackInternals internals) {
    if (attack == null || internals == null) {
      return true;
    }

    return hasInvalidEntityNumbers(attack) || !isFinite(internals.zerosmod$getDamage())
        || !isFinite(internals.zerosmod$getExplosionLevel()) || internals.zerosmod$getExplosionLevel() < 0.0F
        || internals.zerosmod$getExplosionLevel() > KiAttackConfig.getMaxSize();
  }

  public static void markNonPersistent(NBTTagCompound nbt) {
    if (nbt != null) {
      nbt.setBoolean(TAG_REMOVED_BY_ZEROS, true);
    }
  }

  public static void killLoadedAttack(EntityEnergyAtt attack) {
    if (KiAttackConfig.killLoadedFromNbt()) {
      kill(attack);
    }
  }

  public static void kill(Entity attack) {
    if (attack != null) {
      attack.getEntityData().setBoolean(TAG_REMOVED_BY_ZEROS, true);
      attack.setDead();
    }
  }

  public static boolean stop(EntityEnergyAtt attack) {
    if (attack == null || attack.isDead) {
      return false;
    }

    NBTTagCompound data = attack.getEntityData();
    boolean wasAlreadyStopped = data.getBoolean(TAG_STOPPED_HAS_MOTION);
    if (!wasAlreadyStopped) {
      saveStoppedMotion(attack, data);
    }

    clearTargetingState(attack);
    attack.motionX = 0.0D;
    attack.motionY = 0.0D;
    attack.motionZ = 0.0D;
    attack.velocityChanged = true;
    return !wasAlreadyStopped;
  }

  public static boolean isStopped(EntityEnergyAtt attack) {
    return attack != null && attack.getEntityData().getBoolean(TAG_STOPPED_HAS_MOTION);
  }

  public static void markRedirected(EntityEnergyAtt attack) {
    if (attack != null) {
      attack.getEntityData().setBoolean(TAG_REDIRECTED, true);
      clearTargetingState(attack);
    }
  }

  public static void markRedirected(EntityEnergyAtt attack, double originX, double originY, double originZ) {
    if (attack != null) {
      NBTTagCompound data = attack.getEntityData();
      data.setBoolean(TAG_REDIRECTED, true);
      data.setDouble(TAG_REDIRECT_ORIGIN_X, originX);
      data.setDouble(TAG_REDIRECT_ORIGIN_Y, originY);
      data.setDouble(TAG_REDIRECT_ORIGIN_Z, originZ);
      clearTargetingState(attack);
      clearRedirectedShrinkState(attack);
      if (attack instanceof KiAttackInternals) {
        ((KiAttackInternals)attack).zerosmod$setStartPosition((float)originX, (float)originY, (float)originZ);
      }
      resetRedirectedTrailStart(attack);
    }
  }

  public static boolean isRedirected(EntityEnergyAtt attack) {
    return attack != null && attack.getEntityData().getBoolean(TAG_REDIRECTED);
  }

  public static void applyRedirectedOrigin(EntityEnergyAtt attack, KiAttackInternals internals) {
    if (attack == null || internals == null || !isRedirected(attack)) {
      return;
    }

    NBTTagCompound data = attack.getEntityData();
    if (!data.hasKey(TAG_REDIRECT_ORIGIN_X) || !data.hasKey(TAG_REDIRECT_ORIGIN_Y)
        || !data.hasKey(TAG_REDIRECT_ORIGIN_Z)) {
      return;
    }

    double originX = data.getDouble(TAG_REDIRECT_ORIGIN_X);
    double originY = data.getDouble(TAG_REDIRECT_ORIGIN_Y);
    double originZ = data.getDouble(TAG_REDIRECT_ORIGIN_Z);
    if (isFinite(originX) && isFinite(originY) && isFinite(originZ)) {
      internals.zerosmod$setStartPosition((float)originX, (float)originY, (float)originZ);
      clearRedirectedShrinkState(attack);
    }
  }

  public static void clearRedirectedShrinkState(EntityEnergyAtt attack) {
    if (attack == null || !isRedirected(attack) || (!attack.isLaser() && !attack.isSpiral())) {
      return;
    }

    if (attack instanceof KiAttackInternals) {
      ((KiAttackInternals)attack).zerosmod$setShrink(false);
    }
    attack.getDataWatcher().updateObject(20, Integer.valueOf(0));
    attack.waveScale = 1.0F;
  }

  private static void resetRedirectedTrailStart(EntityEnergyAtt attack) {
    if (attack == null || (!attack.isLaser() && !attack.isSpiral())) {
      return;
    }

    attack.lastSegments = 0;
    attack.added2 = false;
    if (!isFinite(attack.dist)) {
      attack.dist = 0.0D;
    }
    if (!isFinite(attack.finalDist)) {
      attack.finalDist = 0.0D;
    }
  }

  public static void clearRedirected(EntityEnergyAtt attack) {
    if (attack != null) {
      NBTTagCompound data = attack.getEntityData();
      data.removeTag(TAG_REDIRECTED);
      data.removeTag(TAG_REDIRECT_ORIGIN_X);
      data.removeTag(TAG_REDIRECT_ORIGIN_Y);
      data.removeTag(TAG_REDIRECT_ORIGIN_Z);
    }
  }

  public static void registerStopZone(Entity center, int range) {
    if (center == null || center.worldObj == null || center.isDead || range <= 0) {
      return;
    }

    int safeRange = MathHelper.clamp_int(range, 1, 128);
    STOP_ZONES.put(new StopZoneKey(center, safeRange), new StopZone(center, safeRange));
  }

  public static void removeStopZones(Entity center, int range) {
    if (center == null || center.worldObj == null) {
      return;
    }

    int safeRange = MathHelper.clamp_int(range, 1, 128);
    Iterator<Map.Entry<StopZoneKey, StopZone>> iterator = STOP_ZONES.entrySet().iterator();
    while (iterator.hasNext()) {
      StopZone zone = iterator.next().getValue();
      if (!zone.isLive() || zone.matches(center, safeRange)) {
        iterator.remove();
      }
    }
  }

  public static boolean isInScriptRange(Entity center, EntityEnergyAtt attack, int range) {
    return center != null && attack != null && pathIntersectsRange(center, attack, MathHelper.clamp_int(range, 1, 128));
  }

  public static double scriptRangeQueryRadius(int range) {
    return MathHelper.clamp_int(range, 1, 128)
        + Math.min(16.0D, KiAttackConfig.getMaxMotion() * STOP_ZONE_LOOKAHEAD_TICKS);
  }

  public static boolean releaseStoppedAttack(EntityEnergyAtt attack) {
    if (attack == null) {
      return false;
    }

    NBTTagCompound data = attack.getEntityData();
    boolean wasStopped = data.getBoolean(TAG_STOPPED_HAS_MOTION);
    restoreStoppedMotion(attack, data);
    clearTargetingState(attack);
    clearStoppedMotion(data);
    return wasStopped;
  }

  private static void sanitizeInternals(EntityEnergyAtt attack, KiAttackInternals internals) {
    int type = internals.zerosmod$getType();
    if (type < 0 || type > 8) {
      kill(attack);
      return;
    }

    int speed = internals.zerosmod$getSpeed();
    if (speed < 0 || speed > 3) {
      internals.zerosmod$setSpeed((byte)MathHelper.clamp_int(speed, 0, 3));
    }

    if (internals.zerosmod$getPerc() <= 0) {
      internals.zerosmod$setPerc((byte)1);
    } else if (internals.zerosmod$getPerc() > 100) {
      internals.zerosmod$setPerc((byte)100);
    }

    if (internals.zerosmod$getDensity() <= 0) {
      internals.zerosmod$setDensity((byte)1);
    }

    double damage = internals.zerosmod$getDamage();
    if (!isFinite(damage) || damage <= 0.0D) {
      kill(attack);
      return;
    }
    if (damage > KiAttackConfig.getMaxDamage()) {
      internals.zerosmod$setDamage(KiAttackConfig.getMaxDamage());
    }

    double originalDamage = internals.zerosmod$getDamageOriginal();
    if (!isFinite(originalDamage) || originalDamage <= 0.0D) {
      internals.zerosmod$setDamageOriginal(internals.zerosmod$getDamage());
    } else if (originalDamage > KiAttackConfig.getMaxDamage()) {
      internals.zerosmod$setDamageOriginal(KiAttackConfig.getMaxDamage());
    }

    float size = internals.zerosmod$getSize();
    if (!isFinite(size) || size <= 0.0F) {
      internals.zerosmod$setSize(0.1F);
    } else if (size > KiAttackConfig.getMaxSize()) {
      internals.zerosmod$setSize(KiAttackConfig.getMaxSize());
    }

    if (!isFinite(internals.zerosmod$getExplosionLevel()) || internals.zerosmod$getExplosionLevel() < 0.0F) {
      internals.zerosmod$setExplosionLevel(0.0F);
    } else if (internals.zerosmod$getExplosionLevel() > KiAttackConfig.getMaxSize()) {
      internals.zerosmod$setExplosionLevel(KiAttackConfig.getMaxSize());
    }

    clampMotion(attack);

    if (!isFinite(attack.rotationPitch)) attack.rotationPitch = 0.0F;
    if (!isFinite(attack.rotationYaw)) attack.rotationYaw = 0.0F;
    if (!isFinite(attack.prevRotationPitch)) attack.prevRotationPitch = attack.rotationPitch;
    if (!isFinite(attack.prevRotationYaw)) attack.prevRotationYaw = attack.rotationYaw;
  }

  private static boolean pauseStoppedAttack(EntityEnergyAtt attack) {
    NBTTagCompound data = attack.getEntityData();
    if (!data.getBoolean(TAG_STOPPED_HAS_MOTION)) {
      return false;
    }

    attack.motionX = 0.0D;
    attack.motionY = 0.0D;
    attack.motionZ = 0.0D;
    attack.velocityChanged = true;
    return true;
  }

  private static void saveStoppedMotion(EntityEnergyAtt attack, NBTTagCompound data) {
    data.setBoolean(TAG_STOPPED_HAS_MOTION, true);
    data.setDouble(TAG_STOPPED_MOTION_X, finiteOrZero(attack.motionX));
    data.setDouble(TAG_STOPPED_MOTION_Y, finiteOrZero(attack.motionY));
    data.setDouble(TAG_STOPPED_MOTION_Z, finiteOrZero(attack.motionZ));
  }

  private static void restoreStoppedMotion(EntityEnergyAtt attack, NBTTagCompound data) {
    if (!data.getBoolean(TAG_STOPPED_HAS_MOTION)) {
      return;
    }

    attack.motionX = finiteOrZero(data.getDouble(TAG_STOPPED_MOTION_X));
    attack.motionY = finiteOrZero(data.getDouble(TAG_STOPPED_MOTION_Y));
    attack.motionZ = finiteOrZero(data.getDouble(TAG_STOPPED_MOTION_Z));
    attack.velocityChanged = true;
  }

  private static void clearStoppedMotion(NBTTagCompound data) {
    data.removeTag(TAG_STOPPED_HAS_MOTION);
    data.removeTag(TAG_STOPPED_MOTION_X);
    data.removeTag(TAG_STOPPED_MOTION_Y);
    data.removeTag(TAG_STOPPED_MOTION_Z);
  }

  private static void clearTargetingState(EntityEnergyAtt attack) {
    attack.shooterHolds = false;
    attack.hadTarget = false;
    if (attack instanceof KiAttackInternals) {
      ((KiAttackInternals)attack).zerosmod$setTarget(null);
    }
  }

  private static boolean stopForActiveZone(EntityEnergyAtt attack) {
    if (STOP_ZONES.isEmpty() || attack == null || attack.worldObj == null || attack.isDead) {
      return false;
    }

    boolean stopped = false;
    Iterator<Map.Entry<StopZoneKey, StopZone>> iterator = STOP_ZONES.entrySet().iterator();
    while (iterator.hasNext()) {
      StopZone zone = iterator.next().getValue();
      if (!zone.isLive()) {
        iterator.remove();
        continue;
      }
      if (zone.matches(attack) && stop(attack)) {
        stopped = true;
      }
    }
    return stopped || attack.getEntityData().getBoolean(TAG_STOPPED_HAS_MOTION);
  }

  private static boolean pathIntersectsRange(Entity center, EntityEnergyAtt attack, int range) {
    double radius = range;
    double radiusSq = radius * radius;
    double centerX = center.posX;
    double centerY = center.posY + center.height * 0.5D;
    double centerZ = center.posZ;

    if (distanceSq(centerX, centerY, centerZ, attack.posX, attack.posY, attack.posZ) <= radiusSq) {
      return true;
    }

    if (attack.boundingBox != null && distanceToBoxSq(centerX, centerY, centerZ, attack.boundingBox) <= radiusSq) {
      return true;
    }

    double maxMotion = KiAttackConfig.getMaxMotion();
    double motionX = clamp(attack.motionX, -maxMotion, maxMotion);
    double motionY = clamp(attack.motionY, -maxMotion, maxMotion);
    double motionZ = clamp(attack.motionZ, -maxMotion, maxMotion);
    double endX = attack.posX + motionX * STOP_ZONE_LOOKAHEAD_TICKS;
    double endY = attack.posY + motionY * STOP_ZONE_LOOKAHEAD_TICKS;
    double endZ = attack.posZ + motionZ * STOP_ZONE_LOOKAHEAD_TICKS;

    if (distanceToSegmentSq(centerX, centerY, centerZ, attack.posX, attack.posY, attack.posZ, endX, endY, endZ) <= radiusSq) {
      return true;
    }

    AxisAlignedBB movedBox = attack.boundingBox == null ? null : attack.boundingBox.addCoord(motionX, motionY, motionZ);
    return movedBox != null && distanceToBoxSq(centerX, centerY, centerZ, movedBox) <= radiusSq;
  }

  private static double distanceToBoxSq(double px, double py, double pz, AxisAlignedBB box) {
    double dx = 0.0D;
    if (px < box.minX) {
      dx = box.minX - px;
    } else if (px > box.maxX) {
      dx = px - box.maxX;
    }

    double dy = 0.0D;
    if (py < box.minY) {
      dy = box.minY - py;
    } else if (py > box.maxY) {
      dy = py - box.maxY;
    }

    double dz = 0.0D;
    if (pz < box.minZ) {
      dz = box.minZ - pz;
    } else if (pz > box.maxZ) {
      dz = pz - box.maxZ;
    }

    return dx * dx + dy * dy + dz * dz;
  }

  private static double distanceToSegmentSq(double px, double py, double pz,
      double ax, double ay, double az, double bx, double by, double bz) {
    double abX = bx - ax;
    double abY = by - ay;
    double abZ = bz - az;
    double abLenSq = abX * abX + abY * abY + abZ * abZ;
    if (abLenSq <= 0.000001D || !isFinite(abLenSq)) {
      return distanceSq(px, py, pz, ax, ay, az);
    }

    double t = ((px - ax) * abX + (py - ay) * abY + (pz - az) * abZ) / abLenSq;
    t = clamp(t, 0.0D, 1.0D);
    return distanceSq(px, py, pz, ax + abX * t, ay + abY * t, az + abZ * t);
  }

  private static double distanceSq(double ax, double ay, double az, double bx, double by, double bz) {
    double dx = ax - bx;
    double dy = ay - by;
    double dz = az - bz;
    return dx * dx + dy * dy + dz * dz;
  }

  private static boolean shouldKillForLifetime(EntityEnergyAtt attack) {
    int maxTicks = KiAttackConfig.maxTicksForType(attack.getType());
    return attack.ticksExisted > maxTicks;
  }

  private static boolean shouldKillForOwner(EntityEnergyAtt attack) {
    Entity owner = attack.shootingEntity;
    if (owner == null) {
      return attack.ticksExisted > KiAttackConfig.getMaxOrphanTicks();
    }
    if (owner.isDead) {
      return true;
    }
    return owner.worldObj != null && attack.worldObj != null && owner.worldObj != attack.worldObj;
  }

  private static boolean shouldKillForUnloadedPosition(EntityEnergyAtt attack) {
    if (!KiAttackConfig.killOutsideLoadedChunk()) {
      return false;
    }

    World world = attack.worldObj;
    if (world == null) {
      return true;
    }

    int x = MathHelper.floor_double(attack.posX);
    int y = MathHelper.floor_double(attack.posY);
    int z = MathHelper.floor_double(attack.posZ);
    int nextX = MathHelper.floor_double(attack.posX + attack.motionX);
    int nextY = MathHelper.floor_double(attack.posY + attack.motionY);
    int nextZ = MathHelper.floor_double(attack.posZ + attack.motionZ);

    return !world.blockExists(x, y, z) || !world.blockExists(nextX, nextY, nextZ);
  }

  private static void exposeScriptData(EntityEnergyAtt attack, KiAttackInternals internals) {
    if (!KiAttackConfig.exposeScriptData()) {
      return;
    }

    NBTTagCompound data = attack.getEntityData();
    data.setBoolean(TAG_IS_KI_ATTACK, true);
    data.setInteger(TAG_AGE, attack.ticksExisted);

    Entity owner = attack.shootingEntity;
    if (owner != null) {
      data.setInteger(TAG_OWNER_ID, owner.getEntityId());
      data.setString(TAG_OWNER_NAME, owner.getCommandSenderName());
    }

    data.setInteger(TAG_TYPE, internals.zerosmod$getType());
    data.setInteger(TAG_SPEED, internals.zerosmod$getSpeed());
    data.setInteger(TAG_DENSITY, internals.zerosmod$getDensity());
    data.setDouble(TAG_DAMAGE, internals.zerosmod$getDamage());
    data.setInteger(TAG_PERC, internals.zerosmod$getPerc());
  }

  private static boolean hasInvalidEntityNumbers(Entity entity) {
    return entity == null
        || !isFinite(entity.posX) || !isFinite(entity.posY) || !isFinite(entity.posZ)
        || !isFinite(entity.prevPosX) || !isFinite(entity.prevPosY) || !isFinite(entity.prevPosZ)
        || !isFinite(entity.motionX) || !isFinite(entity.motionY) || !isFinite(entity.motionZ);
  }

  private static boolean isOutOfWorldBounds(Entity entity) {
    return entity.posY < 0.0D
        || entity.posY > KiAttackConfig.getMaxY();
  }

  private static void clampMotion(Entity entity) {
    double max = KiAttackConfig.getMaxMotion();
    entity.motionX = clamp(entity.motionX, -max, max);
    entity.motionY = clamp(entity.motionY, -max, max);
    entity.motionZ = clamp(entity.motionZ, -max, max);
  }

  private static double finitePositive(double value, double fallback) {
    return isFinite(value) && value > 0.0D ? value : fallback;
  }

  private static double finiteOrZero(double value) {
    return isFinite(value) ? value : 0.0D;
  }

  private static double clamp(double value, double min, double max) {
    if (!isFinite(value)) {
      return 0.0D;
    }
    if (value < min) return min;
    if (value > max) return max;
    return value;
  }

  private static boolean isFinite(double value) {
    return !Double.isNaN(value) && !Double.isInfinite(value);
  }

  public interface KiAttackInternals {
    int zerosmod$getType();
    int zerosmod$getSpeed();
    void zerosmod$setSpeed(byte speed);
    int zerosmod$getPerc();
    void zerosmod$setPerc(byte perc);
    int zerosmod$getDensity();
    void zerosmod$setDensity(byte density);
    double zerosmod$getDamage();
    void zerosmod$setDamage(double damage);
    double zerosmod$getDamageOriginal();
    void zerosmod$setDamageOriginal(double damageOriginal);
    float zerosmod$getSize();
    void zerosmod$setSize(float size);
    float zerosmod$getExplosionLevel();
    void zerosmod$setExplosionLevel(float explosionLevel);
    int zerosmod$getColor();
    void zerosmod$setColor(int color);
    int zerosmod$getColor2();
    void zerosmod$setColor2(int color2);
    Entity zerosmod$getOwner();
    void zerosmod$setOwner(Entity owner);
    Entity zerosmod$getTarget();
    void zerosmod$setTarget(Entity target);
    void zerosmod$setStartPosition(float x, float y, float z);
    void zerosmod$setTargetPosition(float x, float y, float z);
    void zerosmod$setShrink(boolean shrink);
  }

  private static final class StopZoneKey {
    private final int dimension;
    private final int entityId;
    private final int range;

    private StopZoneKey(Entity center, int range) {
      this.dimension = center.dimension;
      this.entityId = center.getEntityId();
      this.range = range;
    }

    @Override public boolean equals(Object obj) {
      if (!(obj instanceof StopZoneKey)) {
        return false;
      }

      StopZoneKey other = (StopZoneKey)obj;
      return this.dimension == other.dimension && this.entityId == other.entityId && this.range == other.range;
    }

    @Override public int hashCode() {
      int result = this.dimension;
      result = 31 * result + this.entityId;
      result = 31 * result + this.range;
      return result;
    }
  }

  private static final class StopZone {
    private final Entity center;
    private final int range;

    private StopZone(Entity center, int range) {
      this.center = center;
      this.range = range;
    }

    private boolean isLive() {
      return this.center != null && this.center.worldObj != null && !this.center.isDead;
    }

    private boolean matches(Entity center, int releaseRange) {
      return isLive() && this.center == center && this.range <= releaseRange;
    }

    private boolean matches(EntityEnergyAtt attack) {
      return isLive()
          && attack != null
          && attack.worldObj == this.center.worldObj
          && attack.dimension == this.center.dimension
          && attack.shootingEntity != this.center
          && !attack.getEntityData().getBoolean(TAG_REDIRECTED)
          && pathIntersectsRange(this.center, attack, this.range);
    }
  }
}
