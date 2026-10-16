package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.darkoro.zerosmod.ZeroSMod;

public class SyncKiAttackDestroyerColorPacketHandler
    implements IMessageHandler<SyncKiAttackDestroyerColorPacket, IMessage> {

  @Override public IMessage onMessage(SyncKiAttackDestroyerColorPacket message, MessageContext ctx) {
    ZeroSMod.proxy.applyKiAttackDestroyerColorSync(message.entityId, message.rgb);
    return null;
  }
}
