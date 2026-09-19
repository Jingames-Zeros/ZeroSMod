package org.darkoro.zerosmod.api;

import noppes.npcs.api.item.IItemStack;
import org.darkoro.zerosmod.ZeroSMod;

import cpw.mods.fml.common.Loader;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.api.entity.IPlayer;

/**
 * @hidden
 * Script-facing Zero S API.
 * <p>
 * Scripts can access this singleton through the CNPC global object named {@code ZSAPI}.
 */
public abstract class AbstractZeroSAPI {

  private static AbstractZeroSAPI instance;

  /**
   * @hidden
   * @return true when Zero S Mod is loaded.
   */
  public static boolean IsAvailable() {
    return Loader.isModLoaded(ZeroSMod.MODID);
  }

  /**
   * @hidden
   * @return the active Zero S API instance, or null if it cannot be created.
   */
  public static AbstractZeroSAPI Instance() {
    if (instance != null) {
      return instance;
    }
    if (!IsAvailable()) {
      return null;
    }

    try {
      Class<?> apiClass = Class.forName("org.darkoro.zerosmod.scripted.ZeroSAPI");
      instance = (AbstractZeroSAPI)apiClass.getMethod("Instance").invoke(null);
    } catch (Exception ignored) {
    }
    return instance;
  }

  /**
   * @hidden
   * @return the loaded Zero S Mod version.
   */
  public abstract String getVersion();

  /**
   * Finds the living entity the player is currently aiming short-range Instant Transmission at.
   *
   * @param player CNPC player wrapper
   * @return the target entity wrapper, or null when DBC would not find a valid short-range IT target
   */
  public abstract IEntityLivingBase getInstantTransmissionLookTarget(IPlayer player);

  /**
   * Finds the name of the entity the player is currently aiming short-range Instant Transmission at.
   *
   * @param player CNPC player wrapper
   * @return target command-sender name, or an empty string when no valid target is found
   */
  public abstract String getInstantTransmissionLookTargetName(IPlayer player);

  /**
   * Checks whether an entity was the target of a successful short-range Instant Transmission recently.
   *
   * @param entity target entity wrapper
   * @param maxAgeTicks maximum allowed age in server ticks
   * @return true if the entity was targeted by a successful IT within the requested age
   */
  public abstract boolean wasRecentInstantTransmissionTarget(IEntity entity, int maxAgeTicks);

  /**
   * Gets the player that most recently used short-range Instant Transmission to the target entity.
   *
   * @param entity target entity wrapper
   * @param maxAgeTicks maximum allowed age in server ticks
   * @return player wrapper, or null if there is no recent matching IT activation
   */
  public abstract IPlayer getRecentInstantTransmissionPlayer(IEntity entity, int maxAgeTicks);

  /**
   * Gets the name of the player that most recently used short-range Instant Transmission to the target entity.
   *
   * @param entity target entity wrapper
   * @param maxAgeTicks maximum allowed age in server ticks
   * @return player name, or an empty string if there is no recent matching IT activation
   */
  public abstract String getRecentInstantTransmissionPlayerName(IEntity entity, int maxAgeTicks);

  /**
   * Gets how long ago an entity was targeted by successful short-range Instant Transmission.
   *
   * @param entity target entity wrapper
   * @return age in ticks, or -1 if the entity has not recently been an IT target
   */
  public abstract int getTicksSinceInstantTransmissionTargeted(IEntity entity);

  /**
   * @param entity entity wrapper to inspect
   * @return true when the entity is a DBC ki attack
   */
  public abstract boolean isKiAttack(IEntity entity);

  /**
   * @param kiAttack DBC ki attack wrapper
   * @return readable DBC ki attack type name, or {@code Unknown}
   */
  public abstract String getKiType(IEntity kiAttack);

  /**
   * @param kiAttack DBC ki attack wrapper
   * @return Minecraft entity id for the ki attack, or -1 when this is not ki
   */
  public abstract int getKiId(IEntity kiAttack);

  /**
   * @param kiAttack DBC ki attack wrapper
   * @return true when the ki attack is currently frozen by Zero S ki scripting
   */
  public abstract boolean isKiStopped(IEntity kiAttack);

  /**
   * Stops nearby DBC ki attacks around the given entity.
   * Stopped attacks remain frozen until released with {@link #releaseKi(IEntity, int)}.
   *
   * @param origin entity wrapper used as the middle of the search radius
   * @param range block radius to search
   * @return number of newly stopped ki attacks
   */
  public abstract int stopKi(IEntity origin, int range);

  /**
   * Releases stopped DBC ki attacks around the given entity.
   *
   * @param origin entity wrapper used as the middle of the search radius
   * @param range block radius to search
   * @return number of ki attacks released
   */
  public abstract int releaseKi(IEntity origin, int range);

  /**
   * Finds nearby DBC ki attacks around the given entity.
   *
   * @param origin entity wrapper used as the middle of the search radius
   * @param range block radius to search
   * @return nearby ki attack wrappers
   */
  public abstract IEntity[] getNearbyKi(IEntity origin, int range);

  /**
   * Changes ownership and color of a DBC ki attack and restarts its lifetime.
   *
   * @param kiAttack DBC ki attack wrapper
   * @param color color name or numeric color id
   * @param owner new owner wrapper
   * @return true when the attack was changed
   */
  public abstract boolean stealKi(IEntity kiAttack, String color, IEntity owner);

  /**
   * Redirects a DBC ki attack toward the target entity.
   *
   * @param kiAttack DBC ki attack wrapper
   * @param target target entity wrapper
   * @return true when the attack was redirected
   */
  public abstract boolean redirectKi(IEntity kiAttack, IEntity target);

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
  public abstract boolean directKi(IEntity kiAttack, double x, double y, double z);

  /**
   * Changes a DBC ki attack color without changing its owner.
   *
   * @param kiAttack DBC ki attack wrapper
   * @param color color name or numeric color id
   * @return true when the color was changed
   */
  public abstract boolean setKiColor(IEntity kiAttack, String color);

  /**
   * Gets player's current combat state
   * @param player combat player
   * @return .
   */
  public abstract ScriptPlayerCombatState getPlayerCombatState(IPlayer player);

  /**
   * Gets item's current ZSWeapon stats
   * @param item item to retrieve stats from
   * @return .
   */
  public abstract ScriptZSWeapon getZSWeapon(IItemStack item);
}
