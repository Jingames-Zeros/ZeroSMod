package org.darkoro.zerosmod.api;

/**
 * Methods added to CNPC player wrappers for DBC Instant Transmission scripts.
 */
public interface ScriptPlayerInstantTransmissionMethods {

  /**
   * @return name of the entity DBC would currently select for short-range Instant Transmission, or an empty string.
   */
  String getInstantTransmissionLookTargetName();
}
