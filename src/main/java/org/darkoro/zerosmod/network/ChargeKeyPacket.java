package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class ChargeKeyPacket implements IMessage {

  private boolean isHolding;

  public ChargeKeyPacket() {}

  public ChargeKeyPacket(boolean isHolding) {
    this.isHolding = isHolding;
  }

  public boolean isHolding() {
    return this.isHolding;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.isHolding = buf.readBoolean();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeBoolean(this.isHolding);
  }

}
