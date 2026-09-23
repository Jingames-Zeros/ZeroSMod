package org.darkoro.zerosmod.api;

import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.item.IItemStack;

/**
 * Methods available through the CNPC global object named {@code ZSAPI}.
 */
public interface ZSAPI {

  /**
   * Finds the living entity the player is currently aiming short-range Instant Transmission at.
   *
   * @param player CNPC player wrapper
   * @return target entity wrapper, or null when DBC would not find a valid target
   */
  IEntityLivingBase getInstantTransmissionLookTarget(IPlayer player);

  /**
   * Finds the name of the entity the player is currently aiming short-range Instant Transmission at.
   *
   * @param player CNPC player wrapper
   * @return target name, or an empty string
   */
  String getInstantTransmissionLookTargetName(IPlayer player);

  /**
   * Checks whether an entity was the target of successful short-range Instant Transmission recently.
   *
   * @param entity target entity wrapper
   * @param maxAgeTicks maximum allowed age in server ticks
   * @return true when entity was targeted within the requested age
   */
  boolean wasRecentInstantTransmissionTarget(IEntity entity, int maxAgeTicks);

  /**
   * Gets the player that most recently used short-range Instant Transmission to the target entity.
   *
   * @param entity target entity wrapper
   * @param maxAgeTicks maximum allowed age in server ticks
   * @return player wrapper, or null
   */
  IPlayer getRecentInstantTransmissionPlayer(IEntity entity, int maxAgeTicks);

  /**
   * Gets the name of the player that most recently used short-range Instant Transmission to the target entity.
   *
   * @param entity target entity wrapper
   * @param maxAgeTicks maximum allowed age in server ticks
   * @return player name, or an empty string
   */
  String getRecentInstantTransmissionPlayerName(IEntity entity, int maxAgeTicks);

  /**
   * Gets how long ago an entity was targeted by successful short-range Instant Transmission.
   *
   * @param entity target entity wrapper
   * @return age in ticks, or -1
   */
  int getTicksSinceInstantTransmissionTargeted(IEntity entity);

  /**
   * @param entity entity wrapper to inspect
   * @return true when entity is a DBC ki attack
   */
  boolean isKiAttack(IEntity entity);

  /**
   * @param kiAttack DBC ki attack wrapper
   * @return readable DBC ki attack type name
   */
  String getKiType(IEntity kiAttack);

  /**
   * @param kiAttack DBC ki attack wrapper
   * @return Minecraft entity id for the ki attack, or -1 when this is not ki
   */
  int getKiId(IEntity kiAttack);

  /**
   * @param kiAttack DBC ki attack wrapper
   * @return true when the ki attack is currently frozen by Zero S ki scripting
   */
  boolean isKiStopped(IEntity kiAttack);

  /**
   * Stops nearby DBC ki attacks around the given entity.
   * Stopped attacks remain frozen until released with {@link #releaseKi(IEntity, int)}.
   *
   * @param origin entity wrapper used as the middle of the search radius
   * @param range block radius to search
   * @return number of newly stopped ki attacks
   */
  int stopKi(IEntity origin, int range);

  /**
   * Releases stopped DBC ki attacks around the given entity.
   *
   * @param origin entity wrapper used as the middle of the search radius
   * @param range block radius to search
   * @return number of ki attacks released
   */
  int releaseKi(IEntity origin, int range);

  /**
   * Finds nearby DBC ki attacks around the given entity.
   *
   * @param origin entity wrapper used as the middle of the search radius
   * @param range block radius to search
   * @return nearby ki attack wrappers
   */
  IEntity[] getNearbyKi(IEntity origin, int range);

  /**
   * Changes ownership and color of a DBC ki attack and restarts its lifetime.
   *
   * @param kiAttack DBC ki attack wrapper
   * @param color color name or numeric color id
   * @param owner new owner wrapper
   * @return true when ownership was changed
   */
  boolean stealKi(IEntity kiAttack, String color, IEntity owner);

  /**
   * Redirects a DBC ki attack toward the target entity.
   *
   * @param kiAttack DBC ki attack wrapper
   * @param target target entity wrapper
   * @return true when the attack was redirected
   */
  boolean redirectKi(IEntity kiAttack, IEntity target);

  /**
   * Sends a DBC ki attack along a manual direction vector while preserving its speed.
   * For example, {@code directKi(ki, 0, 1, 0)} sends it upward.
   *
   * @param kiAttack DBC ki attack wrapper
   * @param x direction X component
   * @param y direction Y component
   * @param z direction Z component
   * @return true when the attack direction was changed
   */
  boolean directKi(IEntity kiAttack, double x, double y, double z);

  /**
   * Changes a DBC ki attack color without changing its owner.
   *
   * @param kiAttack DBC ki attack wrapper
   * @param color color name or numeric color id
   * @return true when the color was changed
   */
  boolean setKiColor(IEntity kiAttack, String color);

  /**
   * Gets player's current combat state
   * @param player combat player
   * @return .
   */
  ScriptPlayerCombatState getPlayerCombatState(IPlayer player);

  /**
   * Gets item's current ZSWeapon stats
   * @param item item to retrieve stats from
   * @return .
   */
  ScriptZSWeapon getZSWeapon(IItemStack item);
}
