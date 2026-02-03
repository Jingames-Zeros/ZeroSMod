package org.darkoro.zerosmod.blocks;

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
    SPIRIT_WATER_FLUID = getOrRegisterFluid("spirit_water");
    SPIRIT_WATER_BLOCK = registerFluidBlock(SPIRIT_WATER_FLUID, new BlockSpiritWater(SPIRIT_WATER_FLUID), "spirit_water");

    COLORLESS_WATER_FLUID = getOrRegisterFluid("colorless_water");
    COLORLESS_WATER_BLOCK = registerFluidBlock(COLORLESS_WATER_FLUID, new BlockColorlessWater(COLORLESS_WATER_FLUID), "colorless_water");

    DRAGON_WATER_FLUID = getOrRegisterFluid("dragon_water");
    DRAGON_WATER_BLOCK = registerFluidBlock(DRAGON_WATER_FLUID, new BlockDragonWater(DRAGON_WATER_FLUID), "dragon_water");
  }

  private static Fluid getOrRegisterFluid(String name) {
    Fluid fluid = FluidRegistry.getFluid(name);
    if (fluid == null) {
      fluid = new Fluid(name);
      FluidRegistry.registerFluid(fluid);
    }
    return fluid;
  }

  private static Block registerFluidBlock(Fluid fluid, Block block, String name) {
    GameRegistry.registerBlock(block, name);
    fluid.setBlock(block);
    return block;
  }
}
