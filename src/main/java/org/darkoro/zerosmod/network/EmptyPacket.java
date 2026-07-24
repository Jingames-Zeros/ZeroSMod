package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

/** Base event class for 'empty' packets */
public abstract class EmptyPacket implements IMessage {

  @Override public final void fromBytes(ByteBuf buf) {}
  @Override public final void toBytes(ByteBuf buf) {}

}
