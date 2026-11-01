package org.darkoro.zerosmod.tpbank;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.event.player.PlayerEvent;
import org.darkoro.zerosmod.api.event.IZeroSEvent;

@Cancelable
public final class TpOverflowPlayerEvent extends PlayerEvent
    implements IZeroSEvent.TpOverflowEvent {

  private final long overflow;
  private final long banked;
  private long amount;

  public TpOverflowPlayerEvent(EntityPlayerMP player, long overflow, long amount) {
    super((IPlayer)NpcAPI.Instance().getIEntity(player));
    this.overflow = overflow;
    this.banked = TpBank.getBanked(player);
    setAmount(amount);
  }

  @Override public String getHookName() {
    return TpBankEventHooks.TP_OVERFLOW_HOOK;
  }

  @Override public long getOverflow() {
    return this.overflow;
  }

  @Override public long getAmount() {
    return this.amount;
  }

  @Override public void setAmount(long amount) {
    this.amount = Math.max(0L, amount);
  }

  @Override public long getBankedTP() {
    return this.banked;
  }
}
