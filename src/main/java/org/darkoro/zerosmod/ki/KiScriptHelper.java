package org.darkoro.zerosmod.ki;

import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import cpw.mods.fml.common.network.NetworkRegistry;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.scripted.NpcAPI;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.network.SyncKiAttackColorPacket;
import org.darkoro.zerosmod.network.SyncKiAttackStatePacket;

public final class KiScriptHelper {

  private static final int MAX_SCRIPT_RANGE = 128;
  private static final String[] TYPE_NAMES = new String[] {
      "Wave", "Blast", "Disk", "Laser", "Spiral", "BigBlast", "Barrage", "Shield", "Explosion"
  };
  private static final String[] COLOR_NAMES = new String[] {
      "AlignmentBased", "white", "blue", "purple", "red", "black", "green", "yellow", "orange", "pink",
      "magenta", "lightPink", "cyan", "darkCyan", "lightCyan", "darkGray", "gray", "darkBlue",
      "lightBlue", "darkPurple", "lightPurple", "darkRed", "lightRed", "darkGreen", "lime",
      "darkYellow", "lightYellow", "gold", "lightOrange", "darkBrown", "lightBrown"
  };
  private static Field destroyerField;
  private static boolean destroyerFieldChecked;
  private static Field targetField;
  private static boolean targetFieldChecked;

  private KiScriptHelper() {}

  public static boolean isKiAttack(Object value) {
    return asKiAttack(value) != null;
  }

  public static boolean isPlayerKi(Object value) {
    return getOwner(value) instanceof EntityPlayer;
  }

  public static boolean isNpcKi(Object value) {
    return getOwner(value) instanceof EntityNPCInterface;
  }

  public static boolean isDestroyerAttack(Object value) {
    EntityEnergyAtt attack = asKiAttack(value);
    if (attack == null) {
      return false;
    }

    try {
      Field field = getDestroyerField(attack);
      return field != null && field.getBoolean(attack);
    } catch (Throwable ignored) {
      return false;
    }
  }

  public static int getKiId(Object value) {
    EntityEnergyAtt attack = asKiAttack(value);
    return attack == null ? -1 : attack.getEntityId();
  }

  public static boolean isKiStopped(Object value) {
    EntityEnergyAtt attack = asKiAttack(value);
    return attack != null && KiAttackSafety.isStopped(attack);
  }

  public static String getPlayerName(Object value) {
    Entity owner = getOwner(value);
    return owner instanceof EntityPlayer ? owner.getCommandSenderName() : "";
  }

  public static String getKiOwnerName(Object value) {
    Entity owner = getOwner(value);
    return owner == null ? "" : owner.getCommandSenderName();
  }

  public static IEntity getKiOwner(Object value) {
    Entity owner = getOwner(value);
    return owner == null ? null : NpcAPI.Instance().getIEntity(owner);
  }

  public static String getKiType(Object value) {
    int type = getKiTypeId(value);
    return type >= 0 && type < TYPE_NAMES.length ? TYPE_NAMES[type] : "Unknown";
  }

  public static int getKiTypeId(Object value) {
    EntityEnergyAtt attack = asKiAttack(value);
    return attack == null ? -1 : attack.getType();
  }

  public static double getKiDamage(Object value) {
    EntityEnergyAtt attack = asKiAttack(value);
    return attack == null ? 0.0D : attack.getDamage();
  }

  public static boolean setKiDamage(Object value, double damage) {
    EntityEnergyAtt attack = asKiAttack(value);
    if (attack == null || !isFinite(damage) || damage <= 0.0D) {
      return false;
    }

    attack.setDamage(damage);
    if (attack instanceof KiAttackSafety.KiAttackInternals) {
      ((KiAttackSafety.KiAttackInternals)attack).zerosmod$setDamageOriginal(damage);
    }
    return true;
  }

  public static boolean setKiColor(Object value, String color) {
    Integer colorId = parseColor(color);
    return colorId != null && setKiColor(value, colorId.intValue());
  }

  public static boolean setKiColor(Object value, int color) {
    return setKiColor(value, color, -1);
  }

  public static boolean setKiColor(Object value, int color, int color2) {
    EntityEnergyAtt attack = asKiAttack(value);
    if (attack == null || !isValidColor(color) || (color2 != -1 && !isValidColor(color2))) {
      return false;
    }

    KiAttackSafety.KiAttackInternals internals = getInternals(attack);
    if (internals == null) {
      return false;
    }

    internals.zerosmod$setColor(color);
    internals.zerosmod$setColor2(color2);
    syncColor(attack, color, color2);
    return true;
  }

