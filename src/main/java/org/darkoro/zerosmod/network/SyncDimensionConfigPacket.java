package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import org.darkoro.zerosmod.config.DimensionConfig;

public class SyncDimensionConfigPacket implements IMessage {

  public float phylacteryVisibility;
  public int phylacteryFixedTime;
  public float phylacterySunBrightness;
  public float dragonRealmVisibility;

  public SyncDimensionConfigPacket() {}

  public SyncDimensionConfigPacket(float phylacteryVisibility, int phylacteryFixedTime,
      float phylacterySunBrightness, float dragonRealmVisibility) {
    this.phylacteryVisibility = phylacteryVisibility;
    this.phylacteryFixedTime = phylacteryFixedTime;
    this.phylacterySunBrightness = phylacterySunBrightness;
    this.dragonRealmVisibility = dragonRealmVisibility;
  }

  public static SyncDimensionConfigPacket buildCurrent() {
    return new SyncDimensionConfigPacket(
        DimensionConfig.getPhylacteryVisibility(),
        DimensionConfig.getPhylacteryFixedTime(),
        DimensionConfig.getPhylacterySunBrightness(),
        DimensionConfig.getDragonRealmVisibility());
  }

  @Override public void fromBytes(ByteBuf buf) {
    this.phylacteryVisibility = buf.readFloat();
    this.phylacteryFixedTime = buf.readInt();
    this.phylacterySunBrightness = buf.readFloat();
    this.dragonRealmVisibility = buf.readFloat();
  }

  @Override public void toBytes(ByteBuf buf) {
    buf.writeFloat(this.phylacteryVisibility);
    buf.writeInt(this.phylacteryFixedTime);
    buf.writeFloat(this.phylacterySunBrightness);
    buf.writeFloat(this.dragonRealmVisibility);
  }
}
