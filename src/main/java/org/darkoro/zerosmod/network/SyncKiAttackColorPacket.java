package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class SyncKiAttackColorPacket implements IMessage {

  public int entityId;
  public int color;
  public int color2;

  public SyncKiAttackColorPacket() {}

  public SyncKiAttackColorPacket(int entityId, int color, int color2) {
    this.entityId = entityId;
    this.color = color;
    this.color2 = color2;
  }

  @Override public void fromBytes(ByteBuf buf) {
    this.entityId = buf.readInt();
    this.color = buf.readInt();
    this.color2 = buf.readInt();
  }

  @Override public void toBytes(ByteBuf buf) {
    buf.writeInt(this.entityId);
    buf.writeInt(this.color);
    buf.writeInt(this.color2);
  }
}
