package org.darkoro.zerosmod.input;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraft.client.Minecraft;
import org.darkoro.zskeybinds.network.ChargeKeyPacket;
import org.darkoro.zskeybinds.network.NetworkHandler;
import org.darkoro.zskeybinds.network.OpenSpcGuiPacket;
import org.darkoro.zskeybinds.network.OpenTournamentGuiPacket;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeyInputHandler {

  private final Minecraft mc = Minecraft.getMinecraft();
  private boolean lastChargeHeld = false;

  @SubscribeEvent
  public void onKeyInput(KeyInputEvent event) {
    if (mc.currentScreen != null) return;
    boolean spcGuiPressed = KeybindHandler.spcGui.isPressed();
    boolean tournamentGuiPressed = KeybindHandler.tournamentGui.isPressed();
    boolean chargeHeld = KeybindHandler.chargeSpc.getKeyCode() < 0 ?
        Mouse.isButtonDown(KeybindHandler.chargeSpc.getKeyCode() + 100) :
        Keyboard.isKeyDown(KeybindHandler.chargeSpc.getKeyCode());
    if (spcGuiPressed) NetworkHandler.INSTANCE.sendToServer(new OpenSpcGuiPacket());
    if (tournamentGuiPressed) NetworkHandler.INSTANCE.sendToServer(new OpenTournamentGuiPacket());
    if (chargeHeld != lastChargeHeld) {
      lastChargeHeld = chargeHeld;
      NetworkHandler.INSTANCE.sendToServer(new ChargeKeyPacket(chargeHeld));
    }
  }

}