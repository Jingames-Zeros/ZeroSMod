/**
 * Zero S Mod script API.
 * @javaFqn org.darkoro.zerosmod.api.ZSAPI
 */
export interface ZSAPI {
  /** Returns the entity DBC would currently select for the player's short-range Instant Transmission, or null. */
  getInstantTransmissionLookTarget(player: IPlayer<any>): IEntityLivingBase<any>;

  /** Returns the name of the entity DBC would currently select for the player's short-range Instant Transmission. */
  getInstantTransmissionLookTargetName(player: IPlayer<any>): string;

  /** Returns true if entity was the target of a successful short-range Instant Transmission within maxAgeTicks. */
  wasRecentInstantTransmissionTarget(entity: IEntity<any>, maxAgeTicks: number): boolean;

  /** Returns the player who recently used short-range Instant Transmission to entity, or null. */
  getRecentInstantTransmissionPlayer(entity: IEntity<any>, maxAgeTicks: number): IPlayer<any>;

  /** Returns the name of the player who recently used short-range Instant Transmission to entity. */
  getRecentInstantTransmissionPlayerName(entity: IEntity<any>, maxAgeTicks: number): string;

  /** Returns ticks since entity was targeted by short-range Instant Transmission, or -1. */
  getTicksSinceInstantTransmissionTargeted(entity: IEntity<any>): number;

  /** Returns true if entity is a DBC ki attack. */
  isKiAttack(entity: IEntity<any>): boolean;

  /** Returns the readable DBC ki attack type name. */
  getKiType(kiAttack: IEntity<any>): string;

  /** Returns the Minecraft entity id for a DBC ki attack, or -1. */
  getKiId(kiAttack: IEntity<any>): number;

  /** Returns true when a DBC ki attack is currently frozen by Zero S ki scripting. */
  isKiStopped(kiAttack: IEntity<any>): boolean;

  /** Freezes DBC ki attacks around origin until released and returns how many were newly stopped. */
  stopKi(origin: IEntity<any>, range: number): number;

  /** Releases stopped DBC ki attacks around origin and returns how many were released. */
  releaseKi(origin: IEntity<any>, range: number): number;

  /** Returns nearby DBC ki attacks around origin. */
  getNearbyKi(origin: IEntity<any>, range: number): IEntity<any>[];

  /** Changes a DBC ki attack owner/color and restarts its lifetime. */
  stealKi(kiAttack: IEntity<any>, color: string, owner: IEntity<any>): boolean;

  /** Redirects a DBC ki attack toward target. */
  redirectKi(kiAttack: IEntity<any>, target: IEntity<any>): boolean;

  /** Sends a DBC ki attack along a manual direction vector while preserving its speed. */
  directKi(kiAttack: IEntity<any>, x: number, y: number, z: number): boolean;

  /** Changes a DBC ki attack color without changing owner. */
  setKiColor(kiAttack: IEntity<any>, color: string): boolean;

  /** Gets the player's live combat state (attack cooldown, current weapon stats), or null when player is null. */
  getPlayerCombatState(player: IPlayer<any>): ScriptPlayerCombatState<any>;

  /** Reads the Zero S weapon stats of an item, or null when item is null. Changes are written back to the item. */
  getZSWeapon(item: IItemStack<any>): ScriptZSWeapon<any>;
}

/**
 * Runtime global object mapping for CNPC's editor.
 * @javaFqn org.darkoro.zerosmod.scripted.ZeroSAPI
 */
export interface ZeroSAPI extends ZSAPI {
  /** Returns the entity DBC would currently select for the player's short-range Instant Transmission, or null. */
  getInstantTransmissionLookTarget(player: IPlayer<any>): IEntityLivingBase<any>;

  /** Returns the name of the entity DBC would currently select for the player's short-range Instant Transmission. */
  getInstantTransmissionLookTargetName(player: IPlayer<any>): string;

