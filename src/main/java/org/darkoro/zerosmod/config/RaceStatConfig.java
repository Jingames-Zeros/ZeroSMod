package org.darkoro.zerosmod.config;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Edits saved DBC settings only. Never invokes DBC loaders or changes runtime stat arrays. */
public final class RaceStatConfig {
  // Editor indices, not DBC race IDs. Keep disk names independent of the runtime Saiyan rename.
  public static final String[] RACES = {"Human", "Saiyan", "Namekian", "Arcosian", "Majin"};
  private static final String[] DISK_RACES = {"Human", "Half-Saiyan", "Namekian", "Arcosian", "Majin"};
  public static final String[] CLASSES = {"MartialArtist", "Spiritualist", "Warrior"};
  public static final String[] STATS = {"Melee", "Defense", "Body", "Stamina", "EnergyPower", "EnergyPool",
      "MaxSkills", "Speed", "RegenRateBody", "RegenRateStamina", "RegenRateEnergy", "FlySpeed"};
  private static final Pattern CATEGORY = Pattern.compile(
      "(?im)^[ \\t]*\"race main attribute\"[ \\t]*\\{[ \\t]*\\r?$" );
  private static final Pattern CATEGORY_END = Pattern.compile("(?m)^[ \\t]*}[ \\t]*\\r?$" );
  private static final Pattern LINE = Pattern.compile("[^\\r\\n]+" );
  private static final int MAX_FILE_BYTES = 1024 * 1024;
  private final Path root;

  public RaceStatConfig(Path configDirectory) {
    root = configDirectory.resolve("jingames/dbc/races");
  }

  public static void validateSelection(int race, int classId) throws InvalidConfigException {
    if (race < 0 || race >= RACES.length || classId < 0 || classId >= CLASSES.length) {
      throw new InvalidConfigException("Invalid race or class.");
    }
  }

  public static boolean validValue(float value) {
    return !Float.isNaN(value) && !Float.isInfinite(value) && value >= -100000 && value <= 100000;
  }

  public synchronized Snapshot read(int race, int classId) throws IOException {
    return document(race, classId).snapshot;
  }

