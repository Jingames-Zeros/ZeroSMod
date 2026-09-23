package org.darkoro.zerosmod.zsweapons.API;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.INbt;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.ScriptNbt;
import org.darkoro.zerosmod.config.ConfigHandler;
import org.darkoro.zerosmod.config.ServerWeaponConfig;
import org.darkoro.zerosmod.zsweapons.ZSWeaponUtils;
import org.darkoro.zerosmod.zsweapons.cache.CachedWeaponStats;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponNBTKey.*;
import static org.darkoro.zerosmod.zsweapons.enums.WeaponTypeId.*;
import java.util.Map;

public class WeaponAPI {
    public static WeaponAPI INSTANCE = new WeaponAPI();

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
        ScriptNbt nbt = ZSWeaponUtils.hasZSWeaponTag(item.getMCItemStack()) ? (ScriptNbt) item.getNbt().getCompound(ZSWEAPON.key) : new ScriptNbt(new NBTTagCompound());
        nbt.setString(TYPE.key, ConfigHandler.normalizeKey(type));
        item.getNbt().setCompound(ZSWEAPON.key, nbt);
    }

    public String[] getLoadedWeaponTypeNames() {
        return ServerWeaponConfig.loadedWeaponStats.keySet().toArray(new String[0]);
    }

    public Map<String, CachedWeaponStats> getLoadedWeaponTypes() {
        return ServerWeaponConfig.loadedWeaponStats;
    }

    public CachedWeaponStats getDefaultWeaponState() {
        return ServerWeaponConfig.loadedWeaponStats.get(DEFAULT);
    }
}
