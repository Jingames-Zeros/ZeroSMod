package org.darkoro.zerosmod.api;

/** Custom-form transformation state on CNPC player wrappers. */
public interface ScriptPlayerTransformationMethods {

  /**
   * @return true while the player charges an NPCDBC custom-form transformation
   */
  boolean isTransforming();
}
