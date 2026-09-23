package org.darkoro.zerosmod.api.event;

import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.api.event.IPlayerEvent;

/**
 * Events exposed by Zero S Mod to the CNPC scripting system.
 */
public interface IZeroSEvent {

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
