package org.darkoro.zerosmod.tab;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.constants.DBCAttribute;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.util.PlayerDataUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.PlayerData;
import org.darkoro.zerosmod.config.PathConfig;
import org.darkoro.zerosmod.network.SyncZSTabDataPacket;

public final class ZSTabDataProvider {

  private static final String[] PASSIVE_KEYS = {
      "spcPassive", "SPCPassive", "equippedPassive", "equipped_passive",
      "zsmod_spc_passive", "SPC_PASSIVE"
  };
  private static final String[] SUPER_1_KEYS = {
      "spcSuper1", "SPCSuper1", "equippedSuper1", "equipped_super_1",
      "zsmod_spc_super_1", "SPC_SUPER_1"
  };
  private static final String[] SUPER_2_KEYS = {
      "spcSuper2", "SPCSuper2", "equippedSuper2", "equipped_super_2",
      "zsmod_spc_super_2", "SPC_SUPER_2"
  };
  private static final String[] ULTIMATE_KEYS = {
      "spcUltimate", "SPCUltimate", "equippedUltimate", "equipped_ultimate",
      "zsmod_spc_ultimate", "SPC_ULTIMATE"
  };

  // NBT Fallback is non-performant - cache values for a short time
  private static final long NBT_CACHE_TTL_MS = 30000L;
  private static final Map<UUID, NbtSlotCache> NBT_SLOT_CACHE = new ConcurrentHashMap<UUID, NbtSlotCache>();

  private ZSTabDataProvider() {}

  public static SyncZSTabDataPacket buildPacket(EntityPlayerMP player) {
    SyncZSTabDataPacket packet = new SyncZSTabDataPacket();
    packet.playerName = player.getCommandSenderName();
    DBCData data = null;

    try {
      data = DBCData.get(player);
      packet.className = nameFromArray(JRMCoreH.ClassesDBC, data.Class, "Unknown");
      packet.raceName = getRaceName(data);
      packet.currentForm = getCurrentFormName(data);
      packet.str = data.STR;
      packet.dex = data.DEX;
      packet.con = data.CON;
      packet.wil = data.WIL;
      packet.mnd = data.MND;
      packet.spi = data.SPI;
      packet.level = data.getPlayerLevel();
      packet.tp = data.TP;
      packet.strMulti = getAttributeMultiplier(data, DBCAttribute.Strength, data.STR);
      packet.dexMulti = getAttributeMultiplier(data, DBCAttribute.Dexterity, data.DEX);
      packet.wilMulti = getAttributeMultiplier(data, DBCAttribute.Willpower, data.WIL);
    } catch (Throwable ignored) {
      packet.className = "Unknown";
      packet.raceName = "Unknown";
      packet.currentForm = "Base";
    }

    packet.passive = getSpcValue(player, "passive", PASSIVE_KEYS);
    packet.super1 = getSpcValue(player, "super1", SUPER_1_KEYS);
    packet.super2 = getSpcValue(player, "super2", SUPER_2_KEYS);
    packet.ultimate = getSpcValue(player, "ultimate", ULTIMATE_KEYS);
    packet.currentPath = PathConfig.getPathForForms(getUnlockedFormNames(player, data, packet.currentForm));
    packet.spiritPercent = getLiveSpiritPercent(player);
    packet.spcArmed = getLiveSpcArmed(player);
    packet.spcUnlocked = getLiveSpcUnlocked(player);
    return packet;
  }

  private static String getRaceName(DBCData data) {
    try {
      String name = data.simplifiedDBCData.getRaceName();
      if (name != null && name.length() > 0) {
        return name;
      }
    } catch (Throwable ignored) {}

    return nameFromArray(JRMCoreH.Races, data.Race, "Unknown");
  }

  private static String getCurrentFormName(DBCData data) {
    try {
      IForm form = data.simplifiedDBCData.getCurrentForm();
      if (form != null) {
        String name = form.getMenuName();
        if (name == null || name.length() == 0) {
          name = form.getName();
        }
        if (name != null && name.length() > 0) {
          return name;
        }
      }
    } catch (Throwable ignored) {}

    try {
      String name = data.simplifiedDBCData.getCurrentDBCFormName();
      if (name != null && name.length() > 0) {
        return name;
      }
    } catch (Throwable ignored) {}

    return "Base";
  }

  private static float getAttributeMultiplier(DBCData data, int attribute, int baseValue) {
    if (data == null || baseValue <= 0) {
      return 1.0F;
    }

    try {
      return Math.max(1.0F, data.stats.getFullAttribute(attribute) / (float) baseValue);
    } catch (Throwable ignored) {
      return 1.0F;
    }
  }

