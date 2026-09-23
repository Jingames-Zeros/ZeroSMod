package org.darkoro.zerosmod.rebirth.gui;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.darkoro.zerosmod.rebirth.network.RebirthNetwork;
import org.darkoro.zerosmod.rebirth.network.RebirthRequest;
import org.darkoro.zerosmod.rebirth.network.RebirthSnapshot;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/** A local draft over a server-owned rebirth session. Saving does not confirm rebirth. */
public final class RebirthScreen extends GuiScreen {
  private static final int PANEL_WIDTH = 416, PANEL_HEIGHT = 308;
  private static final int GREEN = 0x90E080, PINK = 0xB050D0, HOT_PINK = 0xD986E8, TEXT = 0xF3F8F2,
      MUTED = 0xB2C0B1, PANEL = 0xFF111813, CARD = 0xFF18231A, CARD_BRIGHT = 0xFF203222;
  private static final int SAVE = 10, RELOAD = 11, BACK = 12, EXIT = 13, EDIT = 14, CHARACTER = 15,
      CONFIRM = 16, CANCEL = 17, DIALOG_YES = 18, DIALOG_NO = 19;
  private static final AtomicInteger REQUEST_IDS = new AtomicInteger();
  private final GuiTextField[] fields = new GuiTextField[6];
  private RebirthSnapshot snapshot;
  private float scale, originX, originY;
  private int requestId, timeout, dialog = -1;
  private boolean waiting, needsReload, serverClosed;
  private String status;
  private boolean error;

  public RebirthScreen(RebirthSnapshot snapshot) {
    this.snapshot = snapshot;
    status = snapshot.message;
    error = snapshot.error;
  }

  @Override public void initGui() {
    Keyboard.enableRepeatEvents(true);
    scale = Math.min(1F, Math.min((width - 12F) / PANEL_WIDTH, (height - 12F) / PANEL_HEIGHT));
    originX = (width - PANEL_WIDTH * scale) / 2F;
    originY = (height - PANEL_HEIGHT * scale) / 2F;
    buttonList.clear();
    if (snapshot.editing) {
      for (int i = 0; i < fields.length; i++) {
        String value = fields[i] == null ? Integer.toString(snapshot.values[i]) : fields[i].getText();
        boolean focused = fields[i] != null && fields[i].isFocused();
        fields[i] = new GuiTextField(fontRendererObj, 111 + (i / 3) * 200, 70 + (i % 3) * 39, 80, 16);
        fields[i].setMaxStringLength(11);
        fields[i].setText(value);
        fields[i].setFocused(focused);
        fields[i].setTextColor(TEXT);
        fields[i].setDisabledTextColour(MUTED);
      }
      add(SAVE, 16, 279, 118, "Save allocation", GREEN);
      add(RELOAD, 142, 279, 82, "Reload", MUTED);
      add(BACK, 232, 279, 78, "Overview", MUTED);
      add(EXIT, 318, 279, 82, "Close", MUTED);
    } else {
      add(EDIT, 24, 141, 168, "Edit allocation", GREEN);
      add(CHARACTER, 224, 141, 168, "Recreate character", PINK);
      add(CONFIRM, 16, 279, 152, "Confirm rebirth", GREEN);
      add(CANCEL, 176, 279, 124, "Cancel rebirth", HOT_PINK);
      add(EXIT, 308, 279, 92, "Close", MUTED);
      add(RELOAD, 344, 16, 56, "Reload", MUTED);
    }
    add(DIALOG_YES, 65, 197, 139, "Continue", GREEN);
    add(DIALOG_NO, 212, 197, 139, "Go back", MUTED);
    updateControls();
  }

  private void add(int id, int x, int y, int width, String label, int color) {
    buttonList.add(new RebirthButton(id, x, y, width, label, color));
  }

  private int[] values() {
    String[] text = new String[6];
    for (int i = 0; i < text.length; i++) {
      text[i] = fields[i] == null ? "" : fields[i].getText();
    }
    return RebirthDraft.parse(text, snapshot.maximum);
  }

  private boolean dirty() {
    if (!snapshot.editing || snapshot.readOnly) {
      return false;
    }
    return !Arrays.equals(snapshot.values, values());
  }

