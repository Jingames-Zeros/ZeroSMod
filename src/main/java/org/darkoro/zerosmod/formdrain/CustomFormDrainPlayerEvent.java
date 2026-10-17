package org.darkoro.zerosmod.formdrain;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.event.player.PlayerEvent;
import org.darkoro.zerosmod.api.event.IZeroSEvent;

@Cancelable
public final class CustomFormDrainPlayerEvent extends PlayerEvent
    implements IZeroSEvent.FormDrainEvent {

  private double drain;

  public CustomFormDrainPlayerEvent(EntityPlayerMP player, double drain) {
    super((IPlayer)NpcAPI.Instance().getIEntity(player));
    setDrain(drain);
  }

  @Override public String getHookName() {
    return CustomFormDrainEventHooks.FORM_DRAIN_HOOK;
  }

  @Override public double getDrain() {
    return this.drain;
  }

  @Override public void setDrain(double drain) {
    this.drain = isFinite(drain) ? Math.max(0.0D, Math.min(drain, Integer.MAX_VALUE)) : 0.0D;
  }

  private static boolean isFinite(double value) {
    return !Double.isNaN(value) && !Double.isInfinite(value);
  }
}
