package org.darkoro.zerosmod.zsweapons.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.event.player.PlayerEvent;
import org.darkoro.zerosmod.api.ScriptZSWeapon;
import org.darkoro.zerosmod.api.event.IZeroSEvent;

@Cancelable
public final class WeaponSwingPlayerEvent extends PlayerEvent
    implements IZeroSEvent.WeaponSwingEvent {

  private final ScriptZSWeapon weapon;
  private double cooldown;

  public WeaponSwingPlayerEvent(EntityPlayerMP player, ScriptZSWeapon weapon, double cooldown) {
    super((IPlayer)NpcAPI.Instance().getIEntity(player));
    this.weapon = weapon;
    setCooldown(cooldown);
  }

  @Override public String getHookName() {
    return WeaponSwingEventHooks.WEAPON_SWING_HOOK;
  }

  @Override public ScriptZSWeapon getWeapon() {
    return this.weapon;
  }

  @Override public double getCooldown() {
    return this.cooldown;
  }

  @Override public void setCooldown(double ticks) {
    this.cooldown = isFinite(ticks) ? Math.max(0.0D, ticks) : 0.0D;
  }

  private static boolean isFinite(double value) {
    return !Double.isNaN(value) && !Double.isInfinite(value);
  }
}