  public static boolean stealKi(Object value, String color, Object owner) {
    Integer colorId = parseColor(color);
    return colorId != null && stealKi(value, colorId.intValue(), owner);
  }

  public static boolean stealKi(Object value, int color, Object owner) {
    EntityEnergyAtt attack = asKiAttack(value);
    Entity ownerEntity = asEntity(owner);
    if (attack == null || ownerEntity == null || ownerEntity.isDead || !isValidColor(color)) {
      return false;
    }

    KiAttackSafety.KiAttackInternals internals = getInternals(attack);
    if (internals == null) {
      return false;
    }

    KiAttackSafety.clearRedirected(attack);
    internals.zerosmod$setOwner(ownerEntity);
    internals.zerosmod$setColor(color);
    internals.zerosmod$setColor2(-1);
    resetKiLifetime(attack);
    attack.shooterHolds = false;
    clearTargeting(attack);
    syncColor(attack, color, -1);
    return true;
  }

  public static boolean redirectKi(Object value, Object target) {
    EntityEnergyAtt attack = asKiAttack(value);
    Entity targetEntity = asEntity(target);
    if (attack == null || targetEntity == null || targetEntity.isDead || targetEntity == attack) {
      return false;
    }

    double targetY = targetEntity.posY + targetEntity.height * 0.5D;
    if (targetEntity instanceof EntityLivingBase) {
      targetY = targetEntity.posY + ((EntityLivingBase)targetEntity).getEyeHeight() * 0.75D;
    }

    double dx = targetEntity.posX - attack.posX;
    double dy = targetY - attack.posY;
    double dz = targetEntity.posZ - attack.posZ;
    double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
    if (!isFinite(len) || len < 0.0001D) {
      return false;
    }

    return applyDirection(attack, dx / len, dy / len, dz / len, targetEntity.posX, targetY, targetEntity.posZ);
  }

  public static boolean directKi(Object value, int x, int y, int z) {
    return directKi(value, (double)x, (double)y, (double)z);
  }

  public static boolean directKi(Object value, double x, double y, double z) {
    EntityEnergyAtt attack = asKiAttack(value);
    if (attack == null || attack.isDead || !isFinite(x) || !isFinite(y) || !isFinite(z)) {
      return false;
    }

    double len = Math.sqrt(x * x + y * y + z * z);
    if (!isFinite(len) || len < 0.0001D) {
      return false;
    }

    double dirX = x / len;
    double dirY = y / len;
    double dirZ = z / len;
    double targetX = attack.posX + dirX * 64.0D;
    double targetY = attack.posY + dirY * 64.0D;
    double targetZ = attack.posZ + dirZ * 64.0D;
    return applyDirection(attack, dirX, dirY, dirZ, targetX, targetY, targetZ);
  }

  private static boolean applyDirection(EntityEnergyAtt attack, double dirX, double dirY, double dirZ,
      double targetX, double targetY, double targetZ) {
    if (attack == null || attack.isDead) {
      return false;
    }

    KiAttackSafety.releaseStoppedAttack(attack);
    attack.shooterHolds = false;
    clearTargeting(attack);

    double originX = attack.posX;
    double originY = attack.posY;
    double originZ = attack.posZ;
    KiAttackSafety.KiAttackInternals internals = getInternals(attack);
    if (internals != null) {
      internals.zerosmod$setStartPosition((float)originX, (float)originY, (float)originZ);
      internals.zerosmod$setTargetPosition((float)targetX, (float)targetY, (float)targetZ);
    }

    double speed = Math.sqrt(attack.motionX * attack.motionX + attack.motionY * attack.motionY + attack.motionZ * attack.motionZ);
    if (!isFinite(speed) || speed < 0.05D) {
      speed = Math.max(0.25D, attack.getSpe() * 0.4D);
    }

    attack.motionX = dirX * speed;
    attack.motionY = dirY * speed;
    attack.motionZ = dirZ * speed;
    attack.motionXStart = attack.motionX;
    attack.motionYStart = attack.motionY;
    attack.motionZStart = attack.motionZ;
    attack.rotationYaw = (float)(Math.atan2(attack.motionX, attack.motionZ) * 180.0D / Math.PI);
    double horizontal = Math.sqrt(attack.motionX * attack.motionX + attack.motionZ * attack.motionZ);
    attack.rotationPitch = (float)(Math.atan2(attack.motionY, horizontal) * 180.0D / Math.PI);
    attack.prevRotationYaw = attack.rotationYaw;
    attack.prevRotationPitch = attack.rotationPitch;
    attack.startRotationYaw = attack.rotationYaw;
    attack.startRotationPitch = attack.rotationPitch;
    attack.velocityChanged = true;
    KiAttackSafety.markRedirected(attack, originX, originY, originZ);
    syncVelocity(attack);
    syncState(attack, targetX, targetY, targetZ);
    return true;
  }

