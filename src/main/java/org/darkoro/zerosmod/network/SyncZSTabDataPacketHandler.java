package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.darkoro.zerosmod.tab.ClientZSTabDataCache;

public class SyncZSTabDataPacketHandler implements IMessageHandler<SyncZSTabDataPacket, IMessage> {

  @Override
  public IMessage onMessage(SyncZSTabDataPacket message, MessageContext ctx) {
    ClientZSTabDataCache.update(message);
    return null;
  }
}
