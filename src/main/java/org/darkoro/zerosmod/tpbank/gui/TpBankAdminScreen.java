package org.darkoro.zerosmod.tpbank.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.darkoro.zerosmod.rebirth.gui.RebirthButton;
import org.darkoro.zerosmod.tpbank.network.TpBankAdminRequest;
import org.darkoro.zerosmod.tpbank.network.TpBankAdminSnapshot;
import org.darkoro.zerosmod.tpbank.network.TpBankNetwork;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/** Admin screen: statistics, player balances and config. The server authorises every action. */
public final class TpBankAdminScreen extends GuiScreen {
  private static final int PANEL_WIDTH = 360, PANEL_HEIGHT = 270;
  private static final int GREEN = 0x90E080, PINK = 0xB050D0, HOT_PINK = 0xD986E8, TEXT = 0xF3F8F2,
      MUTED = 0xB2C0B1, PANEL = 0xFF111813, CARD = 0xFF18231A, CARD_BRIGHT = 0xFF203222;
  private static final int TAB_STATS = 0, TAB_PLAYERS = 1, TAB_CONFIG = 2;
  private static final int ID_TAB = 10, ID_REFRESH = 20, ID_CLOSE = 21, ID_SET = 30, ID_ADD = 31, ID_REMOVE = 32,
      ID_LOCK = 33, ID_ENABLED = 40, ID_UNLOCKED = 41, ID_NOTIFY = 42, ID_SAVE = 43;
  private static final int LIST_X = 16, LIST_Y = 76, LIST_WIDTH = 190, ROW_HEIGHT = 12, VISIBLE_ROWS = 10;
  private static final AtomicInteger REQUEST_IDS = new AtomicInteger();

  private TpBankAdminSnapshot snapshot;
  private int tab = TAB_STATS;
  private float scale, originX, originY;
  private int requestId, timeout, scroll;
  private boolean waiting, serverClosed, error;
  private String status;
  private String selectedName, selectedSlot;
  private boolean enabled, unlockedByDefault, notifyOnDeposit;
  private GuiTextField search, amount, depositLoss, withdrawFee, interest, period, maxBalance;

  public TpBankAdminScreen(TpBankAdminSnapshot snapshot) {
    this.snapshot = snapshot;
    status = snapshot.message;
    error = snapshot.error;
    loadSettings(snapshot.settings);
  }

  private void loadSettings(TpBankAdminSnapshot.Settings settings) {
    enabled = settings.enabled;
    unlockedByDefault = settings.unlockedByDefault;
    notifyOnDeposit = settings.notifyOnDeposit;
    if (depositLoss != null) {
      depositLoss.setText(Double.toString(settings.depositLoss));
      withdrawFee.setText(Double.toString(settings.withdrawFee));
      interest.setText(Double.toString(settings.interest));
      period.setText(Integer.toString(settings.periodMinutes));
      maxBalance.setText(Long.toString(settings.maxBalance));
    }
  }

  @Override public void initGui() {
    Keyboard.enableRepeatEvents(true);
    scale = Math.min(1F, Math.min((width - 12F) / PANEL_WIDTH, (height - 12F) / PANEL_HEIGHT));
    originX = (width - PANEL_WIDTH * scale) / 2F;
    originY = (height - PANEL_HEIGHT * scale) / 2F;
    boolean first = depositLoss == null;
    search = field(search, 20, 60, 180);
    amount = field(amount, 218, 124, 122);
    depositLoss = field(depositLoss, 218, 68, 122);
    withdrawFee = field(withdrawFee, 218, 96, 122);
    interest = field(interest, 218, 124, 122);
    period = field(period, 218, 152, 122);
    maxBalance = field(maxBalance, 218, 180, 122);
    if (first) {
      loadSettings(snapshot.settings);
    }
    buttonList.clear();
    add(ID_TAB + TAB_STATS, 16, 26, 106, "Statistics", GREEN);
    add(ID_TAB + TAB_PLAYERS, 127, 26, 106, "Players", GREEN);
    add(ID_TAB + TAB_CONFIG, 238, 26, 106, "Config", GREEN);
    add(ID_SET, 214, 142, 62, "Set", GREEN);
    add(ID_ADD, 282, 142, 62, "Add", GREEN);
    add(ID_REMOVE, 214, 166, 62, "Remove", PINK);
    add(ID_LOCK, 282, 166, 62, "Lock", PINK);
    add(ID_ENABLED, 16, 56, 180, "", GREEN);
    add(ID_UNLOCKED, 16, 80, 180, "", GREEN);
    add(ID_NOTIFY, 16, 104, 180, "", GREEN);
    add(ID_SAVE, 16, 128, 180, "Save config", GREEN);
    add(ID_REFRESH, 16, 244, 106, "Refresh", MUTED);
    add(ID_CLOSE, 238, 244, 106, "Close", MUTED);
    updateControls();
  }

