package org.darkoro.zerosmod.zsweapons.enums;

/**
 * NBT key strings used to read/write weapon data on item compounds.
 * The root compound tag is {@link #ZSWEAPON}; all stat keys are nested inside it.
 */
public enum WeaponNBTKey {
    /** Root NBT compound tag that holds all weapon data */
    ZSWEAPON("zsweapon"),
    /** The weapon type identifier stored in the root compound */
    TYPE("type"),

    // General
    ATTACK_COOLDOWN("attackcooldown"),

    // Ki
    CAN_CHARGE("cancharge"),

    // Block
    CAN_BLOCK("canblock");

    /** The exact string value written to / read from NBT */
    public final String key;

    WeaponNBTKey(String key) {
        this.key = key;
    }
}
