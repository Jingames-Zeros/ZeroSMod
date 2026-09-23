package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import com.llamalad7.mixinextras.sugar.Local;
import kamkeel.npcs.controllers.data.attribute.AttributeDefinition;
import kamkeel.npcs.controllers.data.attribute.AttributeValueType;
import kamkeel.npcs.util.AttributeItemUtil;
import net.minecraft.util.EnumChatFormatting;
import org.darkoro.zerosmod.zsweapons.attributes.AttributeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static org.spongepowered.asm.lib.Opcodes.IF_ACMPNE;

@Mixin(
        value = AttributeItemUtil.class,
        remap = false
)
public class AttributeItemUtilMixins {
    @Inject(
            method = "formatAttributeLine(Lkamkeel/npcs/controllers/data/attribute/AttributeDefinition;Lkamkeel/npcs/controllers/data/attribute/AttributeDefinition$AttributeSection;Ljava/lang/Float;Ljava/lang/String;)Ljava/lang/String;",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private static void addAdditionalValueTypes(AttributeDefinition def, AttributeDefinition.AttributeSection section, Float value, String displayName, CallbackInfoReturnable<String> cir) {
        String formattedValue;
        String color;
        String valueString;
        switch (displayName) {
            case AttributeBuilder.CAN_BLOCK_DISPLAY, AttributeBuilder.CAN_CHARGE_KI_DISPLAY -> {
                cir.setReturnValue("§" + def.getColorCode() + displayName);
                return;
            }
            case AttributeBuilder.ATTACK_PERCENT_DISPLAY, AttributeBuilder.KI_COST_PERCENT_DISPLAY,
                 AttributeBuilder.KI_PERCENT_DISPLAY, AttributeBuilder.BLOCK_DEX_PERCENT_DISPLAY,
                 AttributeBuilder.BLOCK_COST_PERCENT_DISPLAY -> {
                formattedValue = (new BigDecimal(Float.toString(value))).stripTrailingZeros().toPlainString();
                cir.setReturnValue(EnumChatFormatting.GREEN + formattedValue + "% " + "§" + def.getColorCode() + displayName);
                return;
            }
            case AttributeBuilder.ATTACK_COOLDOWN_DISPLAY, AttributeBuilder.BLOCK_COOLDOWN_DISPLAY -> {
                color = value >= 0.0F ? EnumChatFormatting.AQUA.toString() : EnumChatFormatting.RED.toString();
                formattedValue = (new BigDecimal(Float.toString(value / 20))).stripTrailingZeros().toPlainString();
                valueString = color + formattedValue + "s";
            }
            case AttributeBuilder.SWEET_SPOT_DISPLAY, AttributeBuilder.RANGE_DISPLAY ->
                valueString = EnumChatFormatting.DARK_GREEN + String.format("%.1f", value) + " blocks";
            default -> {
                return;
            }
        }
        valueString = valueString + EnumChatFormatting.GRAY;
        displayName = "§" + def.getColorCode() + displayName;
        cir.setReturnValue(valueString + " " + displayName);
    }
}