  /** Returns true if entity was the target of a successful short-range Instant Transmission within maxAgeTicks. */
  wasRecentInstantTransmissionTarget(entity: IEntity<any>, maxAgeTicks: number): boolean;

  /** Returns the player who recently used short-range Instant Transmission to entity, or null. */
  getRecentInstantTransmissionPlayer(entity: IEntity<any>, maxAgeTicks: number): IPlayer<any>;

  /** Returns the name of the player who recently used short-range Instant Transmission to entity. */
  getRecentInstantTransmissionPlayerName(entity: IEntity<any>, maxAgeTicks: number): string;

  /** Returns ticks since entity was targeted by short-range Instant Transmission, or -1. */
  getTicksSinceInstantTransmissionTargeted(entity: IEntity<any>): number;

  /** Returns true if entity is a DBC ki attack. */
  isKiAttack(entity: IEntity<any>): boolean;

  /** Returns the readable DBC ki attack type name. */
  getKiType(kiAttack: IEntity<any>): string;

  /** Returns the Minecraft entity id for a DBC ki attack, or -1. */
  getKiId(kiAttack: IEntity<any>): number;

  /** Returns true when a DBC ki attack is currently frozen by Zero S ki scripting. */
  isKiStopped(kiAttack: IEntity<any>): boolean;

  /** Freezes DBC ki attacks around origin until released and returns how many were newly stopped. */
  stopKi(origin: IEntity<any>, range: number): number;

  /** Releases stopped DBC ki attacks around origin and returns how many were released. */
  releaseKi(origin: IEntity<any>, range: number): number;

  /** Returns nearby DBC ki attacks around origin. */
  getNearbyKi(origin: IEntity<any>, range: number): IEntity<any>[];

  /** Changes a DBC ki attack owner/color and restarts its lifetime. */
  stealKi(kiAttack: IEntity<any>, color: string, owner: IEntity<any>): boolean;

  /** Redirects a DBC ki attack toward target. */
  redirectKi(kiAttack: IEntity<any>, target: IEntity<any>): boolean;

  /** Sends a DBC ki attack along a manual direction vector while preserving its speed. */
  directKi(kiAttack: IEntity<any>, x: number, y: number, z: number): boolean;

  /** Changes a DBC ki attack color without changing owner. */
  setKiColor(kiAttack: IEntity<any>, color: string): boolean;

  /** Gets the player's live combat state (attack cooldown, current weapon stats), or null when player is null. */
  getPlayerCombatState(player: IPlayer<any>): ScriptPlayerCombatState<any>;

  /** Reads the Zero S weapon stats of an item, or null when item is null. Changes are written back to the item. */
  getZSWeapon(item: IItemStack<any>): ScriptZSWeapon<any>;
}

/**
 * Fallback mapping for older CNPC editor global-object resolution.
 * @javaFqn org.darkoro.zerosmod.api.AbstractZeroSAPI
 */
export interface AbstractZeroSAPI extends ZSAPI {
  /** Returns the entity DBC would currently select for the player's short-range Instant Transmission, or null. */
  getInstantTransmissionLookTarget(player: IPlayer<any>): IEntityLivingBase<any>;

  /** Returns the name of the entity DBC would currently select for the player's short-range Instant Transmission. */
  getInstantTransmissionLookTargetName(player: IPlayer<any>): string;

  /** Returns true if entity was the target of a successful short-range Instant Transmission within maxAgeTicks. */
  wasRecentInstantTransmissionTarget(entity: IEntity<any>, maxAgeTicks: number): boolean;

  /** Returns the player who recently used short-range Instant Transmission to entity, or null. */
  getRecentInstantTransmissionPlayer(entity: IEntity<any>, maxAgeTicks: number): IPlayer<any>;

  /** Returns the name of the player who recently used short-range Instant Transmission to entity. */
  getRecentInstantTransmissionPlayerName(entity: IEntity<any>, maxAgeTicks: number): string;

  /** Returns ticks since entity was targeted by short-range Instant Transmission, or -1. */
  getTicksSinceInstantTransmissionTargeted(entity: IEntity<any>): number;

