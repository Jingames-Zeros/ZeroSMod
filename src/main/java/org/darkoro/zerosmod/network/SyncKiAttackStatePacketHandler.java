package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.darkoro.zerosmod.ZeroSMod;

public class SyncKiAttackStatePacketHandler implements IMessageHandler<SyncKiAttackStatePacket, IMessage> {

  @Override public IMessage onMessage(SyncKiAttackStatePacket message, MessageContext ctx) {
    ZeroSMod.proxy.applyKiAttackStateSync(message);
    return null;
  }
}
