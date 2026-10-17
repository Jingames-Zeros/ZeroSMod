package org.darkoro.zerosmod.formdrain;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerDataScript;
import noppes.npcs.scripted.NpcAPI;

public final class CustomFormDrainEventHooks {

  public static final String FORM_DRAIN_HOOK = "dbcFormDrain";

  private CustomFormDrainEventHooks() {}

  /** Fires the player-script hook and returns the drain NPCDBC should apply. */
  public static double fire(EntityPlayer player, double drain) {
    if (drain <= 0.0D || !(player instanceof EntityPlayerMP)
        || player.worldObj == null || player.worldObj.isRemote || ScriptController.Instance == null) {
      return drain;
    }

    CustomFormDrainPlayerEvent event =
        new CustomFormDrainPlayerEvent((EntityPlayerMP)player, drain);
    PlayerDataScript handler = ScriptController.Instance.getPlayerScripts(player);
    handler.callScript(FORM_DRAIN_HOOK, event);
    NpcAPI.EVENT_BUS.post(event);
    return event.isCanceled() ? 0.0D : event.getDrain();
  }
}
