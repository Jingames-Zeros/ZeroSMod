package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class SyncBiomeVisualsPacket implements IMessage {

  public int count;

  public int[] biomeIds;
  public int[] skyColors;
  public int[] fogColors;
  public float[] fogStrengths;
  public int[] grassColors;
  public int[] foliageColors;
  public int[] waterColors;

  public SyncBiomeVisualsPacket() {}

  @Override
  public void fromBytes(ByteBuf buf) {
    this.count = buf.readInt();

    this.biomeIds = new int[count];
    this.skyColors = new int[count];
    this.fogColors = new int[count];
    this.fogStrengths = new float[count];
    this.grassColors = new int[count];
    this.foliageColors = new int[count];
    this.waterColors = new int[count];

    for (int i = 0; i < count; i++) {
      biomeIds[i] = buf.readInt();
      skyColors[i] = buf.readInt();
      fogColors[i] = buf.readInt();
      fogStrengths[i] = buf.readFloat();
      grassColors[i] = buf.readInt();
      foliageColors[i] = buf.readInt();
      waterColors[i] = buf.readInt();
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.count);

    for (int i = 0; i < count; i++) {
      buf.writeInt(biomeIds[i]);
      buf.writeInt(skyColors[i]);
      buf.writeInt(fogColors[i]);
      buf.writeFloat(fogStrengths[i]);
      buf.writeInt(grassColors[i]);
      buf.writeInt(foliageColors[i]);
      buf.writeInt(waterColors[i]);
    }
  }
}
