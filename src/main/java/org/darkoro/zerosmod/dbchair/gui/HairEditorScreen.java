package org.darkoro.zerosmod.dbchair.gui;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreHJFC;
import JinRyuu.JRMCore.items.ItemBarberSnC;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.Minecraft;
import org.darkoro.zerosmod.mixin.mixins.late.impl.dbc.client.JRMCoreHairEditorAccess;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import JinRyuu.JBRA.ModelBoxJBRA;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/** A visual front end for DBC's existing 786-character custom-hair code. */
public final class HairEditorScreen extends GuiScreen implements GuiYesNoCallback {
  private static final int W = 800, H = 470;
  private static final int GREEN = 0x90E080, PINK = 0xB050D0, HOT_PINK = 0xD986E8;
  private static final int TEXT = 0xF3F8F2, MUTED = 0xB2C0B1;
  private static final int PANEL = 0xC0111813, CARD = 0xA018231A, BRIGHT = 0xB0203222;
  private static final int[] PAGE_START = {0, 4, 14, 24, 40};
  private static final int[] PAGE_END = {4, 14, 24, 40, 56};
  private static final String[] PAGE_NAMES = {"FRONT", "RIGHT", "LEFT", "BACK", "TOP"};
  private static final String[] PARAMS = {"Length", "Rotate X", "Rotate Y", "Rotate Z", "Bend", "Bendpoint", "Transform"};
  private static final String[] PARAM_INFO = {
      "Controls how far this hair clump extends from its base. Set it to 0 to hide the clump.",
      "Rotates the segment around its local X axis.",
      "Rotates the segment around its local Y axis.",
      "Rotates the segment around its local Z axis.",
      "Controls how sharply the selected hair clump bends.",
      "Sets the point along the hair clump where its bend takes effect.",
      "Changes how the hair clump spreads away from its base, adjusting its overall shape."
  };
  private static final int BACK = 100, REVERT = 101, COPY = 103, IMPORT = 104,
      UNDO = 105, REDO = 106, ZOOM_RESET = 108,
      PAGE_BASE = 110, SEGMENT_BASE = 120, SALON = 136, MODE_MANUAL = 137, MODE_SLIDERS = 138,
      RESET_SEGMENT = 139, CYCLE_FORM = 140, PRESETS = 141;
  private static final String SALON_URL = "https://hairsalon.jingames.net/";
  private static final int PREVIEW_X = 16, PREVIEW_Y = 94, PREVIEW_W = 272, PREVIEW_H = 240;
  private static final int SEGMENTS_X = 304, SEGMENTS_Y = 91;
  private static final int VIEW_X = 28, VIEW_Y = 116, VIEW_W = 248, VIEW_H = 216;
  private static final int SLIDER_X = 458, SLIDER_Y = 117, SLIDER_STEP = 104, SLIDER_W = 96, SLIDER_H = 78;
  private static boolean previewDrawActive;
  private static int previewDrawYaw;
  private static int previewFormDrawState;
  private static int previewSelectedSegment;
  private static final float[] previewHairHitMinX = new float[56];
  private static final float[] previewHairHitMinY = new float[56];
  private static final float[] previewHairHitMaxX = new float[56];
  private static final float[] previewHairHitMaxY = new float[56];
  private static final float[] previewHairHitDepth = new float[56];
  private static final boolean[] previewHairHitValid = new boolean[56];
  private static final FloatBuffer previewModelMatrix = BufferUtils.createFloatBuffer(16);
  private static final FloatBuffer previewProjectionMatrix = BufferUtils.createFloatBuffer(16);
  private static final FloatBuffer previewWindowPosition = BufferUtils.createFloatBuffer(3);
  private static final IntBuffer previewViewport = BufferUtils.createIntBuffer(16);
  private static HairEditorScreen previewHitScreen;

  private final JRMCoreGuiScreen origin;
  private final String originalCode;
  private final Deque<String> undo = new ArrayDeque<String>();
  private final Deque<String> redo = new ArrayDeque<String>();
  private final GuiTextField[] values = new GuiTextField[7];
  private int page;
  private int selected;
  private float scale, ox, oy;
  private boolean draggingPreview;
  private boolean previewDragMoved;
  private int previewClickX, previewClickY;
  private boolean slidersMode = true;
  private int activeSlider = -1;
  private String sliderDragStartCode;
  private int lastDragX, lastDragY;
  private int previewYaw;
  private float previewPitch;
  private int previewZoom = 150;
  private int previewForm = -1;
  private EntityLivingBase previewFormEntity;
  private String status = "Select a hair segment, then edit its shape controls.";
  private boolean error;

  public HairEditorScreen(JRMCoreGuiScreen origin, int startingPage, String code) {
    this.origin = origin;
    this.page = Math.max(0, Math.min(4, startingPage));
    String migrated = normalize(code);
    this.originalCode = migrated;
    this.selected = PAGE_START[this.page];
    setCode(migrated, false);
  }

