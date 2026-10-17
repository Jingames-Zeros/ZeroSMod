package org.darkoro.zerosmod.dome;

import kamkeel.npcs.entity.EntityEnergyDome;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerDataScript;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.api.entity.IEnergyDome;
import noppes.npcs.api.entity.IEntity;

public final class EnergyDomeEventHooks {

  public static final String ENTER_HOOK = "energyDomeEnter";
  public static final String EXIT_HOOK = "energyDomeExit";

  private EnergyDomeEventHooks() {}

  public static void fireEnter(EntityEnergyDome dome, Entity entity) {
    fire(dome, entity, true);
  }

  public static void fireExit(EntityEnergyDome dome, Entity entity) {
    fire(dome, entity, false);
  }

  private static void fire(EntityEnergyDome dome, Entity entity, boolean entering) {
    if (dome == null || entity == null || dome.worldObj == null || dome.worldObj.isRemote
        || ScriptController.Instance == null) {
      return;
    }

    IEnergyDome scriptDome = (IEnergyDome)NpcAPI.Instance().getIEntity(dome);
    IEntity scriptEntity = NpcAPI.Instance().getIEntity(entity);
    EnergyDomeEvent event = entering
        ? new EnergyDomeEvent.Enter(scriptDome, scriptEntity)
        : new EnergyDomeEvent.Exit(scriptDome, scriptEntity);

    Entity owner = dome.getOwnerEntity();
    if (owner instanceof EntityNPCInterface) {
      ((EntityNPCInterface)owner).script.callScript(event.getHookName(), event);
    } else if (owner instanceof EntityPlayer) {
      PlayerDataScript scripts = ScriptController.Instance.getPlayerScripts((EntityPlayer)owner);
      if (scripts != null) {
        scripts.callScript(event.getHookName(), event);
      }
    }

    NpcAPI.EVENT_BUS.post(event);
  }
}
