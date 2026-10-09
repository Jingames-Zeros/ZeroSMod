package org.darkoro.zerosmod.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.scripted.ScriptNbt;
import org.darkoro.zerosmod.config.ConfigHandler;
import org.darkoro.zerosmod.config.ServerWeaponConfig;
import org.darkoro.zerosmod.zsweapons.ZSWeaponUtils;
import org.darkoro.zerosmod.zsweapons.cache.CachedWeaponStats;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponNBTKey.*;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponTypeId.*;

/**
 * Weapon type helpers available through the CNPC global object named {@code WeaponAPI}.
 */
public class WeaponAPI {

  public static final WeaponAPI INSTANCE = new WeaponAPI();

  private WeaponAPI() {}

  /**
   * @param item item to read
   * @return the item's weapon type, or {@code default} when it has none
   */
  public String getWeaponType(IItemStack item) {
    ItemStack mcItem = item == null ? null : item.getMCItemStack();
    if (ZSWeaponUtils.hasZSWeaponTag(mcItem) && ZSWeaponUtils.getZSWeaponTag(mcItem).hasKey(TYPE.key)) {
      return ZSWeaponUtils.getZSWeaponTag(mcItem).getString(TYPE.key);
    }
    return DEFAULT;
  }

  /**
   * Sets an item's weapon type.
   *
   * @param type loaded weapon type name, or {@code special}
   * @param item item to change
   * @return false when the item is null or the type is not loaded
   */
  public boolean setWeaponType(String type, IItemStack item) {
    String key = ConfigHandler.normalizeKey(type);
    if (item == null || !key.equals(SPECIAL) && !ServerWeaponConfig.loadedWeaponStats.containsKey(key)) {
      return false;
    }

    ScriptNbt nbt = ZSWeaponUtils.hasZSWeaponTag(item.getMCItemStack())
        ? (ScriptNbt)item.getNbt().getCompound(ZSWEAPON.key)
        : new ScriptNbt(new NBTTagCompound());
    nbt.setString(TYPE.key, key);
    item.getNbt().setCompound(ZSWEAPON.key, nbt);
    return true;
  }

  /**
   * @return names of all loaded weapon types
   */
  public String[] getLoadedWeaponTypeNames() {
    return ServerWeaponConfig.loadedWeaponStats.keySet().toArray(new String[0]);
  }

  /**
   * @return copies of all loaded weapon types by name; changing them does not affect the server config
   */
  public Map<String, ScriptZSWeapon> getLoadedWeaponTypes() {
    Map<String, ScriptZSWeapon> copies = new LinkedHashMap<String, ScriptZSWeapon>();
    for (Map.Entry<String, CachedWeaponStats> entry : ServerWeaponConfig.loadedWeaponStats.entrySet()) {
      copies.put(entry.getKey(), entry.getValue().detachedCopy());
    }
    return Collections.unmodifiableMap(copies);
  }

  /**
   * @return a copy of the {@code default} weapon type, or null when it is not loaded
   */
  public ScriptZSWeapon getDefaultWeaponState() {
    CachedWeaponStats stats = ServerWeaponConfig.loadedWeaponStats.get(DEFAULT);
    return stats == null ? null : stats.detachedCopy();
  }
}
