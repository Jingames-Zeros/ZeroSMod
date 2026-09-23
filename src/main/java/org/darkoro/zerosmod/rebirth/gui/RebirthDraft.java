package org.darkoro.zerosmod.rebirth.gui;

/** Shared input rules only; the server remains responsible for all player state. */
public final class RebirthDraft {
  public static final String[] LABELS = {"Strength", "Dexterity", "Constitution", "Willpower", "Mind", "Spirit"};
  public static final int COUNT = 6;
  public static final int MIN = 10;

  private RebirthDraft() {}

  public static int[] parse(String[] text, int maximum) {
    if (text == null || text.length != COUNT) {
      return null;
    }
    int[] values = new int[COUNT];
    try {
      for (int i = 0; i < COUNT; i++) {
        values[i] = Integer.parseInt(text[i].trim());
      }
    } catch (NumberFormatException | NullPointerException e) {
      return null;
    }
    return valid(values, maximum) ? values : null;
  }

  public static boolean valid(int[] values, int maximum) {
    if (values == null || values.length != COUNT) {
      return false;
    }
    for (int value : values) {
      if (value < MIN || value > maximum) {
        return false;
      }
    }
    return true;
  }

  public static long total(int[] values) {
    long total = 0;
    for (int value : values) {
      total += value;
    }
    return total;
  }

  public static long remaining(int[] saved, int savedRemaining, int[] draft) {
    return total(saved) + savedRemaining - total(draft);
  }
}
