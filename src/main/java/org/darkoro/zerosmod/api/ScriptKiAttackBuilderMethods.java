package org.darkoro.zerosmod.api;

/**
 * Destroyer accent color on NPCDBC {@code IKiAttack} objects, set before {@code fireKiAttack}.
 */
public interface ScriptKiAttackBuilderMethods {

  /**
   * @param rgb color from {@code 0x000000} through {@code 0xFFFFFF}
   * @return false when {@code rgb} is out of range
   */
  boolean setDestroyerColor(int rgb);

  /**
   * @return accent color as {@code 0xRRGGBB}, or -1 for DBC's default
   */
  int getDestroyerColor();

  /**
   * Restores DBC's default destroyer colors.
   *
   * @return true
   */
  boolean clearDestroyerColor();
}
