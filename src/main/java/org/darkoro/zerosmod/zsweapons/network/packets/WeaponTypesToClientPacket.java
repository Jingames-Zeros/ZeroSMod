package org.darkoro.zerosmod.zsweapons.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import org.darkoro.zerosmod.zsweapons.cache.CachedWeaponStats;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class WeaponTypesToClientPacket implements IMessage {
    public Map<String, CachedWeaponStats> loadedWeaponStats = new HashMap<>();

    public WeaponTypesToClientPacket() {}
    public WeaponTypesToClientPacket(Map<String, CachedWeaponStats> loadedWeaponStats) {
        this.loadedWeaponStats = loadedWeaponStats;
    }

    @Override public void fromBytes(ByteBuf buf) {
        try {
            while (buf.isReadable()) {
                CachedWeaponStats stats = readState(buf);
                loadedWeaponStats.put(stats.getType(), stats);
            }
        } catch (CachedWeaponStats.ProtectedWeaponTypeException ignored) {}
    }

    @Override public void toBytes(ByteBuf buf) {
        for(Entry<String, CachedWeaponStats> entry : loadedWeaponStats.entrySet()) {
            writeState(buf, entry.getValue());
        }
    }

    /**
     * Writes a cached weapon stats to buffer
     * @param buf - ByteBuf
     * @param stats - Weapon State
     */
    private void writeState(ByteBuf buf, CachedWeaponStats stats) {
        if (stats == null) return;

        String type = stats.getType();
        int typeSize = type.length();

        // Write type
        buf.writeShort(typeSize);
        for (int i = 0; i < typeSize; i++) {
            buf.writeChar(type.charAt(i));
        }

        // General settings
        buf.writeInt(stats.getCooldown());
        buf.writeFloat(stats.getAttackMultiplier());
        buf.writeFloat(stats.getRange());
        buf.writeFloat(stats.getSweetSpot());

        // Ki
        buf.writeBoolean(stats.canChargeKi());
        buf.writeFloat(stats.getKiMultiplier());
        buf.writeInt(stats.getKiAdditive());
        buf.writeFloat(stats.getKiCostMultiplier());

        // Block
        buf.writeBoolean(stats.canBlock());
        buf.writeFloat(stats.getBlockDexMultiplier());
        buf.writeFloat(stats.getBlockCostMultiplier());
        buf.writeInt(stats.getBlockCooldown());
    }

    /**
     * Reads a weapon stats from the buffer
     * @param buf
     */
    private CachedWeaponStats readState(ByteBuf buf) throws CachedWeaponStats.ProtectedWeaponTypeException {
        CachedWeaponStats stats;
        // Read weapon type
        short typeLength = buf.readShort();
        char[] typeChars = new char[typeLength];

        for (int i = 0; i < typeLength; i++) {
            typeChars[i] = buf.readChar();
        }

        stats = new CachedWeaponStats(new String(typeChars));

        // General
        stats.setCooldown(buf.readInt());
        stats.setAttackMultiplier(buf.readFloat());
        stats.setRange(buf.readFloat());
        stats.setSweetSpot(buf.readFloat());

        // Ki
        stats.setCanChargeKi(buf.readBoolean());
        stats.setKiMultiplier(buf.readFloat());
        stats.setKiAdditive(buf.readInt());
        stats.setKiCostMultiplier(buf.readFloat());

        // Block
        stats.setCanBlock(buf.readBoolean());
        stats.setBlockDexMultiplier(buf.readFloat());
        stats.setBlockCostMultiplier(buf.readFloat());
        stats.setBlockCooldown(buf.readInt());

        return stats;
    }
}