  @Override public void initGui() {
    // GuiScreen.mc is assigned by Minecraft after the screen constructor returns.
    HairPresetStore.ensureLoaded(mc.mcDataDir, getCode());
    Keyboard.enableRepeatEvents(true);
    scale = Math.min(1F, Math.min((width - 12F) / W, (height - 12F) / H));
    ox = (width - W * scale) / 2F;
    oy = (height - H * scale) / 2F;
    buttonList.clear();
    for (int i = 0; i < PAGE_NAMES.length; i++) add(PAGE_BASE + i, 16 + i * 74, 50, 68, 19,
        PAGE_NAMES[i], page == i ? GREEN : MUTED);
    for (int i = 0; i < PAGE_END[page] - PAGE_START[page]; i++) {
      int col = i % 4, row = i / 4;
      add(SEGMENT_BASE + i, SEGMENTS_X + col * 33, SEGMENTS_Y + row * 38, 30, 32,
          String.format("%02d", PAGE_START[page] + i), selected == PAGE_START[page] + i ? GREEN : MUTED);
    }
    add(MODE_SLIDERS, 458, 94, 78, 18, "SLIDERS", slidersMode ? PINK : MUTED);
    add(MODE_MANUAL, 540, 94, 78, 18, "MANUAL", slidersMode ? MUTED : PINK);
    add(PRESETS, 622, 94, 154, 18, "PRESETS  >", GREEN);
    if (getCode().length() == 786) add(RESET_SEGMENT, 640, 343, 124, 18, "RESET SEGMENT", PINK);
    ensurePreviewForm(previewEntity());
    add(CYCLE_FORM, PREVIEW_X, 343, PREVIEW_W, 18, "Form: " + previewFormName() + "  >", MUTED);
    String[] labels = {"Apply", "Revert", "Copy code", "Import code", "Undo", "Redo", "Hair Salon"};
    int[] ids = {BACK, REVERT, COPY, IMPORT, UNDO, REDO, SALON};
    int[] xs = {572, 678, 110, 220, 330, 414, 16};
    int[] ws = {96, 106, 102, 102, 76, 76, 86};
    for (int i = 0; i < ids.length; i++) add(ids[i], xs[i], 432, ws[i], 22, labels[i],
        i == 0 || ids[i] == COPY ? GREEN : (ids[i] == SALON || ids[i] == IMPORT) ? PINK : MUTED);
    add(ZOOM_RESET, 174, 97, 76, 16, "RESET VIEW", GREEN);
    for (int i = 0; i < values.length; i++) {
      int x = SLIDER_X + (i % 3) * SLIDER_STEP + 5;
      int y = 117 + (i / 3) * SLIDER_H;
      String value = values[i] == null ? "" : values[i].getText();
      values[i] = new GuiTextField(fontRendererObj, x, y + 17, 80, 16);
      values[i].setMaxStringLength(3);
      values[i].setText(value);
      values[i].setTextColor(TEXT);
      values[i].setDisabledTextColour(MUTED);
    }
    refreshValues();
  }

  private void add(int id, int x, int y, int w, int h, String label, int accent) {
    buttonList.add(new HairEditorButton(id, x, y, w, h, label, accent));
  }

  private static String normalize(String code) {
    if (code == null) return "";
    code = code.trim();
    if (code.length() != 392 && code.length() != 784 && code.length() != 786) return "";
    for (int i = 0; i < code.length(); i++) {
      if (code.charAt(i) < '0' || code.charAt(i) > '9') return "";
    }
    if (code.length() == 392 || code.length() == 784) code = JRMCoreH.dnsHairG1toG2(code);
    return code.length() == 786 ? code : "";
  }

  private String getCode() {
    String code = JRMCoreHairEditorAccess.zerosmod$getHairCode();
    return code == null ? "" : code;
  }

  public String currentHairCode() { return getCode(); }

  private void setCode(String code, boolean remember) {
    if (code == null || code.length() != 786) return;
    String before = getCode();
    if (before.equals(code)) {
      syncLive(code);
      refreshValues();
      return;
    }
    if (remember && before.length() == 786) {
      undo.push(before);
      while (undo.size() > 50) undo.removeLast();
      redo.clear();
    }
    JRMCoreHairEditorAccess.zerosmod$setHairCode(code);
    syncLive(code);
    refreshValues();
  }

  private void syncLive(String code) {
    Entity target = ItemBarberSnC.barberTarget;
    if (JRMCoreH.JFC() && target != null && JRMCoreHJFC.isChildNPC(target)) {
      JRMCoreHJFC.childDNSHset(target, code);
    } else {
      JRMCoreH.jrmcDataFC(1, code);
    }
    JRMCoreHairEditorAccess.zerosmod$setLastSentHairCode(code);
  }

  private void refreshValues() {
    String code = getCode();
    if (code.length() != 786 || values[0] == null) return;
    for (int i = 0; i < values.length; i++) {
      values[i].setText(Integer.toString(JRMCoreH.dnsHair2(code, selected * 14 + i * 2)));
    }
  }