  /** Returns true if entity is a DBC ki attack. */
  isKiAttack(entity: IEntity<any>): boolean;

  /** Returns the readable DBC ki attack type name. */
  getKiType(kiAttack: IEntity<any>): string;

  /** Returns the Minecraft entity id for a DBC ki attack, or -1. */
  getKiId(kiAttack: IEntity<any>): number;

  /** Returns true when a DBC ki attack is currently frozen by Zero S ki scripting. */
  isKiStopped(kiAttack: IEntity<any>): boolean;

  /** Freezes DBC ki attacks around origin until released and returns how many were newly stopped. */
  stopKi(origin: IEntity<any>, range: number): number;

  /** Releases stopped DBC ki attacks around origin and returns how many were released. */
  releaseKi(origin: IEntity<any>, range: number): number;

  /** Returns nearby DBC ki attacks around origin. */
  getNearbyKi(origin: IEntity<any>, range: number): IEntity<any>[];

  /** Changes a DBC ki attack owner/color and restarts its lifetime. */
  stealKi(kiAttack: IEntity<any>, color: string, owner: IEntity<any>): boolean;

  /** Redirects a DBC ki attack toward target. */
  redirectKi(kiAttack: IEntity<any>, target: IEntity<any>): boolean;

  /** Sends a DBC ki attack along a manual direction vector while preserving its speed. */
  directKi(kiAttack: IEntity<any>, x: number, y: number, z: number): boolean;

  /** Changes a DBC ki attack color without changing owner. */
  setKiColor(kiAttack: IEntity<any>, color: string): boolean;

  /** Gets the player's live combat state (attack cooldown, current weapon stats), or null when player is null. */
  getPlayerCombatState(player: IPlayer<any>): ScriptPlayerCombatState<any>;

  /** Reads the Zero S weapon stats of an item, or null when item is null. Changes are written back to the item. */
  getZSWeapon(item: IItemStack<any>): ScriptZSWeapon<any>;
}

/**
 * Zero S Mod weapon global API.
 * @javaFqn org.darkoro.zerosmod.api.WeaponAPI
 */
export interface WeaponAPI {
  /** Reads the configured Zero S weapon type from an item, or "default" when it has none. */
  getWeaponType(item: IItemStack<any>): string;

  /** Sets the Zero S weapon type on an item. Returns false when the item is null or the type is not loaded. */
  setWeaponType(type: string, item: IItemStack<any>): boolean;

  /** Returns the names of all loaded Zero S weapon types. */
  getLoadedWeaponTypeNames(): string[];

  /** Returns copies of all loaded Zero S weapon types by name; changing them does not affect the server config. */
  getLoadedWeaponTypes(): any;

  /** Returns a copy of the default Zero S weapon type, or null when it is not loaded. */
  getDefaultWeaponState(): ScriptZSWeapon<any>;
}

/**
 * Zero S Mod script-facing weapon stats object.
 * @javaFqn org.darkoro.zerosmod.api.ScriptZSWeapon
 */
export interface ScriptZSWeapon<T> {
  /** Sets this item's weapon type to a loaded weapon type. */
  setType(type: string): void;

  /** Sets this item to the default weapon stats. */
  setToDefaultStats(): void;

  /** Sets this item to the special weapon type so its stats can be edited. */
  setSpecial(): void;

  /** Returns this weapon's required level. */
  getLevelReq(): number;

  /** Returns this weapon's attack range. */
  getRange(): number;

  /** Returns this weapon's squared attack range. */
  getRangeSq(): number;

  /** Returns this weapon's attack cooldown. */
  getCooldown(): number;

  /** Returns the backing item stack. */
  getItem(): IItemStack<any>;

  /** Returns this weapon's raw type key. */
  getType(): string;

  /** Returns this weapon's formatted type name. */
  getFormattedType(): string;

  /** Returns this weapon's attack percentage multiplier. */
  getAttackPercent(): number;

