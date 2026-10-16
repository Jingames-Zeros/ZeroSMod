/**
 * Zero S Mod player script hooks.
 */
declare namespace IZeroSEvent {
  /** Fired once when normal transform input begins charging an NPCDBC custom form, before the form changes. */
  function beginsTransforming(event: IZeroSEvent.BeginsTransformingEvent): void;

  /** Fired in player scripts when DBC successfully activates short-range Instant Transmission. */
  function activatedInstantTransmission(event: IZeroSEvent.ActivatedInstantTransmissionEvent): void;
}

declare namespace IPlayerEvent {
  /** Fired once when normal transform input begins charging an NPCDBC custom form, before the form changes. */
  function beginsTransforming(event: IZeroSEvent.BeginsTransformingEvent): void;

  /** Fired in player scripts when DBC successfully activates short-range Instant Transmission. */
  function activatedInstantTransmission(event: IZeroSEvent.ActivatedInstantTransmissionEvent): void;
}
