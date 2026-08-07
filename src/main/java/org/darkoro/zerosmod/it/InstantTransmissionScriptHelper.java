package org.darkoro.zerosmod.it;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.i.ExtendedPlayer;
import JinRyuu.JRMCore.server.config.dbc.JGConfigDBCInstantTransmission;
import JinRyuu.JRMCore.server.JGPlayerMP;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;

public final class InstantTransmissionScriptHelper {

  private InstantTransmissionScriptHelper() {}

  public static String getInstantTransmissionLookTargetName(EntityPlayerMP player) {
    EntityLivingBase target = getInstantTransmissionLookTarget(player);
    return target == null ? "" : target.getCommandSenderName();
  }

  public static EntityLivingBase getInstantTransmissionLookTarget(EntityPlayerMP player) {
    if (player == null || player.worldObj == null || player.worldObj.isRemote) {
      return null;
    }

    ExtendedPlayer props = ExtendedPlayer.get(player);
    if (props == null || props.getBlocking() != 2) {
      return null;
    }

    int skillLevel = JRMCoreH.SklLvl(17, player);
    if (skillLevel <= 0 || skillLevel > JGConfigDBCInstantTransmission.CONFIG_INSTANT_TRANSMISSION_SHORT_MAX_RANGE.length) {
      return null;
    }

    Vec3 look = player.getLookVec();
    if (look == null) {
      return null;
    }

    double posX = player.posX + look.xCoord * (player.width + 1.0F) - look.xCoord * 2.0D;
    double posY = player.posY + look.yCoord * (player.width + 1.0F) + player.height * 0.55F - look.yCoord * 2.0D;
    double posZ = player.posZ + look.zCoord * (player.width + 1.0F) - look.zCoord * 2.0D;

    double motionX = -MathHelper.sin(player.getRotationYawHead() / 180.0F * 3.1415927F)
        * MathHelper.cos(player.rotationPitch / 180.0F * 3.1415927F);
    double motionZ = MathHelper.cos(player.getRotationYawHead() / 180.0F * 3.1415927F)
        * MathHelper.cos(player.rotationPitch / 180.0F * 3.1415927F);
    double motionY = -MathHelper.sin(player.rotationPitch / 180.0F * 3.1415927F);
    double length = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
    if (length <= 0.0D) {
      return null;
    }

    motionX /= length;
    motionY /= length;
    motionZ /= length;

    int maxRange = JGConfigDBCInstantTransmission.CONFIG_INSTANT_TRANSMISSION_SHORT_MAX_RANGE[skillLevel - 1];
    return getInstantTransmissionTargetFromPath(player, posX, posY, posZ, motionX, motionY, motionZ, maxRange);
  }

  public static EntityLivingBase getInstantTransmissionProjectileTarget(
      EntityPlayerMP player,
      double posX,
      double posY,
      double posZ,
      double motionX,
      double motionY,
      double motionZ,
      int skillLevel) {
    if (player == null || player.worldObj == null || player.worldObj.isRemote
        || skillLevel <= 0 || skillLevel > JGConfigDBCInstantTransmission.CONFIG_INSTANT_TRANSMISSION_SHORT_MAX_RANGE.length) {
      return null;
    }

    int maxRange = JGConfigDBCInstantTransmission.CONFIG_INSTANT_TRANSMISSION_SHORT_MAX_RANGE[skillLevel - 1];
    return getInstantTransmissionTargetFromPath(player, posX, posY, posZ, motionX, motionY, motionZ, maxRange);
  }

  private static EntityLivingBase getInstantTransmissionTargetFromPath(
      EntityPlayerMP player,
      double posX,
      double posY,
      double posZ,
      double motionX,
      double motionY,
      double motionZ,
      int maxRange) {
    double finderRange = JGConfigDBCInstantTransmission.CONFIG_INSTANT_TRANSMISSION_SHORT_TARGET_FINDER_RANGE;
    for (int i = 0; i < maxRange; i++) {
      posX += motionX;
      posY += motionY;
      posZ += motionZ;

      if (!JGConfigDBCInstantTransmission.CONFIG_INSTANT_TRANSMISSION_SHORT_GO_THROUGH_BLOCKS_ENABLED
          && isBlocked(player, posX, posY, posZ)) {
        return null;
      }

      AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
          posX - finderRange, posY - finderRange, posZ - finderRange,
          posX + finderRange, posY + finderRange, posZ + finderRange);
      List entities = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, box);
      for (Object value : entities) {
        if (value instanceof EntityLivingBase && isValidShortTarget(player, (EntityLivingBase)value)) {
          return (EntityLivingBase)value;
        }
      }
    }

    return null;
  }

  private static boolean isBlocked(EntityPlayerMP player, double posX, double posY, double posZ) {
    int x = (int)posX;
    int y = (int)posY;
    int z = (int)posZ;
    Block block = player.worldObj.getBlock(x, y, z);
    if (block == null || block.getMaterial() == Material.air) {
      return false;
    }

    block.setBlockBoundsBasedOnState((IBlockAccess)player.worldObj, x, y, z);
    AxisAlignedBB box = block.getCollisionBoundingBoxFromPool(player.worldObj, x, y, z);
    return box != null && box.isVecInside(Vec3.createVectorHelper(posX, posY, posZ));
  }

  private static boolean isValidShortTarget(EntityPlayerMP player, EntityLivingBase target) {
    if (target == null || target == player || !target.isEntityAlive() || JRMCoreH.isFusionSpectator((Entity)target)) {
      return false;
    }

    if (target instanceof EntityPlayer && JGConfigDBCInstantTransmission.CONFIG_INSTANT_TRANSMISSION_DIMENSIONAL_RELEASE_SENSE_REQUIRED_ENABLED[0]) {
      JGPlayerMP targetMP = new JGPlayerMP((EntityPlayer)target);
      targetMP.setNBT(getPersisted((EntityPlayer)target));
      return targetMP.getRelease() > 0;
    }

    return true;
  }

  private static NBTTagCompound getPersisted(EntityPlayer player) {
    if (!player.getEntityData().hasKey("PlayerPersisted")) {
      NBTTagCompound nbt = new NBTTagCompound();
      player.getEntityData().setTag("PlayerPersisted", (NBTBase)nbt);
      return nbt;
    }

    return player.getEntityData().getCompoundTag("PlayerPersisted");
  }
}
