package org.darkoro.zerosmod.zsweapons;

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
    ATTACK_MULTIPLIER("attackmultiplier"),
    SWEET_SPOT("sweetspot"),
    RANGE("range"),

    // Ki
    CAN_CHARGE("cancharge"),
    KI_MULTIPLIER("kimultiplier"),
    KI_ADDITIVE("kiadditive"),

    // Block
    CAN_BLOCK("canblock"),
    BLOCK_REDUCTION("blockreduction"),
    BLOCK_COST_MULTIPLIER("blockcostmultiplier"),
    BLOCK_COOLDOWN("blockcooldown");

    /** The exact string value written to / read from NBT */
    public final String key;

    WeaponNBTKey(String key) {
        this.key = key;
    }
}
