/**
 * Zero S Mod player script hooks.
 */
declare namespace IZeroSEvent {
  /** Fired once when normal transform input begins charging an NPCDBC custom form, before the form changes. */
  function beginsTransforming(event: IZeroSEvent.BeginsTransformingEvent): void;

  /** Fired in player scripts when DBC successfully activates short-range Instant Transmission. */
  function activatedInstantTransmission(event: IZeroSEvent.ActivatedInstantTransmissionEvent): void;

  /** Fired before NPCDBC applies a custom form's scheduled Ki drain. */
  function dbcFormDrain(event: IZeroSEvent.FormDrainEvent): void;

  /** Fired when a player performs a valid Zero S weapon swing. */
  function zsWeaponSwing(event: IZeroSEvent.WeaponSwingEvent): void;

  /** Fired in the dome owner's NPC or player script when an entity enters the dome. */
  function energyDomeEnter(event: IZeroSEvent.EnergyDomeEnterEvent): void;

  /** Fired in the dome owner's NPC or player script when an entity leaves the dome. */
  function energyDomeExit(event: IZeroSEvent.EnergyDomeExitEvent): void;
}

declare namespace IPlayerEvent {
  /** Fired once when normal transform input begins charging an NPCDBC custom form, before the form changes. */
  function beginsTransforming(event: IZeroSEvent.BeginsTransformingEvent): void;

  /** Fired in player scripts when DBC successfully activates short-range Instant Transmission. */
  function activatedInstantTransmission(event: IZeroSEvent.ActivatedInstantTransmissionEvent): void;

  /** Fired before NPCDBC applies a custom form's scheduled Ki drain. */
  function dbcFormDrain(event: IZeroSEvent.FormDrainEvent): void;

  /** Fired when a player performs a valid Zero S weapon swing. */
  function zsWeaponSwing(event: IZeroSEvent.WeaponSwingEvent): void;

  /** Fired when an entity enters a player's energy dome. */
  function energyDomeEnter(event: IZeroSEvent.EnergyDomeEnterEvent): void;

  /** Fired when an entity leaves a player's energy dome. */
  function energyDomeExit(event: IZeroSEvent.EnergyDomeExitEvent): void;
}

declare namespace INpcEvent {
  /** Fired when an entity enters an NPC owner's energy dome. */
  function energyDomeEnter(event: IZeroSEvent.EnergyDomeEnterEvent): void;

  /** Fired when an entity leaves an NPC owner's energy dome. */
  function energyDomeExit(event: IZeroSEvent.EnergyDomeExitEvent): void;
}
