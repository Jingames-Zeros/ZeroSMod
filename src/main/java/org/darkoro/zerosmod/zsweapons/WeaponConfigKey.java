package org.darkoro.zerosmod.zsweapons;

/**
 * Config file key strings used when parsing the weapon_server.cfg file.
 * Keys are the normalized (lowercase, no spaces) form used in
 * {@link org.darkoro.zerosmod.config.ServerWeaponConfig#loadWeaponState}.
 */
public enum WeaponConfigKey {
    TYPE("type"),

    // General
    ATTACK_COOLDOWN("attackcooldown"),
    MELEE_MULTIPLIER("meleemultiplier"),
    MELEE_RANGE("meleerange"),
    SWEET_SPOT("sweetspot"),

    // Ki
    CAN_CHARGE_KI("canchargeki"),
    KI_ADDITIVE_DAMAGE("kiadditivedamage"),
    KI_MULTIPLIER("kimultiplier"),

    // Block-related
    CAN_BLOCK("canblock"),
    BLOCK_DEX_PERCENT("blockdexpercent"),
    BLOCK_COST_MULTIPLIER("blockcostmultiplier"),
    BLOCK_COOLDOWN("blockcooldown");

    /** The exact normalized string matched against config file keys */
    public final String key;

    WeaponConfigKey(String key) {
        this.key = key;
    }

    /**
     * Looks up a {@link WeaponConfigKey} by its normalized string value.
     * Returns {@code null} if the key is not recognized.
     *
     * @param key normalized config key string
     * @return matching enum constant, or {@code null}
     */
    public static WeaponConfigKey fromKey(String key) {
        for (WeaponConfigKey k : values()) {
            if (k.key.equals(key)) return k;
        }
        return null;
    }
}
