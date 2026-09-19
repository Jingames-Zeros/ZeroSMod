package org.darkoro.zerosmod.zsweapons.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class TargetEntityToServerPacket implements IMessage {
    public int entityId;

    public TargetEntityToServerPacket() {}
    public TargetEntityToServerPacket(int entityId) {
        this.entityId = entityId;
    }

    @Override public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
    }
}