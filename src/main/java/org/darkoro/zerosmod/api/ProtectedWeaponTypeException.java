package org.darkoro.zerosmod.api;

/**
 * Thrown when changing a stat that is fixed by the weapon's configured type. Only {@code special} weapons can be edited.
 */
public class ProtectedWeaponTypeException extends Exception {

  public ProtectedWeaponTypeException(String type) {
    super("Protected weapon type: " + type);
  }
}
