package org.darkoro.zerosmod.guis.clientside;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.config.RaceStatConfig;
import org.darkoro.zerosmod.network.RaceStatRequest;
import org.darkoro.zerosmod.network.RaceStatResponse;
import org.lwjgl.input.Keyboard;

public final class RaceStatEditorGui extends GuiScreen implements GuiYesNoCallback {
  private static final AtomicInteger REQUEST_IDS = new AtomicInteger();
  private static final String[] LABELS = {"Melee", "Defense", "Body", "Stamina", "Ki power", "Ki pool",
      "Max skills", "Speed", "Body regen", "Stamina regen", "Ki regen", "Fly speed"};
  private final GuiTextField[] fields = new GuiTextField[RaceStatConfig.STATS.length];
  private RaceStatConfig.Snapshot snapshot;
  private int race;
  private int classId;
  private int requestId;
  private int timeout;
  private int left;
  private int top;
  private boolean waiting;
  private boolean error;
  private String status;
  private GuiButton raceButton;
  private GuiButton classButton;
  private GuiButton saveButton;
  private GuiButton reloadButton;
  private GuiButton closeButton;

  public RaceStatEditorGui(RaceStatResponse response) {
    race = response.race;
    classId = response.classId;
    snapshot = response.snapshot;
    status = response.message;
    error = snapshot == null;
  }

  @Override public void initGui() {
    Keyboard.enableRepeatEvents(true);
    left = (width - 316) / 2;
    top = (height - 238) / 2;
    buttonList.clear();
    raceButton = new GuiButton(0, left + 4, top + 30, 152, 20, "");
    classButton = new GuiButton(1, left + 160, top + 30, 152, 20, "");
    saveButton = new GuiButton(2, left + 4, top + 213, 96, 20, "Save to config");
    reloadButton = new GuiButton(3, left + 104, top + 213, 108, 20, "Reload / discard");
    closeButton = new GuiButton(4, left + 216, top + 213, 96, 20, "Close");
    buttonList.add(raceButton);
    buttonList.add(classButton);
    buttonList.add(saveButton);
    buttonList.add(reloadButton);
    buttonList.add(closeButton);
    for (int i = 0; i < fields.length; i++) {
      String draft = fields[i] != null ? fields[i].getText()
          : snapshot == null ? "" : Float.toString(snapshot.values[i]);
      fields[i] = new GuiTextField(fontRendererObj, left + 94 + (i / 6) * 156,
          top + 59 + (i % 6) * 20, 60, 16);
      fields[i].setMaxStringLength(32);
      fields[i].setText(draft);
    }
    updateControls();
  }

  public void receive(RaceStatResponse response) {
    if (response.requestId != requestId || response.race != race || response.classId != classId) return;
    waiting = false;
    status = response.message;
    error = response.snapshot == null;
    if (response.snapshot != null) {
      snapshot = response.snapshot;
      for (int i = 0; i < fields.length; i++) fields[i].setText(Float.toString(snapshot.values[i]));
    }
    updateControls();
  }

  private float[] values() {
    float[] values = new float[fields.length];
    for (int i = 0; i < fields.length; i++) {
      try {
        values[i] = Float.parseFloat(fields[i].getText().trim());
      } catch (NumberFormatException e) {
        return null;
      }
      if (!RaceStatConfig.validValue(values[i])) return null;
    }
    return values;
  }

  private boolean dirty() {
    if (snapshot == null) return false;
    float[] values = values();
    if (values == null) return true;
    for (int i = 0; i < values.length; i++) {
      if (Float.compare(values[i], snapshot.values[i]) != 0) return true;
    }
    return false;
  }

  private void updateControls() {
    boolean dirty = dirty();
    raceButton.displayString = "Race: " + RaceStatConfig.RACES[race] + " >";
    classButton.displayString = RaceStatConfig.CLASSES[classId] + " >";
    raceButton.enabled = classButton.enabled = !waiting && !dirty;
    saveButton.enabled = !waiting && snapshot != null && dirty && values() != null;
    reloadButton.enabled = closeButton.enabled = !waiting;
    for (GuiTextField field : fields) field.setEnabled(!waiting && snapshot != null);
  }

