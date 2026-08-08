package org.darkoro.zerosmod.zsweapons.API;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.item.IItemStack;
import org.darkoro.zerosmod.config.ConfigHandler;
import org.darkoro.zerosmod.config.ServerWeaponConfig;
import org.darkoro.zerosmod.zsweapons.CachedWeaponState;
import org.darkoro.zerosmod.zsweapons.WeaponNBTKey;
import org.darkoro.zerosmod.zsweapons.WeaponTypeId;
import java.util.Map;

import static org.darkoro.zerosmod.zsweapons.WeaponNBTKey.*;
import static org.darkoro.zerosmod.zsweapons.WeaponTypeId.*;

public class WeaponAPI {

    /**
     * Reads weapon type from an item
     * @param item
     * @return String type
     */
    public String getWeaponType(IItemStack item) {
        NBTTagCompound nbt = item.getMCNbt();
        if(nbt != null && nbt.hasKey(ZSWEAPON.key) && nbt.getCompoundTag(ZSWEAPON.key).hasKey(TYPE.key)) {
            return nbt.getCompoundTag(ZSWEAPON.key).getString(TYPE.key);
        }
        return DEFAULT;
    }

    /**
     * Sets item type
     * @param type
     * @param item
     */
    public void setWeaponType(String type, IItemStack item) {
        type = ConfigHandler.normalizeKey(type);
        NBTTagCompound nbt = item.getMCNbt();
        if(nbt == null) nbt = new NBTTagCompound();
        if(nbt.hasKey(ZSWEAPON.key)) {
            nbt.getCompoundTag(ZSWEAPON.key).setString(TYPE.key, type);
        } else {
            NBTTagCompound zsweapon = new NBTTagCompound();
            zsweapon.setString(TYPE.key, type);
            nbt.setTag(ZSWEAPON.key, zsweapon);
        }
        item.setMCNbt(nbt);
    }

    public String[] getLoadedWeaponTypeNames() {
        return ServerWeaponConfig.loadedWeaponStates.keySet().toArray(new String[0]);
    }

    public Map<String, CachedWeaponState> getLoadedWeaponTypes() {
        return ServerWeaponConfig.loadedWeaponStates;
    }

    public CachedWeaponState getDefaultWeaponState() {
        return ServerWeaponConfig.defaultWeaponState;
    }
}
