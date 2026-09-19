package org.darkoro.zerosmod.scripted;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.scripted.NpcAPI;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.api.AbstractZeroSAPI;
import org.darkoro.zerosmod.it.InstantTransmissionEventHooks;
import org.darkoro.zerosmod.it.InstantTransmissionScriptHelper;
import org.darkoro.zerosmod.ki.KiScriptHelper;

public class ZeroSAPI extends AbstractZeroSAPI {

  private static AbstractZeroSAPI instance;

  private ZeroSAPI() {}

  public static AbstractZeroSAPI Instance() {
    if (instance == null) {
      instance = new ZeroSAPI();
    }
    return instance;
  }

  @Override public String getVersion() {
    return ZeroSMod.VERSION;
  }

  @Override public IEntityLivingBase getInstantTransmissionLookTarget(IPlayer player) {
    EntityPlayerMP mcPlayer = asPlayer(player);
    EntityLivingBase target = InstantTransmissionScriptHelper.getInstantTransmissionLookTarget(mcPlayer);
    return target == null ? null : (IEntityLivingBase)NpcAPI.Instance().getIEntity(target);
  }

  @Override public String getInstantTransmissionLookTargetName(IPlayer player) {
    EntityPlayerMP mcPlayer = asPlayer(player);
    return InstantTransmissionScriptHelper.getInstantTransmissionLookTargetName(mcPlayer);
  }

  @Override public boolean wasRecentInstantTransmissionTarget(IEntity entity, int maxAgeTicks) {
    return InstantTransmissionEventHooks.wasRecentTarget(asLiving(entity), maxAgeTicks);
  }

  @Override public IPlayer getRecentInstantTransmissionPlayer(IEntity entity, int maxAgeTicks) {
    EntityPlayerMP player = InstantTransmissionEventHooks.getRecentPlayer(asLiving(entity), maxAgeTicks);
    return player == null ? null : (IPlayer)NpcAPI.Instance().getIEntity(player);
  }

  @Override public String getRecentInstantTransmissionPlayerName(IEntity entity, int maxAgeTicks) {
    return InstantTransmissionEventHooks.getRecentPlayerName(asLiving(entity), maxAgeTicks);
  }

  @Override public int getTicksSinceInstantTransmissionTargeted(IEntity entity) {
    return InstantTransmissionEventHooks.getTicksSinceTargeted(asLiving(entity));
  }

  @Override public boolean isKiAttack(IEntity entity) {
    return KiScriptHelper.isKiAttack(entity);
  }

  @Override public String getKiType(IEntity kiAttack) {
    return KiScriptHelper.getKiType(kiAttack);
  }

  @Override public int getKiId(IEntity kiAttack) {
    return KiScriptHelper.getKiId(kiAttack);
  }

  @Override public boolean isKiStopped(IEntity kiAttack) {
    return KiScriptHelper.isKiStopped(kiAttack);
  }

  @Override public int stopKi(IEntity origin, int range) {
    return KiScriptHelper.stopKiAround(origin, range);
  }

  public int stopKi(IEntity origin, int range, int ignoredTicks) {
    return KiScriptHelper.stopKiAround(origin, range);
  }

  @Override public int releaseKi(IEntity origin, int range) {
    return KiScriptHelper.releaseKiAround(origin, range);
  }

  @Override public IEntity[] getNearbyKi(IEntity origin, int range) {
    return KiScriptHelper.getNearbyKi(origin, range);
  }

  @Override public boolean stealKi(IEntity kiAttack, String color, IEntity owner) {
    return KiScriptHelper.stealKi(kiAttack, color, owner);
  }

  @Override public boolean redirectKi(IEntity kiAttack, IEntity target) {
    return KiScriptHelper.redirectKi(kiAttack, target);
  }

  @Override public boolean directKi(IEntity kiAttack, double x, double y, double z) {
    return KiScriptHelper.directKi(kiAttack, x, y, z);
  }

  @Override public boolean setKiColor(IEntity kiAttack, String color) {
    return KiScriptHelper.setKiColor(kiAttack, color);
  }

  private static EntityPlayerMP asPlayer(IPlayer player) {
    if (player == null) {
      return null;
    }

    Entity entity = player.getMCEntity();
    return entity instanceof EntityPlayerMP ? (EntityPlayerMP)entity : null;
  }

  private static Entity asEntity(IEntity entity) {
    return entity == null ? null : entity.getMCEntity();
  }

  private static EntityLivingBase asLiving(IEntity entity) {
    Entity mcEntity = asEntity(entity);
    return mcEntity instanceof EntityLivingBase ? (EntityLivingBase)mcEntity : null;
  }
}
