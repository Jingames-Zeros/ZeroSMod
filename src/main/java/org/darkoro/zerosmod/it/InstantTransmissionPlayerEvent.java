package org.darkoro.zerosmod.it;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.event.player.PlayerEvent;
import org.darkoro.zerosmod.api.event.IZeroSEvent;

public abstract class InstantTransmissionPlayerEvent extends PlayerEvent {

  public InstantTransmissionPlayerEvent(IPlayer player) {
    super(player);
  }

  public static class Activated extends InstantTransmissionPlayerEvent implements IZeroSEvent.ActivatedInstantTransmissionEvent {

    public final IEntityLivingBase target;
    public final String targetName;

    public Activated(EntityPlayerMP player, EntityLivingBase target) {
      super((IPlayer)NpcAPI.Instance().getIEntity(player));
      this.target = target == null ? null : (IEntityLivingBase)NpcAPI.Instance().getIEntity(target);
      this.targetName = target == null ? "" : target.getCommandSenderName();
    }

    @Override public String getHookName() {
      return InstantTransmissionEventHooks.ACTIVATED_HOOK;
    }

    public IEntityLivingBase getTarget() {
      return this.target;
    }

    public String getTargetName() {
      return this.targetName;
    }
  }
}
