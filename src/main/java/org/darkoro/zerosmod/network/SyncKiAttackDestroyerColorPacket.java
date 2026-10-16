package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class SyncKiAttackDestroyerColorPacket implements IMessage {

  public int entityId;
  public int rgb;

  public SyncKiAttackDestroyerColorPacket() {}

  public SyncKiAttackDestroyerColorPacket(int entityId, int rgb) {
    this.entityId = entityId;
    this.rgb = rgb;
  }

  @Override public void fromBytes(ByteBuf buffer) {
    this.entityId = buffer.readInt();
    this.rgb = buffer.readInt();
  }

  @Override public void toBytes(ByteBuf buffer) {
    buffer.writeInt(this.entityId);
    buffer.writeInt(this.rgb);
  }
}