  private void commitFields() {
    String code = getCode();
    if (code.length() != 786) return;
    String next = code;
    try {
      for (int i = 0; i < values.length; i++) {
        int value = Integer.parseInt(values[i].getText());
        value = i == 0 ? clamp(value, 0, 99) : clamp(value, 18, 82);
        values[i].setText(Integer.toString(value));
        String digits = value < 10 ? "0" + value : Integer.toString(value);
        next = JRMCoreH.dnsHair2set(next, selected * 14 + i * 2, digits);
      }
    } catch (NumberFormatException invalid) {
      status = "Enter whole numbers. Rotations and bend controls use 18–82; length uses 0–99.";
      error = true;
      return;
    }
    if (!next.equals(code)) setCode(next, true);
    status = "Segment " + selected + " updated. Changes preview live on your character.";
    error = false;
  }

  @Override protected void actionPerformed(GuiButton button) {
    if (!button.enabled) return;
    if (button.id >= PAGE_BASE && button.id < PAGE_BASE + 5) {
      commitFields();
      page = button.id - PAGE_BASE;
      selected = PAGE_START[page];
      initGui();
    } else if (button.id >= SEGMENT_BASE && button.id < SEGMENT_BASE + 16) {
      commitFields();
      selected = PAGE_START[page] + button.id - SEGMENT_BASE;
      initGui();
    } else if (button.id == BACK) {
      commitFields();
      applyToCharacter();
    } else if (button.id == REVERT) {
      setCode(originalCode, true);
      status = "Restored the hair code from when this editor opened.";
      error = false;
    } else if (button.id == COPY) {
      GuiScreen.setClipboardString(getCode());
      status = "Copied the original DBC hair code format to the clipboard.";
      error = false;
    } else if (button.id == IMPORT) {
      String imported = normalize(GuiScreen.getClipboardString());
      if (imported.isEmpty()) {
        status = "Clipboard does not contain a DBC hair code (392, 784, or 786 digits).";
        error = true;
      } else {
        setCode(imported, true);
        status = "Imported code and converted it to DBC's current 786-character format.";
        error = false;
      }
    } else if (button.id == UNDO && !undo.isEmpty()) {
      redo.push(getCode());
      setCode(undo.pop(), false);
      status = "Undid the last hair change.";
    } else if (button.id == REDO && !redo.isEmpty()) {
      undo.push(getCode());
      setCode(redo.pop(), false);
      status = "Restored the undone hair change.";
    } else if (button.id == ZOOM_RESET) {
      previewZoom = 150;
      previewYaw = 0;
      previewPitch = 0;
      status = "Preview view reset.";
    } else if (button.id == MODE_MANUAL || button.id == MODE_SLIDERS) {
      commitFields();
      slidersMode = button.id == MODE_SLIDERS;
      initGui();
    } else if (button.id == RESET_SEGMENT) {
      resetSelectedSegment();
    } else if (button.id == CYCLE_FORM) {
      commitFields();
      ensurePreviewForm(previewEntity());
      previewForm = (previewForm + 1) % previewFormCount();
      status = "Preview form: " + previewFormName() + ". This changes only the editor preview.";
      error = false;
      initGui();
    } else if (button.id == PRESETS) {
      commitFields();
      HairPresetStore.ensureLoaded(mc.mcDataDir, getCode());
      mc.displayGuiScreen(new HairPresetScreen(this));
    } else if (button.id == SALON) {
      mc.displayGuiScreen(new GuiConfirmOpenLink(this, SALON_URL, SALON, true));
    }
  }

  private void returnToBarber() {
    origin.guiID = 8;
    mc.displayGuiScreen(origin);
  }

  private void applyToCharacter() {
    if (error || getCode().length() != 786) return;
    JRMCoreHairEditorAccess.zerosmod$setHairCode(getCode());
    Entity target = ItemBarberSnC.barberTarget;
    if (!(JRMCoreH.JFC() && target != null && JRMCoreHJFC.isChildNPC(target))) {
      JRMCoreH.dnsH = getCode();
    }
    GuiButton apply = new GuiButton(14, 0, 0, 0, 0, "Apply");
    invokeBarberApply(apply);
  }

  private void invokeBarberApply(GuiButton apply) {
    for (String methodName : new String[] {"func_146284_a", "actionPerformed"}) {
      try {
        java.lang.reflect.Method method = JRMCoreGuiScreen.class.getDeclaredMethod(methodName, GuiButton.class);
        method.setAccessible(true);
        method.invoke(origin, apply);
        return;
      } catch (NoSuchMethodException missingInThisEnvironment) {
        // JRMCore uses its SRG method name in production and its readable name in dev.
      } catch (ReflectiveOperationException failure) {
        status = "DBC could not apply the hair: " + failure.getMessage();
        error = true;
        return;
      }
    }
    status = "Could not find DBC's barber Apply handler.";
    error = true;
  }

