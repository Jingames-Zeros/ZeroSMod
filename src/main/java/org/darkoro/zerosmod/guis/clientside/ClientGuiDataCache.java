package org.darkoro.zerosmod.guis.clientside;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientGuiDataCache {

  public static final class GuiData {
    public final String title;
    public final boolean isEditable;
    public final boolean isInventory;

    public GuiData(String title, boolean isEditable, boolean isInventory) {
      this.title = title;
      this.isEditable = isEditable;
      this.isInventory = isInventory;
    }
  }

  public static final Map<Integer, GuiData> guiData = new ConcurrentHashMap<>();

  public static void store(int ctxId, GuiData data) {
    guiData.put(ctxId, data);
  }

  public static GuiData get(int ctxId) {
    return guiData.get(ctxId);
  }

  public static void remove(int ctxId) {
    guiData.remove(ctxId);
  }

}
