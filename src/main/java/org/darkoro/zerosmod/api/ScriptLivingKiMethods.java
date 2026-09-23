package org.darkoro.zerosmod.api;

import noppes.npcs.api.entity.IEntity;

/**
 * Methods added to CNPC living entity wrappers, including NPC and player wrappers.
 */
public interface ScriptLivingKiMethods {

  /**
   * Stops nearby DBC ki attacks around this living entity.
   * Stopped attacks remain frozen until released with {@link #releaseKi(int)}.
   *
   * @param range block radius to search
   * @return number of newly stopped ki attacks.
   */
  int stopKi(int range);

  /**
   * Releases nearby stopped DBC ki attacks around this living entity.
   *
   * @param range block radius to search
   * @return number of ki attacks released.
   */
  int releaseKi(int range);

  /**
   * Finds nearby DBC ki attacks around this living entity.
   *
   * @param range block radius to search
   * @return nearby ki attack entity wrappers.
   */
  IEntity[] getNearbyKi(int range);
}
