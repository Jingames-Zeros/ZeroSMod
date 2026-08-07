package org.darkoro.zerosmod.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ChunkEvent;
import org.darkoro.zerosmod.config.KiAttackConfig;

public final class KiAttackGuard {

  public static final KiAttackGuard INSTANCE = new KiAttackGuard();

  private static final String KI_ATTACK_CLASS_NAME = "JinRyuu.JRMCore.entity.EntityEnergyAtt";
  private static final String TAG_IS_KI_ATTACK = "ZeroS_KiAttack";
  private static final String TAG_REMOVED_BY_ZEROS = "ZeroS_KiRemoved";
  private static final String TAG_OWNER_NAME = "ZeroS_KiOwner";
  private static final String TAG_OWNER_ID = "ZeroS_KiOwnerId";
  private static final String TAG_TYPE = "ZeroS_KiType";
  private static final String TAG_SPEED = "ZeroS_KiSpeed";
  private static final String TAG_DENSITY = "ZeroS_KiDensity";
  private static final String TAG_DAMAGE = "ZeroS_KiDamage";
  private static final String TAG_PERC = "ZeroS_KiPerc";
  private static final String TAG_AGE = "ZeroS_KiAge";

  private static Field shootingEntityField;
  private static boolean shootingEntityFieldResolved;
  private static Field damageField;
  private static boolean damageFieldResolved;

  private KiAttackGuard() {}

  @SubscribeEvent public void onEntityJoinWorld(EntityJoinWorldEvent event) {
    if (!KiAttackConfig.isEnabled() || event.world == null || event.world.isRemote
        || !isKiAttack(event.entity)) {
      return;
    }

    exposeScriptData(event.entity);
    if (shouldKillOnJoin(event.entity)) {
      markRemoved(event.entity);
      event.entity.setDead();
      event.setCanceled(true);
    }
  }

  @SubscribeEvent public void onChunkUnload(ChunkEvent.Unload event) {
    if (!KiAttackConfig.killOnChunkUnload() || event.world == null || event.world.isRemote) {
      return;
    }

    Chunk chunk = event.getChunk();
    if (chunk == null || chunk.entityLists == null) {
      return;
    }

    for (List list : chunk.entityLists) {
      for (Object value : list) {
        if (value instanceof Entity && isKiAttack((Entity)value)) {
          Entity entity = (Entity)value;
          markRemoved(entity);
          entity.setDead();
        }
      }
    }
  }

  @SubscribeEvent public void onWorldTick(TickEvent.WorldTickEvent event) {
    if (!KiAttackConfig.isEnabled() || event.phase != TickEvent.Phase.END || event.world == null
        || event.world.isRemote || event.world.getTotalWorldTime() % 2L != 0L) {
      return;
    }

    List loadedEntities = new ArrayList(event.world.loadedEntityList);
    for (Object value : loadedEntities) {
      if (value instanceof Entity && isKiAttack((Entity)value)) {
        guardLiveAttack((Entity)value);
      }
    }
  }

  private static void guardLiveAttack(Entity attack) {
    exposeScriptData(attack);

    if (shouldKillLiveAttack(attack)) {
      markRemoved(attack);
      attack.setDead();
      return;
    }

    clampMotion(attack);
    capDamage(attack);
  }

  private static boolean shouldKillLiveAttack(Entity attack) {
    if (hasInvalidEntityNumbers(attack) || hasInvalidBox(attack.boundingBox)) {
      return true;
    }

    if (attack.posY < 0.0D
        || attack.posY > KiAttackConfig.getMaxY()) {
      return true;
    }

    Entity owner = getShootingEntity(attack);
    if (owner == null) {
      if (attack.ticksExisted > KiAttackConfig.getMaxOrphanTicks()) {
        return true;
      }
    } else if (owner.isDead || owner.worldObj != attack.worldObj) {
      return true;
    }

    int maxTicks = KiAttackConfig.maxTicksForType(callInt(attack, "getType"));
    if (attack.ticksExisted > maxTicks) {
      return true;
    }

    if (KiAttackConfig.killOutsideLoadedChunk()) {
      int x = MathHelper.floor_double(attack.posX);
      int y = MathHelper.floor_double(attack.posY);
      int z = MathHelper.floor_double(attack.posZ);
      int nextX = MathHelper.floor_double(attack.posX + attack.motionX);
      int nextY = MathHelper.floor_double(attack.posY + attack.motionY);
      int nextZ = MathHelper.floor_double(attack.posZ + attack.motionZ);
      return !attack.worldObj.blockExists(x, y, z) || !attack.worldObj.blockExists(nextX, nextY, nextZ);
    }

    return false;
  }

