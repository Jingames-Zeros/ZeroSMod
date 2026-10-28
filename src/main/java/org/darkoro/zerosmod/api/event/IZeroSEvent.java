package org.darkoro.zerosmod.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import kamkeel.npcdbc.api.form.IForm;
import noppes.npcs.api.entity.IEnergyDome;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.api.event.IEnergyBarrierEvent;
import noppes.npcs.api.event.IPlayerEvent;
import org.darkoro.zerosmod.api.ScriptZSWeapon;

/**
 * Events exposed by Zero S Mod to the CNPC scripting system.
 */
public interface IZeroSEvent {

  /**
   * Fired in the dome owner's script when an entity enters a dome.
   *
   * @hookName energyDomeEnter
   */
  interface EnergyDomeEnterEvent extends IEnergyBarrierEvent {

    /** @return the dome */
    IEnergyDome getDome();

    /** @return the entity that entered */
    IEntity getEntity();
  }

  /**
   * Fired in the dome owner's script when an entity leaves a dome, also when the dome is removed.
   *
   * @hookName energyDomeExit
   */
  interface EnergyDomeExitEvent extends IEnergyBarrierEvent {

    /** @return the dome */
    IEnergyDome getDome();

    /** @return the entity that left */
    IEntity getEntity();
  }

  /**
   * Fired before a valid Zero S weapon hit. Cancel it to prevent the hit and its cooldown.
   *
   * @hookName zsWeaponSwing
   */
  @Cancelable
  interface WeaponSwingEvent extends IPlayerEvent {

    /** @return live stats of the swung weapon */
    ScriptZSWeapon getWeapon();

    /** @return cooldown this swing starts, in ticks */
    double getCooldown();

    /** @param ticks cooldown for this swing only; 0 allows the next attack immediately */
    void setCooldown(double ticks);
  }

  /**
   * Fired before NPCDBC drains Ki for a custom form. Cancel it to skip this drain.
   *
   * @hookName dbcFormDrain
   */
  @Cancelable
  interface FormDrainEvent extends IPlayerEvent {

    /** @return Ki about to be drained */
    double getDrain();

    /** @param drain Ki to drain instead, rounded up; 0 skips the drain */
    void setDrain(double drain);
  }

  /**
   * Fired when a player starts charging an NPCDBC custom-form transformation.
   *
   * @hookName beginsTransforming
   */
  interface BeginsTransformingEvent extends IPlayerEvent {

    /** @return the target form */
    IForm getTargetForm();

    /** @return the target form's ID */
    int getTargetFormId();

    /** @return the target form's name */
    String getTargetFormName();
  }

  /**
   * Fired in player scripts when DBC successfully activates short-range Instant Transmission.
   *
   * @hookName activatedInstantTransmission
   */
  interface ActivatedInstantTransmissionEvent extends IPlayerEvent {

    /**
     * @return entity that DBC selected as the short-range Instant Transmission target, or null
     */
    IEntityLivingBase getTarget();

    /**
     * @return target command-sender name, or an empty string if no target was available
     */
    String getTargetName();
  }
}
