package com.jingames.zeros.network;

import com.jingames.zeros.guis.clientside.ClientGuiDataCache;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SyncGuiTitlePacketHandler implements IMessageHandler<SyncGuiTitlePacket, IMessage> {

  @Override
  public IMessage onMessage(SyncGuiTitlePacket message, MessageContext ctx) {
    ClientGuiDataCache.storeTitle(message.ctxId, message.title);
    return null;
  }

}