  private GuiTextField field(GuiTextField old, int x, int y, int fieldWidth) {
    GuiTextField created = new GuiTextField(fontRendererObj, x, y, fieldWidth, 12);
    created.setMaxStringLength(24);
    created.setEnableBackgroundDrawing(false);
    created.setTextColor(TEXT);
    if (old != null) {
      created.setText(old.getText());
    }
    return created;
  }

  private void add(int id, int x, int y, int buttonWidth, String label, int color) {
    buttonList.add(new RebirthButton(id, x, y, buttonWidth, label, color));
  }

  private TpBankAdminSnapshot.Row selected() {
    for (TpBankAdminSnapshot.Row row : snapshot.rows) {
      if (row.name.equals(selectedName) && row.slot.equals(selectedSlot)) {
        return row;
      }
    }
    return null;
  }

  private List<TpBankAdminSnapshot.Row> filtered() {
    String query = search.getText().trim().toLowerCase(Locale.ROOT);
    List<TpBankAdminSnapshot.Row> rows = new ArrayList<>();
    for (TpBankAdminSnapshot.Row row : snapshot.rows) {
      if (row.name.toLowerCase(Locale.ROOT).contains(query)) {
        rows.add(row);
      }
    }
    return rows;
  }

  private void updateControls() {
    TpBankAdminSnapshot.Row row = selected();
    boolean editable = !waiting && row != null && row.live;
    long value = parseAmount(amount.getText());
    for (Object entry : buttonList) {
      GuiButton button = (GuiButton) entry;
      switch (button.id) {
        case ID_SET:
          button.visible = tab == TAB_PLAYERS;
          button.enabled = editable && value >= 0;
          break;
        case ID_ADD:
        case ID_REMOVE:
          button.visible = tab == TAB_PLAYERS;
          button.enabled = editable && value > 0;
          break;
        case ID_LOCK:
          button.visible = tab == TAB_PLAYERS;
          button.enabled = editable;
          button.displayString = row != null && row.unlocked ? "Lock" : "Unlock";
          break;
        case ID_ENABLED:
          toggle(button, "Bank enabled", enabled);
          break;
        case ID_UNLOCKED:
          toggle(button, "Unlocked by default", unlockedByDefault);
          break;
        case ID_NOTIFY:
          toggle(button, "Deposit chat message", notifyOnDeposit);
          break;
        case ID_SAVE:
          button.visible = tab == TAB_CONFIG;
          button.enabled = !waiting && configValid();
          break;
        case ID_TAB + TAB_STATS:
        case ID_TAB + TAB_PLAYERS:
        case ID_TAB + TAB_CONFIG:
          button.enabled = button.id - ID_TAB != tab;
          break;
        case ID_REFRESH:
          button.enabled = !waiting;
          break;
        default:
          break;
      }
    }
  }

  private void toggle(GuiButton button, String label, boolean value) {
    button.visible = tab == TAB_CONFIG;
    button.enabled = !waiting;
    button.displayString = label + ": " + (value ? "ON" : "OFF");
  }

  /** @return the amount, 0 for a literal zero, or -1 when empty or unreadable */
  private static long parseAmount(String text) {
    if (text.trim().equals("0")) {
      return 0L;
    }
    long parsed = TpBankScreen.parse(text);
    return parsed <= 0 ? -1L : parsed;
  }

