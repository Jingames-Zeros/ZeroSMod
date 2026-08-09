package org.darkoro.zerosmod.zsweapons.API;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.item.IItemStack;
import org.darkoro.zerosmod.config.ConfigHandler;
import org.darkoro.zerosmod.config.ServerWeaponConfig;
import org.darkoro.zerosmod.zsweapons.CachedWeaponState;
import org.darkoro.zerosmod.zsweapons.ZSWeaponUtils;

import java.util.Map;

import static org.darkoro.zerosmod.zsweapons.enums.WeaponNBTKey.*;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponTypeId.*;

public class WeaponAPI {

    /**
     * Reads weapon type from an item
     * @param item .
     * @return String type
     */
    public String getWeaponType(IItemStack item) {
        ItemStack mcItem = item.getMCItemStack();
        if(ZSWeaponUtils.hasZSWeaponTag(mcItem) && ZSWeaponUtils.getZSWeaponTag(mcItem).hasKey(TYPE.key)) {
            return ZSWeaponUtils.getZSWeaponTag(mcItem).getString(TYPE.key);
        }
        return DEFAULT;
    }

    /**
     * Sets item type
     * @param type Loaded weapon type
     * @param item .
     */
    public void setWeaponType(String type, IItemStack item) {
        NBTTagCompound nbt = ZSWeaponUtils.hasZSWeaponTag(item.getMCItemStack()) ? item.getMCNbt().getCompoundTag(ZSWEAPON.key) : new NBTTagCompound();
        nbt.setString(TYPE.key, ConfigHandler.normalizeKey(type));
        item.getMCNbt().setTag(ZSWEAPON.key, nbt);
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
