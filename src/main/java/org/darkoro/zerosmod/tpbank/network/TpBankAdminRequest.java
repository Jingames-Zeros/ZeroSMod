package org.darkoro.zerosmod.tpbank.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import org.darkoro.zerosmod.tpbank.TpBankAdminServer;

/** Admin screen action. The server checks the sender's permission on every one of these. */
public final class TpBankAdminRequest implements IMessage {
  public static final int REFRESH = 0, CLOSE = 1, SET = 2, ADD = 3, LOCK = 4, UNLOCK = 5, SAVE_CONFIG = 6;
  public long session;
  public int requestId;
  public int action;
  /** Player and slot edited by SET, ADD, LOCK and UNLOCK. */
  public String target = "", slot = "";
  public long amount;
  public TpBankAdminSnapshot.Settings settings = new TpBankAdminSnapshot.Settings();

  public TpBankAdminRequest() {}

  public TpBankAdminRequest(long session, int requestId, int action) {
    this.session = session;
    this.requestId = requestId;
    this.action = action;
  }

  @Override public void fromBytes(ByteBuf buf) {
    session = buf.readLong();
    requestId = buf.readInt();
    action = buf.readUnsignedByte();
    target = ByteBufUtils.readUTF8String(buf);
    slot = ByteBufUtils.readUTF8String(buf);
    amount = buf.readLong();
    settings.read(buf);
  }

  @Override public void toBytes(ByteBuf buf) {
    buf.writeLong(session);
    buf.writeInt(requestId);
    buf.writeByte(action);
    ByteBufUtils.writeUTF8String(buf, target);
    ByteBufUtils.writeUTF8String(buf, slot);
    buf.writeLong(amount);
    settings.write(buf);
  }

  public static final class Handler implements IMessageHandler<TpBankAdminRequest, IMessage> {
    @Override public IMessage onMessage(TpBankAdminRequest message, MessageContext context) {
      TpBankAdminServer.enqueue(context.getServerHandler().playerEntity, message);
      return null;
    }
  }
}
