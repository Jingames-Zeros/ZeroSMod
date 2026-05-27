package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class SyncZSTabDataPacket implements IMessage {

  public String playerName = "";
  public String className = "Unknown";
  public String raceName = "Unknown";
  public String currentForm = "Base";
  public int str;
  public int dex;
  public int con;
  public int wil;
  public int mnd;
  public int spi;
  public int level;
  public String passive = "None";
  public String super1 = "None";
  public String super2 = "None";
  public String ultimate = "None";

  public SyncZSTabDataPacket() {}

  @Override
  public void fromBytes(ByteBuf buf) {
    playerName = read(buf);
    className = read(buf);
    raceName = read(buf);
    currentForm = read(buf);
    str = buf.readInt();
    dex = buf.readInt();
    con = buf.readInt();
    wil = buf.readInt();
    mnd = buf.readInt();
    spi = buf.readInt();
    level = buf.readInt();
    passive = read(buf);
    super1 = read(buf);
    super2 = read(buf);
    ultimate = read(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    write(buf, playerName);
    write(buf, className);
    write(buf, raceName);
    write(buf, currentForm);
    buf.writeInt(str);
    buf.writeInt(dex);
    buf.writeInt(con);
    buf.writeInt(wil);
    buf.writeInt(mnd);
    buf.writeInt(spi);
    buf.writeInt(level);
    write(buf, passive);
    write(buf, super1);
    write(buf, super2);
    write(buf, ultimate);
  }

  private static String read(ByteBuf buf) {
    return ByteBufUtils.readUTF8String(buf);
  }

  private static void write(ByteBuf buf, String value) {
    ByteBufUtils.writeUTF8String(buf, value == null ? "" : value);
  }
}
