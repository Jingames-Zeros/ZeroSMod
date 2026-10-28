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
export interface ZeroSAPI extends AbstractZeroSAPI {
}

/**
 * Fallback mapping for older CNPC editor global-object resolution.
 * @javaFqn org.darkoro.zerosmod.api.AbstractZeroSAPI
 */
export interface AbstractZeroSAPI extends ZSAPI {
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
 * Dome ability settings; only dome abilities have them. Changes apply to later casts.
 * @javaFqn noppes.npcs.api.ability.IAbility
 */
export interface IAbility {
  /** Returns the dome radius in blocks. */
  getDomeRadius(): number;

  /** Sets the dome radius in blocks, at least 1. */
  setDomeRadius(radius: number): void;

  /** Returns the dome lifetime in ticks, used only when the ability's duration is enabled. */
  getBarrierDuration(): number;

  /** Sets the dome lifetime in ticks, at least 1. */
  setBarrierDuration(ticks: number): void;
}

/**
 * Spawned dome methods; only domes (getBarrierType() 0) have them.
 * @javaFqn noppes.npcs.api.entity.IEnergyBarrier
 */
export interface IEnergyBarrier<T> {
  /** Returns damage per hit to living entities inside, 0 when disabled. */
  getDamage(): number;

  /** Sets damage per hit; 0 disables it. */
  setDamage(damage: number): void;

  /** Returns ticks between hits. */
  getDamageInterval(): number;

  /** Sets ticks between hits, at least 1. */
  setDamageInterval(ticks: number): void;

  /** Returns true when the dome also damages its owner. */
  affectOwner(): boolean;

  /** Sets whether the dome also damages its owner. */
  setAffectOwner(affects: boolean): void;

  /** Returns the followed entity, including a followed caster, or null. */
  getFollower(): IEntity<any>;

  /** Follows an entity in the same world, or stays in place with null. */
  setFollower(entity: IEntity<any>): void;

  /** Returns the entities inside the dome, including the owner. */
  getEntitiesInside(): IEntity<any>[];

  /** Returns true when the entity is inside the dome. */
  isInside(entity: IEntity<any>): boolean;

  /** Resizes the dome to radius (at least 0.1) over ticks; 0 ticks resizes immediately. */
  setTargetRadius(radius: number, ticks: number): void;
}

/**
 * DBC methods on player.getDBCPlayer(). Status effects, case-insensitive: timed Strain, GodPower,
 * Fatigue, NoFuse, Pain, Heat, KO; on/off Majin, Legendary, Divine, GodOfDestruction.
 * @javaFqn noppes.npcs.api.entity.IDBCPlayer
 */
export interface IDBCPlayer {
  /** Returns false while the player is dead in DBC (has a halo). */
  isDBCAlive(): boolean;

  /** Ends the fusion for both online participants and applies DBC's fusion cooldown. */
  defuse(): void;

  /** Returns the remaining fusion time in seconds, or 0 when not fused. */
  getFusionTimeLeft(): number;

  /**
   * Sets one status effect. Timed effects take seconds, rounded up to 5; 0 removes them.
   * On/off effects turn off with 0 and on with any other value. NoFuse is ignored while fused.
   * Throws for an unknown effect name.
   */
  setDBCStatusEffect(effect: string, duration: number): void;

  /** Returns remaining seconds for a timed effect, or 1/0 for an on/off effect. Throws for an unknown name. */
  getDBCStatusEffect(effect: string): number;
}

/**
 * NPCDBC custom-form heat on player.getDBCPlayer(). Vanilla DBC heat is not affected.
 * @javaFqn kamkeel.npcdbc.api.IDBCAddon
 */
export interface IDBCAddon {
  /** Returns stored heat, including heat still cooling down after leaving a form. */
  getCurrentHeat(): number;

  /** Returns heat from 0 to 100 percent of getMaxHeat(), or 0 without a heat form. */
  getCurrentHeatPercent(): number;

  /** Returns the active form's maximum heat, or 0 without a heat form. */
  getMaxHeat(): number;