  private static boolean inRange(String text, double min, double max) {
    try {
      double value = Double.parseDouble(text.trim());
      return value >= min && value <= max;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private boolean configValid() {
    return inRange(depositLoss.getText(), 0, 100) && inRange(withdrawFee.getText(), 0, 99)
        && inRange(interest.getText(), -100, 1000) && validPeriod() && parseAmount(maxBalance.getText()) >= 0;
  }

  private boolean validPeriod() {
    try {
      int value = Integer.parseInt(period.getText().trim());
      return value >= 1 && value <= 525600;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private TpBankAdminRequest request(int action) {
    requestId = REQUEST_IDS.incrementAndGet();
    return new TpBankAdminRequest(snapshot.session, requestId, action);
  }

  private void send(TpBankAdminRequest request) {
    waiting = true;
    timeout = 300;
    status = "Waiting for the server...";
    error = false;
    TpBankNetwork.channel.sendToServer(request);
    updateControls();
  }

  private void edit(int action, long value) {
    TpBankAdminSnapshot.Row row = selected();
    if (row == null) {
      return;
    }
    TpBankAdminRequest request = request(action);
    request.target = row.name;
    request.slot = row.slot;
    request.amount = value;
    send(request);
  }

  private void saveConfig() {
    TpBankAdminRequest request = request(TpBankAdminRequest.SAVE_CONFIG);
    TpBankAdminSnapshot.Settings settings = request.settings;
    settings.enabled = enabled;
    settings.unlockedByDefault = unlockedByDefault;
    settings.notifyOnDeposit = notifyOnDeposit;
    settings.depositLoss = Double.parseDouble(depositLoss.getText().trim());
    settings.withdrawFee = Double.parseDouble(withdrawFee.getText().trim());
    settings.interest = Double.parseDouble(interest.getText().trim());
    settings.periodMinutes = Integer.parseInt(period.getText().trim());
    settings.maxBalance = parseAmount(maxBalance.getText());
    send(request);
  }

  public void receive(TpBankAdminSnapshot response) {
    if (response.session != snapshot.session || response.requestId != requestId) {
      return;
    }
    waiting = false;
    if (response.close) {
      serverClosed = true;
      mc.displayGuiScreen(null);
      return;
    }
    snapshot = response;
    status = response.message;
    error = response.error;
    if (response.configSaved) {
      loadSettings(response.settings); // the server may have limited some values
    }
    updateControls();
  }

  @Override protected void actionPerformed(GuiButton button) {
    if (!button.enabled) {
      return;
    }
    switch (button.id) {
      case ID_TAB + TAB_STATS:
      case ID_TAB + TAB_PLAYERS:
      case ID_TAB + TAB_CONFIG:
        tab = button.id - ID_TAB;
        break;
      case ID_SET: edit(TpBankAdminRequest.SET, parseAmount(amount.getText())); break;
      case ID_ADD: edit(TpBankAdminRequest.ADD, parseAmount(amount.getText())); break;
      case ID_REMOVE: edit(TpBankAdminRequest.ADD, -parseAmount(amount.getText())); break;
      case ID_LOCK:
        TpBankAdminSnapshot.Row row = selected();
        edit(row != null && row.unlocked ? TpBankAdminRequest.LOCK : TpBankAdminRequest.UNLOCK, 0L);
        break;
      case ID_ENABLED: enabled = !enabled; break;
      case ID_UNLOCKED: unlockedByDefault = !unlockedByDefault; break;
      case ID_NOTIFY: notifyOnDeposit = !notifyOnDeposit; break;
      case ID_SAVE: saveConfig(); break;
      case ID_REFRESH: send(request(TpBankAdminRequest.REFRESH)); break;
      case ID_CLOSE: mc.displayGuiScreen(null); break;
      default: break;
    }
    updateControls();
  }

  private GuiTextField[] activeFields() {
    if (tab == TAB_PLAYERS) {
      return new GuiTextField[] {search, amount};
    }
    if (tab == TAB_CONFIG) {
      return new GuiTextField[] {depositLoss, withdrawFee, interest, period, maxBalance};
    }
    return new GuiTextField[0];
  }

  @Override protected void keyTyped(char character, int key) {
    if (key == Keyboard.KEY_ESCAPE) {
      mc.displayGuiScreen(null);
      return;
    }
    for (GuiTextField field : activeFields()) {
      field.textboxKeyTyped(character, key);
    }
    scroll = Math.min(scroll, maxScroll());
    updateControls();
  }

  private int maxScroll() {
    return Math.max(0, filtered().size() - VISIBLE_ROWS);
  }

  @Override protected void mouseClicked(int mouseX, int mouseY, int button) {
    int x = (int) ((mouseX - originX) / scale), y = (int) ((mouseY - originY) / scale);
    super.mouseClicked(x, y, button);
    for (GuiTextField field : activeFields()) {
      field.mouseClicked(x, y, button);
    }
    if (tab == TAB_PLAYERS && x >= LIST_X && x < LIST_X + LIST_WIDTH && y >= LIST_Y) {
      int index = (y - LIST_Y) / ROW_HEIGHT;
      List<TpBankAdminSnapshot.Row> rows = filtered();
      if (index < VISIBLE_ROWS && index + scroll < rows.size()) {
        selectedName = rows.get(index + scroll).name;
        selectedSlot = rows.get(index + scroll).slot;
      }
    }
    updateControls();
  }

  @Override public void handleMouseInput() {
    super.handleMouseInput();
    int wheel = Mouse.getEventDWheel();
    if (wheel != 0 && tab == TAB_PLAYERS) {
      scroll = Math.max(0, Math.min(maxScroll(), scroll + (wheel > 0 ? -1 : 1)));
    }
  }

  @Override public void updateScreen() {
    for (GuiTextField field : activeFields()) {
      field.updateCursorCounter();
    }
    if (waiting && --timeout <= 0) {
      waiting = false;
      requestId = REQUEST_IDS.incrementAndGet();
      status = "No reply from the server. Refresh before trying again.";
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
    text("TP BANK ADMIN", 16, 11, GREEN);
    switch (tab) {
      case TAB_PLAYERS: drawPlayers(); break;
      case TAB_CONFIG: drawConfig(); break;
      default: drawStats(); break;
    }
    drawRect(16, 212, 344, 238, error ? 0xFF2F1C34 : 0xFF172719);
    drawRect(16, 212, 18, 238, error ? 0xFF000000 | HOT_PINK : 0xFF000000 | GREEN);
    fontRendererObj.drawSplitString(status, 24, 215, 316, error ? HOT_PINK : MUTED);
    super.drawScreen(x, y, partialTicks);
    GL11.glPopMatrix();
    GL11.glColor4f(1, 1, 1, 1);
  }

  private void drawStats() {
    long eaten = snapshot.eatenByDepositLoss + snapshot.eatenByWithdrawFee + snapshot.eatenByInterest;
    card(16, 54, "BANKED NOW (ABOUT)", format(snapshot.totalBanked), GREEN);
    card(184, 54, "EATEN BY THE BANK", format(eaten), HOT_PINK);
    text("Slots", 16, 104, MUTED);
    value(format(snapshot.slotCount), 104);
    text("Deposited", 16, 118, MUTED);
    value(format(snapshot.deposited), 118);
    text("Withdrawn", 16, 132, MUTED);
    value(format(snapshot.withdrawn), 132);
    text("Eaten by deposit loss", 16, 146, MUTED);
    value(format(snapshot.eatenByDepositLoss), 146);
    text("Eaten by withdraw fee", 16, 160, MUTED);
    value(format(snapshot.eatenByWithdrawFee), 160);
    text("Eaten by interest", 16, 174, MUTED);
    value(format(snapshot.eatenByInterest), 174);
    text("Gained from interest", 16, 188, MUTED);
    value(format(snapshot.gainedFromInterest), 188);
  }

  private void value(String value, int y) {
    text(value, 344 - fontRendererObj.getStringWidth(value), y, TEXT);
  }

  private void drawPlayers() {
    box(16, 56, 206, 72);
    search.drawTextBox();
    if (search.getText().isEmpty() && !search.isFocused()) {
      text("Search player...", 20, 60, MUTED);
    }
    drawRect(LIST_X, LIST_Y, LIST_X + LIST_WIDTH, LIST_Y + VISIBLE_ROWS * ROW_HEIGHT, CARD);
    List<TpBankAdminSnapshot.Row> rows = filtered();
    for (int i = 0; i < VISIBLE_ROWS && i + scroll < rows.size(); i++) {
      TpBankAdminSnapshot.Row row = rows.get(i + scroll);
      int rowY = LIST_Y + i * ROW_HEIGHT;
      if (row.name.equals(selectedName) && row.slot.equals(selectedSlot)) {
        drawRect(LIST_X, rowY, LIST_X + LIST_WIDTH, rowY + ROW_HEIGHT, CARD_BRIGHT);
      }
      String balance = compact(row.banked);
      int balanceWidth = fontRendererObj.getStringWidth(balance);
      String label = fontRendererObj.trimStringToWidth(row.name + " #" + row.slot, LIST_WIDTH - balanceWidth - 12);
      text(label, LIST_X + 4, rowY + 2, row.live ? GREEN : TEXT);
      text(balance, LIST_X + LIST_WIDTH - balanceWidth - 4, rowY + 2, row.unlocked ? MUTED : HOT_PINK);
    }
    text("Green: online. Pink balance: locked.", LIST_X, 198, MUTED);

    TpBankAdminSnapshot.Row row = selected();
    if (row == null) {
      text("Select a player.", 214, 58, MUTED);
      return;
    }
    text(fontRendererObj.trimStringToWidth(row.name + " #" + row.slot, 130), 214, 56, GREEN);
    text((row.live ? "" : "about ") + compact(row.banked) + " TP", 214, 68, TEXT);
    text(row.unlocked ? "Unlocked" : "Locked", 214, 80, row.unlocked ? TEXT : HOT_PINK);
    text("In " + compact(row.deposited) + "  Out " + compact(row.withdrawn), 214, 92, MUTED);
    text(row.live ? "Amount" : "Not on this slot: read only", 214, 108, row.live ? MUTED : HOT_PINK);
    box(214, 120, 344, 136);
    amount.drawTextBox();
  }

  private void drawConfig() {
    fontRendererObj.drawSplitString("Saving applies at once and rewrites tp_bank.cfg. New interest values also apply to "
        + "time not yet charged, including offline players.", 16, 154, 186, HOT_PINK);
    labelled(depositLoss, "Deposit loss % (0 to 100)", 56, inRange(depositLoss.getText(), 0, 100));
    labelled(withdrawFee, "Withdraw fee % (0 to 99)", 84, inRange(withdrawFee.getText(), 0, 99));
    labelled(interest, "Interest % (-100 to 1000)", 112, inRange(interest.getText(), -100, 1000));
    labelled(period, "Interest period, minutes", 140, validPeriod());
    labelled(maxBalance, "Max balance (0 = no limit)", 168, parseAmount(maxBalance.getText()) >= 0);
  }

  private void labelled(GuiTextField field, String label, int y, boolean valid) {
    text(label, 214, y, valid ? MUTED : HOT_PINK);
    box(214, y + 9, 344, y + 25);
    drawRect(214, y + 9, 216, y + 25, 0xFF000000 | (valid ? GREEN : HOT_PINK));
    field.drawTextBox();
  }

  private void box(int left, int top, int right, int bottom) {
    drawRect(left, top, right, bottom, CARD_BRIGHT);
  }

  private void card(int x, int y, String title, String value, int accent) {
    drawRect(x, y, x + 160, y + 42, CARD);
    drawRect(x, y, x + 160, y + 2, 0xFF000000 | accent);
    text(title, x + 8, y + 8, accent);
    text(fontRendererObj.trimStringToWidth(value, 144), x + 8, y + 23, TEXT);
  }

  private static String format(long value) {
    return String.format("%,d", value);
  }

  /** Short form for narrow columns, e.g. 12.5b. */
  private static String compact(long value) {
    if (value >= 1_000_000_000_000L) {
      return String.format(Locale.ROOT, "%.1ft", value / 1e12D);
    }
    if (value >= 1_000_000_000L) {
      return String.format(Locale.ROOT, "%.1fb", value / 1e9D);
    }
    if (value >= 1_000_000L) {
      return String.format(Locale.ROOT, "%.1fm", value / 1e6D);
    }
    return format(value);
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
      TpBankNetwork.channel.sendToServer(new TpBankAdminRequest(snapshot.session, REQUEST_IDS.incrementAndGet(), TpBankAdminRequest.CLOSE));
    }
  }
}
