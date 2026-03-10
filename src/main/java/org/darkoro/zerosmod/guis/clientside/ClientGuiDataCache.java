package org.darkoro.zerosmod.guis.clientside;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientGuiDataCache {

  public static final Map<Integer, String> guiTitles = new ConcurrentHashMap<>();

  public static void storeTitle(int ctxId, String title) {
    guiTitles.put(ctxId, title);
  }

  public static String getTitle(int ctxId) {
    return guiTitles.get(ctxId);
  }

  public static void removeTitle(int ctxId) {
    guiTitles.remove(ctxId);
  }

}
