package org.darkoro.zerosmod.api;

/**
 * Client display values reported by the player's client.
 */
public interface ScriptPlayerMethods {

  /**
   * @return screen width in scaled GUI coordinates, or -1 before the client reported it
   */
  int getScaledScreenWidth();

  /**
   * @return screen height in scaled GUI coordinates, or -1 before the client reported it
   */
  int getScaledScreenHeight();

  /**
   * @return Minecraft's GUI Scale setting, 0 for Auto, or -1 before the client reported it
   */
  int getGuiScale();
}
