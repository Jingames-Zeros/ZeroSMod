package org.darkoro.zerosmod.api;

/**
 * NPCDBC custom-form heat on {@code player.getDBCPlayer()}. Vanilla DBC heat is not affected.
 */
public interface ScriptDBCAddonHeatMethods {

  /**
   * @return stored heat, including heat still cooling down after leaving a form
   */
  float getCurrentHeat();

  /**
   * @return heat from 0 to 100 percent of {@link #getMaxHeat()}, or 0 without a heat form
   */
  float getCurrentHeatPercent();

  /**
   * @return the active form's maximum heat, or 0 without a heat form
   */
  int getMaxHeat();

  /**
   * Sets heat, clamped to 0 through {@link #getMaxHeat()}. Does nothing without a heat form.
   *
   * @param amount heat amount
   */
  void setCurrentHeat(float amount);

  /**
   * Sets heat as a percentage of {@link #getMaxHeat()}. Does nothing without a heat form.
   *
   * @param percent 0 to 100
   */
  void setHeatPercentage(float percent);

  /** Sets stored heat to 0, also outside a form. */
  void clearHeat();
}
