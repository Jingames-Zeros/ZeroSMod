package org.darkoro.guiapi.guis;

import org.darkoro.guiapi.GenericGuiApi;
import org.darkoro.guiapi.callbacks.GuiContextManager;
import org.darkoro.guiapi.callbacks.IAnvilGuiCallbacks;
import org.darkoro.guiapi.callbacks.IChestGuiCallbacks;
import org.darkoro.guiapi.callbacks.IGuiContextProvider;
import org.darkoro.guiapi.guis.clientside.ClientGuiDataCache;
import org.darkoro.guiapi.guis.clientside.CustomAnvilGUI;
import org.darkoro.guiapi.guis.clientside.CustomChestGUI;
import org.darkoro.guiapi.guis.serverside.CustomAnvilContainer;
import org.darkoro.guiapi.guis.serverside.GUIContainer;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;

public class GUIHandler implements IGuiHandler {

  public static Logger LOGGER;
  public static final int DELAY_TICKS = 2;

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    IGuiContextProvider contextProvider = GuiContextManager.getContext(player, x);

    if (contextProvider == null) {
      LOGGER.error("No context provider found for player '{}', ID '{}'", player.getCommandSenderName(), x);
      return null;
    }

    if (ID == GenericGuiApi.GENERIC_CHEST_GUI && contextProvider instanceof IChestGuiCallbacks chest) {
      String title = chest.getGuiTitle(player);
      int rows = chest.getNumberOfRows(player);
      InventoryBasic inv = new InventoryBasic(title, true, rows * 9);
      chest.populateSlots(player, inv);
      return new GUIContainer(player.inventory, inv, chest);
    } else if (ID == GenericGuiApi.GENERIC_ANVIL_GUI && contextProvider instanceof IAnvilGuiCallbacks anvil) {
      return new CustomAnvilContainer(player.inventory, world, x, y, z, player, anvil);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    String title = "Loading...";
    if (ClientGuiDataCache.getTitle(x) != null) {
      title = ClientGuiDataCache.getTitle(x);
      ClientGuiDataCache.removeTitle(x);
    }

    if (ID == GenericGuiApi.GENERIC_CHEST_GUI) {
      InventoryBasic dummy = new InventoryBasic(title, true, z * 9);
      return new CustomChestGUI(player.inventory, dummy);
    } else if (ID == GenericGuiApi.GENERIC_ANVIL_GUI) {
      return new CustomAnvilGUI(player.inventory, world, 0, 0, 0, player, title);
    }
    return null;
  }

}
