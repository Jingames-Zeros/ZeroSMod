package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class OpenSpcGuiPacket implements IMessage {

  public OpenSpcGuiPacket() {}

  @Override public void fromBytes(ByteBuf buf) {}
  @Override public void toBytes(ByteBuf buf) {}

}
