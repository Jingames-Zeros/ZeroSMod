package com.jingames.zeros.guis.clientside;

import com.jingames.zeros.guis.serverside.CustomAnvilContainer;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class CustomAnvilGUI extends GuiContainer implements ICrafting {

  private static final ResourceLocation anvilResource = new ResourceLocation("textures/gui/container/anvil.png");
  private CustomAnvilContainer customAnvil;
  private GuiTextField itemName;
  private String title;

  public CustomAnvilGUI(InventoryPlayer plyInv, World world, int x, int y, int z, EntityPlayer player, String title) {
    super(new CustomAnvilContainer(plyInv, world, x, y, z, player, null));
    this.customAnvil = (CustomAnvilContainer) this.inventorySlots;
    this.xSize = 176;
    this.ySize = 166;
    this.title = title == null || title.trim().isEmpty() ? I18n.format("container.repair") : title;
  }

  @Override
  public void initGui() {
    super.initGui();
    Keyboard.enableRepeatEvents(true);

    int guiLeft = (this.width - this.xSize) / 2;
    int guiTop = (this.height - this.ySize) / 2;

    this.itemName = new GuiTextField(this.fontRendererObj, guiLeft + 62, guiTop + 24, 103, 12);
    this.itemName.setTextColor(-1);
    this.itemName.setDisabledTextColour(-1);
    this.itemName.setEnableBackgroundDrawing(false);
    this.itemName.setText("");

    this.customAnvil.addCraftingToCrafters(this);
  }

  @Override
  public void onGuiClosed() {
    super.onGuiClosed();
    Keyboard.enableRepeatEvents(false);

    if (this.customAnvil != null) this.customAnvil.removeCraftingFromCrafters(this);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_BLEND);
    this.fontRendererObj.drawString(this.title, 60, 6, 4210752);
    this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.getTextureManager().bindTexture(anvilResource);

    int guiLeft = (this.width - this.xSize) / 2;
    int guiTop = (this.height - this.ySize) / 2;

    this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
    this.drawTexturedModalRect(guiLeft + 59, guiTop + 20, 0, 166, 110, 16);

    if (this.customAnvil.getSlot(2).getStack() == null
        || (this.customAnvil.getSlot(0).getStack() != null
        && this.customAnvil.getSlot(0).getStack().getDisplayName()
            .equals(this.customAnvil.getSlot(2).getStack().getDisplayName()))) {
      this.drawTexturedModalRect(guiLeft + 99, guiTop + 45, 176, 0, 28, 21);
    }
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) {
    if (this.itemName.textboxKeyTyped(typedChar, keyCode)) {
      this.customAnvil.updateItemName(this.itemName.getText());
      PacketBuffer packetbuffer = new PacketBuffer(
          Unpooled.copiedBuffer(this.itemName.getText(), StandardCharsets.UTF_8));
      this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|ItemName", packetbuffer));
    } else {
      super.keyTyped(typedChar, keyCode);
    }
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    this.itemName.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    super.drawScreen(mouseX, mouseY, partialTicks);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_BLEND);
    this.itemName.drawTextBox();
  }

  @Override
  public void sendContainerAndContentsToPlayer(Container cont, List<ItemStack> items) {
    if (!items.isEmpty() && items.get(0) != null) {
      updateNameFromItem(items.get(0));
    } else {
      updateNameFromItem(null);
    }
  }

  @Override
  public void sendSlotContents(Container cont, int slotId, ItemStack stack) {
    if (slotId == 0) updateNameFromItem(stack);
  }

  private void updateNameFromItem(ItemStack stack) {
    if (this.itemName != null) {
      String curTxt = this.itemName.getText();
      String newName = stack != null && stack.hasDisplayName() ? stack.getDisplayName() : "";

      if (!newName.isEmpty() || curTxt.isEmpty() && !newName.equals(curTxt)) {
        this.itemName.setText(newName);
      }
      this.itemName.setEnabled(true);
    }
  }

  @Override
  public void sendProgressBarUpdate(Container cont, int barId, int newVal) {
    if (barId == 0 && this.customAnvil != null) this.customAnvil.maximumCost = newVal;
  }

}