  public static boolean stopKi(Object value) {
    EntityEnergyAtt attack = asKiAttack(value);
    boolean stopped = attack != null && KiAttackSafety.stop(attack);
    if (stopped) {
      syncVelocity(attack);
    }
    return stopped;
  }

  public static boolean stopKi(Object value, int ignoredTicks) {
    return stopKi(value);
  }

  public static boolean releaseKi(Object value) {
    EntityEnergyAtt attack = asKiAttack(value);
    boolean released = attack != null && KiAttackSafety.releaseStoppedAttack(attack);
    if (released) {
      syncVelocity(attack);
    }
    return released;
  }

  public static boolean resetKiLifetime(Object value) {
    EntityEnergyAtt attack = asKiAttack(value);
    if (attack == null) {
      return false;
    }

    resetKiLifetime(attack);
    return true;
  }

  public static boolean killKi(Object value) {
    EntityEnergyAtt attack = asKiAttack(value);
    if (attack == null) {
      return false;
    }

    KiAttackSafety.kill(attack);
    return true;
  }

  public static int stopKiAround(Object center, int range) {
    Entity centerEntity = asEntity(center);
    if (centerEntity == null || centerEntity.worldObj == null || range <= 0) {
      return 0;
    }

    int count = 0;
    int safeRange = MathHelper.clamp_int(range, 1, MAX_SCRIPT_RANGE);
    KiAttackSafety.registerStopZone(centerEntity, safeRange);
    for (EntityEnergyAtt attack : getKiAttacksAround(centerEntity, safeRange, true)) {
      if (attack != centerEntity && attack.shootingEntity != centerEntity
          && KiAttackSafety.isInScriptRange(centerEntity, attack, safeRange)
          && KiAttackSafety.stop(attack)) {
        syncVelocity(attack);
        count++;
      }
    }
    return count;
  }

  public static int stopKiAround(Object center, int range, int ignoredTicks) {
    return stopKiAround(center, range);
  }

  public static int releaseKiAround(Object center, int range) {
    Entity centerEntity = asEntity(center);
    if (centerEntity == null || centerEntity.worldObj == null || range <= 0) {
      return 0;
    }

    int count = 0;
    int safeRange = MathHelper.clamp_int(range, 1, MAX_SCRIPT_RANGE);
    KiAttackSafety.removeStopZones(centerEntity, safeRange);
    double rangeSq = safeRange * safeRange;
    for (EntityEnergyAtt attack : getKiAttacksAround(centerEntity, safeRange, false)) {
      if (attack != centerEntity && centerEntity.getDistanceSqToEntity(attack) <= rangeSq
          && KiAttackSafety.releaseStoppedAttack(attack)) {
        syncVelocity(attack);
        count++;
      }
    }
    return count;
  }

  public static IEntity[] getNearbyKi(Object center, int range) {
    Entity centerEntity = asEntity(center);
    if (centerEntity == null || centerEntity.worldObj == null || range <= 0) {
      return new IEntity[0];
    }

    int safeRange = MathHelper.clamp_int(range, 1, MAX_SCRIPT_RANGE);
    List<IEntity> wrapped = new ArrayList<IEntity>();
    for (EntityEnergyAtt attack : getKiAttacksAround(centerEntity, safeRange, true)) {
      if (attack != centerEntity && KiAttackSafety.isInScriptRange(centerEntity, attack, safeRange)) {
        wrapped.add(NpcAPI.Instance().getIEntity(attack));
      }
    }
    return wrapped.toArray(new IEntity[wrapped.size()]);
  }

