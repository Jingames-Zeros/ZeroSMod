package org.darkoro.zerosmod.transformation;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerDataScript;
import noppes.npcs.scripted.NpcAPI;

public final class CustomFormTransformationEventHooks {

  public static final String BEGINS_TRANSFORMING_HOOK = "beginsTransforming";
  public static final CustomFormTransformationEventHooks INSTANCE =
      new CustomFormTransformationEventHooks();
  private static final Set<UUID> ACTIVE_ATTEMPTS = new HashSet<UUID>();

  private CustomFormTransformationEventHooks() {}

  public static void onBegin(EntityPlayerMP player, int targetFormId) {
    if (player == null || player.worldObj == null || player.worldObj.isRemote || ScriptController.Instance == null) {
      return;
    }

    Form targetForm = (Form)FormController.getInstance().get(targetFormId);
    DBCData data = DBCData.get(player);
    if (targetForm == null || data == null || !data.isTransforming()
        || !isExpectedTarget(player, targetForm) || !ACTIVE_ATTEMPTS.add(player.getUniqueID())) {
      return;
    }

    CustomFormTransformationPlayerEvent.BeginsTransforming event =
        new CustomFormTransformationPlayerEvent.BeginsTransforming(player, targetForm);
    PlayerDataScript handler = ScriptController.Instance.getPlayerScripts(player);
    handler.callScript(BEGINS_TRANSFORMING_HOOK, event);
    NpcAPI.EVENT_BUS.post(event);
  }

  @SubscribeEvent public void onPlayerTick(PlayerTickEvent event) {
    if (event.side != Side.SERVER || event.phase != Phase.END || event.player == null
        || !ACTIVE_ATTEMPTS.contains(event.player.getUniqueID())) {
      return;
    }

    DBCData data = DBCData.get(event.player);
    if (data == null || !data.isTransforming()) {
      ACTIVE_ATTEMPTS.remove(event.player.getUniqueID());
    }
  }

  @SubscribeEvent public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
    if (event.player != null) {
      ACTIVE_ATTEMPTS.remove(event.player.getUniqueID());
    }
  }

  public static boolean isTransforming(EntityPlayer player) {
    if (player == null) {
      return false;
    }

    DBCData data = DBCData.get(player);
    return data != null && data.isTransforming() && getExpectedTarget(player) != null;
  }

  private static boolean isExpectedTarget(EntityPlayer player, Form targetForm) {
    Form expected = getExpectedTarget(player);
    return expected != null && expected.getID() == targetForm.getID();
  }

  private static Form getExpectedTarget(EntityPlayer player) {
    PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);
    if (info == null) {
      return null;
    }

    Form current = info.getCurrentForm();
    if (current != null) {
      if (!current.hasChild()) {
        return null;
      }

      IForm child = current.getChild();
      if (child instanceof Form && info.hasForm((Form)child)) {
        return (Form)child;
      }
      return null;
    }

    return info.getSelectedForm();
  }
}
