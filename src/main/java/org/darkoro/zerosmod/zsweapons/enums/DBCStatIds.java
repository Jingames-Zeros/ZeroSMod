package org.darkoro.zerosmod.zsweapons.enums;

/**
 * Config file key strings used when parsing the weapon_server.cfg file.
 * Keys are the normalized (lowercase, no spaces) form used in
 * {@link org.darkoro.zerosmod.config.ServerWeaponConfig#loadWeaponState}.
 */
public enum DBCStatIds {
    MELEE(0),
    DEFENSE(1),
    BODY(2),
    STAMINA(3),
    ENERGY_POWER(4),
    ENERGY_POOL(5),
    MAX_SKILLS(6),
    SPEED(7),
    REGEN_RATE_BODY(8),
    REGEN_RATE_STAMINA(9),
    REGEN_RATE_ENERGY(10),
    FLY_SPEED(11);

    public final int id;

    DBCStatIds(int id) { this.id = id; }

    public int getId() {
        return id;
    }
}
