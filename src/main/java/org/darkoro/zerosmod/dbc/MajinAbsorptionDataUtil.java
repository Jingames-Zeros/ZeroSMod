package org.darkoro.zerosmod.dbc;

/** Helpers for updating a single field in DBC's serialized Majin absorption data. */
public final class MajinAbsorptionDataUtil {
  private static final String DEFAULT_DATA = "0,0,0+0";

  private MajinAbsorptionDataUtil() {}

  /**
   * Updates one comma-separated integer field while preserving the other absorption data.
   * Returns {@code null} when the stored value is malformed, so callers leave it untouched.
   */
  public static String withIntegerField(String value, int field, int replacement) {
    if (field < 0 || field > 1) {
      return null;
    }

    if (value == null || value.trim().isEmpty()) {
      value = DEFAULT_DATA;
    }

    String[] fields = value.split(",", -1);
    if (fields.length == 1 && isInteger(fields[0])) {
      fields = new String[] {fields[0], "0", "0+0"};
    } else if (fields.length == 2 && isInteger(fields[0]) && isInteger(fields[1])) {
      fields = new String[] {fields[0], fields[1], "0+0"};
    }

    if (fields.length < 3 || !isInteger(fields[0]) || !isInteger(fields[1])) {
      return null;
    }

    fields[field] = Integer.toString(replacement);
    StringBuilder result = new StringBuilder(value.length() + 12);
    for (int i = 0; i < fields.length; i++) {
      if (i > 0) {
        result.append(',');
      }
      result.append(fields[i]);
    }
    return result.toString();
  }

  private static boolean isInteger(String value) {
    if (value == null || value.isEmpty()) {
      return false;
    }
    int start = value.charAt(0) == '-' || value.charAt(0) == '+' ? 1 : 0;
    if (start == value.length()) {
      return false;
    }
    for (int i = start; i < value.length(); i++) {
      if (!Character.isDigit(value.charAt(i))) {
        return false;
      }
    }
    return true;
  }
}