  private static boolean shouldKillOnJoin(Entity attack) {
    return hasInvalidEntityNumbers(attack) || hasInvalidBox(attack.boundingBox) || isOverAreaLimit(attack);
  }

  private static boolean isOverAreaLimit(Entity attack) {
    World world = attack.worldObj;
    if (world == null) {
      return true;
    }

    int chunkX = MathHelper.floor_double(attack.posX) >> 4;
    int chunkZ = MathHelper.floor_double(attack.posZ) >> 4;
    Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
    int chunkCount = 0;
    int chunkBarrageCount = 0;
    int ownerCount = 0;
    int ownerBarrageCount = 0;
    Entity owner = getShootingEntity(attack);

    for (List list : chunk.entityLists) {
      for (Object value : list) {
        if (!(value instanceof Entity) || !isKiAttack((Entity)value)) {
          continue;
        }

        Entity other = (Entity)value;
        if (other.isDead) {
          continue;
        }

        boolean barrage = callBoolean(other, "isBarrage");
        chunkCount++;
        if (barrage) {
          chunkBarrageCount++;
        }
        if (owner != null && getShootingEntity(other) == owner) {
          ownerCount++;
          if (barrage) {
            ownerBarrageCount++;
          }
        }
      }
    }

    boolean attackIsBarrage = callBoolean(attack, "isBarrage");
    if (chunkCount >= KiAttackConfig.getMaxKiPerChunk()
        || (attackIsBarrage && chunkBarrageCount >= KiAttackConfig.getMaxBarragePerChunk())) {
      return true;
    }

    if (owner == null) {
      return false;
    }

    if (ownerCount >= KiAttackConfig.getMaxKiPerOwner()) {
      return true;
    }
    return attackIsBarrage && ownerBarrageCount >= KiAttackConfig.getMaxBarragePerOwner();
  }

