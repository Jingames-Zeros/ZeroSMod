package org.darkoro.zerosmod.api;

/**
 * DBC methods on {@code player.getDBCPlayer()}.
 * <p>
 * Status effects, case-insensitive: timed {@code Strain}, {@code GodPower}, {@code Fatigue},
 * {@code NoFuse}, {@code Pain}, {@code Heat}, {@code KO}; on/off {@code Majin},
 * {@code Legendary}, {@code Divine}, {@code GodOfDestruction}.
 */
public interface ScriptDBCPlayerMethods {

  /**
   * @return false while the player is dead in DBC (has a halo)
   */
  boolean isDBCAlive();

  /** Ends the fusion for both online participants and applies DBC's fusion cooldown. */
  void defuse();

  /**
   * @return remaining fusion time in seconds, or 0 when not fused
   */
  int getFusionTimeLeft();

  /**
   * Sets one status effect. Timed effects take seconds, rounded up to 5; 0 removes them.
   * On/off effects turn off with 0 and on with any other value. NoFuse is ignored while fused.
   *
   * @param effect status-effect name
   * @param duration seconds, or the on/off value
   * @throws RuntimeException for an unknown effect name
   */
  void setDBCStatusEffect(String effect, int duration);

  /**
   * @param effect status-effect name
   * @return remaining seconds for a timed effect, or 1/0 for an on/off effect
   * @throws RuntimeException for an unknown effect name
   */
  int getDBCStatusEffect(String effect);
}
