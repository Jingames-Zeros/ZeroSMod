package org.darkoro.zerosmod.dbcarmor.editor;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import org.darkoro.zerosmod.ZeroSMod;

public final class DBCAEditorSnapshot implements IMessage {
  public int requestId;
  public boolean open;
  public boolean error;
  public String message = "";
  public String selectedSet = "";
  public int selectedPieceId = -1;
  public int selectedBonusId = -1;
  public String[] setNames = new String[0];
  public int[] setPieces = new int[0];
  public int[] setBonuses = new int[0];
  public int[] pieceIds = new int[0];
  public String[] pieceNames = new String[0];
  public int[] bonusIds = new int[0];
  public String[] bonusNames = new String[0];
  public String pieceName = "";
  public String[] stats = {"+0", "+0", "+0", "+0", "+0"};
  public int level;
  public int durability;
  public int potionId;
  public int potionStrength;
  public String[] colors = {"white", "white", "white", "white", "white", "white"};
  public String[] lore = new String[8];
  public String bonusName = "";
  public String[] bonusStats = {"+0", "+0", "+0", "+0", "+0"};
  public int[] bonusPieceIds = new int[0];

  @Override
  public void fromBytes(ByteBuf buf) {
    requestId = buf.readInt();
    open = buf.readBoolean();
    error = buf.readBoolean();
    message = ByteBufUtils.readUTF8String(buf);
    selectedSet = ByteBufUtils.readUTF8String(buf);
    selectedPieceId = buf.readInt();
    selectedBonusId = buf.readInt();
    setNames = readStrings(buf);
    setPieces = readInts(buf);
    setBonuses = readInts(buf);
    pieceIds = readInts(buf);
    pieceNames = readStrings(buf);
    bonusIds = readInts(buf);
    bonusNames = readStrings(buf);
    pieceName = ByteBufUtils.readUTF8String(buf);
    stats = readStrings(buf);
    level = buf.readInt();
    durability = buf.readInt();
    potionId = buf.readInt();
    potionStrength = buf.readInt();
    colors = readStrings(buf);
    lore = readStrings(buf);
    bonusName = ByteBufUtils.readUTF8String(buf);
    bonusStats = readStrings(buf);
    bonusPieceIds = readInts(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(requestId);
    buf.writeBoolean(open);
    buf.writeBoolean(error);
    ByteBufUtils.writeUTF8String(buf, message);
    ByteBufUtils.writeUTF8String(buf, selectedSet);
    buf.writeInt(selectedPieceId);
    buf.writeInt(selectedBonusId);
    writeStrings(buf, setNames);
    writeInts(buf, setPieces);
    writeInts(buf, setBonuses);
    writeInts(buf, pieceIds);
    writeStrings(buf, pieceNames);
    writeInts(buf, bonusIds);
    writeStrings(buf, bonusNames);
    ByteBufUtils.writeUTF8String(buf, pieceName);
    writeStrings(buf, stats);
    buf.writeInt(level);
    buf.writeInt(durability);
    buf.writeInt(potionId);
    buf.writeInt(potionStrength);
    writeStrings(buf, colors);
    writeStrings(buf, lore);
    ByteBufUtils.writeUTF8String(buf, bonusName);
    writeStrings(buf, bonusStats);
    writeInts(buf, bonusPieceIds);
  }

  private static String[] readStrings(ByteBuf buf) {
    int count = buf.readUnsignedByte();
    String[] values = new String[count];
    for (int i = 0; i < count; i++) {
      values[i] = ByteBufUtils.readUTF8String(buf);
    }
    return values;
  }

  private static void writeStrings(ByteBuf buf, String[] values) {
    buf.writeByte(values == null ? 0 : values.length);
    if (values == null) return;
    for (String value : values) {
      ByteBufUtils.writeUTF8String(buf, value == null ? "" : value);
    }
  }

  private static int[] readInts(ByteBuf buf) {
    int count = buf.readUnsignedByte();
    int[] values = new int[count];
    for (int i = 0; i < count; i++) {
      values[i] = buf.readInt();
    }
    return values;
  }

  private static void writeInts(ByteBuf buf, int[] values) {
    buf.writeByte(values == null ? 0 : values.length);
    if (values == null) return;
    for (int value : values) {
      buf.writeInt(value);
    }
  }

  public static final class Handler implements IMessageHandler<DBCAEditorSnapshot, IMessage> {
    @Override
    public IMessage onMessage(DBCAEditorSnapshot message, MessageContext context) {
      ZeroSMod.proxy.receiveDBCAEditorSnapshot(message, context.netHandler);
      return null;
    }
  }
}
