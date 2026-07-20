package org.darkoro.zerosmod.callbacks;

import org.darkoro.zerosmod.guis.serverside.GUIContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public interface IChestGuiCallbacks extends IGuiContextProvider {

  @Override String getGuiTitle(EntityPlayer ply);
  int getNumberOfRows(EntityPlayer ply);
  void populateSlots(EntityPlayer ply, InventoryBasic inv);
  void onSlotClick(EntityPlayer ply, int slotIndex, ItemStack item, GUIContainer container, int mouseButton, int clickType);
  @Override boolean isEditable(EntityPlayer ply);
  default void onGuiClosed(EntityPlayer ply) {}

}
