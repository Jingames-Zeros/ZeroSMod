package org.darkoro.zerosmod.event;

import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.entity.IPlayer;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.network.ServerTaskScheduler;

public class SaiyanMasteryMergeEvent {

  private static final byte RACE_SAIYAN = JRMCoreH.RACE_SAIYAN;
  private static final byte RACE_HALF_SAIYAN = JRMCoreH.RACE_HALF_SAIYAN;
  private static final int DELAYED_ATTEMPTS = 5;

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
    if (!(event.player instanceof EntityPlayerMP)) {
      return;
    }

    mergeNowAndLater((EntityPlayerMP) event.player, "login");
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onProfileChanged(noppes.npcs.scripted.event.player.PlayerEvent.ProfileEvent.Changed event) {
    if (!event.isPost()) {
      return;
    }

    IPlayer apiPlayer = event.getPlayer();
    if (apiPlayer == null || !(apiPlayer.getMCEntity() instanceof EntityPlayerMP)) {
      return;
    }

    mergeNowAndLater((EntityPlayerMP) apiPlayer.getMCEntity(), "profile change");
  }

  private static void mergeNowAndLater(EntityPlayerMP player, String trigger) {
    try {
      mergeSaiyanMastery(player, trigger, "immediate");
    } catch (Throwable t) {
      logFailure(player, t);
    }

    scheduleDelayedMerge(player, trigger, 1);
  }

  private static void scheduleDelayedMerge(final EntityPlayerMP player, final String trigger, final int attempt) {
    if (attempt > DELAYED_ATTEMPTS) {
      return;
    }

    ServerTaskScheduler.schedule(new Runnable() {
      @Override
      public void run() {
        if (player == null || player.worldObj == null || player.isDead) {
          return;
        }

        try {
          mergeSaiyanMastery(player, trigger, "delayed " + attempt);
        } catch (Throwable t) {
          logFailure(player, t);
        }

        scheduleDelayedMerge(player, trigger, attempt + 1);
      }
    });
  }

  private static void mergeSaiyanMastery(EntityPlayerMP player, String trigger, String phase) {
    DBCData dbcData = DBCData.get(player);
    NBTTagCompound nbt = dbcData.getRawCompound();

    byte race = dbcData.Race;
    if (race != RACE_SAIYAN && race != RACE_HALF_SAIYAN) {
      return;
    }

    JRMCoreH.updateFormMasteryVersion(nbt);

    String saiyanKey = JRMCoreH.getNBTFormMasteryRacialKey(RACE_SAIYAN);
    String halfSaiyanKey = JRMCoreH.getNBTFormMasteryRacialKey(RACE_HALF_SAIYAN);
    boolean missingHalfSaiyanData = !hasMasteryData(nbt, halfSaiyanKey);
    String saiyanData = getMasteryData(nbt, saiyanKey, RACE_SAIYAN);
    String halfSaiyanData = getMasteryData(nbt, halfSaiyanKey, RACE_HALF_SAIYAN);
    String mergedData = mergeMasteryData(saiyanData, halfSaiyanData);

    boolean changedMastery = missingHalfSaiyanData || !mergedData.equals(halfSaiyanData);
    if (changedMastery) {
      nbt.setString(halfSaiyanKey, mergedData);
      dbcData.FormMasteryRacial = mergedData;
    }

    if (race == RACE_SAIYAN) {
      dbcData.Race = RACE_HALF_SAIYAN;
      dbcData.FormMasteryRacial = mergedData;
      nbt.setByte(JRMCoreH.race, RACE_HALF_SAIYAN);
      JRMCoreH.setByte(RACE_HALF_SAIYAN, player, JRMCoreH.race);
    }

    if (race == RACE_SAIYAN || changedMastery) {
      dbcData.saveNBTData(true);
    }

    if ((race == RACE_SAIYAN || changedMastery) && ZeroSMod.LOGGER != null) {
      ZeroSMod.LOGGER.info(
          "Merged Saiyan mastery for {} during {} {}. Race {} -> {}.",
          player.getCommandSenderName(),
          trigger,
          phase,
          race,
          dbcData.Race);
    }
  }

