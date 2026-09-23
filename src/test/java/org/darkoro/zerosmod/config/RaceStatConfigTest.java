package org.darkoro.zerosmod.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

/** Filesystem regression checks, runnable without launching Minecraft or touching real configs. */
public final class RaceStatConfigTest {
  private static int checks;

  public static void main(String[] args) throws Exception {
    Path temporary = Files.createTempDirectory("zerosmod-racestats-test-");
    try {
      RaceStatConfig editor = new RaceStatConfig(temporary);
      String[] races = {"Human", "Half-Saiyan", "Namekian", "Arcosian", "Majin"};
      for (int race = 0; race < races.length; race++) {
        Path file = temporary.resolve("jingames/dbc/races/" + races[race].toLowerCase(java.util.Locale.ROOT) + "/main.cfg");
        Files.createDirectories(file.getParent());
        for (String newline : new String[] {"\n", "\r\n"}) {
          for (int classId = 0; classId < RaceStatConfig.CLASSES.length; classId++) {
            String original = fixture(races[race], newline);
            Files.write(file, original.getBytes(StandardCharsets.UTF_8));
            RaceStatConfig.Snapshot initial = editor.read(race, classId);
            check(initial.values.length == 12 && initial.values[0] == 2.5F, "reads twelve values");
            float[] changed = initial.values.clone();
            changed[0] = 7.25F;
            RaceStatConfig.Snapshot saved = editor.save(race, classId, initial.revision, changed);
            String marker = "S:\"DBC " + races[race] + " " + RaceStatConfig.CLASSES[classId]
                + " Stat Multiplier from Attribute\" <";
            int entry = original.indexOf("2.500000", original.indexOf(marker));
            String expected = original.substring(0, entry) + "7.25" + original.substring(entry + "2.500000".length());
            check(Arrays.equals(Files.readAllBytes(file), expected.getBytes(StandardCharsets.UTF_8)),
                "only the selected numeric token changes; all other bytes preserved");
            check(editor.read(race, classId).values[0] == 7.25F, "saved values are visible before restart");
            check(Arrays.equals(saved.revision, editor.read(race, classId).revision), "returned revision matches disk");
            final int selectedRace = race;
            final int selectedClass = classId;
            rejected(() -> editor.save(selectedRace, selectedClass, initial.revision, changed), "stale save rejected");
            check(Arrays.equals(Files.readAllBytes(file), expected.getBytes(StandardCharsets.UTF_8)), "stale save leaves file intact");
            editor.save(race, classId, saved.revision, saved.values);
            check(Arrays.equals(Files.readAllBytes(file), expected.getBytes(StandardCharsets.UTF_8)), "unchanged save preserves precision");
          }
        }
      }

      Path human = temporary.resolve("jingames/dbc/races/human/main.cfg");
      String original = fixture("Human", "\n");
      Files.write(human, original.getBytes(StandardCharsets.UTF_8));
      RaceStatConfig.Snapshot initial = editor.read(0, 0);
      for (float invalid : new float[] {Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, 100001, -100001}) {
        float[] values = initial.values.clone();
        values[4] = invalid;
        rejected(() -> editor.save(0, 0, initial.revision, values), "non-finite/out-of-range value rejected");
        check(new String(Files.readAllBytes(human), StandardCharsets.UTF_8).equals(original), "invalid values do not write");
      }
      float[] boundary = initial.values.clone();
      boundary[0] = -100000;
      boundary[11] = 100000;
      editor.save(0, 0, initial.revision, boundary);
      check(editor.read(0, 0).values[0] == -100000 && editor.read(0, 0).values[11] == 100000, "inclusive bounds supported");
      rejected(() -> editor.read(-1, 0), "negative race rejected");
      rejected(() -> editor.read(5, 0), "unknown race rejected");
      rejected(() -> editor.read(0, 3), "unknown class rejected");
      check(!Files.exists(temporary.resolve("jingames/dbc/races/saiyan")), "full Saiyan never created or edited");

      for (String malformed : new String[] {
          original.replace("Melee 2.500000", "Body 2.500000"),
          original.replace("Melee 2.500000", "Melee nope"),
          original.replace("        FlySpeed 1.0000\n", ""),
          original.replace("        FlySpeed 1.0000\n", "        FlySpeed 1.0000\n        Extra 1\n"),
          original.replace("race main attribute", "other category"),
          original + original,
          original.replace("    S:\"DBC Human Spiritualist", "    S:\"DBC Human MartialArtist")}) {
        byte[] bytes = malformed.getBytes(StandardCharsets.UTF_8);
        Files.write(human, bytes);
        rejected(() -> editor.read(0, 0), "malformed/ambiguous config rejected");
        check(Arrays.equals(Files.readAllBytes(human), bytes), "reading malformed config never repairs or rewrites it");
      }

      Files.write(human, original.getBytes(StandardCharsets.UTF_8));
      final RaceStatConfig.Snapshot beforeExternalEdit = editor.read(0, 0);
      String external = original + "# another admin edited this file\n";
      Files.write(human, external.getBytes(StandardCharsets.UTF_8));
      rejected(() -> editor.save(0, 0, beforeExternalEdit.revision, beforeExternalEdit.values), "external edits detected");
      check(new String(Files.readAllBytes(human), StandardCharsets.UTF_8).equals(external), "external edit preserved");

      String bareNumbers = original.replace("Melee 2.500000", "2.500000");
      Files.write(human, bareNumbers.getBytes(StandardCharsets.UTF_8));
      check(editor.read(0, 0).values[0] == 2.5F, "DBC's bare numeric entry format is supported");
      Files.delete(human);
      rejected(() -> editor.read(0, 0), "missing config rejected instead of generating defaults");
      try (Stream<Path> files = Files.walk(temporary)) {
        check(files.noneMatch(p -> p.toString().endsWith(".tmp")), "no temporary save files left over");
      }
      System.out.println("RaceStatConfig: " + checks + " checks passed.");
    } finally {
      try (Stream<Path> paths = Files.walk(temporary)) {
        for (Path path : (Iterable<Path>) paths.sorted(Comparator.reverseOrder())::iterator) Files.delete(path);
      }
    }
  }

  private static String fixture(String race, String newline) {
    StringBuilder text = new StringBuilder("\uFEFF# Configuration file - untouched café comment\n\n\"race main attribute\" {\n");
    for (String className : RaceStatConfig.CLASSES) {
      text.append("    # Server Sided! DBC ").append(race).append(" Stat Multiplier from Attribute. (From -100000 to 100000).\n")
          .append("    S:\"DBC ").append(race).append(' ').append(className).append(" Stat Multiplier from Attribute\" <\n");
      for (int i = 0; i < RaceStatConfig.STATS.length; i++) {
        text.append("        ").append(RaceStatConfig.STATS[i]).append(i == 0 ? " 2.500000\n" : " 1.0000\n");
      }
      text.append("    >\n    S:\"DBC ").append(race).append(' ').append(className)
          .append(" Attribute Multiplier\" <\n        Strength 1.0000\n    >\n")
          .append("    I:\"Unrelated starting stat\"=500\n\n");
    }
    text.append("}\n\n\"race special\" {\n    B:\"Keep me\"=true\n}\n");
    return text.toString().replace("\n", newline);
  }

  private static void check(boolean condition, String description) {
    if (!condition) throw new AssertionError(description);
    checks++;
  }

  private static void rejected(IOAction action, String description) throws Exception {
    try {
      action.run();
      throw new AssertionError(description);
    } catch (RaceStatConfig.InvalidConfigException expected) {
      checks++;
    }
  }

  private interface IOAction { void run() throws IOException; }
}
