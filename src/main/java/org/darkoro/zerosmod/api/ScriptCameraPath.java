package org.darkoro.zerosmod.api;

import noppes.npcs.api.entity.IEntity;

/**
 * A cinematic camera path made of keyframes, from {@link ZSAPI#createCameraPath()}. The camera moves on a smooth
 * curve through the keyframes and turns the short way between their rotations. For example:
 * <pre>{@code
 * var path = ZSAPI.createCameraPath()
 *     .relativeTo(npc)
 *     .key(0, 0, 1.0, -6, 0, 10)
 *     .key(40, 4, 2.0, -3, -30, 15)
 *     .keyLookAt(80, 1, 1.6, 2, npc)
 *     .ease("inOut");
 * ZSAPI.playCameraPath(player, path, "FlashFlurryRush");
 * }</pre>
 * The shot lasts until the last keyframe; before the first one the camera holds it. Methods return the path, so
 * calls can be chained. Invalid values throw an {@link IllegalArgumentException} with the reason.
 */
public interface ScriptCameraPath {

  /**
   * Makes keyframe positions and rotations relative to an entity, following it as it moves and turns. Offsets
   * are in the entity's own frame from its feet: {@code x} to its right, {@code y} up, {@code z} in front of it.
   * A keyframe yaw of 0 looks the way the entity faces; positive yaw turns right. One path then works for any
   * entity. Without it, or with null, positions are world coordinates and yaw is the world yaw.
   *
   * @param entity entity to follow, or null for world coordinates
   * @return this path
   */
  ScriptCameraPath relativeTo(IEntity entity);

  /**
   * Adds a keyframe with a fixed rotation. A keyframe at the same tick replaces the earlier one.
   *
   * @param tick tick of the shot, 0 to 1200
   * @param x camera position, see {@link #relativeTo(IEntity)}
   * @param y camera position
   * @param z camera position
   * @param yaw camera yaw in degrees
   * @param pitch camera pitch in degrees, positive looks down
   * @return this path
   */
  ScriptCameraPath key(int tick, double x, double y, double z, float yaw, float pitch);

  /**
   * Adds a keyframe that aims the camera at an entity instead of a fixed rotation. Between two keyframes that
   * aim at the same entity, the camera keeps it in the middle of the shot.
   *
   * @param tick tick of the shot, 0 to 1200
   * @param x camera position, see {@link #relativeTo(IEntity)}
   * @param y camera position
   * @param z camera position
   * @param target entity to look at, or null for the {@link #relativeTo(IEntity)} entity (the player without one)
   * @return this path
   */
  ScriptCameraPath keyLookAt(int tick, double x, double y, double z, IEntity target);

  /**
   * Sets how the camera speeds up and slows down over the whole path.
   *
   * @param ease {@code linear} (default), {@code in}, {@code out} or {@code inOut}
   * @return this path
   */
  ScriptCameraPath ease(String ease);

  /** @return the shot length in ticks, the tick of the last keyframe */
  int getDuration();

  /** @return the number of keyframes, at most 64 */
  int getKeyCount();
}