  public static String forceMerge(EntityPlayerMP player) {
    DBCData dbcData = DBCData.get(player);
    NBTTagCompound nbt = dbcData.getRawCompound();
    byte beforeRace = dbcData.Race;
    byte beforeRawRace = nbt.getByte(JRMCoreH.race);
    String halfSaiyanKey = JRMCoreH.getNBTFormMasteryRacialKey(RACE_HALF_SAIYAN);
    boolean hadHalfSaiyanData = hasMasteryData(nbt, halfSaiyanKey);

    mergeSaiyanMastery(player, "command", "manual");

    byte afterRace = dbcData.Race;
    byte afterRawRace = nbt.getByte(JRMCoreH.race);
    boolean hasHalfSaiyanData = hasMasteryData(nbt, halfSaiyanKey);
    return "race " + beforeRace + " -> " + afterRace
        + ", raw race " + beforeRawRace + " -> " + afterRawRace
        + ", half mastery key " + (hadHalfSaiyanData ? "present" : "missing")
        + " -> " + (hasHalfSaiyanData ? "present" : "missing");
  }

  private static void logFailure(EntityPlayerMP player, Throwable t) {
    if (ZeroSMod.LOGGER != null) {
      ZeroSMod.LOGGER.error("Failed to merge Saiyan mastery for {}", player.getCommandSenderName(), t);
    } else {
      System.err.println("[ZeroSMod] Failed to merge Saiyan mastery for " + player.getCommandSenderName());
      t.printStackTrace();
    }
  }

  private static boolean hasMasteryData(NBTTagCompound nbt, String key) {
    return nbt.hasKey(key) && nbt.getString(key).length() > 0;
  }

  private static String getMasteryData(NBTTagCompound nbt, String key, int race) {
    if (hasMasteryData(nbt, key)) {
      return nbt.getString(key);
    }

    return JRMCoreH.getDefaultFormMasteryRacialText(race);
  }

  private static String mergeMasteryData(String sourceData, String targetData) {
    MasteryData source = MasteryData.parse(sourceData);
    MasteryData target = MasteryData.parse(targetData);

    for (int i = 0; i < target.entries.size(); i++) {
      MasteryEntry targetEntry = target.entries.get(i);
      MasteryEntry sourceEntry = source.byName.get(targetEntry.name);
      if (sourceEntry == null && i < source.entries.size()) {
        sourceEntry = source.entries.get(i);
      }

      if (sourceEntry != null && sourceEntry.value > targetEntry.value) {
        targetEntry.value = sourceEntry.value;
      }
    }

    for (int i = 0; i < source.entries.size(); i++) {
      MasteryEntry sourceEntry = source.entries.get(i);
      if (sourceEntry.name.length() > 0 && !target.byName.containsKey(sourceEntry.name)) {
        target.addEntry(new MasteryEntry(sourceEntry.name, sourceEntry.value));
      }
    }

    return target.toDataString();
  }

  private static final class MasteryData {
    private final List<MasteryEntry> entries;
    private final Map<String, MasteryEntry> byName;

    private MasteryData(List<MasteryEntry> entries, Map<String, MasteryEntry> byName) {
      this.entries = entries;
      this.byName = byName;
    }

    private static MasteryData parse(String data) {
      String[] parts = data == null || data.length() == 0 ? new String[0] : data.split(";");
      List<MasteryEntry> entries = new ArrayList<MasteryEntry>();
      Map<String, MasteryEntry> byName = new LinkedHashMap<String, MasteryEntry>();

      for (int i = 0; i < parts.length; i++) {
        MasteryEntry entry = MasteryEntry.parse(parts[i]);
        entries.add(entry);
        if (entry.name.length() > 0) {
          byName.put(entry.name, entry);
        }
      }

      return new MasteryData(entries, byName);
    }

    private void addEntry(MasteryEntry entry) {
      entries.add(entry);
      byName.put(entry.name, entry);
    }

    private String toDataString() {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < entries.size(); i++) {
        if (i > 0) {
          builder.append(';');
        }
        MasteryEntry entry = entries.get(i);
        builder.append(entry.name).append(',').append(formatValue(entry.value));
      }
      return builder.toString();
    }
  }

  private static final class MasteryEntry {
    private final String name;
    private double value;

    private MasteryEntry(String name, double value) {
      this.name = name;
      this.value = value;
    }

    private static MasteryEntry parse(String part) {
      String[] fields = part == null ? new String[0] : part.split(",", 2);
      String name = fields.length > 0 ? fields[0] : "";
      double value = 0;
      if (fields.length > 1) {
        try {
          value = Double.parseDouble(fields[1]);
        } catch (NumberFormatException ignored) {
          value = 0;
        }
      }

      return new MasteryEntry(name, value);
    }
  }

  private static String formatValue(double value) {
    if (value == (long) value) {
      return Long.toString((long) value);
    }

    return Double.toString(value);
  }
}