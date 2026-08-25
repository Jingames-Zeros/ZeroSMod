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

  /** Gets a player's current combat state, containing attack cooldown, current stats */
  getPlayerCombatState(player: IPlayer<any>): ScriptPlayerCombatState<any>;

  /** Gets a ZS Weapon instance from an item stack. Allows for configuration and saving of item type and stats. */
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

  /** Gets a player's current combat state, containing attack cooldown, current stats */
  getPlayerCombatState(player: IPlayer<any>): ScriptPlayerCombatState<any>;

  /** Gets a ZS Weapon instance from an item stack. Allows for configuration and saving of item type and stats. */
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

  /** Gets a player's current combat state, containing attack cooldown, current stats */
  getPlayerCombatState(player: IPlayer<any>): ScriptPlayerCombatState<any>;

  /** Gets a ZS Weapon instance from an item stack. Allows for configuration and saving of item type and stats. */
  getZSWeapon(item: IItemStack<any>): ScriptZSWeapon<any>;
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
  /** Returns the current short-range Instant Transmission target name only while the player is preparing IT. */
  getInstantTransmissionLookTargetName(): string;
}

/**
 * Zero S Mod player events.
 * @javaFqn org.darkoro.zerosmod.api.event.IZeroSEvent
 */
export namespace IZeroSEvent {
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
