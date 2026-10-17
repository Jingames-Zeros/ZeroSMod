package org.darkoro.zerosmod.screen;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.network.SyncPlayerScreenPacket;

/** Reports a client's display settings whenever its scaled GUI dimensions change. */
public final class PlayerScreenReporter {

  public static final PlayerScreenReporter INSTANCE = new PlayerScreenReporter();

  private int lastWidth = -1;
  private int lastHeight = -1;
  private int lastGuiScale = -1;

  private PlayerScreenReporter() {}

  @SubscribeEvent
  public void tick(TickEvent.ClientTickEvent event) {
    if (event.phase != TickEvent.Phase.END) {
      return;
    }

    Minecraft minecraft = Minecraft.getMinecraft();
    if (minecraft.thePlayer == null || minecraft.theWorld == null || minecraft.getNetHandler() == null) {
      reset();
      return;
    }

    ScaledResolution resolution =
        new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
    int width = resolution.getScaledWidth();
    int height = resolution.getScaledHeight();
    int guiScale = minecraft.gameSettings.guiScale;
    if (width == lastWidth && height == lastHeight && guiScale == lastGuiScale) {
      return;
    }

    ZeroSMod.network.sendToServer(new SyncPlayerScreenPacket(width, height, guiScale));
    lastWidth = width;
    lastHeight = height;
    lastGuiScale = guiScale;
  }

  @SubscribeEvent
  public void disconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
    reset();
  }

  private void reset() {
    lastWidth = -1;
    lastHeight = -1;
    lastGuiScale = -1;
  }
}
