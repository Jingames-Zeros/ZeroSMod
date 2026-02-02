package org.darkoro.ZeroSMod.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ModBlocks {
    public static Fluid SPIRIT_WATER_FLUID;
    public static Block SPIRIT_WATER_BLOCK;

    public static Fluid COLORLESS_WATER_FLUID;
    public static Block COLORLESS_WATER_BLOCK;

    public static Fluid DRAGON_WATER_FLUID;
    public static Block DRAGON_WATER_BLOCK;

    public static void registerAll() {
    // Forge is silly
        SPIRIT_WATER_FLUID = FluidRegistry.getFluid("spirit_water");
        if (SPIRIT_WATER_FLUID == null) {
            SPIRIT_WATER_FLUID = new Fluid("spirit_water");
            FluidRegistry.registerFluid(SPIRIT_WATER_FLUID);
        }

        SPIRIT_WATER_BLOCK = new BlockSpiritWater(SPIRIT_WATER_FLUID);
        GameRegistry.registerBlock(SPIRIT_WATER_BLOCK, "spirit_water");
        SPIRIT_WATER_FLUID.setBlock(SPIRIT_WATER_BLOCK);

        COLORLESS_WATER_FLUID = FluidRegistry.getFluid("colorless_water");
        if (COLORLESS_WATER_FLUID == null) {
            COLORLESS_WATER_FLUID = new Fluid("colorless_water");
            FluidRegistry.registerFluid(COLORLESS_WATER_FLUID);
        }

        COLORLESS_WATER_BLOCK = new BlockColorlessWater(COLORLESS_WATER_FLUID);
        GameRegistry.registerBlock(COLORLESS_WATER_BLOCK, "colorless_water");
        COLORLESS_WATER_FLUID.setBlock(COLORLESS_WATER_BLOCK);

        DRAGON_WATER_FLUID = FluidRegistry.getFluid("dragon_water");
        if (DRAGON_WATER_FLUID == null) {
            DRAGON_WATER_FLUID = new Fluid("dragon_water");
            FluidRegistry.registerFluid(DRAGON_WATER_FLUID);
        }

        DRAGON_WATER_BLOCK = new BlockDragonWater(DRAGON_WATER_FLUID);
        GameRegistry.registerBlock(DRAGON_WATER_BLOCK, "dragon_water");
        DRAGON_WATER_FLUID.setBlock(DRAGON_WATER_BLOCK);
    }
}
