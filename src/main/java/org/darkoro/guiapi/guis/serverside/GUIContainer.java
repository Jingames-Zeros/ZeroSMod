package org.darkoro.guiapi.guis.serverside;

import org.darkoro.guiapi.callbacks.GuiContextManager;
import org.darkoro.guiapi.callbacks.IChestGuiCallbacks;
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
  private final boolean isInventory;
  private final IChestGuiCallbacks callbacks;

  public GUIContainer(InventoryPlayer plyInv, IInventory inv, IChestGuiCallbacks callbacks) {
    this.callbacks = callbacks;
    this.inv = inv;
    this.ply = plyInv.player;
    this.numRows = inv.getSizeInventory() / 9;
    this.isEditable = this.callbacks == null || callbacks.isEditable(ply);
    this.isInventory = this.callbacks == null || callbacks.isInventory(ply);

    inv.openInventory();

    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < 9; col++) {
        int slotIndex = col + row * 9;
        this.addSlotToContainer(new ButtonSlot(inv, slotIndex, 8 + col * 18, 18 + row * 18, this.isInventory()));
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
    if (!this.isEditable() || !this.isInventory()) return null;
    Slot slot = this.inventorySlots.get(index);
    if (slot == null || !slot.getHasStack()) return null;
    ItemStack stack = slot.getStack();
    ItemStack stackCopy = stack.copy();
    if (index < this.inv.getSizeInventory()) {
      if (!this.mergeItemStack(stack, this.inv.getSizeInventory(), this.inventorySlots.size(), true)) return null;
    } else {
      if (!this.mergeItemStack(stack, 0, this.inv.getSizeInventory(), false)) return null;
    }
    if (stack.stackSize == 0) slot.putStack(null);
    else slot.onSlotChanged();
    return stackCopy;
  }

  /**
   * Override the slot click method to handle custom button clicks
   */
  @Override
  public ItemStack slotClick(int slotId, int clickData, int clickType, EntityPlayer player) {
    if ((!this.isEditable() && !this.isInventory()) ||
        (this.isEditable() && !this.isInventory() && (slotId < 0 || slotId >= inv.getSizeInventory()))) return null;
    if (this.isInventory()) {
      ItemStack result = super.slotClick(slotId, clickData, clickType, player);
      if (this.callbacks != null && !this.ply.worldObj.isRemote)
        this.callbacks.onSlotClick(this.ply, slotId, this.inv.getStackInSlot(slotId), this, clickData, clickType);
      return result;
    }
    if (this.isEditable() && !this.ply.worldObj.isRemote && this.callbacks != null) {
      this.callbacks.onSlotClick(this.ply, slotId, this.inv.getStackInSlot(slotId), this, clickData, clickType);
    }
    return null;
  }

  @Override
  public void onContainerClosed(EntityPlayer player) {
    super.onContainerClosed(player);
    if (this.inv != null) this.inv.closeInventory();
    if (this.callbacks != null && !this.ply.worldObj.isRemote) this.callbacks.onGuiClosed(this.ply);
    GuiContextManager.clearContext(player, this.callbacks);
  }

  public boolean isEditable() {
    return this.isEditable;
  }

  public boolean isInventory() {
    return this.isInventory;
  }

  public IInventory getInv() {
    return this.inv;
  }

  public IChestGuiCallbacks getCallbacks() { return this.callbacks; }

}