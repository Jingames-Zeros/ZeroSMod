package org.darkoro.zerosmod.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import kamkeel.npcdbc.api.form.IForm;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.api.event.IPlayerEvent;
import org.darkoro.zerosmod.api.ScriptZSWeapon;

/**
 * Events exposed by Zero S Mod to the CNPC scripting system.
 */
public interface IZeroSEvent {

  /**
   * Fired immediately before a valid Zero S weapon hit queues its damage state and cooldown.
   * Cancel the event to prevent the hit and its cooldown. Attacks rejected for an existing
   * cooldown, invalid target, changed held item, or excessive range do not fire this hook.
   *
   * @hookName zsWeaponSwing
   */
  @Cancelable
  interface WeaponSwingEvent extends IPlayerEvent {

    /**
     * Gets the live stats of the weapon being swung.
     *
     * @return current Zero S weapon stats; never null
     */
    ScriptZSWeapon getWeapon();

    /**
     * @return cooldown this swing will start, in ticks
     */
    double getCooldown();

    /**
     * Replaces the cooldown started by this swing without changing the weapon's configured
     * cooldown. Zero allows another attack immediately. Negative or non-finite values become zero.
     *
     * @param ticks replacement cooldown in ticks
     */
    void setCooldown(double ticks);
  }

  /**
   * Fired immediately before NPCDBC applies one scheduled custom-form Ki drain.
   * Cancel the event to skip that drain. Each later drain interval fires a new event.
   * This hook does not fire for creative-mode players or forms without Ki drain enabled.
   * For example, {@code event.setDrain(event.getDrain() * 0.5)} halves this drain and
   * {@code event.setCanceled(true)} skips it.
   *
   * @hookName dbcFormDrain
   */
  @Cancelable
  interface FormDrainEvent extends IPlayerEvent {

    /**
     * Gets NPCDBC's calculated drain before it is rounded to whole Ki.
     *
     * @return non-negative Ki amount about to be drained
     */
    double getDrain();

    /**
     * Replaces the amount drained this interval. DBC stores Ki as a whole number, so positive
     * decimals round up when applied. Zero skips the drain. Negative or non-finite values become
     * zero, and values above the integer Ki limit are clamped.
     *
     * @param drain replacement Ki drain amount
     */
    void setDrain(double drain);
  }

  /**
   * Fired once when a player begins charging an NPCDBC custom-form transformation.
   * This runs before the form changes and still fires if the player releases the key before
   * completing the transformation. Releasing and starting again fires a new event.
   * Vanilla DBC transformations and programmatic form changes do not fire this hook.
   *
   * @hookName beginsTransforming
   */
  interface BeginsTransformingEvent extends IPlayerEvent {

    /** @return custom form the player is attempting to transform into; never null */
    IForm getTargetForm();

    /** @return numeric ID of the target custom form */
    int getTargetFormId();

    /** @return internal name of the target custom form */
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
