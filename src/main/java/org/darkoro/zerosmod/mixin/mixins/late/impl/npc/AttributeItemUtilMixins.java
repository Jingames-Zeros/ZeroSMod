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
        String formattedValue = (new BigDecimal(Float.toString(value))).stripTrailingZeros().toPlainString();
        String color;
        String valueString;
        if (displayName.equals(AttributeBuilder.CAN_BLOCK.getDisplayName()) || displayName.equals(AttributeBuilder.CAN_CHARGE_KI.getDisplayName())) {
            color = value >= 0.0F ? EnumChatFormatting.GREEN.toString() : EnumChatFormatting.RED.toString();
            valueString = color + formattedValue;
        } else if (displayName.equals(AttributeBuilder.ATTACK_COOLDOWN.getDisplayName()) || displayName.equals(AttributeBuilder.BLOCK_COOLDOWN.getDisplayName())) {
            formattedValue = (new BigDecimal(Float.toString(value / 20))).stripTrailingZeros().toPlainString();
            color = value >= 0.0F ? EnumChatFormatting.AQUA.toString() : EnumChatFormatting.RED.toString();
            valueString = color + formattedValue + "s";
        } else if (displayName.equals(AttributeBuilder.SWEET_SPOT.getDisplayName()) || displayName.equals(AttributeBuilder.RANGE.getDisplayName())) {
            color = value >= 0.0F ? EnumChatFormatting.DARK_GREEN.toString() : EnumChatFormatting.RED.toString();
            valueString = color + String.format("%.1f", value) + " blocks";
        } else {
            return;
        }
        valueString = valueString + EnumChatFormatting.GRAY;
        displayName = "§" + def.getColorCode() + displayName;

        cir.setReturnValue(valueString + " " + displayName);
    }
}
