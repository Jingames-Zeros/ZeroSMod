package org.darkoro.zerosmod.api;

/**
 * DBC methods added to the player wrapper returned by {@code player.getDBCPlayer()}.
 *
 * <p>DBC status-effect names are case-insensitive. Supported timed effects are
 * {@code Strain}, {@code GodPower}, {@code Fatigue}, {@code NoFuse}, {@code Pain},
 * {@code Heat}, and {@code KO}. Supported on/off effects are {@code Majin},
 * {@code Legendary}, {@code Divine}, and {@code GodOfDestruction}.</p>
 */
public interface ScriptDBCPlayerMethods {

  /**
   * Checks DBC's life state. This is separate from the inherited {@code isAlive()}, which
   * checks whether the Minecraft entity is alive.
   *
   * @return true when DBC considers the player alive, or false while the player has a halo
   */
  boolean isDBCAlive();

  /**
   * Ends the player's active DBC fusion. Both online fusion participants are defused, their
   * fusion status flags are removed, and DBC's configured post-fusion cooldown is applied.
   * Does nothing when the player is not fused.
   */
  void defuse();

  /**
   * Gets the remaining duration of the player's active DBC fusion. This does not include the
   * post-fusion {@code NoFuse} cooldown.
   *
   * @return remaining fusion time in seconds, or 0 when the player is not fused
   */
  int getFusionTimeLeft();

  /**
   * Sets one DBC status effect without replacing the player's other status effects.
   *
   * <p>For timed effects ({@code Strain}, {@code GodPower}, {@code Fatigue},
   * {@code NoFuse}, {@code Pain}, {@code Heat}, and {@code KO}), {@code duration} is measured
   * in seconds. Positive durations are rounded up to DBC's five-second timer interval, and
   * zero or negative values remove the effect. {@code NoFuse} does nothing while the player
   * is actively fused. {@code Heat} is DBC's legacy UI heat and is separate from NPCDBC
   * custom-form heat.</p>
   *
   * <p>For on/off effects ({@code Majin}, {@code Legendary}, {@code Divine}, and
   * {@code GodOfDestruction}), zero turns the effect off and any other value turns it on.</p>
   *
   * <p>For example, {@code setDBCStatusEffect("Pain", 30)} applies 30 seconds of pain,
   * while {@code setDBCStatusEffect("Majin", 1)} enables the Majin effect.</p>
   *
   * @param effect case-insensitive status-effect name
   * @param duration duration in seconds for a timed effect, or an off/on value for an on/off effect
   * @throws RuntimeException when {@code effect} is not a supported status-effect name
   */
  void setDBCStatusEffect(String effect, int duration);

  /**
   * Gets one DBC status effect.
   * Timed results use DBC's five-second timer interval and are therefore multiples of five.
   *
   * @param effect case-insensitive status-effect name
   * @return remaining seconds for a timed effect, or 1/0 for an enabled/disabled on/off effect
   * @throws RuntimeException when {@code effect} is not a supported status-effect name
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
