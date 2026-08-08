package org.darkoro.zerosmod.zsweapons;

/**
 * Built-in sentinel weapon type identifiers.
 * <p>
 * Weapon type names are user-defined strings loaded from config, so they
 * cannot be enumerated. Only these two reserved identifiers have fixed
 * meaning in the weapon system and are centralized here.
 * </p>
 */
public final class WeaponTypeId {
    /** Fallback type used for bare fists and when no weapon type is set */
    public static final String DEFAULT = "default";

    /**
     * Special type whose stats are read directly from the item's NBT compound
     * rather than from the loaded weapon type map.
     */
    public static final String SPECIAL = "special";

    private WeaponTypeId() {}
}
