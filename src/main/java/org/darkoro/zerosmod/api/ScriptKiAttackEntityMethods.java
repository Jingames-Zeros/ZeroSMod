package org.darkoro.zerosmod.api;

import noppes.npcs.api.entity.IEntity;

/**
 * Methods added to CNPC script entity wrappers when the wrapped entity is a DBC ki attack.
 * <p>
 * In scripts, these are called directly on the entity wrapper, commonly from
 * {@code event.getDamageSource().getImmediateSource()} in a damaged hook.
 */
public interface ScriptKiAttackEntityMethods {

  /**
   * @return true when this entity wrapper is a DBC ki attack.
   */
  boolean isKiAttack();

  /**
   * @return true when the ki attack owner is a player.
   */
  boolean isPlayerKi();

  /**
   * @return true when the ki attack owner is a CustomNPC+ NPC.
   */
  boolean isNpcKi();

  /**
   * @return true when the ki attack has the DBC/NPCDBC destroyer flag.
   */
  boolean isDestroyerAttack();

  /**
   * @return Minecraft entity id for this ki attack, or -1 when this is not ki.
   */
  int getKiId();

  /**
   * @return true when this ki attack is currently frozen by Zero S ki scripting.
   */
  boolean isKiStopped();

  /**
   * @return player owner name, or an empty string when this is not player-owned ki.
   */
  String getPlayerName();

  /**
   * @return ki owner name for player or NPC-owned ki, or an empty string when orphaned.
   */
  String getKiOwnerName();

  /**
   * @return CNPC entity wrapper for the ki owner, or null when orphaned.
   */
  IEntity getKiOwner();

  /**
   * @return readable DBC ki attack type name.
   */
  String getKiType();

  /**
   * @return raw DBC ki attack type id, or -1 for non-ki.
   */
  int getKiTypeId();

  /**
   * @return current DBC ki attack damage value.
   */
  double getKiDamage();

  /**
   * @param damage new DBC ki attack damage value
   * @return true when the damage was changed.
   */
  boolean setKiDamage(double damage);

  /**
   * @param color DBC color name such as {@code purple}, {@code darkBlue}, or {@code gold}
   * @return true when the color was changed.
   */
  boolean setKiColor(String color);

  /**
   * @param color raw DBC color id
   * @return true when the color was changed.
   */
  boolean setKiColor(int color);

  /**
   * @param color primary raw DBC color id
   * @param color2 secondary raw DBC color id, or -1
   * @return true when the color was changed.
   */
  boolean setKiColor(int color, int color2);

  /**
   * Freezes this ki attack in place.
   * The attack remains frozen until {@link #releaseKi()} is called.
   *
   * @return true when the ki attack newly entered a stopped state.
   */
  boolean stopKi();

  /**
   * @return true when this ki attack was released from a stopped state.
   */
  boolean releaseKi();

  /**
   * @return true when this ki attack's lifetime was restarted.
   */
  boolean resetKiLifetime();

  /**
   * @return true when this ki attack was removed.
   */
  boolean killKi();

  /**
   * Changes this ki attack owner and color, then restarts its lifetime.
   *
   * @param color DBC color name
   * @param owner new owner entity wrapper
   * @return true when ownership was changed.
   */
  boolean stealKi(String color, IEntity owner);

  /**
   * Changes this ki attack owner and color, then restarts its lifetime.
   *
   * @param color raw DBC color id
   * @param owner new owner entity wrapper
   * @return true when ownership was changed.
   */
  boolean stealKi(int color, IEntity owner);

  /**
   * Redirects this ki attack toward the target entity.
   *
   * @param target target entity wrapper
   * @return true when the ki attack was redirected.
   */
  boolean redirectKi(IEntity target);

  /**
   * Sends this ki attack along a manual direction vector while preserving its speed.
   * For example, {@code directKi(0, 1, 0)} sends it upward.
   *
   * @param x direction X component
   * @param y direction Y component
   * @param z direction Z component
   * @return true when the attack direction was changed.
   */
  boolean directKi(double x, double y, double z);
}
