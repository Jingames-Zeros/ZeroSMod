package org.darkoro.zerosmod.guis.serverside;

import org.darkoro.zerosmod.callbacks.GuiContextManager;
import org.darkoro.zerosmod.callbacks.IChestGuiCallbacks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
    this(plyInv, inv, callbacks,
        callbacks != null && callbacks.isEditable(plyInv.player),
        callbacks != null && callbacks.isInventory(plyInv.player));
  }

  /**
   * Flag-override constructor - client-side container has no callbacks, so  flags must be passed in
   * explicitly (synced from server) — otherwise client and server predict clicks differently
   */
  public GUIContainer(InventoryPlayer plyInv, IInventory inv, IChestGuiCallbacks callbacks,
                      boolean isEditable, boolean isInventory) {
    this.callbacks = callbacks;
    this.inv = inv;
    this.ply = plyInv.player;
    this.numRows = inv.getSizeInventory() / 9;
    this.isEditable = isEditable;
    this.isInventory = isInventory;

    inv.openInventory();

    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < 9; col++) {
        int slotIndex = col + row * 9;
        this.addSlotToContainer(new ButtonSlot(inv, slotIndex, 8 + col * 18, 18 + row * 18, this.isInventory));
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
    if (!this.isEditable || !this.isInventory) return null;
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
    if (this.callbacks != null && !this.ply.worldObj.isRemote)
      this.callbacks.onSlotClick(this.ply, index, stackCopy, this, 0, 0);
    return stackCopy;
  }

  /**
   * Override the slot click method to handle custom button clicks
   */
  @Override
  public ItemStack slotClick(int slotId, int clickData, int clickType, EntityPlayer player) {
    boolean isCustomSlot = slotId >= 0 && slotId < this.inv.getSizeInventory();
    boolean serverSide = !player.worldObj.isRemote;

    if (this.isInventory && this.isEditable) {
      ItemStack result = super.slotClick(slotId, clickData, clickType, player);
      // slotId -999 covers drag-end (mode 5) and click-outside-drop, both of which
      // mutate the custom inventory / cursor and must reach the callback.
      if (serverSide && this.callbacks != null && (isCustomSlot || slotId == -999)) {
        this.callbacks.onSlotClick(this.ply, slotId, result, this, clickData, clickType);
        this.detectAndSendChanges();
        if (player instanceof EntityPlayerMP mp) mp.updateHeldItem();
      }
      return result;
    }

    // GUI mode; window is blocked for all item movement - null returned on both sides for sync
    // Callbacks only fire on normal or shift clicks - drag etc. might blow up
    if (isCustomSlot && this.isEditable && serverSide && this.callbacks != null
        && (clickType == 0 || clickType == 1)) {
      this.callbacks.onSlotClick(this.ply, slotId, this.inv.getStackInSlot(slotId), this, clickData, clickType);
    }
    if (serverSide && player instanceof EntityPlayerMP mp) {
      this.detectAndSendChanges();
      mp.updateHeldItem();
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

  public IInventory getInv() {
    return this.inv;
  }

  public IChestGuiCallbacks getCallbacks() { return this.callbacks; }

}