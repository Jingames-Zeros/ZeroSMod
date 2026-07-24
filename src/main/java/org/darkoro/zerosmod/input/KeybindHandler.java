package org.darkoro.zerosmod.input;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import java.util.function.Supplier;
import net.minecraft.client.settings.KeyBinding;
import org.darkoro.zerosmod.network.OpenSpcGuiPacket;
import org.lwjgl.input.Keyboard;

public class KeybindHandler {

  // A keybind that fires one packet per press.
  public static final class PressBinding {
    public final KeyBinding key;
    public final Supplier<IMessage> packetFactory;

    private PressBinding(KeyBinding key, Supplier<IMessage> packetFactory) {
      this.key = key;
      this.packetFactory = packetFactory;
    }
  }

  public static KeyBinding spcGui;
  public static KeyBinding chargeSpc;

  public static PressBinding[] pressBindings;

  public static void init() {
    spcGui = register("key.zerosmod.spiritcontrol_gui", Keyboard.KEY_U);
    chargeSpc = register("key.zerosmod.charge_spirit", Keyboard.KEY_O);

    pressBindings = new PressBinding[] {
        new PressBinding(spcGui, OpenSpcGuiPacket::new),
    };
  }

  private static KeyBinding register(String name, int defaultKey) {
    KeyBinding binding = new KeyBinding(name, defaultKey, "key.categories.zerosmod");
    ClientRegistry.registerKeyBinding(binding);
    return binding;
  }

}
