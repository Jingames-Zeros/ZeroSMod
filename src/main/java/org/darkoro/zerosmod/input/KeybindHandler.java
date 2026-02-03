package org.darkoro.zerosmod.input;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeybindHandler {

  public static KeyBinding spcGui;
  public static KeyBinding chargeSpc;
  public static KeyBinding tournamentGui;

  public static void init() {
    spcGui = new KeyBinding("key.zerosmod.spiritcontrol_gui", Keyboard.KEY_U, "key.categories.zerosmod");
    chargeSpc = new KeyBinding("key.zerosmod.charge_spirit", Keyboard.KEY_O, "key.categories.zerosmod");
    tournamentGui = new KeyBinding("key.zerosmod.tournament_gui", Keyboard.KEY_Q, "key.categories.zerosmod");
    ClientRegistry.registerKeyBinding(spcGui);
    ClientRegistry.registerKeyBinding(chargeSpc);
    ClientRegistry.registerKeyBinding(tournamentGui);
  }

}
