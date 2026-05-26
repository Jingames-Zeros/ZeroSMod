package org.darkoro.zerosmod.client;

import JinRyuu.JRMCore.JRMCoreH;

/**
 * Runtime GUI-side JRMCore race patching.
 * Does not remove backend race IDs; only changes what the GUI allows/displays.
 */
public final class JRMCoreRacePatch {

  private static boolean applied = false;

  private JRMCoreRacePatch() {}

  public static void apply() {
    if (applied) {
      return;
    }

    try {
      if (JRMCoreH.Races != null && JRMCoreH.Races.length > 2) {
        JRMCoreH.Races[2] = "Saiyan";
      }

      if (JRMCoreH.RaceAllow != null && JRMCoreH.RaceAllow.length > 1) {
        JRMCoreH.RaceAllow[1] = "DISABLED";
      }

      applied = true;
      System.out.println("[ZeroSMod] Applied JRMCore race GUI patch.");
    } catch (Throwable t) {
      System.err.println("[ZeroSMod] Failed to apply JRMCore race GUI patch.");
      t.printStackTrace();
    }
  }
}
