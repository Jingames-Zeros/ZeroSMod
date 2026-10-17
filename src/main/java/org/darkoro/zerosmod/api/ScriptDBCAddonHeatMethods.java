package org.darkoro.zerosmod.api;

/**
 * Custom-form heat methods added to the DBC Addon player wrapper returned by
 * {@code player.getDBCPlayer()}.
 * These methods use DBC Addon's custom-form heat and do not read or change vanilla DBC heat.
 */
public interface ScriptDBCAddonHeatMethods {

  /**
   * Gets the player's stored custom-form heat as a raw amount. Heat can remain after leaving
   * a form while DBC Addon cools it down.
   *
   * @return current custom-form heat amount
   */
  float getCurrentHeat();

  /**
   * Gets the player's current custom-form heat as a percentage of {@link #getMaxHeat()}.
   * The result is clamped from 0 to 100, even if stored heat exceeds the current form's limit.
   * For example, 50 heat in a form with a maximum of 200 returns 25.
   *
   * @return current heat percentage, or 0 when no heat-enabled custom form is active
   */
  float getCurrentHeatPercent();

  /**
   * Gets the heat limit configured by the player's active custom form.
   *
   * @return current form's maximum heat, or 0 when no heat-enabled custom form is active
   */
  int getMaxHeat();

  /**
   * Sets the player's custom-form heat to a raw amount, clamped between 0 and
   * {@link #getMaxHeat()}.
   * Does nothing when no heat-enabled custom form is active or the amount is not finite.
   * For example, if the current form has a maximum heat of 200,
   * {@code setCurrentHeat(50)} fills one quarter of the heat bar.
   *
   * @param amount raw heat amount
   */
  void setCurrentHeat(float amount);

  /**
   * Sets the player's custom-form heat as a percentage of {@link #getMaxHeat()}.
   * Values below 0 become 0 and values above 100 become 100. Does nothing when no
   * heat-enabled custom form is active or the percentage is not finite.
   * For example, if the current form has a maximum heat of 200,
   * {@code setHeatPercentage(50)} sets the heat to 100.
   *
   * @param percent heat percentage from 0 to 100
   */
  void setHeatPercentage(float percent);

  /**
   * Sets the player's stored custom-form heat to 0. This also clears heat left over while
   * DBC Addon is cooling it down, even when no custom form is active.
   */
  void clearHeat();
}
