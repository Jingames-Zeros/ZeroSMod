package org.darkoro.zerosmod.zsweapons.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class CooldownToClientPacket implements IMessage {
    public double cooldown;
    public double tps;

    public CooldownToClientPacket() {}
    public CooldownToClientPacket(double cooldown, double tps) {
        this.cooldown = cooldown;
        this.tps = tps;
    }

    @Override public void fromBytes(ByteBuf buf) {
        cooldown = buf.readDouble();
        tps = buf.readDouble();
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeDouble(cooldown);
        buf.writeDouble(tps);
    }
}