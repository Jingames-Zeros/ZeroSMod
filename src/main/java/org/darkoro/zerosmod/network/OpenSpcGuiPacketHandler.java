package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayerMP;
import org.darkoro.zerosmod.event.SpiritControlMenuKeyEvent;

public class OpenSpcGuiPacketHandler extends PostEventPacketHandler<OpenSpcGuiPacket> {

  @Override protected Event createEvent(EntityPlayerMP player, OpenSpcGuiPacket message) {
    return new SpiritControlMenuKeyEvent(player);
  }

}
