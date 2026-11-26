package org.darkoro.zerosmod.tpbank.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import org.darkoro.zerosmod.tpbank.TpBankServer;

public final class TpBankRequest implements IMessage {
  public static final int REFRESH = 0, WITHDRAW = 1, WITHDRAW_MAX = 2, CLOSE = 3;
  public long session;
  public int requestId;
  public int action;
  public long amount;

  public TpBankRequest() {}

  public TpBankRequest(long session, int requestId, int action, long amount) {
    this.session = session;
    this.requestId = requestId;
    this.action = action;
    this.amount = amount;
  }

  @Override public void fromBytes(ByteBuf buf) {
    session = buf.readLong();
    requestId = buf.readInt();
    action = buf.readUnsignedByte();
    amount = buf.readLong();
  }

  @Override public void toBytes(ByteBuf buf) {
    buf.writeLong(session);
    buf.writeInt(requestId);
    buf.writeByte(action);
    buf.writeLong(amount);
  }

  public static final class Handler implements IMessageHandler<TpBankRequest, IMessage> {
    @Override public IMessage onMessage(TpBankRequest message, MessageContext context) {
      TpBankServer.enqueue(context.getServerHandler().playerEntity, message);
      return null;
    }
  }
}