  private void resetSelectedSegment() {
    String current = getCode();
    if (current.length() != 786 || originalCode.length() != 786) return;
    int start = selected * 14;
    String reset = current.substring(0, start) + originalCode.substring(start, start + 14)
        + current.substring(start + 14);
    if (reset.equals(current)) {
      status = "Segment " + selected + " already matches its opening value.";
    } else {
      setCode(reset, true);
      status = "Reset segment " + selected + " to its opening value.";
    }
    error = false;
  }

  public int presetCount() {
    return JRMCoreGuiScreen.PresetList == null ? 0 : JRMCoreGuiScreen.PresetList.size();
  }

  public int selectedPreset() { return JRMCoreGuiScreen.HairPrstsSlcted; }

  public boolean presetIsCustom(int index) {
    return HairPresetStore.isCustom(index);
  }

  public String presetDescription(int index) {
    if (index < 0 || index >= presetCount()) return "No preset selected";
    String code = (String) JRMCoreGuiScreen.PresetList.get(index);
    String name = HairPresetStore.nameAt(index);
    return (name == null ? "Preset " + (index + 1) : name) + "  ·  G" + JRMCoreH.dnsHairG(code);
  }

  public void loadPreset(int index) {
    if (index < 0 || index >= presetCount()) return;
    JRMCoreGuiScreen.HairPrstsSlcted = index;
    JRMCoreGuiScreen.HairCstmResetPreset();
    String preset = normalize(JRMCoreHairEditorAccess.zerosmod$getHairCode());
    if (preset.isEmpty()) {
      status = "The selected DBC preset is not a supported hair code.";
      error = true;
      return;
    }
    setCode(preset, true);
    // DBC's preset action changes dnsH before setCode runs. Always sync even when strings match.
    syncLive(preset);
    status = "Loaded " + presetDescription(index) + ".";
    error = false;
  }

  public boolean deleteSelectedPreset() {
    if (!presetIsCustom(selectedPreset())) {
      status = "Default DBC presets cannot be deleted.";
      error = true;
      return false;
    }
    int selected = selectedPreset();
    if (!HairPresetStore.delete(selected, mc.mcDataDir)) {
      status = "Could not save the preset deletion to this client.";
      error = true;
      return false;
    }
    String preset = normalize((String) JRMCoreGuiScreen.PresetList.get(JRMCoreGuiScreen.HairPrstsSlcted));
    if (!preset.isEmpty()) setCode(preset, true);
    syncLive(preset);
    status = "Deleted the selected custom preset.";
    error = false;
    return true;
  }

  public boolean saveNamedPreset(String name) {
    String cleanName = name == null ? "" : name.trim();
    if (cleanName.isEmpty() || getCode().length() != 786) return false;
    if (HairPresetStore.add(cleanName, getCode(), mc.mcDataDir) < 0) {
      status = "Could not write the preset to this client's game folder.";
      error = true;
      return false;
    }
    syncLive(getCode());
    status = "Saved “" + cleanName + "” as a new custom preset.";
    error = false;
    return true;
  }

  public void closePresets() { mc.displayGuiScreen(this); }

  @Override public void confirmClicked(boolean accepted, int id) {
    if (id == SALON && accepted) {
      try {
        java.awt.Desktop.getDesktop().browse(new java.net.URI(SALON_URL));
      } catch (Exception failure) {
        status = "Could not open the Hair Salon website: " + failure.getMessage();
        error = true;
      }
    }
    mc.displayGuiScreen(this);
  }