  /** Sets heat, clamped to 0 through getMaxHeat(). Does nothing without a heat form. */
  setCurrentHeat(amount: number): void;

  /** Sets heat as 0 to 100 percent of getMaxHeat(). Does nothing without a heat form. */
  setHeatPercentage(percent: number): void;

  /** Sets stored heat to 0, also outside a form. */
  clearHeat(): void;
}

/**
 * Destroyer accent color on createKiAttack() objects, set before fireKiAttack().
 * @javaFqn kamkeel.npcdbc.api.IKiAttack
 */
export interface IKiAttack {
  /** Sets the accent color (0x000000 to 0xFFFFFF). Returns false when out of range. */
  setDestroyerColor(rgb: number): boolean;

  /** Returns the accent color as 0xRRGGBB, or -1 for DBC's default. */
  getDestroyerColor(): number;

  /** Restores DBC's default destroyer colors. Returns true. */
  clearDestroyerColor(): boolean;
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

  /** Sets the destroyer accent color (0x000000 to 0xFFFFFF). Returns false for non-destroyer ki or out of range. */
  setDestroyerColor(rgb: number): boolean;

  /** Returns the destroyer accent color as 0xRRGGBB, or -1 for DBC's default. */
  getDestroyerColor(): number;

  /** Restores DBC's default destroyer colors. Returns false for non-destroyer ki. */
  clearDestroyerColor(): boolean;

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
  /** Returns the screen width in scaled GUI coordinates, or -1 before the client reported it. */
  getScaledScreenWidth(): number;

  /** Returns the screen height in scaled GUI coordinates, or -1 before the client reported it. */
  getScaledScreenHeight(): number;

  /** Returns Minecraft's GUI Scale setting, 0 for Auto, or -1 before the client reported it. */
  getGuiScale(): number;

  /** Returns true while the player charges an NPCDBC custom-form transformation. */
  isTransforming(): boolean;

  /** Returns the current short-range Instant Transmission target name only while the player is preparing IT. */
  getInstantTransmissionLookTargetName(): string;
}

/**
 * Script wrappers returned to NPC/player scripts. These declarations mirror the Java wrapper
 * hierarchy because CNPC+'s editor may infer the concrete Script* class instead of its I* API
 * interface.
 * @javaFqn noppes.npcs.scripted.entity.ScriptEntity
 */
export interface ScriptEntity<T> extends IEntity<T> {
}

/**
 * Living CNPC script wrapper, including the Zero S nearby-ki methods.
 * @javaFqn noppes.npcs.scripted.entity.ScriptLivingBase
 */
export interface ScriptLivingBase<T> extends IEntityLivingBase<T> {
  /** Stops nearby DBC ki attacks around this living entity until released. */
  stopKi(range: number): number;

  /** Releases nearby stopped DBC ki attacks around this living entity. */
  releaseKi(range: number): number;

  /** Returns nearby DBC ki attacks around this living entity. */
  getNearbyKi(range: number): IEntity<any>[];
}

/** @javaFqn noppes.npcs.scripted.entity.ScriptLiving */
export interface ScriptLiving<T> extends ScriptLivingBase<T> {
}

/** @javaFqn noppes.npcs.scripted.entity.ScriptNpc */
export interface ScriptNpc<T> extends ScriptLiving<T> {
}

/**
 * CNPC player script wrapper, with Zero S methods available directly on script players.
 * @javaFqn noppes.npcs.scripted.entity.ScriptPlayer
 */
export interface ScriptPlayer<T> extends IPlayer<T> {
  /** Returns the screen width in scaled GUI coordinates. */
  getScaledScreenWidth(): number;

  /** Returns the screen height in scaled GUI coordinates. */
  getScaledScreenHeight(): number;

  /** Returns Minecraft's GUI Scale setting, 0 for Auto. */
  getGuiScale(): number;

  /** Returns true while the player charges a custom-form transformation. */
  isTransforming(): boolean;

