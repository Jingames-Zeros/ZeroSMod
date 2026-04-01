package org.darkoro.guiapi.guis.clientside;

import static cpw.mods.fml.client.config.GuiUtils.drawTexturedModalRect;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import org.darkoro.guiapi.guis.clientside.Spirit.SpiritHudMainMenu;
import org.darkoro.zerosmod.client.ClientCache;
import org.lwjgl.opengl.GL11;

public class ClientEventHandler {
  private Minecraft mc = Minecraft.getMinecraft();
  public static final ResourceLocation Spirit_Bar = new ResourceLocation("zerosmod","textures/gui/spirit.png");

  @SubscribeEvent
  public void onRenderPre(RenderGameOverlayEvent.Post event){
    if(event.type == ElementType.ARMOR) return;
    Minecraft mc = Minecraft.getMinecraft();
    Entity e = mc.renderViewEntity;
    if (e instanceof EntityPlayer) {
      if(ClientCache.spiritConfig.get().isEnabled()){
        ClientCache.spiritbar.renderOverlay((EntityPlayer) e);}
      }
  }
  @SubscribeEvent
  public void onRenderTick(TickEvent.RenderTickEvent e){
    if (ClientCache.isSpiritHudOpen){
      this.mc.displayGuiScreen(new SpiritHudMainMenu(ClientCache.spiritConfig));
      ClientCache.isSpiritHudOpen = false;
    }
  }


}
