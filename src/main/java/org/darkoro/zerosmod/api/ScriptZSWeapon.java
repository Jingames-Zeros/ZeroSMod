package org.darkoro.zerosmod.api;

import noppes.npcs.api.item.IItemStack;

/**
 * Zero S weapon stats of an item. Percent values use 100 as neutral.
 * <p>
 * Stats fixed by a configured weapon type can only be changed on {@code special} weapons;
 * those setters throw {@link ProtectedWeaponTypeException} otherwise.
 */
public interface ScriptZSWeapon {

  /**
   * @param type loaded weapon type name, or {@code special}
   * @throws UnknownWeaponTypeException when the type is not loaded
   */
  void setType(String type) throws UnknownWeaponTypeException;

  /** Resets the stats to the {@code default} weapon type. */
  void setToDefaultStats();

  /** Makes the weapon {@code special}, so every stat can be edited. */
  void setSpecial();

  /** @return level required to use the weapon */
  int getLevelReq();

  /** @return attack range in blocks */
  float getRange();

  /** @return attack range squared */
  float getRangeSq();

  /** @return attack cooldown in ticks */
  int getCooldown();

  /** @return the item these stats belong to */
  IItemStack getItem();

  /** @return weapon type name */
  String getType();

  /** @return weapon type as shown in the item lore */
  String getFormattedType();

  /** @return melee damage percent */
  float getAttackPercent();

  /** @return flat melee damage bonus */
  int getAttackAdditive();

  /** @return distance in blocks at which hits get the most bonus damage */
  float getSweetSpot();

  /** @return true when ki can be charged while holding the weapon */
  boolean canChargeKi();

  /** @return ki attack damage percent */
  float getKiPercent();

  /** @return flat ki attack damage bonus */
  int getKiAdditive();

  /** @return ki cost percent */
  float getKiCostPercent();

  /** @return true when the weapon can block */
  boolean canBlock();

  /** @return percent of dexterity used for block defense */
  float getBlockDexPercent();

  /** @return block cost percent */
  float getBlockCostPercent();

  /** @return cooldown in ticks after blocking */
  int getBlockCooldown();

  void setLevelReq(int levelReq);

  void setKiAdditive(int kiAdditive);

  void setAttackAdditive(int attack);

  void setCooldown(int cooldown) throws ProtectedWeaponTypeException;

  void setAttackPercent(float attackPercent) throws ProtectedWeaponTypeException;

  void setSweetSpot(float sweetSpot) throws ProtectedWeaponTypeException;

  void setCanChargeKi(boolean canChargeKi) throws ProtectedWeaponTypeException;

  void setKiPercent(float kiPercent) throws ProtectedWeaponTypeException;

  void setKiCostPercent(float kiCostPercent) throws ProtectedWeaponTypeException;

  void setCanBlock(boolean canBlock) throws ProtectedWeaponTypeException;

  void setBlockDexPercent(float blockDexPercent) throws ProtectedWeaponTypeException;

  void setBlockCostPercent(float blockCostPercent) throws ProtectedWeaponTypeException;

  void setBlockCooldown(int blockCooldown) throws ProtectedWeaponTypeException;

  void setRange(float range) throws ProtectedWeaponTypeException;

  void setFormattedType(String formattedType) throws ProtectedWeaponTypeException;
}
