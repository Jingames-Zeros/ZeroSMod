package org.darkoro.ZeroSMod.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class SyncGuiTitlePacket implements IMessage {

  public int ctxId;
  public String title;

  public SyncGuiTitlePacket() {}

  public SyncGuiTitlePacket(int ctxId, String title) {
    this.ctxId = ctxId;
    this.title = title;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.ctxId = buf.readInt();
    this.title = ByteBufUtils.readUTF8String(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.ctxId);
    ByteBufUtils.writeUTF8String(buf, this.title);
  }

}
