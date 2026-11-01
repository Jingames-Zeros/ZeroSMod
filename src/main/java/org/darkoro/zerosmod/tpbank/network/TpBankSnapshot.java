package org.darkoro.zerosmod.tpbank.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import org.darkoro.zerosmod.ZeroSMod;

/** Display data only; the server re-reads every balance before acting on a request. */
public final class TpBankSnapshot implements IMessage {
  public long session;
  public int requestId;
  public boolean open, close, error, locked;
  public long banked, withdrawable;
  public int current, maximum;
  public double withdrawFeePercent;
  /** Server-written summary of the bank's rules, e.g. its interest and fees. */
  public String rules = "";
  public String message = "";

  @Override public void fromBytes(ByteBuf buf) {
    session = buf.readLong();
    requestId = buf.readInt();
    open = buf.readBoolean();
    close = buf.readBoolean();
    error = buf.readBoolean();
    locked = buf.readBoolean();
    banked = buf.readLong();
    withdrawable = buf.readLong();
    current = buf.readInt();
    maximum = buf.readInt();
    withdrawFeePercent = buf.readDouble();
    rules = ByteBufUtils.readUTF8String(buf);
    message = ByteBufUtils.readUTF8String(buf);
  }

  @Override public void toBytes(ByteBuf buf) {
    buf.writeLong(session);
    buf.writeInt(requestId);
    buf.writeBoolean(open);
    buf.writeBoolean(close);
    buf.writeBoolean(error);
    buf.writeBoolean(locked);
    buf.writeLong(banked);
    buf.writeLong(withdrawable);
    buf.writeInt(current);
    buf.writeInt(maximum);
    buf.writeDouble(withdrawFeePercent);
    ByteBufUtils.writeUTF8String(buf, rules);
    ByteBufUtils.writeUTF8String(buf, message);
  }

  public static final class Handler implements IMessageHandler<TpBankSnapshot, IMessage> {
    @Override public IMessage onMessage(TpBankSnapshot message, MessageContext context) {
      ZeroSMod.proxy.receiveTpBankSnapshot(message, context.netHandler);
      return null;
    }
  }
}
