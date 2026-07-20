package org.darkoro.zerosmod.input;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.darkoro.zerosmod.network.ChargeKeyPacket;
import org.darkoro.zerosmod.network.NetworkHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeyInputHandler {

  private final Minecraft mc = Minecraft.getMinecraft();
  private boolean lastChargeHeld = false;

  @SubscribeEvent public void onKeyInput(KeyInputEvent event) {
    if (mc.currentScreen != null) return;
    for (KeybindHandler.PressBinding binding : KeybindHandler.pressBindings) {
      if (binding.key.isPressed()) NetworkHandler.INSTANCE.sendToServer(binding.packetFactory.get());
    }

    boolean chargeHeld = isPhysicallyHeld(KeybindHandler.chargeSpc);
    if (chargeHeld != lastChargeHeld) {
      lastChargeHeld = chargeHeld;
      NetworkHandler.INSTANCE.sendToServer(new ChargeKeyPacket(chargeHeld));
    }
  }

  private static boolean isPhysicallyHeld(KeyBinding binding) {
    return binding.getKeyCode() < 0 ?
        Mouse.isButtonDown(binding.getKeyCode() + 100) :
        Keyboard.isKeyDown(binding.getKeyCode());
  }

}
