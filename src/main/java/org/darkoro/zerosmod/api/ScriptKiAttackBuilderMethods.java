package org.darkoro.zerosmod.api;

/**
 * Destroyer-color methods added to NPCDBC's {@code IKiAttack} objects.
 * <p>
 * Configure these methods before passing the attack to NPCDBC's
 * {@code fireKiAttack(...)} method. The color is copied to the fired projectile and only affects
 * its destroyer accent. It does not change the attack's normal DBC palette color.
 * For example:
 * <pre>{@code
 * var attack = DBCAPI.createKiAttack();
 * attack.setDestroyerAttack(true);
 * attack.setDestroyerColor(0x6A0DAD);
 * DBCAPI.fireKiAttack(npc, attack);
 * }</pre>
 */
public interface ScriptKiAttackBuilderMethods {

  /**
   * Sets the destroyer accent override for this configured attack.
   * The value is retained even if destroyer mode is enabled afterward or inherited from a form.
   * It has no visible effect when the fired attack is not a destroyer attack.
   *
   * @param rgb color from {@code 0x000000} through {@code 0xFFFFFF}
   * @return true when {@code rgb} is a valid RGB color and was stored
   */
  boolean setDestroyerColor(int rgb);

  /**
   * @return custom destroyer accent as {@code 0xRRGGBB}, or -1 when DBC's default is active
   */
  int getDestroyerColor();

  /** Restores DBC's normal destroyer colors for this configured attack. */
  void clearDestroyerColor();
}