  /** Returns this weapon's flat attack bonus. */
  getAttackAdditive(): number;

  /** Returns this weapon's sweet spot distance. */
  getSweetSpot(): number;

  /** Returns true if this weapon can charge ki. */
  canChargeKi(): boolean;

  /** Returns this weapon's ki percentage multiplier. */
  getKiPercent(): number;

  /** Returns this weapon's flat ki bonus. */
  getKiAdditive(): number;

  /** Returns this weapon's ki cost percentage multiplier. */
  getKiCostPercent(): number;

  /** Returns true if this weapon can block. */
  canBlock(): boolean;

  /** Returns this weapon's block dexterity percentage multiplier. */
  getBlockDexPercent(): number;

  /** Returns this weapon's block cost percentage multiplier. */
  getBlockCostPercent(): number;

  /** Returns this weapon's block cooldown. */
  getBlockCooldown(): number;

  /** Sets this weapon's required level. */
  setLevelReq(levelReq: number): void;

  /** Sets this weapon's flat ki bonus. */
  setKiAdditive(kiAdditive: number): void;

  /** Sets this weapon's flat attack bonus. */
  setAttackAdditive(attack: number): void;

  /** Sets this weapon's attack cooldown. */
  setCooldown(cooldown: number): void;

  /** Sets this weapon's attack percentage multiplier. */
  setAttackPercent(attackPercent: number): void;

  /** Sets this weapon's sweet spot distance. */
  setSweetSpot(sweetSpot: number): void;

  /** Sets whether this weapon can charge ki. */
  setCanChargeKi(canChargeKi: boolean): void;

  /** Sets this weapon's ki percentage multiplier. */
  setKiPercent(kiPercent: number): void;

  /** Sets this weapon's ki cost percentage multiplier. */
  setKiCostPercent(kiCostPercent: number): void;

  /** Sets whether this weapon can block. */
  setCanBlock(canBlock: boolean): void;

  /** Sets this weapon's block dexterity percentage multiplier. */
  setBlockDexPercent(blockDexPercent: number): void;

  /** Sets this weapon's block cost percentage multiplier. */
  setBlockCostPercent(blockCostPercent: number): void;

  /** Sets this weapon's block cooldown. */
  setBlockCooldown(blockCooldown: number): void;

  /** Sets this weapon's attack range. */
  setRange(range: number): void;

  /** Sets this weapon's formatted type name. */
  setFormattedType(formattedType: string): void;
}

/**
 * Zero S Mod concrete weapon stats object.
 * @javaFqn org.darkoro.zerosmod.zsweapons.cache.CachedWeaponStats
 */
export interface CachedWeaponStats<T> extends ScriptZSWeapon<T> {
}

/**
 * Zero S Mod script-facing player weapon combat state.
 * @javaFqn org.darkoro.zerosmod.api.ScriptPlayerCombatState
 */
export interface ScriptPlayerCombatState<T> {
  /** Updates the current item and weapon stats from the given item. */
  changeItem(item: IItemStack<any>): void;

  /** Sets the current weapon stats independent of the held item. */
  setCurrentZSWeapon(itemStats: ScriptZSWeapon<any>, resetCooldown: boolean): void;

  /** Refreshes the current item if it matches the given item. */
  refreshItem(item: IItemStack<any>): void;

  /** Triggers the current attack cooldown. */
  resetCooldown(): void;

  /** Returns the remaining attack cooldown. */
  getRemainingAttackCooldown(): number;

  /** Returns the current weapon stats. */
  getCurrentZSWeapon(): ScriptZSWeapon<any>;

  /** Returns the current script item. */
  getCurrentScriptItem(): IItemStack<any>;

  /** Sets the remaining attack cooldown. */
  setRemainingAttackCooldown(remainingAttackCooldown: number): void;
}

/**
 * Zero S Mod concrete player weapon combat state.
 * @javaFqn org.darkoro.zerosmod.zsweapons.PlayerCombatState
 */