  private static List<EntityEnergyAtt> getKiAttacksAround(Entity center, int range, boolean includeMotionLookahead) {
    World world = center.worldObj;
    double queryRange = includeMotionLookahead ? KiAttackSafety.scriptRangeQueryRadius(range) : range;
    AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
        center.posX - queryRange, center.posY - queryRange, center.posZ - queryRange,
        center.posX + queryRange, center.posY + queryRange, center.posZ + queryRange);
    return world.getEntitiesWithinAABB(EntityEnergyAtt.class, box);
  }

  private static EntityEnergyAtt asKiAttack(Object value) {
    Entity entity = asEntity(value);
    return entity instanceof EntityEnergyAtt ? (EntityEnergyAtt)entity : null;
  }

  private static Entity asEntity(Object value) {
    if (value instanceof Entity) {
      return (Entity)value;
    }
    if (value instanceof IEntity) {
      return ((IEntity)value).getMCEntity();
    }
    return null;
  }

  private static Entity getOwner(Object value) {
    EntityEnergyAtt attack = asKiAttack(value);
    if (attack == null) {
      return null;
    }

    KiAttackSafety.KiAttackInternals internals = getInternals(attack);
    return internals == null ? attack.shootingEntity : internals.zerosmod$getOwner();
  }

  private static KiAttackSafety.KiAttackInternals getInternals(EntityEnergyAtt attack) {
    return attack instanceof KiAttackSafety.KiAttackInternals ? (KiAttackSafety.KiAttackInternals)attack : null;
  }

  private static void setTarget(EntityEnergyAtt attack, Entity target) {
    KiAttackSafety.KiAttackInternals internals = getInternals(attack);
    if (internals != null) {
      internals.zerosmod$setTarget(target);
      return;
    }

    try {
      Field field = getTargetField(attack);
      if (field != null) {
        field.set(attack, target);
      }
    } catch (Throwable ignored) {
    }
  }

  private static void clearTargeting(EntityEnergyAtt attack) {
    setTarget(attack, null);
    attack.hadTarget = false;
  }

  private static void resetKiLifetime(EntityEnergyAtt attack) {
    attack.ticksExisted = 0;
    attack.setAirTicks(0);
  }

  private static void syncColor(EntityEnergyAtt attack, int color, int color2) {
    if (attack == null || attack.worldObj == null || attack.worldObj.isRemote || ZeroSMod.network == null) {
      return;
    }

    ZeroSMod.network.sendToAllAround(
        new SyncKiAttackColorPacket(attack.getEntityId(), color, color2),
        new NetworkRegistry.TargetPoint(attack.dimension, attack.posX, attack.posY, attack.posZ, 96.0D));
  }

  private static void syncVelocity(EntityEnergyAtt attack) {
    if (attack == null || attack.worldObj == null || attack.worldObj.isRemote
        || !(attack.worldObj instanceof WorldServer)) {
      return;
    }

    ((WorldServer)attack.worldObj).getEntityTracker().func_151248_b(attack, new S12PacketEntityVelocity(attack));
  }

  private static void syncState(EntityEnergyAtt attack, double targetX, double targetY, double targetZ) {
    if (attack == null || attack.worldObj == null || attack.worldObj.isRemote || ZeroSMod.network == null) {
      return;
    }

    ZeroSMod.network.sendToAllAround(
        new SyncKiAttackStatePacket(
            attack.getEntityId(),
            attack.posX,
            attack.posY,
            attack.posZ,
            attack.motionX,
            attack.motionY,
            attack.motionZ,
            attack.rotationYaw,
            attack.rotationPitch,
            (float)attack.posX,
            (float)attack.posY,
            (float)attack.posZ,
            (float)targetX,
            (float)targetY,
            (float)targetZ),
        new NetworkRegistry.TargetPoint(attack.dimension, attack.posX, attack.posY, attack.posZ, 128.0D));
  }

  private static Field getDestroyerField(EntityEnergyAtt attack) {
    if (!destroyerFieldChecked) {
      destroyerFieldChecked = true;
      destroyerField = findField(attack.getClass(), "destroyer");
    }
    return destroyerField;
  }

  private static Field getTargetField(EntityEnergyAtt attack) {
    if (!targetFieldChecked) {
      targetFieldChecked = true;
      targetField = findField(attack.getClass(), "target");
    }
    return targetField;
  }

  private static Field findField(Class<?> type, String name) {
    Class<?> current = type;
    while (current != null) {
      try {
        Field field = current.getDeclaredField(name);
        field.setAccessible(true);
        return field;
      } catch (NoSuchFieldException ignored) {
        current = current.getSuperclass();
      }
    }
    return null;
  }

  private static Integer parseColor(String color) {
    if (color == null) {
      return null;
    }

    String normalized = normalize(color);
    if (normalized.length() == 0) {
      return null;
    }

    try {
      return Integer.valueOf(Integer.parseInt(normalized));
    } catch (NumberFormatException ignored) {
    }

    for (int i = 0; i < COLOR_NAMES.length; i++) {
      if (normalize(COLOR_NAMES[i]).equals(normalized)) {
        return Integer.valueOf(i);
      }
    }
    return null;
  }

  private static String normalize(String value) {
    return value.toLowerCase(Locale.ROOT).replace("_", "").replace("-", "").replace(" ", "");
  }

  private static boolean isValidColor(int color) {
    return color >= 0 && color < COLOR_NAMES.length;
  }

  private static boolean isFinite(double value) {
    return !Double.isNaN(value) && !Double.isInfinite(value);
  }
}