  private static Collection<String> getUnlockedFormNames(EntityPlayerMP player, DBCData data, String currentForm) {
    List<String> formNames = new ArrayList<String>();

    try {
      PlayerDBCInfo playerDBCInfo = PlayerDataUtil.getDBCInfo(player);
      if (playerDBCInfo != null) {
        for (Integer formId : playerDBCInfo.unlockedForms) {
          IForm form = FormController.getInstance().get(formId);
          if (form != null) {
            addIfPresent(formNames, form.getName());
            addIfPresent(formNames, form.getMenuName());
          }
        }
      }
    } catch (Throwable ignored) {}

    try {
      if (data != null) {
        for (Map.Entry<Integer, String> entry : data.getUnlockedDBCFormsMap().entrySet()) {
          addIfPresent(formNames, entry.getValue());
        }
      }
    } catch (Throwable ignored) {}

    addIfPresent(formNames, currentForm);
    return formNames;
  }

  private static void addIfPresent(List<String> values, String value) {
    if (value != null && value.trim().length() > 0) {
      values.add(value);
    }
  }

  private static String nameFromArray(String[] values, int index, String fallback) {
    if (values != null && index >= 0 && index < values.length) {
      String value = values[index];
      if (value != null && value.length() > 0) {
        return value;
      }
    }
    return fallback;
  }

  private static String getSpcValue(EntityPlayerMP player, String slotName, String[] keys) {
    String liveValue = getLiveSpcAbilityName(player, slotName);
    if (!liveValue.isEmpty()) return liveValue;
    long now = System.currentTimeMillis();
    UUID playerId = player.getUniqueID();
    NbtSlotCache cache = NBT_SLOT_CACHE.get(playerId);
    if (cache == null || cache.isExpired(now)) {
      pruneExpired(now);
      cache = new NbtSlotCache(now);
      NBT_SLOT_CACHE.put(playerId, cache);
    }

    String cached = cache.valuesBySlot.get(slotName);
    if (cached != null) return !cached.isEmpty() ? cached : "None";
    String value = scanNbtForSpcValue(player, keys);
    cache.valuesBySlot.put(slotName, value);
    return !value.isEmpty() ? value : "None";
  }

  private static String scanNbtForSpcValue(EntityPlayerMP player, String[] keys) {
    String value = findInCompound(player.getEntityData(), keys);
    if (value.length() > 0) {
      return value;
    }

    NBTTagCompound persisted = player.getEntityData().getCompoundTag(EntityPlayerMP.PERSISTED_NBT_TAG);
    value = findInCompound(persisted, keys);
    if (value.length() > 0) {
      return value;
    }

    try {
      PlayerData playerData = PlayerData.get(player);
      if (playerData != null) {
        value = findInCompound(playerData.getNBT(), keys);
      }
    } catch (Throwable ignored) {}

    return value;
  }

  private static void pruneExpired(long now) {
    if (NBT_SLOT_CACHE.size() < 64) {
      return;
    }

    Iterator<NbtSlotCache> iterator = NBT_SLOT_CACHE.values().iterator();
    while (iterator.hasNext()) {
      if (iterator.next().isExpired(now)) {
        iterator.remove();
      }
    }
  }

  private static final class NbtSlotCache {
    private final long createdMillis;
    // Only touched from the server thread (buildPacket runs via ServerTaskScheduler).
    private final Map<String, String> valuesBySlot = new HashMap<String, String>();

    private NbtSlotCache(long createdMillis) {
      this.createdMillis = createdMillis;
    }

    private boolean isExpired(long now) {
      return now - createdMillis > NBT_CACHE_TTL_MS;
    }
  }

  private static String getLiveSpcAbilityName(EntityPlayerMP player, String slotName) {
    try {
      Object scPlayer = player.getExtendedProperties("spiritcontrol");
      if (scPlayer == null) {
        return "";
      }

      Method getAbilityFromSlot = scMethodsFor(scPlayer).getAbilityFromSlot;
      if (getAbilityFromSlot == null) {
        return "";
      }

      Object ability = getAbilityFromSlot.invoke(scPlayer, slotName);
      return getAbilityName(ability);
    } catch (Throwable ignored) {
      return "";
    }
  }

  private static int getLiveSpiritPercent(EntityPlayerMP player) {
    try {
      Object scPlayer = player.getExtendedProperties("spiritcontrol");
      if (scPlayer == null) {
        return -1;
      }

      ScMethods methods = scMethodsFor(scPlayer);
      if (methods.getSpirit == null || methods.getMaxSpirit == null) {
        return -1;
      }

      double spirit = invokeDouble(scPlayer, methods.getSpirit);
      double maxSpirit = invokeDouble(scPlayer, methods.getMaxSpirit);
      if (maxSpirit <= 0.0D) {
        return -1;
      }

      double percent = Math.floor(spirit / maxSpirit * 100.0D);
      if (Double.isNaN(percent) || Double.isInfinite(percent)) {
        return -1;
      }

      return Math.max(0, Math.min(100, (int) percent));
    } catch (Throwable ignored) {
      return -1;
    }
  }

