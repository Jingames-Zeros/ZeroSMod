package org.darkoro.zerosmod.zsweapons.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import org.darkoro.zerosmod.zsweapons.CachedWeaponState;

import java.util.HashMap;
import java.util.Map;

public class WeaponTypesToClientPacket implements IMessage {
    public CachedWeaponState defaultState;
    public Map<String, CachedWeaponState> loadedWeaponStates = new HashMap<>();

    public WeaponTypesToClientPacket() {}
    public WeaponTypesToClientPacket(CachedWeaponState defaultState, Map<String, CachedWeaponState> loadedWeaponStates) {
        this.defaultState = defaultState;
        this.loadedWeaponStates = loadedWeaponStates;
    }

    @Override public void fromBytes(ByteBuf buf) {
        while(buf.isReadable()) {
            if(defaultState == null) {
                defaultState = readState(buf);
            } else {
                CachedWeaponState state = readState(buf);
                loadedWeaponStates.put(state.type, state);
            }
        }
    }

    @Override public void toBytes(ByteBuf buf) {
        writeState(buf, defaultState);
        for(var entry : loadedWeaponStates.entrySet()) {
            writeState(buf, entry.getValue());
        }
    }

    /**
     * Writes a cached weapon state to buffer
     * @param buf - ByteBuf
     * @param state - Weapon State
     */
    private void writeState(ByteBuf buf, CachedWeaponState state) {
        int typeSize = state.type.length();
        // Write type
        buf.writeShort(typeSize);
        for(int i = 0; i < typeSize; i++) {
            buf.writeChar(state.type.charAt(i));
        }

        // General settings
        buf.writeInt(state.cooldown);
        buf.writeFloat(state.attackMultiplier);
        buf.writeFloat(state.getRange());
        buf.writeFloat(state.sweetSpot);

        // Ki
        buf.writeBoolean(state.canChargeKi);
        buf.writeFloat(state.kiMultiplier);
        buf.writeInt(state.kiAdditive);

        // Block
        buf.writeBoolean(state.canBlock);
        buf.writeFloat(state.blockReduction);
        buf.writeFloat(state.blockCostMultiplier);
        buf.writeInt(state.blockCooldown);
    }

    /**
     * Reads a weapon state from the buffer
     * @param buf
     */
    private CachedWeaponState readState(ByteBuf buf) {
        CachedWeaponState state;
        if(defaultState == null) {
            state = new CachedWeaponState("default");
        } else {
            state = new CachedWeaponState();
        }

        // Read weapon type
        short typeLength = buf.readShort();
        char typeChars[] = new char[typeLength];
        for(int i = 0; i < typeLength; i++) {
            typeChars[i] = buf.readChar();
        }
        String type = new String(typeChars);
        state.type = type;

        // General
        state.cooldown = buf.readInt();
        state.attackMultiplier = buf.readFloat();
        state.setRange(buf.readFloat());
        state.sweetSpot = buf.readFloat();

        // Ki
        state.canChargeKi = buf.readBoolean();
        state.kiMultiplier = buf.readFloat();
        state.kiAdditive = buf.readInt();

        // Block
        state.canBlock = buf.readBoolean();
        state.blockReduction = buf.readFloat();
        state.blockCostMultiplier = buf.readFloat();
        state.blockCooldown = buf.readInt();
        return state;
    }
}