export interface PlayerCombatState<T> extends ScriptPlayerCombatState<T> {
}

/**
 * Zero S DBC methods for the player wrapper returned by player.getDBCPlayer().
 * @javaFqn noppes.npcs.api.entity.IDBCPlayer
 */
export interface IDBCPlayer {
  /** Returns DBC's life state: true while alive and false while the player has a halo. This is separate from inherited isAlive(). */
  isDBCAlive(): boolean;

  /** Ends an active fusion for both online participants and applies DBC's configured fusion cooldown. */
  defuse(): void;

  /** Returns active fusion time left in seconds, excluding the post-fusion cooldown, or 0 when unfused. */
  getFusionTimeLeft(): number;

  /**
   * Sets one DBC status effect without replacing other effects. Names are case-insensitive.
   * Timed effects use seconds and round positive durations up to DBC's five-second interval;
   * zero or less removes them. Timed names: Strain, GodPower, Fatigue, NoFuse, Pain, Heat, KO.
   * NoFuse does nothing while fused. Heat is legacy DBC UI heat, not NPCDBC custom-form heat.
   * Toggle names: Majin, Legendary, Divine, GodOfDestruction. Zero disables; any other value enables.
   * Example: setDBCStatusEffect("Pain", 30) applies 30 seconds; setDBCStatusEffect("Majin", 1) enables Majin.
   * Throws a script error for an unsupported name.
   */
  setDBCStatusEffect(effect: string, duration: number): void;

  /**
   * Returns remaining seconds for a timed DBC status effect, in multiples of five, or 1/0 for a toggle.
   * Names are case-insensitive. Throws a script error for an unsupported name.
   */
  getDBCStatusEffect(effect: string): number;
}

/**
 * Custom-form heat methods for the DBC Addon player wrapper returned by player.getDBCPlayer().
 * These methods do not read or change vanilla DBC heat.
 * @javaFqn kamkeel.npcdbc.api.IDBCAddon
 */
export interface IDBCAddon {
  /** Returns stored custom-form heat as a raw amount, including heat that is cooling down after leaving a form. */
  getCurrentHeat(): number;

  /** Returns the active custom form's maximum heat, or 0 when no heat-enabled custom form is active. */
  getMaxHeat(): number;

  /**
   * Sets raw custom-form heat, clamped from 0 to getMaxHeat().
   * If the maximum is 200, setCurrentHeat(50) fills one quarter of the bar.
   * Does nothing when no heat-enabled custom form is active or amount is not finite.
   */
  setCurrentHeat(amount: number): void;

  /**
   * Sets custom-form heat to a percentage of getMaxHeat(), clamped from 0 to 100.
   * If the maximum is 200, setHeatPercentage(50) sets the heat to 100.
   * Does nothing when no heat-enabled custom form is active or percent is not finite.
   */
  setHeatPercentage(percent: number): void;

  /** Sets stored custom-form heat to 0, including heat that is cooling down outside a form. */
  clearHeat(): void;
}

/**
 * Zero S Mod patch for all CNPC entity wrappers.
 * @javaFqn noppes.npcs.api.entity.IEntity
 */
export interface IEntity<T> {
  /** Returns true when this entity is a DBC ki attack. */
  isKiAttack(): boolean;

  /** Returns true when this ki attack was fired by a player. */
  isPlayerKi(): boolean;

  /** Returns true when this ki attack was fired by a CustomNPC+ NPC. */
  isNpcKi(): boolean;

  /** Returns true when this ki attack has the DBC/NPCDBC destroyer flag. */
  isDestroyerAttack(): boolean;

  /** Returns the Minecraft entity id for this ki attack, or -1. */
  getKiId(): number;

  /** Returns true when this ki attack is currently frozen by Zero S ki scripting. */
  isKiStopped(): boolean;

  /** Returns the player owner name, or an empty string. */
  getPlayerName(): string;

  /** Returns the current ki owner name, or an empty string. */
  getKiOwnerName(): string;

