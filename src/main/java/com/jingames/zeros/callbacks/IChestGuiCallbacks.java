package com.jingames.zeros.callbacks;

import com.jingames.zeros.guis.serverside.GUIContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public interface IChestGuiCallbacks extends IGuiContextProvider {

  String getGuiTitle(EntityPlayer ply);
  int getNumberOfRows(EntityPlayer ply);
  void populateSlots(EntityPlayer ply, InventoryBasic inv);
  void onSlotClick(EntityPlayer ply, int slotIndex, ItemStack item, GUIContainer container, int mouseButton, int clickType);
  boolean isEditable(EntityPlayer ply);

}
