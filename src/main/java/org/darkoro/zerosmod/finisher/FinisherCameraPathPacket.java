package org.darkoro.zerosmod.finisher;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import org.darkoro.zerosmod.ZeroSMod;

/** A whole camera path, sent once when the shot starts. Stopping it uses {@link FinisherCameraPacket}. */
public class FinisherCameraPathPacket implements IMessage {

  // Entity id the path follows, or -1 for world coordinates
  public int relativeId;
  public int ease;
  public int[] ticks;
  public double[] x;
  public double[] y;
  public double[] z;
  public boolean[] lookAt;
  // Entity id a lookAt keyframe aims at; -1 for the followed entity (the player without one)
  public int[] targetIds;
  public float[] yaw;
  public float[] pitch;

  public FinisherCameraPathPacket() {}

  public FinisherCameraPathPacket(int relativeId, int ease, int keys) {
    this.relativeId = relativeId;
    this.ease = ease;
    allocate(keys);
  }

  private void allocate(int keys) {
    ticks = new int[keys];
    x = new double[keys];
    y = new double[keys];
    z = new double[keys];
    lookAt = new boolean[keys];
    targetIds = new int[keys];
    yaw = new float[keys];
    pitch = new float[keys];
  }

  public int duration() {
    return ticks.length == 0 ? 0 : ticks[ticks.length - 1];
  }

  @Override public void fromBytes(ByteBuf buf) {
    relativeId = buf.readInt();
    ease = buf.readUnsignedByte();
    allocate(Math.min(buf.readUnsignedByte(), FinisherCameraPath.MAX_KEYS));
    for (int i = 0; i < ticks.length; i++) {
      ticks[i] = buf.readUnsignedShort();
      x[i] = buf.readDouble();
      y[i] = buf.readDouble();
      z[i] = buf.readDouble();
      lookAt[i] = buf.readBoolean();
      if (lookAt[i]) {
        targetIds[i] = buf.readInt();
      } else {
        yaw[i] = buf.readFloat();
        pitch[i] = buf.readFloat();
      }
    }
  }

  @Override public void toBytes(ByteBuf buf) {
    buf.writeInt(relativeId);
    buf.writeByte(ease);
    buf.writeByte(ticks.length);
    for (int i = 0; i < ticks.length; i++) {
      buf.writeShort(ticks[i]);
      buf.writeDouble(x[i]);
      buf.writeDouble(y[i]);
      buf.writeDouble(z[i]);
      buf.writeBoolean(lookAt[i]);
      if (lookAt[i]) {
        buf.writeInt(targetIds[i]);
      } else {
        buf.writeFloat(yaw[i]);
        buf.writeFloat(pitch[i]);
      }
    }
  }

  public static class Handler implements IMessageHandler<FinisherCameraPathPacket, IMessage> {
    @Override public IMessage onMessage(FinisherCameraPathPacket message, MessageContext ctx) {
      ZeroSMod.proxy.receiveFinisherCameraPath(message);
      return null;
    }
  }
}
