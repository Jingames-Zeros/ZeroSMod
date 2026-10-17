package org.darkoro.zerosmod.dome;

import net.minecraft.entity.Entity;

/** Internal bridge between CNPC+'s dome entity and its script wrapper. */
public interface EnergyDomeExtension {

  float zerosmod$getDamage();

  void zerosmod$setDamage(float damage);

  int zerosmod$getDamageInterval();

  void zerosmod$setDamageInterval(int ticks);

  boolean zerosmod$affectOwner();

  void zerosmod$setAffectOwner(boolean affects);

  Entity zerosmod$getFollower();

  void zerosmod$setFollower(Entity entity);

  Entity[] zerosmod$getEntitiesInside();

  boolean zerosmod$isInside(Entity entity);

  void zerosmod$setTargetRadius(float radius, int ticks);
}
