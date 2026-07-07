package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class SyncGuiTitlePacket implements IMessage {

  public int ctxId;
  public String title;
  public boolean isEditable;
  public boolean isInventory;

  public SyncGuiTitlePacket() {}

  public SyncGuiTitlePacket(int ctxId, String title) {
    this(ctxId, title, false, false);
  }

  public SyncGuiTitlePacket(int ctxId, String title, boolean isEditable, boolean isInventory) {
    this.ctxId = ctxId;
    this.title = title;
    this.isEditable = isEditable;
    this.isInventory = isInventory;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.ctxId = buf.readInt();
    this.title = ByteBufUtils.readUTF8String(buf);
    this.isEditable = buf.readBoolean();
    this.isInventory = buf.readBoolean();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.ctxId);
    ByteBufUtils.writeUTF8String(buf, this.title);
    buf.writeBoolean(this.isEditable);
    buf.writeBoolean(this.isInventory);
  }

}
