package com.jingames.zeros.guis.clientside;

import com.jingames.zeros.guis.serverside.GUIContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CustomChestGUI extends GuiContainer {

  private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
  private IInventory upInv;
  private InventoryPlayer plyInv;
  private int numRows;


  public CustomChestGUI(InventoryPlayer playerInv, IInventory inv) {
    super(new GUIContainer(playerInv, inv, null));
    this.upInv = inv;
    this.plyInv = playerInv;
    this.numRows = inv.getSizeInventory() / 9;

    this.xSize = 176;
    this.ySize = 114 + this.numRows * 18;
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    String customInventoryTitle = this.upInv.hasCustomInventoryName() ?
        this.upInv.getInventoryName() : "Fallback Title";
    this.fontRendererObj.drawString(customInventoryTitle, 8, 6, 4210752);
    this.fontRendererObj.drawString(
        this.plyInv.hasCustomInventoryName() ? this.plyInv.getInventoryName() :
            I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);

    int guiLeft = (this.width - this.xSize) / 2;
    int guiTop = (this.height - this.ySize) / 2;

    int topHeight = this.numRows * 18 + 17;

    this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, topHeight);
    this.drawTexturedModalRect(guiLeft, guiTop + topHeight, 0, 126, this.xSize, 96);
  }
}
