package org.darkoro.zerosmod.rebirth.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import org.darkoro.zerosmod.ZeroSMod;

/** Only display data crosses the wire, never the player's persisted NBT or skills. */
public final class RebirthSnapshot implements IMessage {
  public long session;
  public int requestId;
  public boolean open, close, error, editing, readOnly, available, changed, confirmed, usedCharacter, operator;
  public int maximum, remaining;
  public double cooldownDays;
  public int[] values = new int[6];
  public String cooldown = "", message = "";

  @Override public void fromBytes(ByteBuf buf) {
    session = buf.readLong();
    requestId = buf.readInt();
    open = buf.readBoolean();
    close = buf.readBoolean();
    error = buf.readBoolean();
    editing = buf.readBoolean();
    readOnly = buf.readBoolean();
    available = buf.readBoolean();
    changed = buf.readBoolean();
    confirmed = buf.readBoolean();
    usedCharacter = buf.readBoolean();
    operator = buf.readBoolean();
    maximum = buf.readInt();
    remaining = buf.readInt();
    cooldownDays = buf.readDouble();
    for (int i = 0; i < values.length; i++) {
      values[i] = buf.readInt();
    }
    cooldown = ByteBufUtils.readUTF8String(buf);
    message = ByteBufUtils.readUTF8String(buf);
  }

  @Override public void toBytes(ByteBuf buf) {
    buf.writeLong(session);
    buf.writeInt(requestId);
    buf.writeBoolean(open);
    buf.writeBoolean(close);
    buf.writeBoolean(error);
    buf.writeBoolean(editing);
    buf.writeBoolean(readOnly);
    buf.writeBoolean(available);
    buf.writeBoolean(changed);
    buf.writeBoolean(confirmed);
    buf.writeBoolean(usedCharacter);
    buf.writeBoolean(operator);
    buf.writeInt(maximum);
    buf.writeInt(remaining);
    buf.writeDouble(cooldownDays);
    for (int value : values) {
      buf.writeInt(value);
    }
    ByteBufUtils.writeUTF8String(buf, cooldown);
    ByteBufUtils.writeUTF8String(buf, message);
  }

  public static final class Handler implements IMessageHandler<RebirthSnapshot, IMessage> {
    @Override public IMessage onMessage(RebirthSnapshot message, MessageContext context) {
      ZeroSMod.proxy.receiveRebirthSnapshot(message, context.netHandler);
      return null;
    }
  }
}
