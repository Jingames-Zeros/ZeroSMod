/**
 * Editor declarations for the server's Forge global scripts.
 *
 * These describe the objects registered by API.addGlobalObject in the server's
 * library scripts. They do not create those objects or run the scripts.
 * Keep the method names and signatures in sync with libraryObject.
 */
export interface ZSServerLib {
  /** Sends a debug message to a player looked up by name. */
  debugMessage(playerName: string, text: string): void;

  /** Resets an NPC after its remembered target dies. */
  checkReset(npc: ICustomNpc<any>): boolean;
  /** Resets an NPC when its target dies and no vulnerable player is nearby. */
  checkResetParty(npc: ICustomNpc<any>): boolean;

  /** Shows a speech overlay. A timeout of zero leaves it visible until removed. */
  speak(player: IPlayer<any>, text: string, speakID: number, timeout: number, color?: number, hasShadow?: boolean, size?: number, noCenter?: boolean, xOffset?: number, yOffset?: number): void;
  /** Removes a speech overlay by ID. */
  cancelSpeak(player: IPlayer<any>, speakID: number): void;

  /** Returns a random value between min and max; getInt selects whole numbers. */
  getRandom(min: number, max: number, getInt?: boolean): number;
  /** Returns the horizontal angle from entity1 to entity2. */
  getAngleToEntity(entity1: IEntity<any>, entity2: IEntity<any>): number;
  /** Returns a normalized direction vector between two positions or coordinate arrays. */
  get3dDirection(pos1: any, pos2: any): number[];
  /** Checks whether an entity is a player. */
  isPlayer(entity: IEntity<any>): boolean;
  /** Checks whether a player is a valid, active DBC player. */
  isValidPlayer(player: IPlayer<any>): boolean;
  /** Estimates a player's DBC level from their attributes. */
  getDbcLevel(player: IPlayer<any>): number;
  /** Checks sword or DBC blocking. */
  checkBlocking(player: IPlayer<any>): boolean;

  /** Gives the configured quest-reward Z Sword if the player has none. */
  giveZSword(player: IPlayer<any>): void;
  /** Removes Z Swords from the player's inventory. */
  removeZSword(player: IPlayer<any>): boolean;
  hasZSword(player: IPlayer<any>): boolean;
  /** Returns the first Z Sword found, or undefined when absent. */
  findZSword(player: IPlayer<any>): ILinkedItem;
  holdingZSword(player: IPlayer<any>): boolean;
  isZSword(item: IItemStack): boolean;
  getZSwordUpgradePoints(player: IPlayer<any>): number;
  setZSwordUpgradePoints(player: IPlayer<any>, amount: number): void;
  addZSwordUpgradePoints(player: IPlayer<any>, amount: number): void;
  takeZSwordUpgradePoints(player: IPlayer<any>, amount: number): void;
  /** Clears ability (0) or heavy (1) upgrades, optionally refunding points. */
  resetZSwordUpgrades(player: IPlayer<any>, type: number, refund: boolean): boolean;

  getProfileData(player: IPlayer<any>): IProfile;
  getActiveSlotId(player: IPlayer<any>): number;
  isInRange(anchorPos: IPos, targetPos: IPos, range: number): boolean;
  /** Executes the kill command for a player or player name. */
  kill(player: any): boolean;
  /** Converts a CNPC position to [x, y, z]. */
  posToDouble(pos: IPos): number[];
  messageAll(message: string): void;
  whisper(player: any, from: string, message: string): void;
  whisperAll(from: string, message: string): void;
  /** Returns true when a player's forms block the named path. */
  isPathLocked(player: IPlayer<any>, pathName: string): boolean;
  /** Removes the player's custom path forms after a qualifying quest. */
  pathEraser(player: IPlayer<any>): void;
  /** Applies DBCDamage using default or supplied stats. */
  doPluginDamage(player: IPlayer<any>, damage: number, customStats?: any): void;
  /** Applies DBCDamage without dexterity or block mitigation. */
  doPureDamage(player: IPlayer<any>, damage: number): void;

  /** Constructor: use new lib.animationHandler(entity). */
  animationHandler(entity: IEntity<any>): ZSLibAnimationHandler;
  /** Constructor: use new lib.dbcDisplayHandler(npc, enabled). */
  dbcDisplayHandler(npc: ICustomNpc<any>, enabled: boolean): ZSLibDBCDisplayHandler;
  /** Constructor: use new lib.progressBar(maxValue, initialValue, overlayId, player). */
  progressBar(maxValue: number, initialValue: number, overlayId: number, player?: IPlayer<any>): ZSLibProgressBar;
  /** Constructor: use new lib.deck(size, noReset). */
  deck(size: number, noReset?: boolean): ZSLibDeck;
  /** Constructor: use new lib.raidBossHandler(npc). */
  raidBossHandler(npc: ICustomNpc<any>): ZSLibRaidBossHandler;
}

