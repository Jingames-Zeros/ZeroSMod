package com.jingames.zeros.guis.serverside;

import com.jingames.zeros.callbacks.GuiContextManager;
import com.jingames.zeros.callbacks.IChestGuiCallbacks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GUIContainer extends Container {

  private final EntityPlayer ply;
  private final IInventory inv;
  private final int numRows;
  private final boolean isEditable;
  private final IChestGuiCallbacks callbacks;

  public GUIContainer(InventoryPlayer plyInv, IInventory inv, IChestGuiCallbacks callbacks) {
    this.callbacks = callbacks;
    this.inv = inv;
    this.ply = plyInv.player;
    this.numRows = inv.getSizeInventory() / 9;
    this.isEditable = this.callbacks == null || callbacks.isEditable(ply);

    inv.openInventory();

    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < 9; col++) {
        int slotIndex = col + row * 9;
        this.addSlotToContainer(new ButtonSlot(inv, slotIndex, 8 + col * 18, 18 + row * 18));
      }
    }

    int yPos = (this.numRows * 18 + 17) + 14;
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        this.addSlotToContainer(new Slot(plyInv, col + row * 9 + 9, 8 + col * 18, yPos + row * 18));
      }
    }

    yPos = yPos + (3 * 18) + 4;
    for (int col = 0; col < 9; col++) {
      this.addSlotToContainer(new Slot(plyInv, col, 8 + col * 18, yPos));
    }
  }

  @Override
  public boolean canInteractWith(EntityPlayer player) {
    return true;
  }

  /**
   * Called when a player shift-clicks on a slot
   */
  @Override
  public ItemStack transferStackInSlot(EntityPlayer player, int index) {
    return null;
  }

  /**
   * Override the slot click method to handle custom button clicks
   */
  @Override
  public ItemStack slotClick(int slotId, int clickData, int clickType, EntityPlayer player) {
    if (slotId >= 0 && slotId < inv.getSizeInventory()) {
      if (!this.isEditable || this.getSlot(slotId).inventory.equals(this.ply.inventory)) return null;
      if (!this.ply.worldObj.isRemote && this.callbacks != null) {
        this.callbacks.onSlotClick(this.ply, slotId, this.inv.getStackInSlot(slotId), this, clickData, clickType);
      }
      return null;
    }
    return super.slotClick(slotId, clickData, clickType, player);
  }

  @Override
  public void onContainerClosed(EntityPlayer player) {
    super.onContainerClosed(player);
    if (this.inv != null) this.inv.closeInventory();
    GuiContextManager.clearContext(player, this.callbacks);
  }

  public boolean isEditable() {
    return this.isEditable;
  }

  public IInventory getInv() {
    return this.inv;
  }

}