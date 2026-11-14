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
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ScriptDBCPlayer.class, remap = false)
public abstract class MixinScriptDBCPlayer<T extends EntityPlayerMP>
    implements ScriptDBCPlayerMethods {

  @Unique private static final int ZEROSMOD$DBC_TIMER_SECONDS = 5;
  @Unique private static final String ZEROSMOD$FUSION_NBT = "jrmcFuzion";
  @Unique private static final String ZEROSMOD$VALID_EFFECTS =
      "Strain, GodPower, Fatigue, NoFuse, Pain, Heat, KO, Majin, Legendary, Divine, "
          + "GodOfDestruction";

  @Shadow public T player;

  @Override public boolean isDBCAlive() {
    return JRMCoreHDBC.isAlive(this.player);
  }

  @Override public void defuse() {
    DBCData playerData = DBCData.get(this.player);
    String[] fusion =
        zerosmod$getActiveFusion(playerData.Fusion, this.player.getCommandSenderName());
    if (fusion == null) {
      return;
    }

    String partnerName = fusion[0].equalsIgnoreCase(this.player.getCommandSenderName())
        ? fusion[1]
        : fusion[0];
    EntityPlayerMP partner = zerosmod$findOnlinePlayer(partnerName);

    zerosmod$applyFusionCooldown(this.player);
    if (partner != null && partner != this.player) {
      zerosmod$applyFusionCooldown(partner);
    }
  }

  @Override public int getFusionTimeLeft() {
    DBCData data = DBCData.get(this.player);
    String[] fusion = zerosmod$getActiveFusion(data.Fusion, this.player.getCommandSenderName());
    if (fusion == null) {
      return 0;
    }

    return zerosmod$storedIntervalsToSeconds(zerosmod$parsePositiveInt(fusion[2]));
  }

  @Override public void setDBCStatusEffect(String effect, int duration) {
    String normalized = zerosmod$normalizeEffect(effect);
    DBCData data = DBCData.get(this.player);

    switch (normalized) {
      case "majin":
        zerosmod$setToggle(data, DBCStatusEffects.Majin, duration != 0);
        return;
      case "legendary":
        zerosmod$setToggle(data, DBCStatusEffects.Legendary, duration != 0);
        return;
      case "divine":
        zerosmod$setToggle(data, DBCStatusEffects.Divine, duration != 0);
        return;
      case "godofdestruction":
        zerosmod$setToggle(data, DBCStatusEffects.GodOfDestruction, duration != 0);
        return;
      case "nofuse":
        if (data.stats.isFused()) {
          return;
        }
        zerosmod$setNoFuse(data, duration);
        return;
      case "strain":
        zerosmod$setTimed(data, "jrmcStrain", duration);
        return;
      case "godpower":
        zerosmod$setTimed(data, "jrmcGodPwr", duration);
        return;
      case "fatigue":
        zerosmod$setTimed(data, "jrmcGodStrain", duration);
        return;
      case "pain":
        zerosmod$setTimed(data, "jrmcGyJ7dp", duration);
        if (duration > 0) {
          NBTTagCompound nbt = data.getRawCompound();
          nbt.setInteger("jrmcUIWasInPainDuration", 0);
          nbt.setByte("jrmcUIWasInPain", (byte) 1);
        }
        return;
      case "heat":
        zerosmod$setTimed(data, "jrmcEf8slc", duration);
        data.getRawCompound().setDouble("jrmcEf8slcD", 0.0D);
        return;
      case "ko":
        zerosmod$setTimed(data, "jrmcHar4va", duration);
        return;
      default:
        throw zerosmod$invalidEffect(effect);
    }
  }

  @Override public int getDBCStatusEffect(String effect) {
    String normalized = zerosmod$normalizeEffect(effect);
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
        return zerosmod$getNoFuseSeconds(data);
      case "strain":
        return zerosmod$getTimedSeconds(data, "jrmcStrain");
      case "godpower":
        return zerosmod$getTimedSeconds(data, "jrmcGodPwr");
      case "fatigue":
        return zerosmod$getTimedSeconds(data, "jrmcGodStrain");
      case "pain":
        return zerosmod$getTimedSeconds(data, "jrmcGyJ7dp");
      case "heat":
        return zerosmod$getTimedSeconds(data, "jrmcEf8slc");
      case "ko":
        return zerosmod$getTimedSeconds(data, "jrmcHar4va");
      default:
        throw zerosmod$invalidEffect(effect);
    }
  }

  @Unique private static void zerosmod$setToggle(
      DBCData data, DBCStatusEffects effect, boolean enabled) {
    effect.set(data.player, enabled);
    data.loadNBTData(true);
  }

  @Unique private static void zerosmod$setTimed(DBCData data, String nbtName, int seconds) {
    data.getRawCompound().setInteger(nbtName, zerosmod$secondsToStoredIntervals(seconds));
    data.loadNBTData(true);
  }

  @Unique private static int zerosmod$getTimedSeconds(DBCData data, String nbtName) {
    return zerosmod$storedIntervalsToSeconds(data.getRawCompound().getInteger(nbtName));
  }

  @Unique private static void zerosmod$setNoFuse(DBCData data, int seconds) {
    String stored = String.valueOf(zerosmod$secondsToStoredIntervals(seconds));
    data.getRawCompound().setString(ZEROSMOD$FUSION_NBT, stored);
    data.loadNBTData(true);
  }

  @Unique private static int zerosmod$getNoFuseSeconds(DBCData data) {
    String fusion = data.Fusion == null ? "" : data.Fusion.trim();
    if (fusion.isEmpty() || fusion.contains(",")) {
      return 0;
    }
    return zerosmod$storedIntervalsToSeconds(zerosmod$parsePositiveInt(fusion));
  }

  @Unique private static void zerosmod$applyFusionCooldown(EntityPlayerMP participant) {
    DBCData data = DBCData.get(participant);
    String cooldown = String.valueOf(Math.max(0, JRMCoreConfig.FznOverTime));
    data.getRawCompound().setString(ZEROSMOD$FUSION_NBT, cooldown);
    data.StatusEffects = data.setSE(10, false);
    data.StatusEffects = data.setSE(11, false);
    data.loadNBTData(true);
  }

  @Unique private static EntityPlayerMP zerosmod$findOnlinePlayer(String name) {
    MinecraftServer server = MinecraftServer.getServer();
    return server == null ? null : server.getConfigurationManager().func_152612_a(name);
  }

  @Unique private static String[] zerosmod$getActiveFusion(String value, String playerName) {
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

  @Unique private static int zerosmod$secondsToStoredIntervals(int seconds) {
    if (seconds <= 0) {
      return 0;
    }
    return seconds / ZEROSMOD$DBC_TIMER_SECONDS
        + (seconds % ZEROSMOD$DBC_TIMER_SECONDS == 0 ? 0 : 1);
  }

  @Unique private static int zerosmod$storedIntervalsToSeconds(int intervals) {
    if (intervals <= 0) {
      return 0;
    }
    if (intervals > Integer.MAX_VALUE / ZEROSMOD$DBC_TIMER_SECONDS) {
      return Integer.MAX_VALUE;
    }
    return intervals * ZEROSMOD$DBC_TIMER_SECONDS;
  }

  @Unique private static int zerosmod$parsePositiveInt(String value) {
    try {
      return Math.max(0, Integer.parseInt(value.trim()));
    } catch (NumberFormatException ignored) {
      return 0;
    }
  }

  @Unique private static String zerosmod$normalizeEffect(String effect) {
    if (effect == null) {
      throw zerosmod$invalidEffect(null);
    }
    return effect.trim().toLowerCase(Locale.ROOT);
  }

  @Unique private static CustomNPCsException zerosmod$invalidEffect(String effect) {
    return new CustomNPCsException(
        "Invalid DBC status effect '%s'. Valid effects: %s", effect, ZEROSMOD$VALID_EFFECTS);
  }
}
