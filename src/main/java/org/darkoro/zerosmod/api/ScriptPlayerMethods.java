package org.darkoro.zerosmod.api;

/**
 * Client display methods added to CNPC player wrappers.
 * <p>
 * Zero S reports these values after the player joins and updates them when the window size or GUI
 * Scale setting changes. The dimensions use Minecraft's scaled overlay coordinates rather than
 * physical display pixels.
 */
public interface ScriptPlayerMethods {

  /**
   * Gets the width of the player's current client window in Minecraft's scaled GUI coordinates.
   * This is the same coordinate space used to position overlays and already accounts for the
   * player's GUI Scale setting.
   *
   * @return scaled screen width, or {@code -1} before the client has reported it
   */
  int getScaledScreenWidth();

  /**
   * Gets the height of the player's current client window in Minecraft's scaled GUI coordinates.
   * This is the same coordinate space used to position overlays and already accounts for the
   * player's GUI Scale setting.
   *
   * @return scaled screen height, or {@code -1} before the client has reported it
   */
  int getScaledScreenHeight();

  /**
   * Gets the player's raw Minecraft GUI Scale setting. A value of zero means {@code Auto}; positive
   * values identify the selected scale. Use {@link #getScaledScreenWidth()} and
   * {@link #getScaledScreenHeight()} when positioning overlays.
   *
   * @return raw GUI Scale setting, or {@code -1} before the client has reported it
   */
  int getGuiScale();
}