export interface ZSLibAnimationHandler {
  setAnimation(animation: any): void;
  removeAnimation(): void;
  getAnimData(): any;
}

export interface ZSLibDBCDisplayHandler {
  transformConfig(): void;
  slowTransform(form: any): void;
  quickTransform(form: any, disableAura: boolean): void;
  setForm(form: any): void;
  setAura(aura: any, active: boolean): void;
  toggleAura(enabled: boolean): void;
  disable(): void;
  enable(): void;
  getNpcDisplay(): any;
}

export interface ZSLibProgressBar {
  config(): void;
  buildBar(): void;
  setValue(value: number, player?: IPlayer<any>): void;
  displayBar(player: IPlayer<any>): void;
  removeBar(player: IPlayer<any>): void;
  setBreakPoints(breakPoints: number[]): void;
  removeBreakPoints(): void;
  setTimeout(timeout: number): void;
  toggleTimeout(timeout: boolean): void;
}

export interface ZSLibDeck {
  drawRandom(): number;
  drawLast(): number;
  drawFirst(): number;
  resetDeck(): void;
  changeSize(size: number, preventNew?: boolean): void;
  toggleAutoReset(reset: boolean): void;
  getDeck(): number[];
}

export interface ZSLibRaidBossHandler {
  defaultConfig(): void;
  enableMeleeCleave(totalDamage: number, splitCap: number, range?: number): void;
  executeMeleeCleave(): void;
  setMeleeSplitCap(cap: number): void;
  setMeleeCleaveDamage(damage: number): void;
  setMeleeCleaveRange(range: number): void;
  getMeleeSplitCap(): number;
  getMeleeCleaveDamage(): number;
  getMeleeCleaveRange(): number;
  disableMeleeCleave(): void;
  enableAntiKi(totalDamage: number, splitCap: number, startAfter: number, pulseInterval: number, range?: number): void;
  startAntiKi(act: any): void;
  pulseAntiKi(act: any): void;
  cancelAntiKiAm(): void;
  setAntiKiSplitCap(splitCap: number): void;
  setAntiKiDamage(damage: number): void;
  setAntiKiRange(range: number): void;
  setAntiKiDelay(startAfter: number): void;
  setPulseInterval(pulseInterval: number): void;
  getAntiKiSplitCap(): number;
  getAntiKiDamage(): number;
  getAntiKiRange(): number;
  getAntiKiDelay(): number;
  getPulseInterval(): number;
  disableAntiKi(): void;
  enablePassiveAoe(totalDamage: number, splitCap: number, interval: number, range?: number): void;
  startPassiveAoe(): void;
  passiveAoePulse(act: any): void;
  cancelPassiveAoe(): void;
  setPassiveAoeDamage(damage: number): void;
  setPassiveAoeSplitCap(splitCap: number): void;
  setPassiveAoeRange(range: number): void;
  setPassiveAoeInterval(interval: number): void;
  getPassiveAoeDamage(): number;
  getPassiveAoeSplitCap(): number;
  getPassiveAoeRange(): number;
  getPassiveAoeInterval(): number;
  disablePassiveAoe(): void;
  doNearbyDamage(totalDamage: number, minDamagePer: number, range: number): void;
  handleMeleeEvent(event: any): void;
}

/** Directly exposed by the separate libPaths.js Forge global script. */
export interface ZSServerLibPaths {
  Saiyan: ZSLibSaiyanPaths;
  /** Includes 5THFORMPATH, whose leading digit requires bracket access. */
  Arcosian: any;
  Human: ZSLibHumanPaths;
  Namekian: ZSLibNamekianPaths;
  Majin: ZSLibMajinPaths;
  STRICTPATHS: string[];
  GLOBALNONSTRICT: string[];
  GLOBALSTRICT: string[];
}

export interface ZSLibSaiyanPaths {
  Saiyan: string[];
  SSJ1PATH: string[];
  SSJ4PATH: string[];
  SAIGODFORMPATH: string[];
}

export interface ZSLibHumanPaths {
  Human: string[];
  HUMANGODFORMPATH: string[];
}

export interface ZSLibNamekianPaths {
  Namekian: string[];
  NAMGODFORMPATH: string[];
}

export interface ZSLibMajinPaths {
  Majin: string[];
  CHAOSPATH: string[];
  MIMICRYPATH: string[];
  GOODPATH: string[];
}

/** Directly exposed by the separate libPaths.js Forge global script. */
export interface ZSServerLibEraserIds {
  Eraser: number;
  PASSFLAG: boolean;
}
