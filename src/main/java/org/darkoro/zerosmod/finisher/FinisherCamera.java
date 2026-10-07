package org.darkoro.zerosmod.finisher;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import noppes.npcs.api.entity.IAnimatable;
import noppes.npcs.api.handler.data.IAnimation;
import noppes.npcs.api.handler.data.IAnimationData;
import noppes.npcs.scripted.NpcAPI;
import org.darkoro.zerosmod.ZeroSMod;

public final class FinisherCamera {

  public static final FinisherCamera INSTANCE = new FinisherCamera();
  public static final int MAX_TICKS = 1200;

  private static final Map<UUID, PendingStop> pendingStops = new HashMap<UUID, PendingStop>();

  private FinisherCamera() {}

  // CNPC+ animations can loop or outlast the shot, so the one the shot started is cleared when it ends.
  @SubscribeEvent
  public void onServerTick(TickEvent.ServerTickEvent event) {
    if (event.phase != TickEvent.Phase.END || pendingStops.isEmpty()) {
      return;
    }

    Iterator<PendingStop> iterator = pendingStops.values().iterator();
    while (iterator.hasNext()) {
      PendingStop pending = iterator.next();
      if (--pending.ticksLeft <= 0) {
        iterator.remove();
        pending.stopAnimation();
      }
    }
  }

  public static boolean play(EntityPlayerMP player, Entity target, int ticks, FinisherCameraPreset preset) {
    return play(player, target, ticks, preset, null);
  }

  public static boolean play(EntityPlayerMP player, Entity target, int ticks, FinisherCameraPreset preset, String animation) {
    if (player == null || preset == null || ticks <= 0) {
      return false;
    }
    if (target != null && target.worldObj != player.worldObj) {
      return false;
    }
    boolean animate = animation != null && !animation.isEmpty();
    if (animate && !hasAnimation(animation)) {
      return false;
    }

    int duration = Math.min(ticks, MAX_TICKS);
    int targetId = target == null || target == player ? -1 : target.getEntityId();
    ZeroSMod.network.sendTo(new FinisherCameraPacket(targetId, duration, preset.ordinal()), player);
    if (animate) {
      IAnimationData data = animationData(player);
      data.setEnabled(true);
      data.setAnimation(NpcAPI.Instance().getAnimations().get(animation));
      data.updateClient();
      pendingStops.put(player.getUniqueID(), new PendingStop(player, animation, duration));
    }
    return true;
  }

  private static IAnimationData animationData(EntityPlayerMP player) {
    return ((IAnimatable)NpcAPI.Instance().getIEntity(player)).getAnimationData();
  }

  public static boolean hasAnimation(String name) {
    return NpcAPI.Instance().getAnimations().has(name);
  }

  public static String[] animationNames() {
    IAnimation[] animations = NpcAPI.Instance().getAnimations().getAllAnimations();
    String[] names = new String[animations.length];
    for (int i = 0; i < animations.length; i++) {
      names[i] = animations[i].getName();
    }
    return names;
  }

  public static void stop(EntityPlayerMP player) {
    if (player == null) {
      return;
    }

    ZeroSMod.network.sendTo(new FinisherCameraPacket(-1, 0, 0), player);
    PendingStop pending = pendingStops.remove(player.getUniqueID());
    if (pending != null) {
      pending.stopAnimation();
    }
  }

  private static final class PendingStop {

    private final EntityPlayerMP player;
    private final String animation;
    private int ticksLeft;

    private PendingStop(EntityPlayerMP player, String animation, int ticksLeft) {
      this.player = player;
      this.animation = animation;
      this.ticksLeft = ticksLeft;
    }

    // Leaves the player alone if they have since logged out, respawned or switched to another animation.
    private void stopAnimation() {
      if (player.isDead || player.playerNetServerHandler == null) {
        return;
      }

      IAnimationData data = animationData(player);
      IAnimation current = data.getAnimation();
      if (current != null && animation.equals(current.getName())) {
        data.setAnimation(null);
        data.updateClient();
      }
    }
  }

  public static EntityLivingBase findLookTarget(EntityPlayerMP player, double range) {
    Vec3 eye = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
    Vec3 look = player.getLook(1.0F);
    Vec3 end = eye.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);
    List<?> entities = player.worldObj.getEntitiesWithinAABBExcludingEntity(player,
        player.boundingBox.addCoord(look.xCoord * range, look.yCoord * range, look.zCoord * range).expand(1.0D, 1.0D, 1.0D));

    EntityLivingBase best = null;
    double bestDistance = range;
    for (Object object : entities) {
      if (!(object instanceof EntityLivingBase)) {
        continue;
      }

      EntityLivingBase entity = (EntityLivingBase)object;
      MovingObjectPosition hit = entity.boundingBox.expand(0.3D, 0.3D, 0.3D).calculateIntercept(eye, end);
      if (hit == null) {
        continue;
      }

      double distance = eye.distanceTo(hit.hitVec);
      if (distance < bestDistance) {
        best = entity;
        bestDistance = distance;
      }
    }
    return best;
  }
}
