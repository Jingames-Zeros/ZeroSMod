package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.darkoro.zerosmod.config.DimensionConfig;

public class SyncDimensionConfigPacketHandler implements IMessageHandler<SyncDimensionConfigPacket, IMessage> {

  @Override public IMessage onMessage(SyncDimensionConfigPacket message, MessageContext ctx) {
    DimensionConfig.applyDimensionValues(
        message.phylacteryVisibility,
        message.phylacteryFixedTime,
        message.phylacterySunBrightness,
        message.dragonRealmVisibility);
    return null;
  }
}