  private static boolean hasInvalidBox(AxisAlignedBB box) {
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

  private static boolean hasInvalidEntityNumbers(Entity entity) {
    return entity == null
        || !isFinite(entity.posX) || !isFinite(entity.posY) || !isFinite(entity.posZ)
        || !isFinite(entity.prevPosX) || !isFinite(entity.prevPosY) || !isFinite(entity.prevPosZ)
        || !isFinite(entity.motionX) || !isFinite(entity.motionY) || !isFinite(entity.motionZ);
  }

  private static boolean isKiAttack(Entity entity) {
    if (entity == null) {
      return false;
    }

    Class<?> type = entity.getClass();
    while (type != null) {
      if (KI_ATTACK_CLASS_NAME.equals(type.getName())) {
        return true;
      }
      type = type.getSuperclass();
    }
    return false;
  }

  private static Entity getShootingEntity(Entity attack) {
    Field field = getShootingEntityField(attack);
    if (field == null) {
      return null;
    }

    try {
      Object value = field.get(attack);
      return value instanceof Entity ? (Entity)value : null;
    } catch (IllegalAccessException ignored) {
      return null;
    }
  }

  private static Field getShootingEntityField(Entity attack) {
    if (shootingEntityFieldResolved) {
      return shootingEntityField;
    }

    shootingEntityFieldResolved = true;
    Class<?> type = attack.getClass();
    while (type != null) {
      try {
        Field field = type.getDeclaredField("shootingEntity");
        field.setAccessible(true);
        shootingEntityField = field;
        return shootingEntityField;
      } catch (NoSuchFieldException ignored) {
        type = type.getSuperclass();
      }
    }
    return null;
  }

  private static boolean callBoolean(Entity entity, String methodName) {
    try {
      Method method = entity.getClass().getMethod(methodName);
      Object value = method.invoke(entity);
      return value instanceof Boolean && (Boolean)value;
    } catch (Exception ignored) {
      return false;
    }
  }

  private static int callInt(Entity entity, String methodName) {
    try {
      Method method = entity.getClass().getMethod(methodName);
      Object value = method.invoke(entity);
      return value instanceof Number ? ((Number)value).intValue() : 0;
    } catch (Exception ignored) {
      return 0;
    }
  }

  private static double callDouble(Entity entity, String methodName) {
    try {
      Method method = entity.getClass().getMethod(methodName);
      Object value = method.invoke(entity);
      return value instanceof Number ? ((Number)value).doubleValue() : 0.0D;
    } catch (Exception ignored) {
      return 0.0D;
    }
  }

  private static void capDamage(Entity attack) {
    double damage = callDouble(attack, "getDamage");
    if (!isFinite(damage) || damage <= 0.0D) {
      markRemoved(attack);
      attack.setDead();
      return;
    }

    if (damage <= KiAttackConfig.getMaxDamage()) {
      return;
    }

    if (callSetDamage(attack, KiAttackConfig.getMaxDamage())) {
      return;
    }

    Field field = getDamageField(attack);
    if (field != null) {
      try {
        field.setDouble(attack, KiAttackConfig.getMaxDamage());
      } catch (IllegalAccessException ignored) {
      }
    }
  }

  private static boolean callSetDamage(Entity entity, double damage) {
    try {
      Method method = entity.getClass().getMethod("setDamage", double.class);
      method.invoke(entity, damage);
      return true;
    } catch (NoSuchMethodException ignored) {
      return false;
    } catch (IllegalAccessException ignored) {
      return false;
    } catch (InvocationTargetException ignored) {
      return false;
    }
  }

  private static Field getDamageField(Entity attack) {
    if (damageFieldResolved) {
      return damageField;
    }

    damageFieldResolved = true;
    Class<?> type = attack.getClass();
    while (type != null) {
      try {
        Field field = type.getDeclaredField("damage");
        field.setAccessible(true);
        damageField = field;
        return damageField;
      } catch (NoSuchFieldException ignored) {
        type = type.getSuperclass();
      }
    }
    return null;
  }

  private static void clampMotion(Entity entity) {
    double max = KiAttackConfig.getMaxMotion();
    entity.motionX = clamp(entity.motionX, -max, max);
    entity.motionY = clamp(entity.motionY, -max, max);
    entity.motionZ = clamp(entity.motionZ, -max, max);
  }

  private static void exposeScriptData(Entity attack) {
    if (!KiAttackConfig.exposeScriptData()) {
      return;
    }

    NBTTagCompound data = attack.getEntityData();
    data.setBoolean(TAG_IS_KI_ATTACK, true);
    data.setInteger(TAG_AGE, attack.ticksExisted);
    data.setInteger(TAG_TYPE, callInt(attack, "getType"));
    data.setInteger(TAG_SPEED, callInt(attack, "getSpe"));
    data.setInteger(TAG_DENSITY, callInt(attack, "getDen"));
    data.setDouble(TAG_DAMAGE, callDouble(attack, "getDamage"));
    data.setInteger(TAG_PERC, callInt(attack, "getPerc"));

    Entity owner = getShootingEntity(attack);
    if (owner != null) {
      data.setInteger(TAG_OWNER_ID, owner.getEntityId());
      data.setString(TAG_OWNER_NAME, owner.getCommandSenderName());
    }
  }

  private static void markRemoved(Entity entity) {
    entity.getEntityData().setBoolean(TAG_REMOVED_BY_ZEROS, true);
  }

  private static boolean isFinite(double value) {
    return !Double.isNaN(value) && !Double.isInfinite(value);
  }

  private static double clamp(double value, double min, double max) {
    if (!isFinite(value)) {
      return 0.0D;
    }
    if (value < min) return min;
    if (value > max) return max;
    return value;
  }
}
