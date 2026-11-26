package org.darkoro.zerosmod.tpbank.gui;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.darkoro.zerosmod.rebirth.gui.RebirthButton;
import org.darkoro.zerosmod.tpbank.network.TpBankNetwork;
import org.darkoro.zerosmod.tpbank.network.TpBankRequest;
import org.darkoro.zerosmod.tpbank.network.TpBankSnapshot;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/** Withdraw screen over a server-owned TP Bank session. Balances shown are the server's. */
public final class TpBankScreen extends GuiScreen {
  private static final int PANEL_WIDTH = 320, PANEL_HEIGHT = 236;
  private static final int GREEN = 0x90E080, PINK = 0xB050D0, HOT_PINK = 0xD986E8, TEXT = 0xF3F8F2,
      MUTED = 0xB2C0B1, PANEL = 0xFF111813, CARD = 0xFF18231A, CARD_BRIGHT = 0xFF203222;
  private static final int WITHDRAW = 10, WITHDRAW_MAX = 11, RELOAD = 12, EXIT = 13;
  private static final AtomicInteger REQUEST_IDS = new AtomicInteger();
  private GuiTextField amountField;
  private TpBankSnapshot snapshot;
  private float scale, originX, originY;
  private int requestId, timeout;
  private boolean waiting, serverClosed, error;
  private String status;

  public TpBankScreen(TpBankSnapshot snapshot) {
    this.snapshot = snapshot;
    status = snapshot.message;
    error = snapshot.error;
  }

  @Override public void initGui() {
    Keyboard.enableRepeatEvents(true);
    scale = Math.min(1F, Math.min((width - 12F) / PANEL_WIDTH, (height - 12F) / PANEL_HEIGHT));
    originX = (width - PANEL_WIDTH * scale) / 2F;
    originY = (height - PANEL_HEIGHT * scale) / 2F;
    String text = amountField == null ? "" : amountField.getText();
    amountField = new GuiTextField(fontRendererObj, 20, 136, 136, 14);
    amountField.setMaxStringLength(20);
    amountField.setEnableBackgroundDrawing(false);
    amountField.setText(text);
    amountField.setFocused(true);
    amountField.setTextColor(TEXT);
    amountField.setDisabledTextColour(MUTED);
    buttonList.clear();
    add(WITHDRAW, 164, 133, 140, "Withdraw", GREEN);
    add(WITHDRAW_MAX, 16, 204, 140, "Withdraw max", PINK);
    add(RELOAD, 164, 204, 66, "Refresh", MUTED);
    add(EXIT, 238, 204, 66, "Close", MUTED);
    updateControls();
  }

  private void add(int id, int x, int y, int width, String label, int color) {
    buttonList.add(new RebirthButton(id, x, y, width, label, color));
  }

  private long room() {
    return Math.max(0L, (long) snapshot.maximum - Math.max(0, snapshot.current));
  }

  /** @return how much the player would receive for a request of {@code amount} */
  private long preview(long amount) {
    return Math.max(0L, Math.min(amount, snapshot.withdrawable));
  }

  /** @return banked TP needed to receive {@code received}; mirrors the server's fee rounding */
  private long cost(long received) {
    double keep = 1.0D - snapshot.withdrawFeePercent / 100.0D;
    return keep >= 1.0D ? received : (long) Math.ceil(received / keep);
  }

  private boolean affordable(long amount) {
    return amount > 0 && cost(amount) <= snapshot.banked;
  }

  private void updateControls() {
    long amount = parse(amountField.getText());
    for (Object entry : buttonList) {
      GuiButton button = (GuiButton) entry;
      switch (button.id) {
        case WITHDRAW:
          button.enabled = !waiting && !snapshot.locked && affordable(amount) && snapshot.withdrawable > 0;
          break;
        case WITHDRAW_MAX:
          button.enabled = !waiting && !snapshot.locked && snapshot.withdrawable > 0;
          break;
        default:
          button.enabled = !waiting;
          break;
      }
    }
    amountField.setEnabled(!waiting && !snapshot.locked);
  }

  /**
   * Accepts digits with optional separators and a k/m/b suffix, e.g. {@code 1,500,000} or {@code 1.5m}.
   *
   * @return the amount, 0 for empty input, or -1 when unreadable
   */
  static long parse(String input) {
    String text = input.trim().toLowerCase(Locale.ROOT).replace(",", "").replace("_", "").replace(" ", "").replace("'", "");
    if (text.isEmpty()) {
      return 0L;
    }
    long multiplier = 1L;
    char suffix = text.charAt(text.length() - 1);
    if (suffix == 'k' || suffix == 'm' || suffix == 'b') {
      multiplier = suffix == 'k' ? 1_000L : suffix == 'm' ? 1_000_000L : 1_000_000_000L;
      text = text.substring(0, text.length() - 1);
    }
    try {
      double value = multiplier == 1L ? Long.parseLong(text) : Double.parseDouble(text) * multiplier;
      return value <= 0 || value >= Long.MAX_VALUE || Double.isNaN(value) ? -1L : (long) value;
    } catch (NumberFormatException e) {
      return -1L;
    }
  }

  private void request(int action, long amount) {
    requestId = REQUEST_IDS.incrementAndGet();
    waiting = true;
    timeout = 300;
    status = "Waiting for the server...";
    error = false;
    TpBankNetwork.channel.sendToServer(new TpBankRequest(snapshot.session, requestId, action, amount));
    updateControls();
  }