  @Override protected void keyTyped(char c, int key) {
    if (key == Keyboard.KEY_ESCAPE) {
      commitFields();
      returnToBarber();
      return;
    }
    if (!slidersMode) {
      for (GuiTextField field : values) if (field.textboxKeyTyped(c, key)) {
        if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) commitFields();
        return;
      }
    }
  }

  @Override protected void mouseClicked(int mouseX, int mouseY, int button) {
    int x = (int) ((mouseX - ox) / scale), y = (int) ((mouseY - oy) / scale);
    if (!slidersMode) {
      int clickedField = manualFieldAt(x, y);
      for (int i = 0; i < values.length; i++) {
        if (values[i].isFocused() && clickedField != i) {
          commitFields();
          values[i].setFocused(false);
        }
      }
    }
    if (button == 0 && slidersMode) {
      int slider = sliderAt(x, y);
      if (slider >= 0) {
        activeSlider = slider;
        sliderDragStartCode = getCode();
        updateSlider(slider, x);
        return;
      }
    }
    if (button == 0 && x >= VIEW_X && x < VIEW_X + VIEW_W && y >= VIEW_Y && y < VIEW_Y + VIEW_H) {
      draggingPreview = true;
      previewDragMoved = false;
      previewClickX = x;
      previewClickY = y;
      lastDragX = x;
      lastDragY = y;
    }
    super.mouseClicked(x, y, button);
    if (!slidersMode) for (GuiTextField field : values) field.mouseClicked(x, y, button);
  }

  private int manualFieldAt(int x, int y) {
    for (int i = 0; i < values.length; i++) {
      int fieldX = SLIDER_X + (i % 3) * SLIDER_STEP + 5;
      int fieldY = SLIDER_Y + (i / 3) * SLIDER_H + 17;
      if (x >= fieldX && x < fieldX + 80 && y >= fieldY && y < fieldY + 16) return i;
    }
    return -1;
  }

  @Override protected void mouseClickMove(int mouseX, int mouseY, int button, long elapsed) {
    int x = (int) ((mouseX - ox) / scale), y = (int) ((mouseY - oy) / scale);
    if (activeSlider >= 0 && button == 0) {
      updateSlider(activeSlider, x);
    } else if (draggingPreview && button == 0) {
      if (Math.abs(x - lastDragX) + Math.abs(y - lastDragY) > 2) previewDragMoved = true;
      previewYaw = Math.floorMod(previewYaw + (x - lastDragX) * 2, 360);
      previewPitch = clampFloat(previewPitch - (y - lastDragY) * 0.5F, -12F, 12F);
      lastDragX = x;
      lastDragY = y;
    }
    super.mouseClickMove(x, y, button, elapsed);
  }

  @Override protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
    if (button == 0 && activeSlider >= 0) {
      if (sliderDragStartCode != null && !sliderDragStartCode.equals(getCode())) {
        undo.push(sliderDragStartCode);
        while (undo.size() > 50) undo.removeLast();
        redo.clear();
        status = "Slider change applied.";
        error = false;
      }
      activeSlider = -1;
      sliderDragStartCode = null;
    }
    int x = (int) ((mouseX - ox) / scale), y = (int) ((mouseY - oy) / scale);
    if (button == 0 && draggingPreview) {
      if (!previewDragMoved) selectHairAtPreview(previewClickX, previewClickY);
      draggingPreview = false;
    }
    super.mouseMovedOrUp(x, y, button);
  }

  @Override public void handleMouseInput() {
    super.handleMouseInput();
    int wheel = Mouse.getEventDWheel();
    if (wheel == 0) return;
    int mx = Mouse.getEventX() * width / mc.displayWidth;
    int my = height - Mouse.getEventY() * height / mc.displayHeight - 1;
    int x = (int) ((mx - ox) / scale), y = (int) ((my - oy) / scale);
    if (x >= VIEW_X && x < VIEW_X + VIEW_W && y >= VIEW_Y && y < VIEW_Y + VIEW_H) {
      previewZoom = clamp(previewZoom + (wheel > 0 ? 10 : -10), 90, 270);
    } else if (x >= SEGMENTS_X && x < SEGMENTS_X + 136 && y >= SEGMENTS_Y && y < 255) {
      int row = Math.max(0, Math.min(3, (y - SEGMENTS_Y) / 38));
      int col = Math.max(0, Math.min(3, (x - SEGMENTS_X) / 33));
      int next = PAGE_START[page] + row * 4 + col;
      if (next < PAGE_END[page]) {
        commitFields();
        selected = next;
        initGui();
      }
    }
  }

  @Override public void updateScreen() {
    if (!slidersMode) for (GuiTextField field : values) field.updateCursorCounter();
  }

  @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    GL11.glPushMatrix();
    GL11.glTranslatef(ox, oy, 0);
    GL11.glScalef(scale, scale, 1);
    int x = (int) ((mouseX - ox) / scale), y = (int) ((mouseY - oy) / scale);
    drawRect(-3, -3, W + 3, H + 3, 0x50000000);
    drawRect(0, 0, W, H, PANEL);
    drawRect(0, 0, W, 2, 0xFF000000 | GREEN);
    drawRect(16, 42, 784, 43, 0xFF314033);
    text("DBC HAIR WORKSHOP", 16, 12, GREEN);
    text("CUSTOM HAIR EDITOR", 16, 27, TEXT);
    text("56 editable segments  |  live preview  |  DBC code stays shareable", 388, 27, MUTED);
    for (int i = 0; i < PAGE_NAMES.length; i++) {
      GuiButton b = find(PAGE_BASE + i);
      drawRect(b.xPosition, b.yPosition + b.height - 2, b.xPosition + b.width,
          b.yPosition + b.height, 0xFF000000 | (page == i ? GREEN : 0xFF4A564B));
    }
    drawRect(PREVIEW_X, 81, PREVIEW_X + PREVIEW_W, 82, 0xB0314033);
    text("HAIR PREVIEW", 16, 73, MUTED);
    drawRect(PREVIEW_X, PREVIEW_Y, PREVIEW_X + PREVIEW_W, PREVIEW_Y + PREVIEW_H, 0xFF0B100D);
    drawRect(PREVIEW_X, PREVIEW_Y, PREVIEW_X + PREVIEW_W, PREVIEW_Y + 2, 0xFF000000 | GREEN);
    drawRect(PREVIEW_X, PREVIEW_Y, PREVIEW_X + 2, PREVIEW_Y + PREVIEW_H, 0xFF000000 | GREEN);
    drawRect(VIEW_X, VIEW_Y, VIEW_X + VIEW_W, VIEW_Y + VIEW_H, 0xFF101713);
    drawCharacterPreview(x, y);
    drawRect(SEGMENTS_X, 81, SEGMENTS_X + 136, 82, 0xB0314033);
    text(PAGE_NAMES[page] + " SEGMENTS", SEGMENTS_X, 73, MUTED);
    drawRect(SEGMENTS_X, 88, SEGMENTS_X + 136, 258, CARD);
    drawRect(448, 50, 784, 366, CARD);
    drawRect(448, 50, 450, 360, 0xC0000000 | PINK);
    text("SEGMENT " + selected, 462, 62, GREEN);
    text("One selectable strand or clump of hair.", 462, 78, MUTED);
    for (int i = 0; i < values.length; i++) {
      int cx = SLIDER_X + (i % 3) * SLIDER_STEP, cy = 117 + (i / 3) * SLIDER_H;
      drawRect(cx, cy, cx + SLIDER_W, cy + 72, BRIGHT);
      text(PARAMS[i], cx + 5, cy + 4, i == 0 ? GREEN : TEXT);
      drawInfoIcon(cx + SLIDER_W - 15, cy + 3);
      int min = minValue(i), max = maxValue(i);
      if (slidersMode) {
        int value = parseValue(i);
        text(Integer.toString(value), cx + 5, cy + 19, TEXT);
        int tx = cx + 7, tw = 82, ty = cy + 42;
        drawRect(tx, ty, tx + tw, ty + 4, 0xFF405043);
        int knob = tx + (value - min) * tw / (max - min);
        drawRect(tx, ty, knob, ty + 4, 0xFF000000 | GREEN);
        drawRect(knob - 2, ty - 3, knob + 3, ty + 8, 0xFF000000 | PINK);
      } else {
        values[i].drawTextBox();
      }
      text(min + "-" + max, cx + 5, cy + 56, MUTED);
    }
    drawRect(16, 367, 784, 368, 0xB0314033);
    drawRect(16, 380, 784, 414, error ? 0xC02F1C34 : 0xB0172719);
    drawRect(16, 380, 18, 414, 0xFF000000 | (error ? HOT_PINK : GREEN));
    fontRendererObj.drawSplitString(status, 25, 389, 745, error ? HOT_PINK : MUTED);
    super.drawScreen(x, y, partialTicks);
    int infoIndex = hoveredInfoIndex(x, y);
    if (infoIndex >= 0) drawInfoPopup(infoIndex, x, y);
    GL11.glPopMatrix();
    GL11.glColor4f(1, 1, 1, 1);
  }

  private GuiButton find(int id) {
    for (Object entry : buttonList) if (((GuiButton) entry).id == id) return (GuiButton) entry;
    return null;
  }

  private void drawInfoIcon(int x, int y) {
    drawRect(x, y, x + 12, y + 12, 0xFF788278);
    drawCenteredString(fontRendererObj, "i", x + 6, y + 2, 0xFFFFFFFF);
  }

  private int hoveredInfoIndex(int x, int y) {
    for (int i = 0; i < values.length; i++) {
      int cardX = SLIDER_X + (i % 3) * SLIDER_STEP;
      int cardY = SLIDER_Y + (i / 3) * SLIDER_H;
      int iconX = cardX + SLIDER_W - 15;
      if (x >= iconX && x < iconX + 12 && y >= cardY + 3 && y < cardY + 15) return i;
    }
    return -1;
  }

  private void drawInfoPopup(int index, int mouseX, int mouseY) {
    int popupW = 228, popupH = 54;
    int x = clamp(mouseX + 10, 448, W - popupW - 8);
    int y = clamp(mouseY + 12, 52, H - popupH - 8);
    drawRect(x, y, x + popupW, y + popupH, 0xF0141D17);
    drawRect(x, y, x + popupW, y + 2, 0xFF000000 | PINK);
    drawRect(x, y, x + 2, y + popupH, 0xFF000000 | PINK);
    text(PARAMS[index], x + 8, y + 7, HOT_PINK);
    fontRendererObj.drawSplitString(PARAM_INFO[index], x + 8, y + 20, popupW - 16, TEXT);
  }

  private void drawCharacterPreview(int mouseX, int mouseY) {
    EntityLivingBase preview = previewEntity();
    if (preview == null) {
      text("No character", 267, 178, MUTED);
      return;
    }
    int sx = (int) ((ox + VIEW_X * scale) * mc.displayWidth / width);
    int sw = (int) (VIEW_W * scale * mc.displayWidth / width);
    int top = (int) ((oy + VIEW_Y * scale) * mc.displayHeight / height);
    int sh = (int) (VIEW_H * scale * mc.displayHeight / height);
    int sy = mc.displayHeight - top - sh;
    GL11.glEnable(GL11.GL_SCISSOR_TEST);
    GL11.glScissor(sx, sy, sw, sh);
    previewHitScreen = this;
    java.util.Arrays.fill(previewHairHitValid, false);
    int centerX = VIEW_X + VIEW_W / 2;
    int centerY = VIEW_Y + VIEW_H / 2;
    // DBC's helper anchors at the feet; this offset places the eye line near the preview center.
    int renderY = centerY + (int) (previewZoom * 1.38F);
    byte originalCoreState = JRMCoreH.State;
    byte originalCoreState2 = JRMCoreH.State2;
    int originalSelectedState = JRMCoreGuiScreen.StateSlcted;
    int originalHairPreview = JRMCoreGuiScreen.hairPreview;
    int renderForm = previewFormState();
    JRMCoreH.State = (byte) renderForm;
    // DBC uses State2 as the transition's prior/secondary form. Leaving the
    // player's live State2 here makes the preview alternate back to that form.
    JRMCoreH.State2 = (byte) renderForm;
    JRMCoreGuiScreen.StateSlcted = renderForm;
    JRMCoreGuiScreen.hairPreview = previewForm;
    GL11.glPushMatrix();
    GL11.glTranslatef(centerX, centerY, 0);
    GL11.glRotatef(previewPitch, 1, 0, 0);
    GL11.glTranslatef(-centerX, -centerY, 0);
    try {
      previewDrawYaw = previewYaw;
      previewFormDrawState = renderForm;
      previewSelectedSegment = selected;
      previewDrawActive = true;
      JRMCoreGuiScreen.head(centerX, renderY, previewZoom, 0, 0, preview);
    } finally {
      previewDrawActive = false;
      previewHitScreen = null;
      previewFormDrawState = 0;
      GL11.glPopMatrix();
      JRMCoreH.State = originalCoreState;
      JRMCoreH.State2 = originalCoreState2;
      JRMCoreGuiScreen.StateSlcted = originalSelectedState;
      JRMCoreGuiScreen.hairPreview = originalHairPreview;
    }
    GL11.glDisable(GL11.GL_SCISSOR_TEST);
  }

  public static boolean zerosmod$isPreviewDrawActive() { return previewDrawActive; }
  public static int zerosmod$getPreviewYaw() { return previewDrawYaw; }
  public static int zerosmod$getPreviewFormState() {
    return previewFormDrawState;
  }
  public static int zerosmod$getPreviewSelectedSegment() { return previewSelectedSegment; }

  public static void zerosmod$recordPreviewHairHit(int segment, JinRyuu.JBRA.ModelRendererJBRA renderer,
      float modelScale) {
    HairEditorScreen screen = previewHitScreen;
    if (!previewDrawActive || screen == null || segment < 0 || segment >= previewHairHitValid.length) return;
    GL11.glPushMatrix();
    GL11.glTranslatef(renderer.offsetX, renderer.offsetY, renderer.offsetZ);
    boolean rotated = renderer.rotateAngleX != 0 || renderer.rotateAngleY != 0 || renderer.rotateAngleZ != 0;
    boolean translated = renderer.rotationPointX != 0 || renderer.rotationPointY != 0 || renderer.rotationPointZ != 0;
    if (rotated || translated) {
      GL11.glTranslatef(renderer.rotationPointX * modelScale,
          renderer.rotationPointY * modelScale, renderer.rotationPointZ * modelScale);
      GL11.glRotatef((float) Math.toDegrees(renderer.rotateAngleZ), 0, 0, 1);
      GL11.glRotatef((float) Math.toDegrees(renderer.rotateAngleY), 0, 1, 0);
      GL11.glRotatef((float) Math.toDegrees(renderer.rotateAngleX), 1, 0, 0);
      GL11.glScalef(renderer.sizeXZ, renderer.lengthY, renderer.sizeXZ);
    }
    previewModelMatrix.clear();
    previewProjectionMatrix.clear();
    previewViewport.clear();
    GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, previewModelMatrix);
    GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, previewProjectionMatrix);
    GL11.glGetInteger(GL11.GL_VIEWPORT, previewViewport);
    float minX = Float.POSITIVE_INFINITY, minY = Float.POSITIVE_INFINITY;
    float maxX = Float.NEGATIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;
    float minDepth = Float.POSITIVE_INFINITY;
    for (Object object : renderer.cubeList) {
      if (!(object instanceof ModelBoxJBRA)) continue;
      ModelBoxJBRA box = (ModelBoxJBRA) object;
      for (int corner = 0; corner < 8; corner++) {
        float bx = (corner & 1) == 0 ? box.posX1 : box.posX2;
        float by = (corner & 2) == 0 ? box.posY1 : box.posY2;
        float bz = (corner & 4) == 0 ? box.posZ1 : box.posZ2;
        previewWindowPosition.clear();
        if (!GLU.gluProject(bx * modelScale, by * modelScale, bz * modelScale,
            previewModelMatrix, previewProjectionMatrix, previewViewport, previewWindowPosition)) continue;
        float guiX = previewWindowPosition.get(0) * screen.width / Minecraft.getMinecraft().displayWidth;
        float guiY = screen.height - previewWindowPosition.get(1) * screen.height / Minecraft.getMinecraft().displayHeight;
        float hitX = (guiX - screen.ox) / screen.scale;
        float hitY = (guiY - screen.oy) / screen.scale;
        minX = Math.min(minX, hitX);
        maxX = Math.max(maxX, hitX);
        minY = Math.min(minY, hitY);
        maxY = Math.max(maxY, hitY);
        minDepth = Math.min(minDepth, previewWindowPosition.get(2));
      }
    }
    GL11.glPopMatrix();
    if (minX <= maxX && minY <= maxY) {
      previewHairHitMinX[segment] = minX;
      previewHairHitMinY[segment] = minY;
      previewHairHitMaxX[segment] = maxX;
      previewHairHitMaxY[segment] = maxY;
      previewHairHitDepth[segment] = minDepth;
      previewHairHitValid[segment] = true;
    }
  }

  private void selectHairAtPreview(int x, int y) {
    int nearest = -1;
    float nearestDistance = Float.POSITIVE_INFINITY;
    float nearestDepth = Float.POSITIVE_INFINITY;
    for (int segment = 0; segment < previewHairHitValid.length; segment++) {
      if (!previewHairHitValid[segment]) continue;
      float dx = Math.max(0, Math.max(previewHairHitMinX[segment] - x, x - previewHairHitMaxX[segment]));
      float dy = Math.max(0, Math.max(previewHairHitMinY[segment] - y, y - previewHairHitMaxY[segment]));
      float distance = dx * dx + dy * dy;
      if (distance <= 12F * 12F && (distance < nearestDistance
          || (distance == nearestDistance && previewHairHitDepth[segment] < nearestDepth))) {
        nearestDistance = distance;
        nearestDepth = previewHairHitDepth[segment];
        nearest = segment;
      }
    }
    if (nearest < 0) return;
    commitFields();
    for (int i = 0; i < PAGE_START.length; i++) {
      if (nearest >= PAGE_START[i] && nearest < PAGE_END[i]) {
        page = i;
        break;
      }
    }
    selected = nearest;
    status = "Selected strand " + (nearest + 1) + " from the preview.";
    error = false;
    initGui();
  }

  private EntityLivingBase previewEntity() {
    Entity target = ItemBarberSnC.barberTarget;
    return target instanceof EntityLivingBase ? (EntityLivingBase) target : mc.thePlayer;
  }

  private void ensurePreviewForm(EntityLivingBase entity) {
    if (entity == null) return;
    if (previewFormEntity != entity || previewForm < 0) {
      previewFormEntity = entity;
      previewForm = 0;
      int state = entity.getEntityData().getByte("jrmcState");
      int[] states = JRMCoreGuiScreen.hairPreviewStates;
      if (states != null) for (int i = 0; i < states.length; i++) {
        if (states[i] == state) { previewForm = i; break; }
      }
    }
    previewForm = Math.max(0, Math.min(previewFormCount() - 1, previewForm));
  }

  private int previewFormCount() {
    int[] states = JRMCoreGuiScreen.hairPreviewStates;
    return states == null ? 4 : Math.max(1, states.length);
  }

  private int previewFormState() {
    int[] states = JRMCoreGuiScreen.hairPreviewStates;
    return states != null && previewForm >= 0 && previewForm < states.length ? states[previewForm] : previewForm;
  }

  private String previewFormName() {
    if (previewForm == 0) return "Base";
    return "Super " + previewForm;
  }

  private int sliderAt(int x, int y) {
    if (x < SLIDER_X || x >= SLIDER_X + 2 * SLIDER_STEP + SLIDER_W || y < SLIDER_Y || y >= SLIDER_Y + 3 * SLIDER_H) return -1;
    int col = (x - SLIDER_X) / SLIDER_STEP, row = (y - SLIDER_Y) / SLIDER_H;
    int index = row * 3 + col;
    int trackY = SLIDER_Y + row * SLIDER_H + 39;
    return index < values.length && y >= trackY && y < trackY + 18 ? index : -1;
  }

  private void updateSlider(int index, int mouseX) {
    int min = minValue(index), max = maxValue(index);
    int tx = SLIDER_X + (index % 3) * SLIDER_STEP + 7;
    int value = min + clamp(mouseX - tx, 0, 82) * (max - min) / 82;
    value = clamp(value, min, max);
    String code = getCode();
    String digits = value < 10 ? "0" + value : Integer.toString(value);
    setCode(JRMCoreH.dnsHair2set(code, selected * 14 + index * 2, digits), false);
  }

  private int parseValue(int index) {
    try { return clamp(Integer.parseInt(values[index].getText()), minValue(index), maxValue(index)); }
    catch (NumberFormatException invalid) { return minValue(index); }
  }

  private int minValue(int index) { return index == 0 ? 0 : 18; }
  private int maxValue(int index) { return index == 0 ? 99 : 82; }
  private float clampFloat(float value, float min, float max) { return Math.max(min, Math.min(max, value)); }

  private void text(String value, int x, int y, int color) {
    fontRendererObj.drawString(value, x, y, color);
  }

  private int clamp(int value, int min, int max) { return Math.max(min, Math.min(max, value)); }

  @Override public boolean doesGuiPauseGame() { return false; }

  @Override public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
  }
}
