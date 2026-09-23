package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import org.darkoro.zerosmod.config.RaceStatConfig;

/** Fixed-size, server-validated request. Clients never supply paths or property names. */
public class RaceStatRequest implements IMessage {
  public int requestId;
  public int race;
  public int classId;
  public boolean save;
  public byte[] revision;
  public float[] values;

  public RaceStatRequest() {}

  public RaceStatRequest(int requestId, int race, int classId, byte[] revision, float[] values) {
    this.requestId = requestId;
    this.race = race;
    this.classId = classId;
    this.save = values != null;
    this.revision = revision;
    this.values = values;
  }

  @Override public void fromBytes(ByteBuf buf) {
    requestId = buf.readInt();
    race = buf.readUnsignedByte();
    classId = buf.readUnsignedByte();
    save = buf.readBoolean();
    if (save) {
      revision = new byte[32];
      buf.readBytes(revision);
      values = new float[RaceStatConfig.STATS.length];
      for (int i = 0; i < values.length; i++) values[i] = buf.readFloat();
    }
  }

  @Override public void toBytes(ByteBuf buf) {
    buf.writeInt(requestId);
    buf.writeByte(race);
    buf.writeByte(classId);
    buf.writeBoolean(save);
    if (save) {
      buf.writeBytes(revision);
      for (float value : values) buf.writeFloat(value);
    }
  }

  public static class Handler implements IMessageHandler<RaceStatRequest, IMessage> {
    @Override public IMessage onMessage(RaceStatRequest message, MessageContext ctx) {
      RaceStatEditorServer.enqueue(ctx.getServerHandler().playerEntity, message);
      return null;
    }
  }
}
