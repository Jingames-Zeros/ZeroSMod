package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class SyncKiAttackStatePacket implements IMessage {

  public int entityId;
  public double posX;
  public double posY;
  public double posZ;
  public double motionX;
  public double motionY;
  public double motionZ;
  public float rotationYaw;
  public float rotationPitch;
  public float startX;
  public float startY;
  public float startZ;
  public float targetX;
  public float targetY;
  public float targetZ;

  public SyncKiAttackStatePacket() {}

  public SyncKiAttackStatePacket(int entityId, double posX, double posY, double posZ,
      double motionX, double motionY, double motionZ, float rotationYaw, float rotationPitch,
      float startX, float startY, float startZ, float targetX, float targetY, float targetZ) {
    this.entityId = entityId;
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
    this.motionX = motionX;
    this.motionY = motionY;
    this.motionZ = motionZ;
    this.rotationYaw = rotationYaw;
    this.rotationPitch = rotationPitch;
    this.startX = startX;
    this.startY = startY;
    this.startZ = startZ;
    this.targetX = targetX;
    this.targetY = targetY;
    this.targetZ = targetZ;
  }

  @Override public void fromBytes(ByteBuf buf) {
    this.entityId = buf.readInt();
    this.posX = buf.readDouble();
    this.posY = buf.readDouble();
    this.posZ = buf.readDouble();
    this.motionX = buf.readDouble();
    this.motionY = buf.readDouble();
    this.motionZ = buf.readDouble();
    this.rotationYaw = buf.readFloat();
    this.rotationPitch = buf.readFloat();
    this.startX = buf.readFloat();
    this.startY = buf.readFloat();
    this.startZ = buf.readFloat();
    this.targetX = buf.readFloat();
    this.targetY = buf.readFloat();
    this.targetZ = buf.readFloat();
  }

  @Override public void toBytes(ByteBuf buf) {
    buf.writeInt(this.entityId);
    buf.writeDouble(this.posX);
    buf.writeDouble(this.posY);
    buf.writeDouble(this.posZ);
    buf.writeDouble(this.motionX);
    buf.writeDouble(this.motionY);
    buf.writeDouble(this.motionZ);
    buf.writeFloat(this.rotationYaw);
    buf.writeFloat(this.rotationPitch);
    buf.writeFloat(this.startX);
    buf.writeFloat(this.startY);
    buf.writeFloat(this.startZ);
    buf.writeFloat(this.targetX);
    buf.writeFloat(this.targetY);
    buf.writeFloat(this.targetZ);
  }
}
