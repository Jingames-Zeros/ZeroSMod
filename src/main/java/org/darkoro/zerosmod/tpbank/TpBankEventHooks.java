package org.darkoro.zerosmod.tpbank;

import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerDataScript;
import noppes.npcs.scripted.NpcAPI;

public final class TpBankEventHooks {

  public static final String TP_OVERFLOW_HOOK = "tpOverflow";

  private TpBankEventHooks() {}

  /**
   * Fires the player-script hook and returns the TP the bank should store.
   *
   * @param overflow TP above the cap
   * @param amount   TP left after the configured deposit loss
   */
  public static long fireOverflow(EntityPlayerMP player, long overflow, long amount) {
    if (ScriptController.Instance == null) {
      return amount;
    }

    TpOverflowPlayerEvent event = new TpOverflowPlayerEvent(player, overflow, amount);
    PlayerDataScript handler = ScriptController.Instance.getPlayerScripts(player);
    handler.callScript(TP_OVERFLOW_HOOK, event);
    NpcAPI.EVENT_BUS.post(event);
    return event.isCanceled() ? 0L : event.getAmount();
  }
}
