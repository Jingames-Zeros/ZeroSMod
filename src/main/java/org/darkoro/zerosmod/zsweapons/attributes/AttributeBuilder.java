package org.darkoro.zerosmod.zsweapons.attributes;

import kamkeel.npcs.controllers.AttributeController;
import kamkeel.npcs.controllers.data.attribute.AttributeDefinition;
import kamkeel.npcs.controllers.data.attribute.AttributeValueType;

import javax.print.attribute.standard.MediaSize;
import java.util.HashSet;
import java.util.Set;

public class AttributeBuilder {
    public static final String ATTACK_COOLDOWN_KEY = "attack_cooldown";
    public static final String ATTACK_PERCENT_KEY = "attack_multiplier";
    public static final String RANGE_KEY = "range";
    public static final String SWEET_SPOT_KEY = "sweet_spot";
    public static final String CAN_CHARGE_KI_KEY = "can_charge_ki";
    public static final String KI_PERCENT_KEY = "ki_multiplier";
    public static final String KI_ADDITIVE_KEY = "ki_additive";
    public static final String KI_COST_PERCENT_KEY = "ki_cost_multiplier";
    public static final String CAN_BLOCK_KEY = "can_block";
    public static final String BLOCK_DEX_PERCENT_KEY = "block_dex_multiplier";
    public static final String BLOCK_COST_PERCENT_KEY = "block_dex_cost_multiplier";
    public static final String BLOCK_COOLDOWN_KEY = "block_cooldown";
    public static final String ATTACK_COOLDOWN_DISPLAY = "Attack Cooldown";
    public static final String ATTACK_PERCENT_DISPLAY = "Attack Multiplier";
    public static final String RANGE_DISPLAY = "Attack Range";
    public static final String SWEET_SPOT_DISPLAY = "Sweet Spot Distance";
    public static final String CAN_CHARGE_KI_DISPLAY = "Allows Charging Ki";
    public static final String KI_PERCENT_DISPLAY = "Ki Power Multiplier";
    public static final String KI_ADDITIVE_DISPLAY = "Ki Power";
    public static final String KI_COST_PERCENT_DISPLAY = "Ki Cost Multiplier";
    public static final String CAN_BLOCK_DISPLAY = "Allows Blocking";
    public static final String BLOCK_DEX_PERCENT_DISPLAY = "Block Dexterity Multiplier";
    public static final String BLOCK_COST_PERCENT_DISPLAY = "Block Cost Multiplier";
    public static final String BLOCK_COOLDOWN_DISPLAY = "Block Cooldown";
    public static AttributeDefinition ATTACK_COOLDOWN;
    public static AttributeDefinition ATTACK_PERCENT;
    public static AttributeDefinition RANGE;
    public static AttributeDefinition SWEET_SPOT;
    public static AttributeDefinition CAN_CHARGE_KI;
    public static AttributeDefinition KI_PERCENT;
    public static AttributeDefinition KI_ADDITIVE;
    public static AttributeDefinition KI_COST_PERCENT;
    public static AttributeDefinition CAN_BLOCK;
    public static AttributeDefinition BLOCK_DEX_PERCENT;
    public static AttributeDefinition BLOCK_COST_PERCENT;
    public static AttributeDefinition BLOCK_COOLDOWN;

    public AttributeBuilder() {
        ATTACK_COOLDOWN = AttributeController.registerAttribute(ATTACK_COOLDOWN_KEY, ATTACK_COOLDOWN_DISPLAY, '4', AttributeValueType.FLAT, AttributeDefinition.AttributeSection.INFO);
        ATTACK_PERCENT = AttributeController.registerAttribute(ATTACK_PERCENT_KEY, ATTACK_PERCENT_DISPLAY, 'c', AttributeValueType.PERCENT, AttributeDefinition.AttributeSection.STATS);
        RANGE = AttributeController.registerAttribute(RANGE_KEY, RANGE_DISPLAY, '7', AttributeValueType.FLAT, AttributeDefinition.AttributeSection.EXTRA);
        SWEET_SPOT = AttributeController.registerAttribute(SWEET_SPOT_KEY, SWEET_SPOT_DISPLAY, '7', AttributeValueType.FLAT, AttributeDefinition.AttributeSection.EXTRA);
        CAN_CHARGE_KI = AttributeController.registerAttribute(CAN_CHARGE_KI_KEY, CAN_CHARGE_KI_DISPLAY, '3', AttributeValueType.FLAT, AttributeDefinition.AttributeSection.EXTRA);
        KI_PERCENT = AttributeController.registerAttribute(KI_PERCENT_KEY, KI_PERCENT_DISPLAY, '3', AttributeValueType.PERCENT, AttributeDefinition.AttributeSection.STATS);
        KI_ADDITIVE = AttributeController.registerAttribute(KI_ADDITIVE_KEY, KI_ADDITIVE_DISPLAY, '3', AttributeValueType.FLAT, AttributeDefinition.AttributeSection.BASE);
        KI_COST_PERCENT = AttributeController.registerAttribute(KI_COST_PERCENT_KEY, KI_COST_PERCENT_DISPLAY, '3', AttributeValueType.PERCENT, AttributeDefinition.AttributeSection.STATS);
        CAN_BLOCK = AttributeController.registerAttribute(CAN_BLOCK_KEY, CAN_BLOCK_DISPLAY, '6', AttributeValueType.FLAT, AttributeDefinition.AttributeSection.EXTRA);
        BLOCK_DEX_PERCENT = AttributeController.registerAttribute(BLOCK_DEX_PERCENT_KEY, BLOCK_DEX_PERCENT_DISPLAY, '6', AttributeValueType.PERCENT, AttributeDefinition.AttributeSection.STATS);
        BLOCK_COST_PERCENT = AttributeController.registerAttribute(BLOCK_COST_PERCENT_KEY, BLOCK_COST_PERCENT_DISPLAY, '6', AttributeValueType.PERCENT, AttributeDefinition.AttributeSection.STATS);
        BLOCK_COOLDOWN = AttributeController.registerAttribute(BLOCK_COOLDOWN_KEY, BLOCK_COOLDOWN_DISPLAY, '6', AttributeValueType.FLAT, AttributeDefinition.AttributeSection.INFO);
    }
}
