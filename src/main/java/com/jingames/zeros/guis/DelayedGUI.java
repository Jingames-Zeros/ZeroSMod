package com.jingames.zeros.guis;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;


public class DelayedGUI {

  /**
   * The player the specified GUI will be opened for with Delay
   */
  public final EntityPlayer player;
  /**
   * The Mod instance under which the GUI will be handled
   */
  public final Object mod;
  /**
   * The GUI ID - use GenericApiGui.GENERIC_CHEST_GUI or GenericApiGui.GENERIC_ANVIL_GUI
   */
  public final int guiId;
  /**
   * The world object in which the player resides for the GUI
   */
  public final World world;
  /**
   * The Context ID of the GUI to open
   */
  public final int x;
  /**
   * Y coordinate of the player, unused for the GUIs
   */
  public final int y;
  /**
   * The amount of rows to generate the GUI with
   * <p>
   * Only used for Chest GUIs and only on Client Side
   */
  public final int z;
  /**
   * The number of ticks to delay the GUI opening. Use GUIHandler.DELAY_TICKS for this unless you
   * really need a different value
   */
  public int ticksToDelay;

  public DelayedGUI(EntityPlayer player, Object mod, int guiId, World world, int x, int y, int z, int ticksToDelay) {
    this.player = player;
    this.mod = mod;
    this.guiId = guiId;
    this.world = world;
    this.x = x;
    this.y = y;
    this.z = z;
    this.ticksToDelay = ticksToDelay;
  }

}