package com.jingames.zeros.guis.serverside;

import com.jingames.zeros.callbacks.GuiContextManager;
import com.jingames.zeros.callbacks.IAnvilGuiCallbacks;
import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class CustomAnvilContainer extends ContainerRepair {

  private final String initialMessage;
  private String newTxtName;
  private final IAnvilGuiCallbacks callbacks;
  private final InventoryPlayer plyInv;

  public CustomAnvilContainer(InventoryPlayer playerInventory, World world, int x, int y, int z, EntityPlayer player, IAnvilGuiCallbacks callbacks) {
    super(playerInventory, world, x, y, z, player);
    this.plyInv = playerInventory;
    this.callbacks = callbacks;
    this.initialMessage = callbacks != null ? callbacks.getInitialMessage(player) : "";
    this.newTxtName = this.initialMessage;

    ItemStack stack = new ItemStack(Blocks.wool);
    stack.setStackDisplayName(this.initialMessage);
    IInventory inputInv = this.getSlot(0).inventory;

    Slot slot0 = new Slot(inputInv, 0, 27, 47) {
      @Override
      public boolean canTakeStack(EntityPlayer playerIn) {
        return false;
      }

      @Override
      public boolean isItemValid(ItemStack stack) {
        return false;
      }
    };

    Slot slot1 = new Slot(inputInv, 1, 76, 47) {
      @Override
      public boolean canTakeStack(EntityPlayer playerIn) {
        return false;
      }

      @Override
      public boolean isItemValid(ItemStack stack) {
        return false;
      }
    };
    this.inventorySlots.set(0, slot0);
    this.inventorySlots.set(1, slot1);

    inputInv.setInventorySlotContents(0, stack);
    inputInv.setInventorySlotContents(1, null);

    this.updateRepairOutput();
  }

  @Override
  public boolean canInteractWith(EntityPlayer playerIn) {
    return true;
  }

  @Override
  public boolean canDragIntoSlot(Slot slotIn) {
    return false;
  }

  @Override
  public void updateRepairOutput() {
    ItemStack slot0 = this.getSlot(0).inventory.getStackInSlot(0);
    this.maximumCost = 0;

    if (slot0 == null || slot0.getDisplayName().trim().equals(this.initialMessage)) {
      this.getSlot(2).putStack(null);
    } else {
      ItemStack slot2 = slot0.copy();

      if (!(this.newTxtName == null || this.newTxtName.trim().isEmpty())) {
        slot2.setStackDisplayName(this.newTxtName);
      } else {
        if (slot0.hasDisplayName() && !this.initialMessage.equals(this.newTxtName)) {
          slot2.setStackDisplayName(this.initialMessage);
        } else if (!slot0.hasDisplayName()) {
          slot2.setStackDisplayName(this.newTxtName);
        }
      }
      this.getSlot(2).inventory.setInventorySlotContents(0, slot2);
    }
    this.detectAndSendChanges();

    for (ICrafting crafter : this.crafters) {
      crafter.sendProgressBarUpdate(this, 0, this.maximumCost);
    }
  }

  @Override
  public void updateItemName(String name) {
    this.newTxtName = name;
    this.updateRepairOutput();
  }

  @Override
  public ItemStack slotClick(int slotID, int dragType, int clickType, EntityPlayer player) {
    if (slotID >= 0 && slotID < this.inventorySlots.size() && this.getSlot(slotID).inventory.equals(this.plyInv)) return null;
    if (slotID == 2 && !player.worldObj.isRemote && this.callbacks != null) {
      String process = this.newTxtName == null ? "" : this.newTxtName.trim();
      if (process.isEmpty()) {
        player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.YELLOW + "Please enter a value."));
        return null;
      }

      if (process.equals(this.initialMessage.trim())) {
        player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.YELLOW + "Value has not been changed."));
        return null;
      }

      Function<String, String> validator = this.callbacks.getInputValidator(player);
      String err = validator != null ? validator.apply(process) : "No validator provided.";

      if (err == null) {
        this.callbacks.getOutputHandler(player).apply(process);
      } else {
        player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.DARK_RED + err));
      }
      return null;
    }
    return super.slotClick(slotID, dragType, clickType, player);
  }



  @Override
  public void onContainerClosed(EntityPlayer playerIn) {
    GuiContextManager.clearContext(playerIn, this.callbacks);
    if (!playerIn.worldObj.isRemote) {
      this.getSlot(0).inventory.setInventorySlotContents(0, null);
      this.getSlot(1).putStack(null);
    }
    super.onContainerClosed(playerIn);
  }

  public String getInitialMessage() {
    return this.initialMessage;
  }

}
