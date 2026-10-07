package org.darkoro.zerosmod.api;

/**
 * Thrown when a weapon type name is not loaded in the server weapon config.
 */
public class UnknownWeaponTypeException extends Exception {

  public UnknownWeaponTypeException(String type) {
    super("Unknown weapon type: " + type);
  }
}
