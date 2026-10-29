package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import org.darkoro.zerosmod.ZeroSMod;

public class MusicPackHintPacket implements IMessage {

  public String url;

  public MusicPackHintPacket() {}

  public MusicPackHintPacket(String url) {
    this.url = url;
  }

  @Override public void fromBytes(ByteBuf buf) {
    this.url = ByteBufUtils.readUTF8String(buf);
  }

  @Override public void toBytes(ByteBuf buf) {
    ByteBufUtils.writeUTF8String(buf, this.url);
  }

  public static class Handler implements IMessageHandler<MusicPackHintPacket, IMessage> {
    @Override public IMessage onMessage(MusicPackHintPacket message, MessageContext ctx) {
      ZeroSMod.proxy.receiveMusicPackHint(message.url);
      return null;
    }
  }
}
