package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.darkoro.zerosmod.ZeroSMod;

public class SyncKiAttackColorPacketHandler implements IMessageHandler<SyncKiAttackColorPacket, IMessage> {

  @Override public IMessage onMessage(SyncKiAttackColorPacket message, MessageContext ctx) {
    ZeroSMod.proxy.applyKiAttackColorSync(message.entityId, message.color, message.color2);
    return null;
  }
}