  public void receive(TpBankSnapshot response) {
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
    if (!error) {
      amountField.setText("");
    }
    updateControls();
  }

  @Override protected void actionPerformed(GuiButton button) {
    if (!button.enabled) {
      return;
    }
    switch (button.id) {
      case WITHDRAW: request(TpBankRequest.WITHDRAW, parse(amountField.getText())); break;
      case WITHDRAW_MAX: request(TpBankRequest.WITHDRAW_MAX, 0L); break;
      case RELOAD: request(TpBankRequest.REFRESH, 0L); break;
      case EXIT: mc.displayGuiScreen(null); break;
      default: break;
    }
  }

  @Override protected void keyTyped(char character, int key) {
    if (key == Keyboard.KEY_ESCAPE) {
      mc.displayGuiScreen(null);
      return;
    }
    if ((key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) && !waiting) {
      actionPerformed((GuiButton) buttonList.get(0));
      return;
    }
    if (!waiting && amountField.textboxKeyTyped(character, key)) {
      updateControls();
    }
  }

  @Override protected void mouseClicked(int mouseX, int mouseY, int button) {
    int x = (int) ((mouseX - originX) / scale), y = (int) ((mouseY - originY) / scale);
    super.mouseClicked(x, y, button);
    amountField.mouseClicked(x, y, button);
  }

  @Override public void updateScreen() {
    amountField.updateCursorCounter();
    if (waiting && --timeout <= 0) {
      waiting = false;
      requestId = REQUEST_IDS.incrementAndGet();
      status = "No reply from the server. Refresh to check your balance before trying again.";
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
    drawRect(16, 48, 304, 49, 0xFF314033);
    text("TP BANK", 16, 13, GREEN);
    text(snapshot.locked ? "LOCKED" : "UNLOCKED", 16, 29, snapshot.locked ? HOT_PINK : TEXT);
    String cap = "Cap " + format(snapshot.maximum);
    text(cap, 304 - fontRendererObj.getStringWidth(cap), 29, MUTED);

    card(16, 56, "BANKED TP", format(snapshot.banked), snapshot.locked ? MUTED : GREEN);
    card(164, 56, "CURRENT TP", format(snapshot.current), PINK);
    drawRect(172, 94, 296, 97, 0xFF0D130E);
    int fill = snapshot.maximum <= 0 ? 0 : (int) (124L * Math.max(0, snapshot.current) / snapshot.maximum);
    drawRect(172, 94, 172 + Math.min(124, fill), 97, 0xFF000000 | PINK);
    if (!snapshot.rules.isEmpty()) {
      text(fontRendererObj.trimStringToWidth(snapshot.rules, 288), 16, 106, MUTED);
    }

    text("AMOUNT TO WITHDRAW", 16, 120, MUTED);
    drawRect(16, 132, 156, 153, CARD_BRIGHT);
    long amount = parse(amountField.getText());
    boolean bad = amount < 0 || (amount > 0 && !affordable(amount));
    drawRect(16, 132, 18, 153, 0xFF000000 | (bad || snapshot.locked ? HOT_PINK : GREEN));
    amountField.drawTextBox();
    text(fontRendererObj.trimStringToWidth(previewLine(amount), 288), 16, 160, bad ? HOT_PINK : MUTED);

    drawRect(16, 172, 304, 198, error ? 0xFF2F1C34 : 0xFF172719);
    drawRect(16, 172, 18, 198, error ? 0xFF000000 | HOT_PINK : 0xFF000000 | GREEN);
    fontRendererObj.drawSplitString(status, 24, 175, 276, error ? HOT_PINK : MUTED);
    super.drawScreen(x, y, partialTicks);
    GL11.glPopMatrix();
    GL11.glColor4f(1, 1, 1, 1);
  }

  private String previewLine(long amount) {
    if (snapshot.locked) {
      return "Withdrawals are unavailable while the bank is locked.";
    }
    if (room() <= 0) {
      return "You are at the TP cap. Spend TP before withdrawing.";
    }
    if (amount < 0) {
      return "Enter a whole number, e.g. 1500000 or 1.5m.";
    }
    if (amount == 0) {
      return snapshot.banked > 0 ? "Max withdrawal now: " + format(snapshot.withdrawable) + " TP." : "Your bank is empty.";
    }
    if (!affordable(amount)) {
      return "Not enough TP in the bank. Max now: " + format(snapshot.withdrawable) + " TP.";
    }
    long take = preview(amount);
    long fee = cost(take) - take;
    return "You will receive " + format(take) + " TP" + (take < amount ? " (TP cap)" : "")
        + (fee > 0 ? ", fee " + format(fee) + " TP." : ".");
  }

  private void card(int x, int y, String title, String value, int accent) {
    drawRect(x, y, x + 140, y + 46, CARD);
    drawRect(x, y, x + 140, y + 2, 0xFF000000 | accent);
    text(title, x + 8, y + 9, accent);
    text(fontRendererObj.trimStringToWidth(value, 124), x + 8, y + 24, TEXT);
  }

  private static String format(long value) {
    return String.format("%,d", value);
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
      TpBankNetwork.channel.sendToServer(new TpBankRequest(snapshot.session, REQUEST_IDS.incrementAndGet(), TpBankRequest.CLOSE, 0L));
    }
  }
}
