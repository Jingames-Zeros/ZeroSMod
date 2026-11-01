package org.darkoro.zerosmod.tpbank;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import kamkeel.npcs.controllers.ProfileController;
import kamkeel.npcs.controllers.data.profile.Profile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.config.TpBankConfig;

/**
 * Admin statistics about the TP Bank, kept in {@code <world>/zerosmod/tp_bank_stats.json}.
 *
 * <p>This is a read-only report: balances always come from the player's NBT, and nothing here ever
 * decides how many TP a player owns. Saved balances are from each slot's last bank change, so
 * {@link #estimate} works out what pending interest would make of them. Deleted slots leave the
 * list when their owner is next seen online; their share of the running totals stays.</p>
 */
public final class TpBankStats {

  public static final TpBankStats INSTANCE = new TpBankStats();

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final int SAVE_INTERVAL_TICKS = 5 * 60 * 20;
  private static final ExecutorService WRITER = Executors.newSingleThreadExecutor(runnable -> {
    Thread thread = new Thread(runnable, "ZeroSMod TP Bank stats");
    thread.setDaemon(true);
    return thread;
  });

  private static Data data;
  private static boolean dirty;
  private static int ticks;

  private TpBankStats() {}

  public static final class Data {
    public final Totals totals = new Totals();
    public final Map<String, PlayerEntry> players = new LinkedHashMap<>();
  }

  public static final class Totals {
    public long deposited;
    public long withdrawn;
    public long eatenByDepositLoss;
    public long eatenByWithdrawFee;
    public long eatenByInterest;
    public long gainedFromInterest;

    /** @return every TP the bank has taken from players */
    public long eaten() {
      return eatenByDepositLoss + eatenByWithdrawFee + eatenByInterest;
    }
  }

  public static final class PlayerEntry {
    public String name;
    public final Map<String, SlotEntry> slots = new LinkedHashMap<>();
  }

  public static final class SlotEntry {
    public long banked;
    public long deposited;
    public long withdrawn;
    public boolean unlocked;
    public long interestAt;
    public long lastChange;
  }

  /** One slot of one player, for listings. */
  public static final class Row {
    public final String uuid;
    public final String name;
    public final String slot;
    public final SlotEntry entry;
    public final long estimate;

    private Row(String uuid, String name, String slot, SlotEntry entry) {
      this.uuid = uuid;
      this.name = name;
      this.slot = slot;
      this.entry = entry;
      this.estimate = estimate(entry);
    }
  }

  public static Totals totals() {
    return data().totals;
  }

  /** @return every slot of every player, richest first */
  public static List<Row> rows() {
    List<Row> rows = new ArrayList<>();
    for (Map.Entry<String, PlayerEntry> player : data().players.entrySet()) {
      for (Map.Entry<String, SlotEntry> slot : player.getValue().slots.entrySet()) {
        rows.add(new Row(player.getKey(), player.getValue().name, slot.getKey(), slot.getValue()));
      }
    }
    rows.sort((a, b) -> Long.compare(b.estimate, a.estimate));
    return rows;
  }

  /** @return the slots of the player with that name, richest first */
  public static List<Row> rowsOf(String name) {
    List<Row> rows = rows();
    rows.removeIf(row -> !row.name.equalsIgnoreCase(name));
    return rows;
  }

  /** @return the slot's balance as if its pending interest were applied now */
  public static long estimate(SlotEntry entry) {
    long periods = (System.currentTimeMillis() - entry.interestAt) / TpBankConfig.getInterestPeriodMillis();
    if (!entry.unlocked || entry.banked <= 0 || periods <= 0) {
      return entry.banked;
    }
    double next = Math.floor(entry.banked * Math.pow(1.0D + TpBankConfig.getInterestPercent() / 100.0D, periods));
    return Math.max(0L, Math.min(next >= Long.MAX_VALUE ? Long.MAX_VALUE : (long) next, TpBankConfig.getMaxBalance()));
  }

  static void syncBalance(EntityPlayer player, long banked, boolean unlocked, long interestAt) {
    SlotEntry slot = slot(player);
    slot.banked = banked;
    slot.unlocked = unlocked;
    slot.interestAt = interestAt;
    slot.lastChange = System.currentTimeMillis();
    dirty = true;
  }

  static void recordDeposit(EntityPlayer player, long added) {
    slot(player).deposited += added;
    totals().deposited += added;
    dirty = true;
  }

  static void recordDepositLoss(long lost) {
    totals().eatenByDepositLoss += lost;
    dirty = true;
  }

