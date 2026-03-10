package org.darkoro.zerosmod.guis.serverside;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ButtonSlot extends Slot {

  private final boolean isInventory;

  public ButtonSlot(IInventory inv, int index, int xPos, int yPos, boolean isInventory) {
    super(inv, index, xPos, yPos);
    this.isInventory = isInventory;
  }

  @Override public boolean canTakeStack(EntityPlayer ply) {
    return isInventory;
  }
  @Override public boolean isItemValid(ItemStack stack) { return isInventory; }

}
