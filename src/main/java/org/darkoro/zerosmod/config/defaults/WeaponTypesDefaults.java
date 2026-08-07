package org.darkoro.zerosmod.config.defaults;

public class WeaponTypesDefaults {
    public static String[] header = {
            "[Weapon Types]",
            "# Any missing attributes will be filled from default stats.",
            ""
    };

    public static String[] defaultStats = {
            "# Default stats used for fist and to fill blanks",
            "Type : Default",
            "Attack Cooldown : 10",
            "Melee Multiplier : 1.0",
            "Melee Range : 3.0",
            "Can Charge Ki : true",
            "Ki Additive Damage : 0",
            "Ki Multiplier : 1.0",
            "Can Block : true",
            "Block Dex Percent : 1.0",
            "Block Cost Multiplier : 1.0",
            "Block Cooldown : 10",
            ""
    };

    public static String[] sword = {
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
    public static String[][] values = {header, defaultStats, sword};
}
