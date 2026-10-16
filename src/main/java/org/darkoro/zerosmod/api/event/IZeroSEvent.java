package org.darkoro.zerosmod.api.event;

import kamkeel.npcdbc.api.form.IForm;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.api.event.IPlayerEvent;

/**
 * Events exposed by Zero S Mod to the CNPC scripting system.
 */
public interface IZeroSEvent {

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
