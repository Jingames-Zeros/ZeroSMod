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

  /**
   * Checks whether the player's TP Bank is unlocked. Each DBC character (CNPC profile slot) has
   * its own bank, which starts locked unless the server config unlocks it by default. While
   * locked, overflow TP is discarded as DBC normally does and the balance is frozen: it accrues
   * no interest and cannot be withdrawn.
   *
   * @return true when the bank accepts deposits and withdrawals
   */
  boolean isTpBankUnlocked();

  /**
   * Unlocks or locks the player's TP Bank for their current DBC character. Locking keeps the
   * balance; unlocking again restarts interest from that moment. For example, call
   * {@code event.player.getDBCPlayer().setTpBankUnlocked(true)} when an unlock quest completes.
   *
   * @param unlocked true to unlock, false to lock
   */
  void setTpBankUnlocked(boolean unlocked);

  /**
   * Gets the player's TP Bank balance for their current DBC character, after any interest due.
   * The bank holds TP that {@code /jrmctp} gave above DBC's TP cap; see the {@code tpOverflow} hook. Works while
   * the bank is locked.
   *
   * @return banked TP, never negative
   */
  long getBankedTP();

  /**
   * Replaces the TP Bank balance, even while the bank is locked. Values above the configured
   * balance limit are clamped to it.
   *
   * @param amount new balance; negative values become zero
   */
  void setBankedTP(long amount);

  /**
   * Adds TP to the TP Bank, even while the bank is locked. No deposit loss is applied. TP above
   * the configured balance limit is lost.
   *
   * @param amount TP to add; negative values add nothing
   * @return the new balance
   */
  long depositBankedTP(long amount);

  /**
   * Gives the player TP from an unlocked bank, never past DBC's TP cap. The configured
   * withdrawal fee is taken from the bank on top of what the player receives.
   *
   * @param amount most TP the player should receive
   * @return TP the player received; 0 while the bank is locked
   */
  long withdrawBankedTP(long amount);

  /**
   * Opens the TP Bank screen, where the player can withdraw banked TP. A locked bank opens in a
   * read-only state. For example, call this from a banker NPC's {@code interact} hook with
   * {@code event.player.getDBCPlayer().openTpBank()}.
   */
  void openTpBank();
}
