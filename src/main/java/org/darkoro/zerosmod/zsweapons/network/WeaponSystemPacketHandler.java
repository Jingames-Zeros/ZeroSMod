package org.darkoro.zerosmod.zsweapons.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import org.darkoro.zerosmod.zsweapons.client.ClientWeaponHandler;
import org.darkoro.zerosmod.zsweapons.network.packets.WeaponTypesToClientPacket;
import org.darkoro.zerosmod.zsweapons.network.packets.CooldownToClientPacket;
import org.darkoro.zerosmod.zsweapons.network.packets.TargetEntityToServerPacket;
import org.darkoro.zerosmod.zsweapons.server.ServerWeaponHandler;

public class WeaponSystemPacketHandler {
    private static boolean registered;

    public static synchronized int registerPackets(SimpleNetworkWrapper network, int nextDiscriminator) {
        network.registerMessage(CooldownToClientHandler.class, CooldownToClientPacket.class, ++nextDiscriminator, Side.CLIENT);
        network.registerMessage(TargetEntityToServerHandler.class, TargetEntityToServerPacket.class, ++nextDiscriminator, Side.SERVER);
        network.registerMessage(WeaponTypesToClientHandler.class, WeaponTypesToClientPacket.class, ++nextDiscriminator, Side.CLIENT);
        return ++nextDiscriminator;
    }

    public static final class CooldownToClientHandler implements IMessageHandler<CooldownToClientPacket, IMessage> {
        @Override public IMessage onMessage(CooldownToClientPacket message, MessageContext ctx) {
            ClientWeaponHandler.INSTANCE.handleCooldownPacket(message);
            return null;
        }
    }

    public static final class TargetEntityToServerHandler implements IMessageHandler<TargetEntityToServerPacket, IMessage> {
        @Override public IMessage onMessage(TargetEntityToServerPacket message, MessageContext ctx) {
            ServerWeaponHandler.INSTANCE.handleTargetEntityPacket(message, ctx);
            return null;
        }
    }

    public static final class WeaponTypesToClientHandler implements IMessageHandler<WeaponTypesToClientPacket, IMessage> {
        @Override public IMessage onMessage(WeaponTypesToClientPacket message, MessageContext ctx) {
            ClientWeaponHandler.INSTANCE.handleWeaponTypesPacket(message);
            return null;
        }
    }
}