  static void recordWithdraw(EntityPlayer player, long received, long fee) {
    slot(player).withdrawn += received;
    totals().withdrawn += received;
    totals().eatenByWithdrawFee += fee;
    dirty = true;
  }

  /** @param change how much interest moved the balance; negative when it shrank */
  static void recordInterest(long change) {
    if (change < 0) {
      totals().eatenByInterest -= change;
    } else {
      totals().gainedFromInterest += change;
    }
    dirty = true;
  }

  private static SlotEntry slot(EntityPlayer player) {
    PlayerEntry entry = data().players.computeIfAbsent(player.getUniqueID().toString(), id -> new PlayerEntry());
    entry.name = player.getCommandSenderName();
    return entry.slots.computeIfAbsent(slotId(player), id -> new SlotEntry());
  }

  public static String slotId(EntityPlayer player) {
    try {
      return Integer.toString(ProfileController.Instance.getProfile(player).getCurrentSlotId());
    } catch (RuntimeException e) {
      return "0"; // statistics must never get in the way of the bank
    }
  }

  /**
   * Refreshes an online player's name and forgets slots their profile no longer has. Does nothing
   * for a player with no entry yet, or whose profile CNPC has not loaded: asking for it through
   * {@code getProfile} would run CNPC's login early.
   */
  public static void refresh(EntityPlayer player) {
    PlayerEntry entry = data().players.get(player.getUniqueID().toString());
    if (entry == null) {
      return;
    }
    if (!player.getCommandSenderName().equals(entry.name)) {
      entry.name = player.getCommandSenderName();
      dirty = true;
    }
    Profile profile = ProfileController.activeProfiles.get(player.getUniqueID());
    if (profile == null) {
      return;
    }
    Set<Integer> existing = profile.getSlots().keySet();
    if (!existing.isEmpty() && entry.slots.keySet().removeIf(
        slot -> !slot.equals(slotId(player)) && !existing.contains(parseSlot(slot)))) {
      dirty = true;
    }
  }

  private static Integer parseSlot(String slot) {
    try {
      return Integer.valueOf(slot);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private static Data data() {
    if (data == null) {
      if (file() == null) {
        return new Data(); // no world yet: keeping this would later overwrite the saved file
      }
      data = load();
    }
    return data;
  }

  private static File file() {
    File root = DimensionManager.getCurrentSaveRootDirectory();
    return root == null ? null : new File(root, "zerosmod/tp_bank_stats.json");
  }

  private static Data load() {
    File file = file();
    if (file == null || !file.isFile()) {
      return new Data();
    }
    try {
      Data loaded = GSON.fromJson(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8), Data.class);
      if (loaded != null) {
        return loaded;
      }
    } catch (IOException | RuntimeException e) {
      ZeroSMod.LOGGER.error("Could not read TP Bank statistics; starting over", e);
    }
    // Keep the unreadable file for inspection instead of overwriting it at the next save.
    if (!file.renameTo(new File(file.getParentFile(), file.getName() + ".corrupt"))) {
      ZeroSMod.LOGGER.error("Could not set aside the unreadable TP Bank statistics file");
    }
    return new Data();
  }

  private static void write(File file, String json) {
    try {
      Files.createDirectories(file.getParentFile().toPath());
      File temporary = new File(file.getParentFile(), file.getName() + ".tmp");
      Files.write(temporary.toPath(), json.getBytes(StandardCharsets.UTF_8));
      Files.move(temporary.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException | RuntimeException e) {
      ZeroSMod.LOGGER.error("Could not save TP Bank statistics", e);
    }
  }

  private static void submitSave() {
    File file = file();
    if (file == null || data == null) {
      return;
    }
    String json = GSON.toJson(data); // on the server thread, so the data cannot change mid-write
    dirty = false;
    WRITER.submit(() -> write(file, json));
  }

  @SubscribeEvent
  public void onServerTick(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END && ++ticks >= SAVE_INTERVAL_TICKS) {
      ticks = 0;
      if (dirty) {
        submitSave();
      }
    }
  }

  /** Saves and forgets the data, so the next world loads its own. */
  public static void onServerStopping() {
    if (dirty) {
      submitSave();
    }
    try {
      WRITER.submit(() -> {}).get(10, TimeUnit.SECONDS);
    } catch (Exception e) {
      ZeroSMod.LOGGER.error("Timed out saving TP Bank statistics", e);
    }
    data = null;
    dirty = false;
    ticks = 0;
  }
}