  private static boolean getLiveSpcArmed(EntityPlayerMP player) {
    try {
      Object scPlayer = player.getExtendedProperties("spiritcontrol");
      if (scPlayer == null) {
        return true;
      }

      Method isArmed = scMethodsFor(scPlayer).isArmed;
      if (isArmed == null) {
        return true;
      }

      Object value = isArmed.invoke(scPlayer);
      return !(value instanceof Boolean) || (Boolean) value;
    } catch (Throwable ignored) {
      return true;
    }
  }

  private static boolean getLiveSpcUnlocked(EntityPlayerMP player) {
    try {
      Object scPlayer = player.getExtendedProperties("spiritcontrol");
      if (scPlayer == null) {
        return false;
      }

      Method hasUnlockedSpiritControl = scMethodsFor(scPlayer).hasUnlockedSpiritControl;
      if (hasUnlockedSpiritControl == null) {
        return false;
      }

      Object value = hasUnlockedSpiritControl.invoke(scPlayer);
      return value instanceof Boolean && (Boolean) value;
    } catch (Throwable ignored) {
      return false;
    }
  }

  private static double invokeDouble(Object target, Method method) throws Exception {
    Object value = method.invoke(target);
    return value instanceof Number ? ((Number) value).doubleValue() : 0.0D;
  }

  private static String getAbilityName(Object ability) {
    if (ability == null) {
      return "";
    }

    AbilityMethods methods = abilityMethodsFor(ability);
    try {
      if (methods.getName != null) {
        Object name = methods.getName.invoke(ability);
        if (name instanceof String && ((String) name).length() > 0) {
          return (String) name;
        }
      }
    } catch (Throwable ignored) {}

    try {
      if (methods.getId != null) {
        Object id = methods.getId.invoke(ability);
        if (id instanceof String && ((String) id).length() > 0) {
          return (String) id;
        }
      }
    } catch (Throwable ignored) {}

    return ability.toString();
  }

  // Class.getMethod is slow and buildPacket runs once per second per player, so
  // reflected methods are resolved once per target class (null = method absent).
  private static volatile ScMethods scMethodsCache;
  private static volatile AbilityMethods abilityMethodsCache;

  private static ScMethods scMethodsFor(Object scPlayer) {
    ScMethods cached = scMethodsCache;
    Class<?> owner = scPlayer.getClass();
    if (cached == null || cached.owner != owner) {
      cached = new ScMethods(owner);
      scMethodsCache = cached;
    }
    return cached;
  }

  private static AbilityMethods abilityMethodsFor(Object ability) {
    AbilityMethods cached = abilityMethodsCache;
    Class<?> owner = ability.getClass();
    if (cached == null || cached.owner != owner) {
      cached = new AbilityMethods(owner);
      abilityMethodsCache = cached;
    }
    return cached;
  }

  private static Method findMethod(Class<?> owner, String name, Class<?>... params) {
    try {
      return owner.getMethod(name, params);
    } catch (Throwable ignored) {
      return null;
    }
  }

  private static final class ScMethods {
    private final Class<?> owner;
    private final Method getAbilityFromSlot;
    private final Method getSpirit;
    private final Method getMaxSpirit;
    private final Method isArmed;
    private final Method hasUnlockedSpiritControl;

    private ScMethods(Class<?> owner) {
      this.owner = owner;
      this.getAbilityFromSlot = findMethod(owner, "getAbilityFromSlot", String.class);
      this.getSpirit = findMethod(owner, "getSpirit");
      this.getMaxSpirit = findMethod(owner, "getMaxSpirit");
      this.isArmed = findMethod(owner, "isArmed");
      this.hasUnlockedSpiritControl = findMethod(owner, "hasUnlockedSpiritControl");
    }
  }

  private static final class AbilityMethods {
    private final Class<?> owner;
    private final Method getName;
    private final Method getId;

    private AbilityMethods(Class<?> owner) {
      this.owner = owner;
      this.getName = findMethod(owner, "getName");
      this.getId = findMethod(owner, "getId");
    }
  }

  private static String findInCompound(NBTTagCompound compound, String[] keys) {
    if (compound == null) {
      return "";
    }

    for (int i = 0; i < keys.length; i++) {
      String value = findStringRecursive(compound, keys[i], 0);
      if (value.length() > 0) {
        return value;
      }
    }

    return "";
  }

  private static String findStringRecursive(NBTTagCompound compound, String key, int depth) {
    if (depth > 3 || compound == null || key == null) {
      return "";
    }

    if (compound.hasKey(key)) {
      try {
        String value = compound.getString(key);
        if (value != null && value.length() > 0) {
          return value;
        }
      } catch (Throwable ignored) {}
    }

    for (Object rawKey : compound.func_150296_c()) {
      if (!(rawKey instanceof String)) {
        continue;
      }

      String childKey = (String) rawKey;
      try {
        if (compound.getTag(childKey) instanceof NBTTagCompound) {
          String value = findStringRecursive(compound.getCompoundTag(childKey), key, depth + 1);
          if (value.length() > 0) {
            return value;
          }
        }
      } catch (Throwable ignored) {}
    }

    return "";
  }
}
