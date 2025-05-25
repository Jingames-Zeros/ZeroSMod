package com.jingames.zeros.guis;

import com.jingames.zeros.GenericGuiApi;
import com.jingames.zeros.callbacks.GuiContextManager;
import com.jingames.zeros.callbacks.IAnvilGuiCallbacks;
import com.jingames.zeros.callbacks.IChestGuiCallbacks;
import com.jingames.zeros.callbacks.IGuiContextProvider;
import com.jingames.zeros.guis.clientside.ClientGuiDataCache;
import com.jingames.zeros.guis.clientside.CustomChestGUI;
import com.jingames.zeros.guis.serverside.CustomAnvilContainer;
import com.jingames.zeros.guis.serverside.GUIContainer;
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
      return new CustomAnvilContainer(player.inventory, world, 0, 0, 0, player, null);
    }
    return null;
  }

}