  /** Returns the current short-range Instant Transmission target while preparing IT. */
  getInstantTransmissionLookTargetName(): string;

  /** Returns nearby DBC ki attacks around this player. */
  getNearbyKi(range: number): IEntity<any>[];
}

/**
 * DBC player script wrapper returned by player.getDBCPlayer().
 * @javaFqn noppes.npcs.scripted.entity.ScriptDBCPlayer
 */
export interface ScriptDBCPlayer extends IDBCPlayer {
  isDBCAlive(): boolean;
  defuse(): void;
  getFusionTimeLeft(): number;
  setDBCStatusEffect(effect: string, duration: number): void;
  getDBCStatusEffect(effect: string): number;
}

/**
 * DBC Addon script wrapper returned by player.getDBCPlayer().
 * @javaFqn kamkeel.npcdbc.scripted.ScriptDBCAddon
 */
export interface ScriptDBCAddon extends IDBCAddon {
  getCurrentHeat(): number;
  getCurrentHeatPercent(): number;
  getMaxHeat(): number;
  setCurrentHeat(amount: number): void;
  setHeatPercentage(percent: number): void;
  clearHeat(): void;
}

/**
 * Script wrapper for a spawned CNPC+ energy dome.
 * @javaFqn noppes.npcs.scripted.entity.ScriptEnergyDome
 */
export interface ScriptEnergyDome extends IEnergyDome<any> {
}

/**
 * Script configuration object for CNPC+ dome abilities.
 * @javaFqn kamkeel.npcs.controllers.data.ability.type.energy.AbilityDome
 */
export interface AbilityDome extends IAbility {
  getDomeRadius(): number;
  setDomeRadius(radius: number): void;
  getBarrierDuration(): number;
  setBarrierDuration(ticks: number): void;
}

/**
 * Zero S Mod player events.
 * @javaFqn org.darkoro.zerosmod.api.event.IZeroSEvent
 */
export namespace IZeroSEvent {
  /**
   * Fired in the dome owner's script when an entity enters a dome.
   * @hookName energyDomeEnter
   */
  interface EnergyDomeEnterEvent extends IEnergyBarrierEvent {
    /** Returns the dome. */
    getDome(): IEnergyDome<any>;

    /** Returns the entity that entered. */
    getEntity(): IEntity<any>;
  }

  /**
   * Fired in the dome owner's script when an entity leaves a dome, also when the dome is removed.
   * @hookName energyDomeExit
   */
  interface EnergyDomeExitEvent extends IEnergyBarrierEvent {
    /** Returns the dome. */
    getDome(): IEnergyDome<any>;

    /** Returns the entity that left. */
    getEntity(): IEntity<any>;
  }

  /**
   * Fired before a valid Zero S weapon hit. Cancel it to prevent the hit and its cooldown.
   * @hookName zsWeaponSwing
   */
  interface WeaponSwingEvent extends IPlayerEvent {
    /** Returns the live stats of the swung weapon. */
    getWeapon(): ScriptZSWeapon<any>;

    /** Returns the cooldown this swing starts, in ticks. */
    getCooldown(): number;

    /** Sets the cooldown for this swing only; 0 allows the next attack immediately. */
    setCooldown(ticks: number): void;
  }

  /**
   * Fired before NPCDBC drains Ki for a custom form. Cancel it to skip this drain.
   * @hookName dbcFormDrain
   */
  interface FormDrainEvent extends IPlayerEvent {
    /** Returns the Ki about to be drained. */
    getDrain(): number;

    /** Sets the Ki to drain instead, rounded up; 0 skips the drain. */
    setDrain(drain: number): void;
  }

  /**
   * Fired when a player starts charging an NPCDBC custom-form transformation.
   * @hookName beginsTransforming
   */
  interface BeginsTransformingEvent extends IPlayerEvent {
    /** Returns the target form. */
    getTargetForm(): IForm;

    /** Returns the target form's ID. */
    getTargetFormId(): number;

    /** Returns the target form's name. */
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
