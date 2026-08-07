package org.darkoro.zerosmod.zsweapons.API;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.item.IItemStack;
import org.darkoro.zerosmod.config.ConfigHandler;
import org.darkoro.zerosmod.config.ServerWeaponConfig;
import org.darkoro.zerosmod.zsweapons.CachedWeaponState;
import java.util.Map;

public class WeaponAPI {

    /**
     * Reads weapon type from an item
     * @param item
     * @return String type
     */
    public String getWeaponType(IItemStack item) {
        NBTTagCompound nbt = item.getMCNbt();
        if(nbt != null && nbt.hasKey("zsweapon") && nbt.getCompoundTag("zsweapon").hasKey("type")) {
            return nbt.getCompoundTag("zsweapon").getString("type");
        }
        return "default";
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
        if(nbt.hasKey("zsweapon")) {
            nbt.getCompoundTag("zsweapon").setString("type", type);
        } else {
            NBTTagCompound zsweapon = new NBTTagCompound();
            zsweapon.setString("type", type);
            nbt.setTag("zsweapon", zsweapon);
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