  private void updateControls() {
    boolean active = !waiting && !needsReload && dialog < 0;
    int[] values = values();
    for (Object entry : buttonList) {
      GuiButton button = (GuiButton) entry;
      button.visible = button.id >= DIALOG_YES ? dialog >= 0 : dialog < 0;
      button.enabled = active;
      switch (button.id) {
        case SAVE:
          button.enabled &= !snapshot.readOnly && snapshot.available && !snapshot.confirmed
              && dirty() && values != null && RebirthDraft.total(values) <= Integer.MAX_VALUE;
          break;
        case EDIT:
        case CHARACTER:
          button.enabled &= snapshot.available && !snapshot.confirmed;
          break;
        case CONFIRM:
          button.enabled &= snapshot.available && snapshot.changed && !snapshot.confirmed;
          break;
        case CANCEL:
          button.enabled &= snapshot.available && snapshot.changed && !snapshot.confirmed;
          break;
        case RELOAD:
        case EXIT:
          button.enabled = !waiting && dialog < 0;
          break;
        case DIALOG_YES:
        case DIALOG_NO:
          button.enabled = dialog >= 0;
          break;
        default:
          break;
      }
    }
    for (GuiTextField field : fields) {
      if (field != null) {
        field.setEnabled(active && !snapshot.readOnly && snapshot.available && !snapshot.confirmed);
      }
    }
  }

  private void request(int action) {
    requestId = REQUEST_IDS.incrementAndGet();
    waiting = true;
    timeout = 300;
    status = "Waiting for the server...";
    error = false;
    RebirthNetwork.channel.sendToServer(new RebirthRequest(snapshot.session, requestId, action,
        action == RebirthRequest.SAVE ? values() : null));
    updateControls();
  }

  public void receive(RebirthSnapshot response) {
    if (response.session != snapshot.session || response.requestId != requestId) {
      return;
    }
    waiting = false;
    needsReload = false;
    if (response.close) {
      serverClosed = true;
      mc.displayGuiScreen(null);
      return;
    }
    snapshot = response;
    status = response.message;
    error = response.error;
    Arrays.fill(fields, null);
    initGui();
  }

  @Override protected void actionPerformed(GuiButton button) {
    if (!button.enabled) {
      return;
    }
    switch (button.id) {
      case SAVE: request(RebirthRequest.SAVE); break;
      case EDIT: request(RebirthRequest.EDIT); break;
      case CHARACTER:
      case CONFIRM:
      case CANCEL:
        dialog = button.id;
        updateControls();
        break;
      case EXIT:
      case BACK:
      case RELOAD:
        if (dirty()) {
          dialog = button.id;
          updateControls();
        } else {
          navigate(button.id);
        }
        break;
      case DIALOG_NO:
        dialog = -1;
        updateControls();
        break;
      case DIALOG_YES:
        int action = dialog;
        dialog = -1;
        if (action == CHARACTER) {
          request(RebirthRequest.CHARACTER);
        } else if (action == CONFIRM) {
          request(RebirthRequest.CONFIRM);
        } else if (action == CANCEL) {
          request(RebirthRequest.CANCEL);
        } else {
          navigate(action);
        }
        break;
      default:
        break;
    }
  }

  private void navigate(int action) {
    if (action == EXIT) {
      mc.displayGuiScreen(null);
    } else {
      request(action == BACK ? RebirthRequest.OVERVIEW : RebirthRequest.REFRESH);
    }
  }

  @Override protected void keyTyped(char character, int key) {
    if (key == Keyboard.KEY_ESCAPE) {
      if (dialog >= 0) {
        dialog = -1;
        updateControls();
      } else if (!waiting) {
        if (dirty()) {
          dialog = EXIT;
          updateControls();
        } else {
          navigate(EXIT);
        }
      }
      return;
    }
    if (waiting || needsReload || dialog >= 0 || !snapshot.editing || snapshot.readOnly || !snapshot.available) {
      return;
    }
    if (key == Keyboard.KEY_TAB) {
      int focus = -1;
      for (int i = 0; i < fields.length; i++) {
        if (fields[i].isFocused()) {
          focus = i;
        }
        fields[i].setFocused(false);
      }
      int next = focus < 0 ? (isShiftKeyDown() ? 5 : 0) : (focus + (isShiftKeyDown() ? 5 : 1)) % 6;
      fields[next].setFocused(true);
      return;
    }
    for (GuiTextField field : fields) {
      if (field.textboxKeyTyped(character, key)) {
        error = values() == null;
        status = error ? "Enter whole numbers from 10 to " + snapshot.maximum + "."
            : dirty() ? "Unsaved allocation. Save your changes before returning to the overview."
                : "No unsaved changes.";
        updateControls();
        return;
      }
    }
  }

