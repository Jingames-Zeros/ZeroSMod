package org.darkoro.zerosmod.config.defaults;

public class WeaponTypesDefaults {
    public final static String[] header = {
            "[Weapon Types]",
            "# Any missing attributes will be filled from default stats.",
            "# A new weapon type is started once a type is defined. All stats after type definition are attributed to that type.",
            "# Stat order does not matter except for 'Attack Cooldown' which will overwrite 'Block Cooldown'.",
            "",
            "# STAT FORMATS",
            "# All stats follow a 'key : value' format where the key is trimmed, stripped and case insensitive",
            "# E.g. 'Attack Cooldown : 10' is valid, 'atTACkcooLDOWN : 10' is also valid, 'Attack Speed' is not valid",
            "# AVAILABLE STATS",
            "# Type : STRING",
            "# Attack Cooldown : INTEGER",
            "# Melee Multiplier : FLOAT",
            "# Melee Range : FLOAT",
            "# Sweet Spot : FLOAT",
            "# Can Charge Ki : BOOLEAN",
            "# Ki Additive Damage : INTEGER",
            "# Ki Multiplier : FLOAT",
            "# Can Block : BOOLEAN",
            "# Block Dex Percent : FLOAT",
            "# Block Cost Multiplier : FLOAT",
            "# Block Cooldown : INTEGER",
            ""
    };

    public final static String[] defaultStats = {
            "# Default stats used for fist and to fill blanks - THIS NEEDS TO BE LOWERCASE",
            "Type : default",
            "Attack Cooldown : 10",
            "Melee Multiplier : 1.0",
            "Melee Range : 3.0",
            "Sweet Spot : 2.0",
            "Can Charge Ki : true",
            "Ki Additive Damage : 0",
            "Ki Multiplier : 1.0",
            "Can Block : true",
            "Block Dex Percent : 1.0",
            "Block Cost Multiplier : 1.0",
            "Block Cooldown : 10",
            ""
    };

    public final static String[] fist = {
            "# Fist type - Identical to default weapon type but allows blocking and ki charging - THIS NEEDS TO BE LOWERCASE - THANKS",
            "Type : fist",
            "Can Charge Ki : true",
            "Can Block : true",
            ""
    };

    public final static String[] sword = {
          "# Sword type - Used for testing",
          "Type : Sword",
          "Attack Cooldown : 20",
          "Melee Multiplier : 2.0",
          "Melee Range : 4.0",
          "Can Charge Ki : false",
          "Can Block : true",
          "Block Dex Percent : 1.0",
          "Block Cost Multiplier : 1.0",
          "Block Cooldown : 10",
          "Sweet Spot : 2.0",
          ""
    };

    // ADD ALL DEFAULTS TO HERE
    public final static String[][] values = {header, defaultStats, fist, sword};
}
