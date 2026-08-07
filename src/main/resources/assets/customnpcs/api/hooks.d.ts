/**
 * Zero S Mod player script hooks.
 */
declare namespace IZeroSEvent {
  /** Fired in player scripts when DBC successfully activates short-range Instant Transmission. */
  function activatedInstantTransmission(event: IZeroSEvent.ActivatedInstantTransmissionEvent): void;
}

declare namespace IPlayerEvent {
  /** Fired in player scripts when DBC successfully activates short-range Instant Transmission. */
  function activatedInstantTransmission(event: IZeroSEvent.ActivatedInstantTransmissionEvent): void;
}
