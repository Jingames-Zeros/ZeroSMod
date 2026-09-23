package org.darkoro.zerosmod.dbcarmor.editor;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public final class DBCAEditorRequest implements IMessage {
  public static final int REFRESH = 0;
  public static final int SELECT_SET = 1;
  public static final int SELECT_PIECE = 2;
  public static final int CREATE_SET = 3;
  public static final int CREATE_PIECE = 4;
  public static final int SAVE_PIECE = 5;
  public static final int SAVE_ALL = 6;
  public static final int RELOAD = 7;
  public static final int APPLY_HELD = 8;
  public static final int CLOSE = 9;
  public static final int SELECT_BONUS = 10;
  public static final int CREATE_BONUS = 11;
  public static final int SAVE_BONUS = 12;

  public int requestId;
  public int action;
  public int pieceId;
  public int bonusId;
  public String setName = "";
  public String text = "";
  public String[] values = new String[0];

  public DBCAEditorRequest() {}

  public DBCAEditorRequest(int requestId, int action, String setName, int pieceId, int bonusId,
      String text, String[] values) {
    this.requestId = requestId;
    this.action = action;
    this.setName = setName == null ? "" : setName;
    this.pieceId = pieceId;
    this.bonusId = bonusId;
    this.text = text == null ? "" : text;
    this.values = values == null ? new String[0] : values.clone();
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    requestId = buf.readInt();
    action = buf.readUnsignedByte();
    pieceId = buf.readInt();
    bonusId = buf.readInt();
    setName = ByteBufUtils.readUTF8String(buf);
    text = ByteBufUtils.readUTF8String(buf);
    int count = buf.readUnsignedByte();
    values = new String[count];
    for (int i = 0; i < count; i++) {
      values[i] = ByteBufUtils.readUTF8String(buf);
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(requestId);
    buf.writeByte(action);
    buf.writeInt(pieceId);
    buf.writeInt(bonusId);
    ByteBufUtils.writeUTF8String(buf, setName);
    ByteBufUtils.writeUTF8String(buf, text);
    buf.writeByte(values.length);
    for (String value : values) {
      ByteBufUtils.writeUTF8String(buf, value == null ? "" : value);
    }
  }

  public static final class Handler implements IMessageHandler<DBCAEditorRequest, IMessage> {
    @Override
    public IMessage onMessage(DBCAEditorRequest message, MessageContext context) {
      return null;
    }
  }
}
