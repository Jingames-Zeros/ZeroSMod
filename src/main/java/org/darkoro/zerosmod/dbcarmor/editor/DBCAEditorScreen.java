package org.darkoro.zerosmod.dbcarmor.editor;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public final class DBCAEditorScreen extends GuiScreen {
  private static final int PANEL_WIDTH = 760;
  private static final int PANEL_HEIGHT = 420;
  private static final int GREEN = 0x90E080;
  private static final int PINK = 0xB050D0;
  private static final int HOT_PINK = 0xD986E8;
  private static final int TEXT = 0xF3F8F2;
  private static final int MUTED = 0xB2C0B1;
  private static final int PANEL = 0xFF111813;
  private static final int CARD = 0xFF18231A;
  private static final int CARD_BRIGHT = 0xFF203222;
  private static final int SAVE = 1;
  private static final int APPLY = 2;
  private static final int SAVE_ALL = 3;
  private static final int RELOAD = 4;
  private static final int CLOSE = 5;
  private static final int CREATE_SET = 6;
  private static final int CREATE_PIECE = 7;
  private static final int POTION = 8;
  private static final int CREATE_BONUS = 9;
  private static final int SAVE_BONUS = 10;
  private static final int SHOW_PIECES = 11;
  private static final int SHOW_BONUSES = 12;
  private static final int COLOR_BASE = 30;
  private static final int SET_X = 16;
  private static final int PIECE_X = 174;
  private static final int LIST_Y = 55;
  private static final int LIST_W = 138;
  private static final int PIECE_W = 142;
  private static final int LIST_H = 307;
  private static final int SEARCH_Y = 92;
  private static final int ROW_Y = 121;
  private static final int ROW_H = 20;
  private static final int ROW_STEP = 24;
  private static final int SET_ROWS = 10;
  private static final int PIECE_ROWS = 10;
  private static final int MODE_PIECES = 0;
  private static final int MODE_BONUSES = 1;
  private static final AtomicInteger REQUEST_IDS = new AtomicInteger();
  private static final String[] COLOR_LABELS = {"Stats color", "Level color", "Potion label color",
      "Potion value color", "Durability color", "Max durability color"};
  private static final String[] POTION_NAMES = {"None", "Speed", "Slowness", "Haste",
      "Mining Fatigue", "Strength", "Instant Health", "Instant Damage", "Jump Boost", "Nausea",
      "Regeneration", "Resistance", "Fire Resistance", "Water Breathing", "Invisibility",
      "Blindness", "Night Vision", "Hunger", "Weakness", "Poison", "Wither", "Health Boost",
      "Absorption", "Saturation"};
  private static final String[] VALID_COLORS = {"dark_red", "red", "gold", "yellow", "dark_green",
      "green", "aqua", "dark_aqua", "dark_blue", "blue", "light_purple", "dark_purple",
      "white", "gray", "dark_gray", "black", "obfuscated", "bold", "strikethrough",
      "underline", "italic"};

  private final GuiTextField[] fields = new GuiTextField[24];
  private final GuiTextField[] bonusFields = new GuiTextField[10];
  private final String[] colorValues = new String[6];
  private int potionValue;
  private GuiTextField setSearchField;
  private GuiTextField pieceSearchField;
  private GuiTextField newSetField;
  private GuiTextField newPieceField;
  private DBCAEditorSnapshot snapshot;
  private float scale;
  private float originX;
  private float originY;
  private int setScroll;
  private int pieceScroll;
  private int listMode = MODE_PIECES;
  private int openColor = -1;
  private boolean potionOpen;
  private int guiMouseX;
  private int guiMouseY;
  private int requestId;
  private int timeout;
  private boolean waiting;
  private boolean serverClosed;
  private String status;
  private boolean error;

  public DBCAEditorScreen(DBCAEditorSnapshot snapshot) {
    this.snapshot = snapshot;
    status = snapshot.message;
    error = snapshot.error;
  }

  @Override
  public void initGui() {
    Keyboard.enableRepeatEvents(true);
    scale = Math.min(1F, Math.min((width - 12F) / PANEL_WIDTH, (height - 12F) / PANEL_HEIGHT));
    originX = (width - PANEL_WIDTH * scale) / 2F;
    originY = (height - PANEL_HEIGHT * scale) / 2F;
    String setSearch = setSearchField == null ? "" : setSearchField.getText();
    String pieceSearch = pieceSearchField == null ? "" : pieceSearchField.getText();
    buttonList.clear();

    add(SHOW_PIECES, 174, 20, 64, 18, "Pieces", listMode == MODE_PIECES ? GREEN : MUTED);
    add(SHOW_BONUSES, 242, 20, 74, 18, "Bonuses", listMode == MODE_BONUSES ? GREEN : MUTED);
    setSearchField = field(SET_X + 8, SEARCH_Y, LIST_W - 16, setSearch);
    pieceSearchField = field(PIECE_X + 8, SEARCH_Y, PIECE_W - 16, pieceSearch);
    newSetField = field(SET_X, 389, 92, "");
    newPieceField = field(PIECE_X, 389, 92, "");
    add(CREATE_SET, 114, 386, 40, 20, "New", PINK);
    add(CREATE_PIECE, 272, 386, 44, 20, "New", PINK);
    add(CREATE_BONUS, 272, 386, 44, 20, "New", PINK);

    createPieceFields();
    createBonusFields();
    addDropdown(POTION, 506, 141, 112, 18, dropdownText(potionName(potionValue), 14), MUTED);
    for (int i = 0; i < colorValues.length; i++) {
      addDropdown(COLOR_BASE + i, colorX(i), colorY(i), 96, 18,
          dropdownText(colorValues[i], 10), MUTED);
    }
    add(SAVE, 332, 386, 94, 20, "Save piece", GREEN);
    add(SAVE_BONUS, 332, 386, 94, 20, "Save bonus", GREEN);
    add(APPLY, 434, 386, 104, 20, "Apply held", PINK);
    add(SAVE_ALL, 546, 386, 82, 20, "Save all", MUTED);
    add(RELOAD, 636, 386, 54, 20, "Reload", MUTED);
    add(CLOSE, 698, 386, 46, 20, "Close", MUTED);
    syncScrolls();
    updateControls();
  }

  private DBCAEditorButton add(int id, int x, int y, int width, int height, String label, int color) {
    DBCAEditorButton button = new DBCAEditorButton(id, x, y, width, height, label, color);
    buttonList.add(button);
    return button;
  }

  private DBCAEditorButton addDropdown(int id, int x, int y, int width, int height, String label,
      int color) {
    DBCAEditorButton button = new DBCAEditorButton(id, x, y, width, height, label, color, true);
    buttonList.add(button);
    return button;
  }

  private GuiTextField field(int x, int y, int width, String value) {
    GuiTextField field = new GuiTextField(fontRendererObj, x, y, width, 15);
    field.setMaxStringLength(96);
    field.setText(value == null ? "" : value);
    field.setTextColor(TEXT);
    field.setDisabledTextColour(MUTED);
    return field;
  }

  private void createPieceFields() {
    fields[0] = field(548, 63, 176, colorOut(snapshot.pieceName));
    for (int i = 0; i < 5; i++) {
      fields[i + 1] = field(342 + i * 62, 101, 46,
          snapshot.stats.length > i ? snapshot.stats[i] : "+0");
    }
    fields[6] = field(342, 141, 74, Integer.toString(snapshot.level));
    fields[7] = field(424, 141, 74, Integer.toString(snapshot.durability));
    potionValue = clampPotion(snapshot.potionId);
    fields[8] = null;
    fields[9] = field(626, 141, 74, Integer.toString(snapshot.potionStrength));
    for (int i = 0; i < colorValues.length; i++) {
      colorValues[i] = snapshot.colors.length > i ? snapshot.colors[i] : "white";
      fields[i + 10] = null;
    }
    for (int i = 0; i < 8; i++) {
      fields[i + 16] = field(354 + (i / 4) * 186, 275 + (i % 4) * 18, 184,
          snapshot.lore.length > i ? colorOut(snapshot.lore[i]) : "");
    }
  }

  private void updateControls() {
    boolean hasPiece = snapshot.selectedPieceId >= 0;
    boolean hasBonus = snapshot.selectedBonusId >= 0;
    for (Object entry : buttonList) {
      GuiButton button = (GuiButton) entry;
      button.visible = true;
      if (button.id == SAVE) {
        button.visible = listMode == MODE_PIECES;
        button.enabled = !waiting && hasPiece;
      } else if (button.id == SAVE_BONUS) {
        button.visible = listMode == MODE_BONUSES;
        button.enabled = !waiting && hasBonus;
      } else if (button.id == APPLY) {
        button.visible = listMode == MODE_PIECES;
        button.enabled = !waiting && hasPiece;
      }
      else if (button.id == SAVE_ALL || button.id == RELOAD || button.id == CLOSE) button.enabled = !waiting;
      else if (button.id == CREATE_SET) button.enabled = !waiting;
      else if (button.id == CREATE_PIECE) {
        button.visible = listMode == MODE_PIECES;
        button.enabled = !waiting && !snapshot.selectedSet.isEmpty();
      } else if (button.id == CREATE_BONUS) {
        button.visible = listMode == MODE_BONUSES;
        button.enabled = !waiting && !snapshot.selectedSet.isEmpty();
      } else if (button.id == SHOW_PIECES || button.id == SHOW_BONUSES) {
        button.enabled = !waiting;
      } else if (button.id == POTION) {
        button.visible = listMode == MODE_PIECES;
        button.enabled = !waiting && hasPiece;
      }
      else if (button.id >= COLOR_BASE && button.id < COLOR_BASE + colorValues.length) {
        button.visible = listMode == MODE_PIECES;
        button.enabled = !waiting && hasPiece;
      }
    }
    for (GuiTextField field : fields) if (field != null) field.setEnabled(!waiting && hasPiece
        && listMode == MODE_PIECES);
    for (GuiTextField field : bonusFields) if (field != null) field.setEnabled(!waiting && hasBonus
        && listMode == MODE_BONUSES);
    setSearchField.setEnabled(!waiting);
    pieceSearchField.setEnabled(!waiting);
    newSetField.setEnabled(!waiting);
    newPieceField.setEnabled(!waiting && !snapshot.selectedSet.isEmpty());
  }

  public void receive(DBCAEditorSnapshot response) {
    if (response.requestId != requestId) return;
    waiting = false;
    snapshot = response;
    status = response.message;
    error = response.error;
    syncScrollsToSelection();
    initGui();
  }

  private void request(int action, String setName, int pieceId, String text, String[] values) {
    requestId = REQUEST_IDS.incrementAndGet();
    waiting = true;
    timeout = 300;
    status = "Waiting for the server...";
    error = false;
    DBCAEditorNetwork.channel.sendToServer(new DBCAEditorRequest(requestId, action, setName, pieceId,
        snapshot.selectedBonusId, text, values));
    updateControls();
  }

  private void requestBonus(int action, String setName, int bonusId, String text, String[] values) {
    requestId = REQUEST_IDS.incrementAndGet();
    waiting = true;
    timeout = 300;
    status = "Waiting for the server...";
    error = false;
    DBCAEditorNetwork.channel.sendToServer(new DBCAEditorRequest(requestId, action, setName,
        snapshot.selectedPieceId, bonusId, text, values));
    updateControls();
  }

  private String[] values() {
    String[] values = new String[fields.length];
    for (int i = 0; i < fields.length; i++) {
      if (i == 8) values[i] = Integer.toString(potionValue);
      else
      if (i >= 10 && i < 16) values[i] = colorValues[i - 10];
      else values[i] = fields[i] == null ? "" : fields[i].getText();
    }
    return values;
  }

  private String[] bonusValues() {
    String[] values = new String[bonusFields.length];
    for (int i = 0; i < bonusFields.length; i++) {
      values[i] = bonusFields[i] == null ? "" : bonusFields[i].getText();
    }
    return values;
  }

  @Override
  protected void actionPerformed(GuiButton button) {
    if (!button.enabled) return;
    if (button.id == SHOW_PIECES) {
      listMode = MODE_PIECES;
      pieceScroll = 0;
      openColor = -1;
      potionOpen = false;
      syncScrolls();
      initGui();
    } else if (button.id == SHOW_BONUSES) {
      listMode = MODE_BONUSES;
      pieceScroll = 0;
      openColor = -1;
      potionOpen = false;
      syncScrolls();
      initGui();
    } else if (button.id >= COLOR_BASE && button.id < COLOR_BASE + colorValues.length) {
      openColor = openColor == button.id - COLOR_BASE ? -1 : button.id - COLOR_BASE;
      potionOpen = false;
    } else if (button.id == POTION) {
      potionOpen = !potionOpen;
      openColor = -1;
    } else if (button.id == CREATE_SET) {
      request(DBCAEditorRequest.CREATE_SET, snapshot.selectedSet, snapshot.selectedPieceId,
          newSetField.getText(), null);
    } else if (button.id == CREATE_PIECE) {
      request(DBCAEditorRequest.CREATE_PIECE, snapshot.selectedSet, snapshot.selectedPieceId,
          newPieceField.getText(), null);
    } else if (button.id == CREATE_BONUS) {
      requestBonus(DBCAEditorRequest.CREATE_BONUS, snapshot.selectedSet, snapshot.selectedBonusId,
          newPieceField.getText(), null);
    } else if (button.id == SAVE) {
      request(DBCAEditorRequest.SAVE_PIECE, snapshot.selectedSet, snapshot.selectedPieceId, "", values());
    } else if (button.id == SAVE_BONUS) {
      requestBonus(DBCAEditorRequest.SAVE_BONUS, snapshot.selectedSet, snapshot.selectedBonusId, "",
          bonusValues());
    } else if (button.id == APPLY) {
      request(DBCAEditorRequest.APPLY_HELD, snapshot.selectedSet, snapshot.selectedPieceId, "", null);
    } else if (button.id == SAVE_ALL) {
      request(DBCAEditorRequest.SAVE_ALL, snapshot.selectedSet, snapshot.selectedPieceId, "",
          listMode == MODE_PIECES ? values() : bonusValues());
    } else if (button.id == RELOAD) {
      setScroll = 0;
      pieceScroll = 0;
      request(DBCAEditorRequest.RELOAD, snapshot.selectedSet, snapshot.selectedPieceId, "", null);
    } else if (button.id == CLOSE) {
      serverClosed = true;
      mc.displayGuiScreen(null);
    }
  }

  @Override
  protected void keyTyped(char character, int key) {
    if (key == Keyboard.KEY_ESCAPE) {
      if (openColor >= 0) {
        openColor = -1;
      } else if (potionOpen) {
        potionOpen = false;
      } else {
        serverClosed = true;
        mc.displayGuiScreen(null);
      }
      return;
    }
    if (waiting) return;
    if (setSearchField.textboxKeyTyped(character, key)) {
      setScroll = 0;
      syncScrolls();
      return;
    }
    if (pieceSearchField.textboxKeyTyped(character, key)) {
      pieceScroll = 0;
      syncScrolls();
      return;
    }
    if (newSetField.textboxKeyTyped(character, key) || newPieceField.textboxKeyTyped(character, key)) {
      return;
    }
    if (listMode == MODE_PIECES) {
      for (GuiTextField field : fields) {
        if (field != null && field.textboxKeyTyped(character, key)) return;
      }
    } else {
      for (GuiTextField field : bonusFields) {
        if (field != null && field.textboxKeyTyped(character, key)) return;
      }
    }
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int button) {
    int x = (int) ((mouseX - originX) / scale);
    int y = (int) ((mouseY - originY) / scale);
    if (handlePotionDropdownClick(x, y)) return;
    if (handleColorDropdownClick(x, y)) return;
    if (potionOpen) potionOpen = false;
    if (openColor >= 0) openColor = -1;
    if (handleListClick(x, y)) return;
    super.mouseClicked(x, y, button);
    if (waiting) return;
    setSearchField.mouseClicked(x, y, button);
    pieceSearchField.mouseClicked(x, y, button);
    newSetField.mouseClicked(x, y, button);
    newPieceField.mouseClicked(x, y, button);
    if (listMode == MODE_PIECES) {
      for (GuiTextField field : fields) {
        if (field != null) field.mouseClicked(x, y, button);
      }
    } else {
      for (GuiTextField field : bonusFields) {
        if (field != null) field.mouseClicked(x, y, button);
      }
    }
  }

  @Override
  public void handleMouseInput() {
    super.handleMouseInput();
    int wheel = Mouse.getEventDWheel();
    if (wheel == 0 || waiting) return;
    int mx = Mouse.getEventX() * width / mc.displayWidth;
    int my = height - Mouse.getEventY() * height / mc.displayHeight - 1;
    int x = (int) ((mx - originX) / scale);
    int y = (int) ((my - originY) / scale);
    int amount = wheel > 0 ? -1 : 1;
    if (inside(x, y, SET_X, ROW_Y, LIST_W, SET_ROWS * ROW_STEP)) {
      setScroll += amount;
      syncScrolls();
    } else if (inside(x, y, PIECE_X, ROW_Y, PIECE_W, PIECE_ROWS * ROW_STEP)) {
      pieceScroll += amount;
      syncScrolls();
    }
  }

  @Override
  public void updateScreen() {
    setSearchField.updateCursorCounter();
    pieceSearchField.updateCursorCounter();
    newSetField.updateCursorCounter();
    newPieceField.updateCursorCounter();
    if (listMode == MODE_PIECES) {
      for (GuiTextField field : fields) if (field != null) field.updateCursorCounter();
    } else {
      for (GuiTextField field : bonusFields) if (field != null) field.updateCursorCounter();
    }
    if (waiting && --timeout <= 0) {
      waiting = false;
      status = "No reply from the server. Reload or reopen /dbca edit.";
      error = true;
      updateControls();
    }
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    GL11.glPushMatrix();
    GL11.glTranslatef(originX, originY, 0);
    GL11.glScalef(scale, scale, 1);
    int x = (int) ((mouseX - originX) / scale);
    int y = (int) ((mouseY - originY) / scale);
    guiMouseX = x;
    guiMouseY = y;
    drawRect(-3, -3, PANEL_WIDTH + 3, PANEL_HEIGHT + 3, 0x70000000);
    drawRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT, PANEL);
    drawRect(0, 0, PANEL_WIDTH, 2, 0xFF000000 | GREEN);
    drawRect(16, 48, 744, 49, 0xFF314033);
    text("DBC ARMOUR", 16, 13, GREEN);
    text("SET AND ARMOR PIECE EDITOR", 16, 29, TEXT);
    drawLists();
    drawEditor();
    drawStatus();
    super.drawScreen(x, y, partialTicks);
    drawFields();
    drawPotionDropdown();
    drawColorDropdown();
    GL11.glPopMatrix();
    GL11.glColor4f(1, 1, 1, 1);
  }

  private boolean handleListClick(int x, int y) {
    int[] setIndices = filteredSetIndices();
    for (int row = 0; row < SET_ROWS; row++) {
      int index = setScroll + row;
      if (index >= setIndices.length) break;
      if (inside(x, y, SET_X, ROW_Y + row * ROW_STEP, LIST_W - 6, ROW_H)) {
        pieceScroll = 0;
        request(DBCAEditorRequest.SELECT_SET, snapshot.setNames[setIndices[index]], -1, "", null);
        return true;
      }
    }
    if (listMode == MODE_PIECES) {
      int[] pieceIndices = filteredPieceIndices();
      for (int row = 0; row < PIECE_ROWS; row++) {
        int index = pieceScroll + row;
        if (index >= pieceIndices.length) break;
        if (inside(x, y, PIECE_X, ROW_Y + row * ROW_STEP, PIECE_W - 6, ROW_H)) {
          request(DBCAEditorRequest.SELECT_PIECE, snapshot.selectedSet,
              snapshot.pieceIds[pieceIndices[index]], "", null);
          return true;
        }
      }
    } else {
      int[] bonusIndices = filteredBonusIndices();
      for (int row = 0; row < PIECE_ROWS; row++) {
        int index = pieceScroll + row;
        if (index >= bonusIndices.length) break;
        if (inside(x, y, PIECE_X, ROW_Y + row * ROW_STEP, PIECE_W - 6, ROW_H)) {
          requestBonus(DBCAEditorRequest.SELECT_BONUS, snapshot.selectedSet,
              snapshot.bonusIds[bonusIndices[index]], "", null);
          return true;
        }
      }
    }
    return false;
  }

  private boolean handleColorDropdownClick(int x, int y) {
    if (openColor < 0) return false;
    int popupX = colorPopupX(openColor);
    int popupY = colorPopupY(openColor);
    for (int i = 0; i < VALID_COLORS.length; i++) {
      int col = i / 7;
      int row = i % 7;
      int optionX = popupX + col * 94;
      int optionY = popupY + row * 14;
      if (inside(x, y, optionX, optionY, 92, 13)) {
        colorValues[openColor] = VALID_COLORS[i];
        GuiButton button = getButton(COLOR_BASE + openColor);
        if (button != null) button.displayString = dropdownText(colorValues[openColor], 10);
        openColor = -1;
        return true;
      }
    }
    return false;
  }

  private boolean handlePotionDropdownClick(int x, int y) {
    if (!potionOpen) return false;
    int popupX = potionPopupX();
    int popupY = 161;
    for (int i = 0; i < POTION_NAMES.length; i++) {
      int col = i / 8;
      int row = i % 8;
      int optionX = popupX + col * 106;
      int optionY = popupY + row * 14;
      if (inside(x, y, optionX, optionY, 104, 13)) {
        potionValue = i;
        GuiButton button = getButton(POTION);
        if (button != null) button.displayString = dropdownText(potionName(potionValue), 14);
        potionOpen = false;
        return true;
      }
    }
    return false;
  }

  private void drawLists() {
    section(SET_X, LIST_Y, LIST_W, LIST_H, "SETS", countLabel(filteredSetIndices().length, snapshot.setNames.length));
    section(PIECE_X, LIST_Y, PIECE_W, LIST_H, listMode == MODE_PIECES ? "PIECES" : "BONUSES",
        secondListCountLabel());
    text("Search", SET_X + 8, SEARCH_Y - 11, MUTED);
    text("Search", PIECE_X + 8, SEARCH_Y - 11, MUTED);
    drawSetRows();
    if (listMode == MODE_PIECES) drawPieceRows();
    else drawBonusRows();
    text("New set", SET_X, 376, MUTED);
    text(listMode == MODE_PIECES ? "New piece" : "New bonus", PIECE_X, 376, MUTED);
  }

  private void drawSetRows() {
    int[] indices = filteredSetIndices();
    if (snapshot.setNames.length == 0) {
      text("Create a set to begin.", SET_X + 8, ROW_Y + 3, MUTED);
      return;
    }
    if (indices.length == 0) {
      text("No matches.", SET_X + 8, ROW_Y + 3, MUTED);
      return;
    }
    for (int row = 0; row < SET_ROWS; row++) {
      int index = setScroll + row;
      if (index >= indices.length) break;
      String set = snapshot.setNames[indices[index]];
      boolean selected = set.equals(snapshot.selectedSet);
      drawRow(SET_X, ROW_Y + row * ROW_STEP, LIST_W - 6, trim(set, 19), selected);
    }
    drawScrollBar(SET_X + LIST_W - 4, ROW_Y, SET_ROWS * ROW_STEP - 4, indices.length, SET_ROWS, setScroll);
  }

  private void drawPieceRows() {
    int[] indices = filteredPieceIndices();
    if (snapshot.selectedSet.isEmpty()) {
      text("Select or create a set.", PIECE_X + 8, ROW_Y + 3, MUTED);
      return;
    }
    if (snapshot.pieceIds.length == 0) {
      text("Create a piece.", PIECE_X + 8, ROW_Y + 3, MUTED);
      return;
    }
    if (indices.length == 0) {
      text("No matches.", PIECE_X + 8, ROW_Y + 3, MUTED);
      return;
    }
    for (int row = 0; row < PIECE_ROWS; row++) {
      int index = pieceScroll + row;
      if (index >= indices.length) break;
      int pieceIndex = indices[index];
      boolean selected = snapshot.pieceIds[pieceIndex] == snapshot.selectedPieceId;
      drawRow(PIECE_X, ROW_Y + row * ROW_STEP, PIECE_W - 6,
          "#" + snapshot.pieceIds[pieceIndex] + " " + trim(snapshot.pieceNames[pieceIndex], 17), selected);
    }
    drawScrollBar(PIECE_X + PIECE_W - 4, ROW_Y, PIECE_ROWS * ROW_STEP - 4, indices.length,
        PIECE_ROWS, pieceScroll);
  }

  private void drawRow(int x, int y, int width, String label, boolean selected) {
    drawRect(x, y, x + width, y + ROW_H, selected ? 0xFF365C3A : CARD_BRIGHT);
    drawRect(x, y + ROW_H - 2, x + width, y + ROW_H, selected ? 0xFF000000 | GREEN : 0xFF4A564B);
    text(label, x + 8, y + 6, selected ? TEXT : MUTED);
  }

  private void drawScrollBar(int x, int y, int height, int total, int visible, int scroll) {
    drawRect(x, y, x + 2, y + height, 0xFF243326);
    if (total <= visible) return;
    int knobHeight = Math.max(18, height * visible / total);
    int travel = height - knobHeight;
    int knobY = y + travel * scroll / Math.max(1, total - visible);
    drawRect(x, knobY, x + 2, knobY + knobHeight, 0xFF000000 | GREEN);
  }

  private void drawEditor() {
    drawRect(332, 55, 744, 374, CARD);
    boolean selected = listMode == MODE_PIECES ? snapshot.selectedPieceId >= 0 : snapshot.selectedBonusId >= 0;
    drawRect(332, 55, 744, 57, 0xFF000000 | (selected ? GREEN : PINK));
    if (listMode == MODE_PIECES) {
      text(snapshot.selectedPieceId >= 0 ? "PIECE #" + snapshot.selectedPieceId : "NO PIECE SELECTED",
          342, 66, snapshot.selectedPieceId >= 0 ? GREEN : PINK);
      text("Name", 504, 66, MUTED);
      drawPieceFieldLabels();
    } else {
      text(snapshot.selectedBonusId >= 0 ? "BONUS #" + snapshot.selectedBonusId : "NO BONUS SELECTED",
          342, 66, snapshot.selectedBonusId >= 0 ? GREEN : PINK);
      text("Name", 504, 66, MUTED);
      drawBonusFieldLabels();
    }
  }

  private void drawPieceFieldLabels() {
    String[] statLabels = {"STR", "DEX", "CON", "WIL", "SPI"};
    for (int i = 0; i < statLabels.length; i++) text(statLabels[i], 342 + i * 62, 89, MUTED);
    text("Level", 342, 129, MUTED);
    text("Durability", 424, 129, MUTED);
    text("Potion", 506, 129, MUTED);
    text("Amplifier", 626, 129, MUTED);
    for (int i = 0; i < COLOR_LABELS.length; i++) {
      text(COLOR_LABELS[i], colorX(i), colorY(i) - 11, MUTED);
    }
    drawRect(342, 258, 724, 259, 0xFF314033);
    text("Lore lines", 342, 246, MUTED);
    for (int i = 0; i < 8; i++) {
      text(Integer.toString(i + 1), 342 + (i / 4) * 186, 278 + (i % 4) * 18, MUTED);
    }
  }

  private void drawBonusFieldLabels() {
    String[] statLabels = {"STR", "DEX", "CON", "WIL", "SPI"};
    for (int i = 0; i < statLabels.length; i++) text(statLabels[i], 342 + i * 62, 89, MUTED);
    for (int i = 0; i < 4; i++) {
      text("Piece " + (i + 1), 342 + i * 82, 129, MUTED);
    }
  }

  private void drawStatus() {
    drawRect(332, 350, 744, 374, error ? 0xFF2F1C34 : 0xFF172719);
    drawRect(332, 350, 334, 374, error ? 0xFF000000 | HOT_PINK : 0xFF000000 | GREEN);
    fontRendererObj.drawSplitString(status == null ? "" : status, 342, 358, 390,
        error ? HOT_PINK : MUTED);
  }

  private void drawFields() {
    setSearchField.drawTextBox();
    pieceSearchField.drawTextBox();
    newSetField.drawTextBox();
    newPieceField.drawTextBox();
    if (listMode == MODE_PIECES) {
      for (GuiTextField field : fields) if (field != null) field.drawTextBox();
    } else {
      for (GuiTextField field : bonusFields) if (field != null) field.drawTextBox();
    }
  }

  private void drawColorDropdown() {
    if (listMode != MODE_PIECES || openColor < 0) return;
    int x = colorPopupX(openColor);
    int y = colorPopupY(openColor);
    drawRect(x - 2, y - 2, x + 284, y + 100, 0xF0111813);
    drawRect(x - 2, y - 2, x + 284, y, 0xFF000000 | PINK);
    for (int i = 0; i < VALID_COLORS.length; i++) {
      int col = i / 7;
      int row = i % 7;
      int optionX = x + col * 94;
      int optionY = y + row * 14;
      boolean selected = VALID_COLORS[i].equals(colorValues[openColor]);
      boolean hover = inside(guiMouseX, guiMouseY, optionX, optionY, 92, 13);
      drawDropdownOption(optionX, optionY, 92, trim(VALID_COLORS[i], 14),
          selected, hover);
    }
  }

  private String secondListCountLabel() {
    if (snapshot.selectedSet.isEmpty()) return "No set";
    if (listMode == MODE_PIECES) {
      return countLabel(filteredPieceIndices().length, snapshot.pieceIds.length);
    }
    return countLabel(filteredBonusIndices().length, snapshot.bonusIds.length);
  }

  private void drawBonusRows() {
    int[] indices = filteredBonusIndices();
    if (snapshot.selectedSet.isEmpty()) {
      text("Select or create a set.", PIECE_X + 8, ROW_Y + 3, MUTED);
      return;
    }
    if (snapshot.bonusIds.length == 0) {
      text("Create a bonus.", PIECE_X + 8, ROW_Y + 3, MUTED);
      return;
    }
    if (indices.length == 0) {
      text("No matches.", PIECE_X + 8, ROW_Y + 3, MUTED);
      return;
    }
    for (int row = 0; row < PIECE_ROWS; row++) {
      int index = pieceScroll + row;
      if (index >= indices.length) break;
      int bonusIndex = indices[index];
      boolean selected = snapshot.bonusIds[bonusIndex] == snapshot.selectedBonusId;
      drawRow(PIECE_X, ROW_Y + row * ROW_STEP, PIECE_W - 6,
          "#" + snapshot.bonusIds[bonusIndex] + " " + trim(snapshot.bonusNames[bonusIndex], 17),
          selected);
    }
    drawScrollBar(PIECE_X + PIECE_W - 4, ROW_Y, PIECE_ROWS * ROW_STEP - 4, indices.length,
        PIECE_ROWS, pieceScroll);
  }

  private void createBonusFields() {
    bonusFields[0] = field(548, 63, 176, colorOut(snapshot.bonusName));
    for (int i = 0; i < 5; i++) {
      bonusFields[i + 1] = field(342 + i * 62, 101, 46,
          snapshot.bonusStats.length > i ? snapshot.bonusStats[i] : "+0");
    }
    for (int i = 0; i < 4; i++) {
      bonusFields[i + 6] = field(342 + i * 82, 141, 58,
          snapshot.bonusPieceIds.length > i ? Integer.toString(snapshot.bonusPieceIds[i]) : "");
    }
  }

  private void drawPotionDropdown() {
    if (listMode != MODE_PIECES || !potionOpen) return;
    int x = potionPopupX();
    int y = 161;
    drawRect(x - 2, y - 2, x + 320, y + 114, 0xF0111813);
    drawRect(x - 2, y - 2, x + 320, y, 0xFF000000 | PINK);
    for (int i = 0; i < POTION_NAMES.length; i++) {
      int col = i / 8;
      int row = i % 8;
      int optionX = x + col * 106;
      int optionY = y + row * 14;
      boolean selected = i == potionValue;
      boolean hover = inside(guiMouseX, guiMouseY, optionX, optionY, 104, 13);
      drawDropdownOption(optionX, optionY, 104, trim(POTION_NAMES[i], 16), selected, hover);
    }
  }

  private void section(int x, int y, int width, int height, String title, String subTitle) {
    drawRect(x, y, x + width, y + height, CARD);
    drawRect(x, y, x + width, y + 2, 0xFF000000 | GREEN);
    text(title, x + 8, y + 9, GREEN);
    String count = trim(subTitle, 12);
    text(count, x + width - 8 - fontRendererObj.getStringWidth(count), y + 9, MUTED);
  }

  private void drawDropdownOption(int x, int y, int width, String label, boolean selected,
      boolean hover) {
    int background = hover ? 0xFF365C3A : selected ? 0xFF3B2442 : CARD_BRIGHT;
    int accent = selected ? PINK : hover ? GREEN : 0x4A564B;
    drawRect(x, y, x + width, y + 13, background);
    drawRect(x, y + 11, x + width, y + 13, 0xFF000000 | accent);
    text(label, x + 4, y + 3, selected || hover ? TEXT : MUTED);
  }

  private void syncScrollsToSelection() {
    int[] setIndices = filteredSetIndices();
    for (int i = 0; i < setIndices.length; i++) {
      if (snapshot.setNames[setIndices[i]].equals(snapshot.selectedSet)) {
        setScroll = Math.max(0, Math.min(i, Math.max(0, setIndices.length - SET_ROWS)));
        break;
      }
    }
    int[] pieceIndices = filteredPieceIndices();
    if (listMode == MODE_PIECES) {
      for (int i = 0; i < pieceIndices.length; i++) {
        if (snapshot.pieceIds[pieceIndices[i]] == snapshot.selectedPieceId) {
          pieceScroll = Math.max(0, Math.min(i, Math.max(0, pieceIndices.length - PIECE_ROWS)));
          break;
        }
      }
    } else {
      int[] bonusIndices = filteredBonusIndices();
      for (int i = 0; i < bonusIndices.length; i++) {
        if (snapshot.bonusIds[bonusIndices[i]] == snapshot.selectedBonusId) {
          pieceScroll = Math.max(0, Math.min(i, Math.max(0, bonusIndices.length - PIECE_ROWS)));
          break;
        }
      }
    }
    syncScrolls();
  }

  private void syncScrolls() {
    setScroll = Math.max(0, Math.min(setScroll, Math.max(0, filteredSetIndices().length - SET_ROWS)));
    int secondListSize = listMode == MODE_PIECES ? filteredPieceIndices().length
        : filteredBonusIndices().length;
    pieceScroll = Math.max(0, Math.min(pieceScroll, Math.max(0, secondListSize - PIECE_ROWS)));
  }

  private int[] filteredSetIndices() {
    String query = setSearchField == null ? "" : setSearchField.getText().trim().toLowerCase();
    int count = 0;
    for (String name : snapshot.setNames) if (matches(name, query)) count++;
    int[] indices = new int[count];
    int out = 0;
    for (int i = 0; i < snapshot.setNames.length; i++) {
      if (matches(snapshot.setNames[i], query)) indices[out++] = i;
    }
    return indices;
  }

  private int[] filteredPieceIndices() {
    String query = pieceSearchField == null ? "" : pieceSearchField.getText().trim().toLowerCase();
    int count = 0;
    for (int i = 0; i < snapshot.pieceNames.length; i++) {
      if (matches(snapshot.pieceNames[i], query) || matches("#" + snapshot.pieceIds[i], query)
          || matches(Integer.toString(snapshot.pieceIds[i]), query)) count++;
    }
    int[] indices = new int[count];
    int out = 0;
    for (int i = 0; i < snapshot.pieceNames.length; i++) {
      if (matches(snapshot.pieceNames[i], query) || matches("#" + snapshot.pieceIds[i], query)
          || matches(Integer.toString(snapshot.pieceIds[i]), query)) indices[out++] = i;
    }
    return indices;
  }

  private int[] filteredBonusIndices() {
    String query = pieceSearchField == null ? "" : pieceSearchField.getText().trim().toLowerCase();
    int count = 0;
    for (int i = 0; i < snapshot.bonusNames.length; i++) {
      if (matches(snapshot.bonusNames[i], query) || matches("#" + snapshot.bonusIds[i], query)
          || matches(Integer.toString(snapshot.bonusIds[i]), query)) count++;
    }
    int[] indices = new int[count];
    int out = 0;
    for (int i = 0; i < snapshot.bonusNames.length; i++) {
      if (matches(snapshot.bonusNames[i], query) || matches("#" + snapshot.bonusIds[i], query)
          || matches(Integer.toString(snapshot.bonusIds[i]), query)) indices[out++] = i;
    }
    return indices;
  }

  private static boolean matches(String value, String query) {
    return query == null || query.isEmpty()
        || (value != null && value.toLowerCase().contains(query));
  }

  private int colorX(int slot) {
    return 342 + slot % 3 * 108;
  }

  private int colorY(int slot) {
    return 181 + slot / 3 * 39;
  }

  private int colorPopupY(int slot) {
    return colorY(slot) + 20;
  }

  private int colorPopupX(int slot) {
    return Math.min(colorX(slot), PANEL_WIDTH - 300);
  }

  private static int clampPotion(int potionId) {
    return Math.max(0, Math.min(potionId, POTION_NAMES.length - 1));
  }

  private int potionPopupX() {
    return Math.min(506, PANEL_WIDTH - 340);
  }

  private String potionName(int potionId) {
    int id = clampPotion(potionId);
    return trim(POTION_NAMES[id], 17);
  }

  private String dropdownText(String value, int width) {
    return trim(value, width);
  }

  private GuiButton getButton(int id) {
    for (Object entry : buttonList) {
      GuiButton button = (GuiButton) entry;
      if (button.id == id) return button;
    }
    return null;
  }

  private static boolean inside(int x, int y, int left, int top, int width, int height) {
    return x >= left && y >= top && x < left + width && y < top + height;
  }

  private static String countLabel(int shown, int total) {
    return shown == total ? total + " total" : shown + " / " + total;
  }

  private void text(String value, int x, int y, int color) {
    fontRendererObj.drawString(value == null ? "" : value, x, y, color);
  }

  private static String colorOut(String value) {
    return value == null ? "" : value.replace("\u00a7", "&");
  }

  private String trim(String value, int width) {
    return fontRendererObj.trimStringToWidth(value == null ? "" : value, width * 6);
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
    if (!serverClosed && mc.thePlayer != null && mc.getNetHandler() != null) {
      DBCAEditorNetwork.channel.sendToServer(new DBCAEditorRequest(REQUEST_IDS.incrementAndGet(),
          DBCAEditorRequest.CLOSE, snapshot.selectedSet, snapshot.selectedPieceId,
          snapshot.selectedBonusId, "", null));
    }
  }
}
