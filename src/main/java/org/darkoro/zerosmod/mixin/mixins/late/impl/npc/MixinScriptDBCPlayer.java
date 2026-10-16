package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreHDBC;
import java.util.Locale;
import kamkeel.npcdbc.constants.DBCStatusEffects;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.scripted.entity.ScriptDBCPlayer;
import org.darkoro.zerosmod.api.ScriptDBCPlayerMethods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ScriptDBCPlayer.class, remap = false)
public abstract class MixinScriptDBCPlayer<T extends EntityPlayerMP>
    implements ScriptDBCPlayerMethods {

  private static final int DBC_TIMER_SECONDS = 5;
  private static final String FUSION_NBT = "jrmcFuzion";
  private static final String VALID_EFFECTS =
      "Strain, GodPower, Fatigue, NoFuse, Pain, Heat, KO, Majin, Legendary, Divine, "
          + "GodOfDestruction";

  @Shadow public T player;

  @Override public boolean isDBCAlive() {
    return JRMCoreHDBC.isAlive(this.player);
  }

  @Override public void defuse() {
    DBCData playerData = DBCData.get(this.player);
    String[] fusion = getActiveFusion(playerData.Fusion, this.player.getCommandSenderName());
    if (fusion == null) {
      return;
    }

    String partnerName = fusion[0].equalsIgnoreCase(this.player.getCommandSenderName())
        ? fusion[1]
        : fusion[0];
    EntityPlayerMP partner = findOnlinePlayer(partnerName);

    applyFusionCooldown(this.player);
    if (partner != null && partner != this.player) {
      applyFusionCooldown(partner);
    }
  }

  @Override public int getFusionTimeLeft() {
    DBCData data = DBCData.get(this.player);
    String[] fusion = getActiveFusion(data.Fusion, this.player.getCommandSenderName());
    if (fusion == null) {
      return 0;
    }

    return storedIntervalsToSeconds(parsePositiveInt(fusion[2]));
  }

  @Override public void setDBCStatusEffect(String effect, int duration) {
    String normalized = normalizeEffect(effect);
    DBCData data = DBCData.get(this.player);

    switch (normalized) {
      case "majin":
        setToggle(data, DBCStatusEffects.Majin, duration != 0);
        return;
      case "legendary":
        setToggle(data, DBCStatusEffects.Legendary, duration != 0);
        return;
      case "divine":
        setToggle(data, DBCStatusEffects.Divine, duration != 0);
        return;
      case "godofdestruction":
        setToggle(data, DBCStatusEffects.GodOfDestruction, duration != 0);
        return;
      case "nofuse":
        if (data.stats.isFused()) {
          return;
        }
        setNoFuse(data, duration);
        return;
      case "strain":
        setTimed(data, "jrmcStrain", duration);
        return;
      case "godpower":
        setTimed(data, "jrmcGodPwr", duration);
        return;
      case "fatigue":
        setTimed(data, "jrmcGodStrain", duration);
        return;
      case "pain":
        setTimed(data, "jrmcGyJ7dp", duration);
        if (duration > 0) {
          NBTTagCompound nbt = data.getRawCompound();
          nbt.setInteger("jrmcUIWasInPainDuration", 0);
          nbt.setByte("jrmcUIWasInPain", (byte) 1);
        }
        return;
      case "heat":
        setTimed(data, "jrmcEf8slc", duration);
        data.getRawCompound().setDouble("jrmcEf8slcD", 0.0D);
        return;
      case "ko":
        setTimed(data, "jrmcHar4va", duration);
        return;
      default:
        throw invalidEffect(effect);
    }
  }

  @Override public int getDBCStatusEffect(String effect) {
    String normalized = normalizeEffect(effect);
    DBCData data = DBCData.get(this.player);

    switch (normalized) {
      case "majin":
        return DBCStatusEffects.Majin.has(this.player) ? 1 : 0;
      case "legendary":
        return DBCStatusEffects.Legendary.has(this.player) ? 1 : 0;
      case "divine":
        return DBCStatusEffects.Divine.has(this.player) ? 1 : 0;
      case "godofdestruction":
        return DBCStatusEffects.GodOfDestruction.has(this.player) ? 1 : 0;
      case "nofuse":
        return getNoFuseSeconds(data);
      case "strain":
        return getTimedSeconds(data, "jrmcStrain");
      case "godpower":
        return getTimedSeconds(data, "jrmcGodPwr");
      case "fatigue":
        return getTimedSeconds(data, "jrmcGodStrain");
      case "pain":
        return getTimedSeconds(data, "jrmcGyJ7dp");
      case "heat":
        return getTimedSeconds(data, "jrmcEf8slc");
      case "ko":
        return getTimedSeconds(data, "jrmcHar4va");
      default:
        throw invalidEffect(effect);
    }
  }

  private static void setToggle(DBCData data, DBCStatusEffects effect, boolean enabled) {
    effect.set(data.player, enabled);
    data.loadNBTData(false);
  }

  private static void setTimed(DBCData data, String nbtName, int seconds) {
    data.getRawCompound().setInteger(nbtName, secondsToStoredIntervals(seconds));
    data.loadNBTData(false);
  }

  private static int getTimedSeconds(DBCData data, String nbtName) {
    return storedIntervalsToSeconds(data.getRawCompound().getInteger(nbtName));
  }

  private static void setNoFuse(DBCData data, int seconds) {
    String stored = String.valueOf(secondsToStoredIntervals(seconds));
    data.getRawCompound().setString(FUSION_NBT, stored);
    data.Fusion = stored;
  }

  private static int getNoFuseSeconds(DBCData data) {
    String fusion = data.Fusion == null ? "" : data.Fusion.trim();
    if (fusion.isEmpty() || fusion.contains(",")) {
      return 0;
    }
    return storedIntervalsToSeconds(parsePositiveInt(fusion));
  }

  private static void applyFusionCooldown(EntityPlayerMP participant) {
    DBCData data = DBCData.get(participant);
    String cooldown = String.valueOf(Math.max(0, JRMCoreConfig.FznOverTime));
    data.getRawCompound().setString(FUSION_NBT, cooldown);
    data.StatusEffects = data.setSE(10, false);
    data.StatusEffects = data.setSE(11, false);
    data.Fusion = cooldown;
  }

  private static EntityPlayerMP findOnlinePlayer(String name) {
    MinecraftServer server = MinecraftServer.getServer();
    return server == null ? null : server.getConfigurationManager().func_152612_a(name);
  }

  private static String[] getActiveFusion(String value, String playerName) {
    if (value == null) {
      return null;
    }

    String[] parts = value.trim().split(",", -1);
    if (parts.length != 3
        || (!parts[0].equalsIgnoreCase(playerName) && !parts[1].equalsIgnoreCase(playerName))) {
      return null;
    }
    return parts;
  }

  private static int secondsToStoredIntervals(int seconds) {
    if (seconds <= 0) {
      return 0;
    }
    return seconds / DBC_TIMER_SECONDS + (seconds % DBC_TIMER_SECONDS == 0 ? 0 : 1);
  }

  private static int storedIntervalsToSeconds(int intervals) {
    if (intervals <= 0) {
      return 0;
    }
    if (intervals > Integer.MAX_VALUE / DBC_TIMER_SECONDS) {
      return Integer.MAX_VALUE;
    }
    return intervals * DBC_TIMER_SECONDS;
  }

  private static int parsePositiveInt(String value) {
    try {
      return Math.max(0, Integer.parseInt(value.trim()));
    } catch (NumberFormatException ignored) {
      return 0;
    }
  }

  private static String normalizeEffect(String effect) {
    if (effect == null) {
      throw invalidEffect(null);
    }
    return effect.trim().toLowerCase(Locale.ROOT);
  }

  private static CustomNPCsException invalidEffect(String effect) {
    return new CustomNPCsException(
        "Invalid DBC status effect '%s'. Valid effects: %s", effect, VALID_EFFECTS);
  }
}