  @Override protected void mouseClicked(int mouseX, int mouseY, int button) {
    int x = (int) ((mouseX - originX) / scale), y = (int) ((mouseY - originY) / scale);
    super.mouseClicked(x, y, button);
    if (dialog < 0 && !waiting && !needsReload && snapshot.editing && !snapshot.readOnly) {
      for (GuiTextField field : fields) {
        field.mouseClicked(x, y, button);
      }
    }
  }

  @Override public void updateScreen() {
    for (GuiTextField field : fields) {
      if (field != null) {
        field.updateCursorCounter();
      }
    }
    if (waiting && --timeout <= 0) {
      waiting = false;
      needsReload = true;
      requestId = REQUEST_IDS.incrementAndGet();
      status = "No reply. Reload to verify the server state, or close and reopen /rebirth.";
      error = true;
      updateControls();
    }
  }

  @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    GL11.glPushMatrix();
    GL11.glTranslatef(originX, originY, 0);
    GL11.glScalef(scale, scale, 1);
    int x = (int) ((mouseX - originX) / scale), y = (int) ((mouseY - originY) / scale);
    drawRect(-3, -3, PANEL_WIDTH + 3, PANEL_HEIGHT + 3, 0x70000000);
    drawRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT, PANEL);
    drawRect(0, 0, PANEL_WIDTH, 2, 0xFF000000 | GREEN);
    drawRect(16, 48, 400, 49, 0xFF314033);
    text("REBIRTH", 16, 13, GREEN);
    text(snapshot.editing ? "ATTRIBUTE ALLOCATION" : "CHARACTER WORKSHOP", 16, 29, TEXT);
    if (snapshot.editing) {
      drawEditor();
    } else {
      drawOverview();
    }
    drawRect(16, 236, 400, 268, error ? 0xFF2F1C34 : 0xFF172719);
    drawRect(16, 236, 18, 268, error ? 0xFF000000 | HOT_PINK : 0xFF000000 | GREEN);
    fontRendererObj.drawSplitString(status, 24, 243, 368, error ? HOT_PINK : MUTED);
    if (dialog >= 0) {
      drawDialog();
    }
    super.drawScreen(x, y, partialTicks);
    GL11.glPopMatrix();
    GL11.glColor4f(1, 1, 1, 1);
  }

  private void drawOverview() {
    card(16, 60, "01  REALLOCATE", "Redistribute your six attributes. Keep the same total points; shape a new build.", GREEN);
    card(216, 60, "02  RECREATE", "Recreate your DBC character, then return here to review and confirm your rebirth.", PINK);
    drawStatStrip(16, 171);
    drawRect(16, 218, 400, 229, 0xFF1A2B1D);
    String state = snapshot.confirmed ? "REBIRTH CONFIRMED" : !snapshot.available ? "COOLDOWN ACTIVE"
        : snapshot.changed ? "REBIRTH IN PROGRESS" : "READY TO REBIRTH";
    text(state, 24, 220, snapshot.available ? GREEN : PINK);
    String detail;
    if (!snapshot.available) {
      detail = snapshot.cooldown;
    } else if (snapshot.changed) {
      detail = balance(snapshot.remaining) + (snapshot.usedCharacter ? "  |  Character recreated" : "  |  Stats adjusted");
    } else {
      detail = snapshot.operator ? "Operator: no cooldown after rebirth." : "Cooldown after rebirth: " + days() + " days.";
    }
    text(fontRendererObj.trimStringToWidth(detail, 188), 204, 220, MUTED);
  }

  private void card(int x, int y, String title, String description, int accent) {
    drawRect(x, y, x + 184, y + 98, CARD);
    drawRect(x, y, x + 184, y + 2, 0xFF000000 | accent);
    text(title, x + 8, y + 9, accent);
    fontRendererObj.drawSplitString(description, x + 8, y + 28, 168, MUTED);
  }

  private void drawStatStrip(int x, int y) {
    drawRect(x, y, x + 384, y + 38, CARD_BRIGHT);
    int cell = 64;
    for (int i = 0; i < RebirthDraft.COUNT; i++) {
      int left = x + i * cell;
      if (i > 0) {
        drawRect(left, y + 7, left + 1, y + 31, 0xFF314033);
      }
      text(shortLabel(i), left + 8, y + 7, MUTED);
      text(Integer.toString(snapshot.values[i]), left + 8, y + 21, TEXT);
    }
  }

  private void drawEditor() {
    text(snapshot.readOnly ? "READ ONLY" : "10 - " + snapshot.maximum + " per stat", 224, 29, MUTED);
    for (int i = 0; i < fields.length; i++) {
      int x = 16 + (i / 3) * 200, y = 60 + (i % 3) * 39;
      drawRect(x, y, x + 184, y + 34, CARD);
      boolean valid = RebirthDraft.parse(new String[] {fields[i].getText(), "10", "10", "10", "10", "10"}, snapshot.maximum) != null;
      drawRect(x, y, x + 2, y + 34, 0xFF000000 | (valid ? GREEN : HOT_PINK));
      text(RebirthDraft.LABELS[i], x + 8, y + 6, TEXT);
      text(fontRendererObj.trimStringToWidth("Saved " + snapshot.values[i], 82), x + 8, y + 21, MUTED);
      fields[i].drawTextBox();
    }
    int[] values = values();
    long remaining = values == null ? snapshot.remaining : RebirthDraft.remaining(snapshot.values, snapshot.remaining, values);
    long budget = RebirthDraft.total(snapshot.values) + snapshot.remaining;
    long draft = values == null ? -1 : RebirthDraft.total(values);
    drawRect(16, 180, 400, 229, 0xFF1A2B1D);
    text(values == null ? "CHECK YOUR VALUES" : balance(remaining), 24, 187,
        values == null || remaining < 0 ? HOT_PINK : remaining == 0 ? GREEN : PINK);
    text("Saved total", 24, 207, MUTED);
    text(Long.toString(RebirthDraft.total(snapshot.values)), 96, 207, TEXT);
    text("Draft total", 174, 207, MUTED);
    text(values == null ? "--" : Long.toString(draft), 240, 207, values == null ? HOT_PINK : TEXT);
    text("Budget", 302, 207, MUTED);
    text(Long.toString(budget), 350, 207, TEXT);
    String hint = snapshot.readOnly ? "Saved values only. Use Overview to manage your rebirth."
        : "Save allocation first. Confirm rebirth from the overview.";
    text(hint, 24, 220, MUTED);
  }

  private String balance(long remaining) {
    return remaining == 0 ? "ALL POINTS ALLOCATED" : remaining > 0 ? remaining + " POINTS REMAINING"
        : (-remaining) + " POINTS OVER BUDGET";
  }

  private String days() {
    return new DecimalFormat("0.##").format(snapshot.cooldownDays);
  }

  private String shortLabel(int index) {
    switch (index) {
      case 0:
        return "STR";
      case 1:
        return "DEX";
      case 2:
        return "CON";
      case 3:
        return "WIL";
      case 4:
        return "MND";
      case 5:
        return "SPI";
      default:
        return "";
    }
  }

  private void drawDialog() {
    drawRect(0, 2, PANEL_WIDTH, PANEL_HEIGHT, 0xD0111813);
    drawRect(49, 86, 367, 233, 0xFF1F2F21);
    drawRect(49, 86, 367, 88, 0xFF000000 | (dialog == CANCEL ? HOT_PINK : PINK));
    String title, detail;
    if (dialog == CHARACTER) {
      title = "Recreate your character?";
      detail = "This opens DBC character creation. Your rebirth backup and allocated points are kept. Finish creating your character, then use /rebirth to continue.";
    } else if (dialog == CONFIRM) {
      title = "Confirm this rebirth?";
      detail = "Apply the saved allocation and finish rebirth. " + (snapshot.operator ? "Your operator cooldown exemption applies."
          : "This begins your " + days() + " day cooldown.") + " The original rebirth backup will be cleared.";
    } else if (dialog == CANCEL) {
      title = "Cancel your rebirth?";
      detail = "Restore your original character and discard the pending rebirth, including saved stat changes. This does not start a cooldown.";
    } else {
      title = "Discard unsaved edits?";
      detail = "Only the values you typed on this screen will be discarded. Your saved allocation and ongoing rebirth are kept.";
    }
    text(title, 65, 99, TEXT);
    fontRendererObj.drawSplitString(detail, 65, 122, 286, MUTED);
  }

  private void text(String value, int x, int y, int color) {
    fontRendererObj.drawString(value, x, y, color);
  }

  @Override public boolean doesGuiPauseGame() {
    return false;
  }

  @Override public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
    if (!serverClosed && mc.thePlayer != null && mc.getNetHandler() != null) {
      RebirthNetwork.channel.sendToServer(new RebirthRequest(snapshot.session, REQUEST_IDS.incrementAndGet(), RebirthRequest.CLOSE, null));
    }
  }
}
