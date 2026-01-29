package org.darkoro.guiapi.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ModBlocks {
    public static Fluid SPIRIT_WATER_FLUID;
    public static Block SPIRIT_WATER_BLOCK;

    public static Fluid COLORLESS_WATER_FLUID;
    public static Block COLORLESS_WATER_BLOCK;

    public static void registerAll() {
        SPIRIT_WATER_FLUID = new Fluid("spirit_water");
        COLORLESS_WATER_FLUID = new Fluid("colorless_water");

        SPIRIT_WATER_BLOCK = new BlockSpiritWater(SPIRIT_WATER_FLUID);
        COLORLESS_WATER_BLOCK = new BlockColorlessWater(COLORLESS_WATER_FLUID);

        FluidRegistry.registerFluid(SPIRIT_WATER_FLUID);
        FluidRegistry.registerFluid(COLORLESS_WATER_FLUID);
        GameRegistry.registerBlock(SPIRIT_WATER_BLOCK, "spirit_water");
        GameRegistry.registerBlock(COLORLESS_WATER_BLOCK, "colorless_water");
    }
}
