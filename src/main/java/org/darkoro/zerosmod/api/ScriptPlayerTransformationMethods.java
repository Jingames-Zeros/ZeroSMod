package org.darkoro.zerosmod.api;

/** Custom-form transformation methods added to CNPC player wrappers. */
public interface ScriptPlayerTransformationMethods {

  /**
   * Checks whether the player is currently charging an NPCDBC custom-form transformation.
   * This becomes true after NPCDBC accepts the normal transform input and false if the player
   * stops charging or the transformation completes. Programmatic form changes do not count.
   *
   * @return true while a custom-form transformation is being charged
   */
  boolean isTransforming();
}
