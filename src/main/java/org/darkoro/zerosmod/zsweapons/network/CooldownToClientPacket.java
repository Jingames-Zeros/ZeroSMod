package org.darkoro.zerosmod.zsweapons.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class CooldownToClientPacket implements IMessage {
    public int fullCooldown;
    public double cooldown;
    public double tps;

    public CooldownToClientPacket() {}
    public CooldownToClientPacket(int fullCooldown, double cooldown, double tps) {
        this.fullCooldown = fullCooldown;
        this.cooldown = cooldown;
        this.tps = tps;
    }

    @Override public void fromBytes(ByteBuf buf) {
        fullCooldown = buf.readInt();
        cooldown = buf.readDouble();
        tps = buf.readDouble();
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(fullCooldown);
        buf.writeDouble(cooldown);
        buf.writeDouble(tps);
    }
}