package org.darkoro.zerosmod.guis;

import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.callbacks.GuiContextManager;
import org.darkoro.zerosmod.callbacks.IAnvilGuiCallbacks;
import org.darkoro.zerosmod.callbacks.IChestGuiCallbacks;
import org.darkoro.zerosmod.callbacks.IGuiContextProvider;
import org.darkoro.zerosmod.network.SyncGuiTitlePacket;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.Side;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;

public class GUIScheduler {
  public static final GUIScheduler INSTANCE = new GUIScheduler();
  private final List<DelayedGUI> pendingOpens = Collections.synchronizedList(new ArrayList<>());

  private GUIScheduler() {}

  public void scheduleOpen(DelayedGUI request) {
    if (request.player.worldObj.isRemote) {
      return;
    }

    IGuiContextProvider ctx = GuiContextManager.getContext(request.player, request.x);

    if (ctx instanceof IChestGuiCallbacks chest) {
      ZeroSMod.network.sendTo(new SyncGuiTitlePacket(request.x, chest.getGuiTitle(request.player)), (EntityPlayerMP) request.player);
    } else if (ctx instanceof IAnvilGuiCallbacks anvil) {
      ZeroSMod.network.sendTo(new SyncGuiTitlePacket(request.x, anvil.getGuiTitle(request.player)), (EntityPlayerMP) request.player);
    }

    this.pendingOpens.add(request);
  }

  @SubscribeEvent
  public void onServerTick(ServerTickEvent event) {
    if (event.side == Side.SERVER && event.phase == Phase.END) {
      Iterator<DelayedGUI> iterator = pendingOpens.iterator();
      while (iterator.hasNext()) {
        DelayedGUI request = iterator.next();
        request.ticksToDelay--;

        if (request.ticksToDelay <= 0) {
          if (request.player != null && !request.player.isDead && request.player.worldObj == request.world) {
            request.player.openGui(request.mod, request.guiId, request.world, request.x, request.y, request.z);
          }
          iterator.remove();
        }
      }
    }
  }
}