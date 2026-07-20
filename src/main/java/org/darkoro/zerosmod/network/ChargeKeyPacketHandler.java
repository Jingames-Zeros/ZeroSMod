package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayerMP;
import org.darkoro.zerosmod.event.ChargeKeyEvent;

public class ChargeKeyPacketHandler extends PostEventPacketHandler<ChargeKeyPacket> {

  @Override protected Event createEvent(EntityPlayerMP player, ChargeKeyPacket message) {
    return new ChargeKeyEvent(player, message.isHolding());
  }

}
