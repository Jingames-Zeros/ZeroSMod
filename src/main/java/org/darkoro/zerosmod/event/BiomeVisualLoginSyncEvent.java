package org.darkoro.zerosmod.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.network.BiomeVisualSyncUtil;
import org.darkoro.zerosmod.network.SyncDimensionConfigPacket;

public class BiomeVisualLoginSyncEvent {

  @SubscribeEvent
  public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
    if (!(event.player instanceof EntityPlayerMP)) return;

    EntityPlayerMP player = (EntityPlayerMP) event.player;
    ZeroSMod.network.sendTo(BiomeVisualSyncUtil.buildFullPacket(), player);
    ZeroSMod.network.sendTo(SyncDimensionConfigPacket.buildCurrent(), player);
  }
}
