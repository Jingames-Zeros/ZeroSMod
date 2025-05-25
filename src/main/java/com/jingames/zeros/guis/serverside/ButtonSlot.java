package com.jingames.zeros.guis.serverside;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ButtonSlot extends Slot {
  public ButtonSlot(IInventory inv, int index, int xPos, int yPos) {
    super(inv, index, xPos, yPos);
  }

  @Override
  public boolean canTakeStack(EntityPlayer ply) {
    return false;
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    return false;
  }

}