  private void request(boolean save) {
    float[] values = save ? values() : null;
    if (save && (snapshot == null || values == null)) return;
    requestId = REQUEST_IDS.incrementAndGet();
    waiting = true;
    timeout = 300;
    error = false;
    status = save ? "Saving to server config..." : "Loading saved values...";
    byte[] revision = save ? snapshot.revision : null;
    if (!save) {
      snapshot = null;
      for (GuiTextField field : fields) field.setText("");
    }
    ZeroSMod.network.sendToServer(new RaceStatRequest(requestId, race, classId, revision, values));
    updateControls();
  }

  @Override protected void actionPerformed(GuiButton button) {
    if (!button.enabled || waiting) return;
    switch (button.id) {
      case 0:
        race = (race + 1) % RaceStatConfig.RACES.length;
        request(false);
        break;
      case 1:
        classId = (classId + 1) % RaceStatConfig.CLASSES.length;
        request(false);
        break;
      case 2:
        request(true);
        break;
      case 3:
        request(false);
        break;
      case 4:
        close();
        break;
      default:
        break;
    }
  }

  private void close() {
    if (waiting) return;
    if (dirty()) {
      mc.displayGuiScreen(new GuiYesNo(this, "Discard unsaved stat changes?", "Saved configs will be kept.", 0));
    } else {
      mc.displayGuiScreen(null);
    }
  }

  @Override public void confirmClicked(boolean confirmed, int id) {
    mc.displayGuiScreen(confirmed ? null : this);
  }

  @Override protected void keyTyped(char typedChar, int keyCode) {
    if (keyCode == Keyboard.KEY_ESCAPE) { close(); return; }
    if (waiting || snapshot == null) return;
    if (keyCode == Keyboard.KEY_TAB) {
      int selected = -1;
      for (int i = 0; i < fields.length; i++) {
        if (fields[i].isFocused()) selected = i;
        fields[i].setFocused(false);
      }
      int direction = isShiftKeyDown() ? -1 : 1;
      fields[(selected + direction + fields.length) % fields.length].setFocused(true);
      return;
    }
    for (GuiTextField field : fields) {
      if (field.textboxKeyTyped(typedChar, keyCode)) {
        error = values() == null;
        status = error ? "Enter numbers from -100000 to 100000."
            : dirty() ? "Unsaved changes. Save or discard before switching." : "No unsaved changes.";
        updateControls();
        return;
      }
    }
  }

  @Override protected void mouseClicked(int mouseX, int mouseY, int button) {
    super.mouseClicked(mouseX, mouseY, button);
    for (GuiTextField field : fields) field.mouseClicked(mouseX, mouseY, button);
  }

  @Override public void updateScreen() {
    for (GuiTextField field : fields) field.updateCursorCounter();
    if (waiting && --timeout <= 0) {
      waiting = false;
      // A late reply must not overwrite new edits; reload before allowing another save.
      requestId = REQUEST_IDS.incrementAndGet();
      snapshot = null;
      error = true;
      status = "No reply. Reload to verify the saved values.";
      updateControls();
    }
  }

  @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    drawRect(left, top, left + 316, top + 238, 0xCC202530);
    drawCenteredString(fontRendererObj, "Race Stat Multipliers", width / 2, top + 5, 0xFFFFFF);
    drawCenteredString(fontRendererObj, "Server config - restart required to apply", width / 2, top + 17, 0xFFD080);
    for (int i = 0; i < fields.length; i++) {
      drawString(fontRendererObj, LABELS[i], left + 5 + (i / 6) * 156, top + 63 + (i % 6) * 20, 0xDDDDDD);
      fields[i].drawTextBox();
    }
    fontRendererObj.drawSplitString(status, left + 5, top + 184, 306, error ? 0xFF8888 : 0xAADDBB);
    super.drawScreen(mouseX, mouseY, partialTicks);
  }

  @Override public boolean doesGuiPauseGame() { return false; }
  @Override public void onGuiClosed() { Keyboard.enableRepeatEvents(false); }
}
