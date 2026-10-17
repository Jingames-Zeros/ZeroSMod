package org.darkoro.zerosmod.zsweapons.event;

import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerDataScript;
import noppes.npcs.scripted.NpcAPI;
import org.darkoro.zerosmod.api.ScriptZSWeapon;

public final class WeaponSwingEventHooks {

  public static final String WEAPON_SWING_HOOK = "zsWeaponSwing";

  private WeaponSwingEventHooks() {}

  public static WeaponSwingPlayerEvent fire(
      EntityPlayerMP player, ScriptZSWeapon weapon, double cooldown) {
    WeaponSwingPlayerEvent event = new WeaponSwingPlayerEvent(player, weapon, cooldown);
    if (ScriptController.Instance == null) {
      return event;
    }

    PlayerDataScript handler = ScriptController.Instance.getPlayerScripts(player);
    handler.callScript(WEAPON_SWING_HOOK, event);
    NpcAPI.EVENT_BUS.post(event);
    return event;
  }
}
