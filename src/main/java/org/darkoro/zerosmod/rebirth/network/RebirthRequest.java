package org.darkoro.zerosmod.rebirth.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public final class RebirthRequest implements IMessage {
  public static final int REFRESH = 0, EDIT = 1, SAVE = 2, CHARACTER = 3,
      CONFIRM = 4, CANCEL = 5, CLOSE = 6, OVERVIEW = 7;
  public long session;
  public int requestId;
  public int action;
  public int[] values;

  public RebirthRequest() {}

  public RebirthRequest(long session, int requestId, int action, int[] values) {
    this.session = session;
    this.requestId = requestId;
    this.action = action;
    this.values = values == null ? null : values.clone();
  }

  @Override public void fromBytes(ByteBuf buf) {
    session = buf.readLong();
    requestId = buf.readInt();
    action = buf.readUnsignedByte();
    if (action == SAVE) {
      values = new int[6];
      for (int i = 0; i < values.length; i++) {
        values[i] = buf.readInt();
      }
    }
  }

  @Override public void toBytes(ByteBuf buf) {
    buf.writeLong(session);
    buf.writeInt(requestId);
    buf.writeByte(action);
    if (action == SAVE) {
      for (int value : values) {
        buf.writeInt(value);
      }
    }
  }

  public static final class Handler implements IMessageHandler<RebirthRequest, IMessage> {
    @Override public IMessage onMessage(RebirthRequest message, MessageContext context) {
      return null;
    }
  }
}
