package org.darkoro.zerosmod.zsweapons.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class CooldownToClientPacket implements IMessage {
    public int fullCooldown;
    public int cooldown;
    public float tps;

    public CooldownToClientPacket() {}
    public CooldownToClientPacket(int fullCooldown, int cooldown, float tps) {
        this.fullCooldown = fullCooldown;
        this.cooldown = cooldown;
        this.tps = tps;
    }

    @Override public void fromBytes(ByteBuf buf) {
        fullCooldown = buf.readInt();
        cooldown = buf.readInt();
        tps = buf.readFloat();
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(fullCooldown);
        buf.writeInt(cooldown);
        buf.writeFloat(tps);
    }
}