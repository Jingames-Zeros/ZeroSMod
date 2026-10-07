package org.darkoro.zerosmod.finisher;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import org.darkoro.zerosmod.ZeroSMod;

public class FinisherCameraPacket implements IMessage {

  public int targetId;
  public int ticks;
  public int preset;

  public FinisherCameraPacket() {}

  public FinisherCameraPacket(int targetId, int ticks, int preset) {
    this.targetId = targetId;
    this.ticks = ticks;
    this.preset = preset;
  }

  public boolean isStop() {
    return ticks <= 0;
  }

  @Override public void fromBytes(ByteBuf buf) {
    targetId = buf.readInt();
    ticks = buf.readInt();
    preset = buf.readUnsignedByte();
  }

  @Override public void toBytes(ByteBuf buf) {
    buf.writeInt(targetId);
    buf.writeInt(ticks);
    buf.writeByte(preset);
  }

  public static class Handler implements IMessageHandler<FinisherCameraPacket, IMessage> {
    @Override public IMessage onMessage(FinisherCameraPacket message, MessageContext ctx) {
      ZeroSMod.proxy.receiveFinisherCamera(message);
      return null;
    }
  }
}
