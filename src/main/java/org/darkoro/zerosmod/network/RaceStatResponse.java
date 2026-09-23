package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.config.RaceStatConfig;

public class RaceStatResponse implements IMessage {
  public int requestId;
  public int race;
  public int classId;
  public boolean open;
  public String message;
  public RaceStatConfig.Snapshot snapshot;

  public RaceStatResponse() {}

  public RaceStatResponse(int requestId, int race, int classId, boolean open,
      String message, RaceStatConfig.Snapshot snapshot) {
    this.requestId = requestId;
    this.race = race;
    this.classId = classId;
    this.open = open;
    this.message = message;
    this.snapshot = snapshot;
  }

  @Override public void fromBytes(ByteBuf buf) {
    requestId = buf.readInt();
    race = buf.readUnsignedByte();
    classId = buf.readUnsignedByte();
    open = buf.readBoolean();
    message = ByteBufUtils.readUTF8String(buf);
    if (buf.readBoolean()) {
      byte[] revision = new byte[32];
      buf.readBytes(revision);
      float[] values = new float[RaceStatConfig.STATS.length];
      for (int i = 0; i < values.length; i++) values[i] = buf.readFloat();
      snapshot = new RaceStatConfig.Snapshot(revision, values);
    }
  }

  @Override public void toBytes(ByteBuf buf) {
    buf.writeInt(requestId);
    buf.writeByte(race);
    buf.writeByte(classId);
    buf.writeBoolean(open);
    ByteBufUtils.writeUTF8String(buf, message);
    buf.writeBoolean(snapshot != null);
    if (snapshot != null) {
      buf.writeBytes(snapshot.revision);
      for (float value : snapshot.values) buf.writeFloat(value);
    }
  }

  public static class Handler implements IMessageHandler<RaceStatResponse, IMessage> {
    @Override public IMessage onMessage(RaceStatResponse message, MessageContext ctx) {
      ZeroSMod.proxy.receiveRaceStats(message);
      return null;
    }
  }
}