  public synchronized Snapshot save(int race, int classId, byte[] revision, float[] values) throws IOException {
    if (values == null || values.length != STATS.length) {
      throw new InvalidConfigException("Expected twelve stat multipliers.");
    }
    for (float value : values) {
      if (!validValue(value)) throw new InvalidConfigException("Values must be finite numbers from -100000 to 100000.");
    }
    Document doc = document(race, classId);
    if (!Arrays.equals(revision, doc.snapshot.revision)) {
      throw new InvalidConfigException("Config changed on disk. Reload the saved values before editing again.");
    }
    StringBuilder edited = new StringBuilder(doc.text);
    for (int i = STATS.length - 1; i >= 0; i--) {
      // Preserve the original spelling/precision of every unchanged number.
      if (Float.compare(values[i], doc.snapshot.values[i]) != 0) {
        edited.replace(doc.starts[i], doc.ends[i], Float.toString(values[i]));
      }
    }
    byte[] bytes = edited.toString().getBytes(StandardCharsets.UTF_8);
    if (Arrays.equals(bytes, doc.bytes)) return doc.snapshot;
    Path temporary = Files.createTempFile(doc.path.getParent(), ".zerosmod-racestats-", ".tmp");
    try {
      Files.copy(doc.path, temporary, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
      Files.write(temporary, bytes);
      if (!Arrays.equals(doc.bytes, readBytes(doc.path))) {
        throw new InvalidConfigException("Config changed during save. Reload the saved values and try again.");
      }
      // Fail without replacing the original if this filesystem cannot perform an atomic replacement.
      Files.move(temporary, doc.path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    } finally {
      Files.deleteIfExists(temporary);
    }
    return new Snapshot(hash(bytes), values.clone());
  }

  private Document document(int race, int classId) throws IOException {
    validateSelection(race, classId);
    Path path = root.resolve(DISK_RACES[race].toLowerCase(Locale.ROOT)).resolve("main.cfg");
    byte[] bytes = readBytes(path);
    String text = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(bytes)).toString();
    Matcher category = CATEGORY.matcher(text);
    if (!category.find()) throw new InvalidConfigException("Missing Race Main Attribute category.");
    int categoryStart = category.end();
    if (category.find()) throw new InvalidConfigException("Duplicate Race Main Attribute categories.");
    Matcher end = CATEGORY_END.matcher(text);
    if (!end.find(categoryStart)) throw new InvalidConfigException("Unclosed Race Main Attribute category.");
    int categoryEnd = end.start();
    String key = "DBC " + DISK_RACES[race] + " " + CLASSES[classId] + " Stat Multiplier from Attribute";
    Pattern block = Pattern.compile("(?m)^[ \\t]*S:\"" + Pattern.quote(key)
        + "\"[ \\t]*<[ \\t]*\\r?\\n([\\s\\S]*?)^[ \\t]*>[ \\t]*\\r?$" );
    Matcher list = block.matcher(text).region(categoryStart, categoryEnd);
    if (!list.find()) throw new InvalidConfigException("Missing stat multiplier list for this race/class.");
    int start = list.start(1);
    int finish = list.end(1);
    if (list.find()) throw new InvalidConfigException("Duplicate stat multiplier lists.");
    float[] values = new float[STATS.length];
    int[] starts = new int[STATS.length];
    int[] ends = new int[STATS.length];
    Matcher line = LINE.matcher(text).region(start, finish);
    int index = 0;
    while (line.find()) {
      String entry = line.group();
      if (entry.trim().isEmpty()) continue;
      if (index >= STATS.length) throw new InvalidConfigException("Too many stat multiplier entries.");
      // Forge trims each entry; DBC reads lists by position and expects a single space after the label.
      Matcher number = Pattern.compile("^[ \\t]*(?:" + Pattern.quote(STATS[index])
          + " )?([^ \\t]+)[ \\t]*$").matcher(entry);
      if (!number.matches()) throw new InvalidConfigException("Unexpected entry/order at " + STATS[index] + ".");
      try {
        values[index] = Float.parseFloat(number.group(1));
      } catch (NumberFormatException e) {
        throw new InvalidConfigException("Invalid number for " + STATS[index] + ".");
      }
      if (!validValue(values[index])) throw new InvalidConfigException("Out-of-range value for " + STATS[index] + ".");
      starts[index] = line.start() + number.start(1);
      ends[index] = line.start() + number.end(1);
      index++;
    }
    if (index != STATS.length) throw new InvalidConfigException("Expected twelve stat multiplier entries.");
    return new Document(path, bytes, text, starts, ends, new Snapshot(hash(bytes), values));
  }

  private static byte[] readBytes(Path path) throws IOException {
    if (!Files.isRegularFile(path)) throw new InvalidConfigException("Race main.cfg is missing. Check the server config.");
    if (Files.size(path) > MAX_FILE_BYTES) throw new InvalidConfigException("Race main.cfg exceeds the editor size limit.");
    byte[] bytes = Files.readAllBytes(path);
    if (bytes.length > MAX_FILE_BYTES) throw new InvalidConfigException("Race main.cfg exceeds the editor size limit.");
    return bytes;
  }

  private static byte[] hash(byte[] bytes) {
    try {
      return MessageDigest.getInstance("SHA-256").digest(bytes);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public static final class Snapshot {
    public final byte[] revision;
    public final float[] values;
    public Snapshot(byte[] revision, float[] values) {
      this.revision = revision;
      this.values = values;
    }
  }

  public static final class InvalidConfigException extends IOException {
    public InvalidConfigException(String message) { super(message); }
  }

  private static final class Document {
    final Path path;
    final byte[] bytes;
    final String text;
    final int[] starts;
    final int[] ends;
    final Snapshot snapshot;
    Document(Path path, byte[] bytes, String text, int[] starts, int[] ends, Snapshot snapshot) {
      this.path = path;
      this.bytes = bytes;
      this.text = text;
      this.starts = starts;
      this.ends = ends;
      this.snapshot = snapshot;
    }
  }
}