  /** Returns the current ki owner entity, or null. */
  getKiOwner(): IEntity<any>;

  /** Returns the readable DBC ki attack type name. */
  getKiType(): string;

  /** Returns the raw DBC ki attack type id, or -1. */
  getKiTypeId(): number;

  /** Returns the current DBC ki attack damage. */
  getKiDamage(): number;

  /** Changes this ki attack's damage. */
  setKiDamage(damage: number): boolean;

  /** Changes this ki attack's color by DBC color name. */
  setKiColor(color: string): boolean;

  /** Changes this ki attack's color by raw DBC color id. */
  setKiColor(color: number): boolean;

  /** Changes this ki attack's primary and secondary color ids. */
  setKiColor(color: number, color2: number): boolean;

  /** Freezes this ki attack in place until releaseKi() is called. */
  stopKi(): boolean;

  /** Releases this ki attack from a stopped state. */
  releaseKi(): boolean;

  /** Restarts this ki attack's lifetime. */
  resetKiLifetime(): boolean;

  /** Removes this ki attack. */
  killKi(): boolean;

  /** Changes this ki attack owner and color, then restarts its lifetime. */
  stealKi(color: string, owner: IEntity<any>): boolean;

  /** Changes this ki attack owner and color, then restarts its lifetime. */
  stealKi(color: number, owner: IEntity<any>): boolean;

  /** Redirects this ki attack toward target. */
  redirectKi(target: IEntity<any>): boolean;

  /** Sends this ki attack along a manual direction vector while preserving its speed. */
  directKi(x: number, y: number, z: number): boolean;

  /** Returns the current short-range Instant Transmission target name when this entity is a player preparing IT. */
  getInstantTransmissionLookTargetName(): string;
}

/**
 * Zero S Mod patch for CNPC living entity wrappers.
 * @javaFqn noppes.npcs.api.entity.IEntityLivingBase
 */
export interface IEntityLivingBase<T> {
  /** Stops nearby DBC ki attacks around this living entity until released. */
  stopKi(range: number): number;

  /** Releases nearby stopped DBC ki attacks around this living entity. */
  releaseKi(range: number): number;

  /** Returns nearby DBC ki attacks around this living entity. */
  getNearbyKi(range: number): IEntity<any>[];
}

/**
 * Zero S Mod patch for CNPC player wrappers.
 * @javaFqn noppes.npcs.api.entity.IPlayer
 */
export interface IPlayer<T> {
  /**
   * Returns true while normal transform input is charging an NPCDBC custom form.
   * Returns false after charging stops or completes. Programmatic form changes do not count.
   */
  isTransforming(): boolean;

  /** Returns the current short-range Instant Transmission target name only while the player is preparing IT. */
  getInstantTransmissionLookTargetName(): string;
}

/**
 * Zero S Mod player events.
 * @javaFqn org.darkoro.zerosmod.api.event.IZeroSEvent
 */
export namespace IZeroSEvent {
  /**
   * Fired once when a player begins charging an NPCDBC custom-form transformation.
   * It runs before the form changes and still fires if charging is later abandoned.
   * Releasing and starting again fires a new event. Vanilla and programmatic changes do not fire it.
   * @hookName beginsTransforming
   */
  interface BeginsTransformingEvent extends IPlayerEvent {
    /** Returns the custom form the player is attempting to transform into. Never null. */
    getTargetForm(): IForm;

    /** Returns the numeric ID of the target custom form. */
    getTargetFormId(): number;

    /** Returns the internal name of the target custom form. */
    getTargetFormName(): string;
  }

  /**
   * Fired in player scripts when DBC successfully activates short-range Instant Transmission.
   * @hookName activatedInstantTransmission
   */
  interface ActivatedInstantTransmissionEvent extends IPlayerEvent {
    /** Returns the entity DBC selected as the Instant Transmission target, or null. */
    getTarget(): IEntityLivingBase<any>;

    /** Returns the selected target name, or an empty string. */
    getTargetName(): string;
  }
}
