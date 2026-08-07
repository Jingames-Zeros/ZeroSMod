package org.darkoro.zerosmod.it;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerDataScript;
import noppes.npcs.scripted.NpcAPI;

public final class InstantTransmissionEventHooks {

  public static final String ACTIVATED_HOOK = "activatedInstantTransmission";
  private static final int MAX_RETAIN_TICKS = 1200;
  private static final Map<TargetKey, RecentActivation> RECENT_ACTIVATIONS = new HashMap<TargetKey, RecentActivation>();

  private InstantTransmissionEventHooks() {}

  public static void onActivated(EntityPlayerMP player, EntityLivingBase target) {
    if (player == null || player.worldObj == null || player.worldObj.isRemote || ScriptController.Instance == null) {
      return;
    }

    recordActivation(player, target);
    InstantTransmissionPlayerEvent.Activated event = new InstantTransmissionPlayerEvent.Activated(player, target);
    PlayerDataScript handler = ScriptController.Instance.getPlayerScripts(player);
    handler.callScript(ACTIVATED_HOOK, event);
    NpcAPI.EVENT_BUS.post(event);
  }

  public static boolean wasRecentTarget(EntityLivingBase target, int maxAgeTicks) {
    return getRecentActivation(target, maxAgeTicks) != null;
  }

  public static EntityPlayerMP getRecentPlayer(EntityLivingBase target, int maxAgeTicks) {
    RecentActivation activation = getRecentActivation(target, maxAgeTicks);
    if (activation == null || MinecraftServer.getServer() == null) {
      return null;
    }

    return MinecraftServer.getServer().getConfigurationManager().func_152612_a(activation.playerName);
  }

  public static String getRecentPlayerName(EntityLivingBase target, int maxAgeTicks) {
    RecentActivation activation = getRecentActivation(target, maxAgeTicks);
    return activation == null ? "" : activation.playerName;
  }

  public static int getTicksSinceTargeted(EntityLivingBase target) {
    RecentActivation activation = getRecentActivation(target, MAX_RETAIN_TICKS);
    if (activation == null || target == null || target.worldObj == null) {
      return -1;
    }

    long age = target.worldObj.getTotalWorldTime() - activation.worldTime;
    return age > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)age;
  }

  private static void recordActivation(EntityPlayerMP player, EntityLivingBase target) {
    if (target == null || target.worldObj == null) {
      return;
    }

    prune(target.worldObj.getTotalWorldTime());
    RECENT_ACTIVATIONS.put(new TargetKey(target), new RecentActivation(player.getCommandSenderName(), target.worldObj.getTotalWorldTime()));
  }

  private static RecentActivation getRecentActivation(EntityLivingBase target, int maxAgeTicks) {
    if (target == null || target.worldObj == null || maxAgeTicks < 0) {
      return null;
    }

    prune(target.worldObj.getTotalWorldTime());
    RecentActivation activation = RECENT_ACTIVATIONS.get(new TargetKey(target));
    if (activation == null) {
      return null;
    }

    long age = target.worldObj.getTotalWorldTime() - activation.worldTime;
    return age >= 0 && age <= maxAgeTicks ? activation : null;
  }

  private static void prune(long worldTime) {
    Iterator<RecentActivation> iterator = RECENT_ACTIVATIONS.values().iterator();
    while (iterator.hasNext()) {
      RecentActivation activation = iterator.next();
      if (worldTime - activation.worldTime > MAX_RETAIN_TICKS) {
        iterator.remove();
      }
    }
  }

  private static final class TargetKey {
    private final int dimension;
    private final int entityId;

    private TargetKey(EntityLivingBase target) {
      this.dimension = target.dimension;
      this.entityId = target.getEntityId();
    }

    @Override public boolean equals(Object value) {
      if (this == value) {
        return true;
      }
      if (!(value instanceof TargetKey)) {
        return false;
      }

      TargetKey other = (TargetKey)value;
      return this.dimension == other.dimension && this.entityId == other.entityId;
    }

    @Override public int hashCode() {
      return 31 * this.dimension + this.entityId;
    }
  }

  private static final class RecentActivation {
    private final String playerName;
    private final long worldTime;

    private RecentActivation(String playerName, long worldTime) {
      this.playerName = playerName;
      this.worldTime = worldTime;
    }
  }
}
