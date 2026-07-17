package org.darkoro.zerosmod.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.darkoro.zerosmod.blocks.liquids.BlockColorlessWater;
import org.darkoro.zerosmod.blocks.liquids.BlockDragonWater;
import org.darkoro.zerosmod.blocks.liquids.BlockSpiritWater;
import org.darkoro.zerosmod.blocks.liquids.BlockBlackWater;
import org.darkoro.zerosmod.blocks.liquids.BlockYellowWater;
import org.darkoro.zerosmod.blocks.liquids.BlockSolidBlackWater;
import org.darkoro.zerosmod.blocks.liquids.BlockPinkWater;
import org.darkoro.zerosmod.blocks.liquids.BlockPurpleWater;
import org.darkoro.zerosmod.blocks.liquids.BlockDarkGreenWater;

public class ModBlocks {

  public static Fluid SPIRIT_WATER_FLUID;
  public static Block SPIRIT_WATER_BLOCK;

  public static Fluid COLORLESS_WATER_FLUID;
  public static Block COLORLESS_WATER_BLOCK;

  public static Fluid DRAGON_WATER_FLUID;
  public static Block DRAGON_WATER_BLOCK;

  public static Fluid BLACK_WATER_FLUID;
  public static Block BLACK_WATER_BLOCK;

  public static Fluid YELLOW_WATER_FLUID;
  public static Block YELLOW_WATER_BLOCK;

  public static Fluid SOLID_BLACK_WATER_FLUID;
  public static Block SOLID_BLACK_WATER_BLOCK;

  public static Fluid PINK_WATER_FLUID;
  public static Block PINK_WATER_BLOCK;

  public static Fluid PURPLE_WATER_FLUID;
  public static Block PURPLE_WATER_BLOCK;

  public static Fluid DARK_GREEN_WATER_FLUID;
  public static Block DARK_GREEN_WATER_BLOCK;

  public static void registerAll() {
    SPIRIT_WATER_FLUID = getOrRegisterFluid("spirit_water");
    SPIRIT_WATER_BLOCK = registerFluidBlock(SPIRIT_WATER_FLUID, new BlockSpiritWater(SPIRIT_WATER_FLUID), "spirit_water");

    COLORLESS_WATER_FLUID = getOrRegisterFluid("colorless_water");
    COLORLESS_WATER_BLOCK = registerFluidBlock(COLORLESS_WATER_FLUID, new BlockColorlessWater(COLORLESS_WATER_FLUID), "colorless_water");

    DRAGON_WATER_FLUID = getOrRegisterFluid("dragon_water");
    DRAGON_WATER_BLOCK = registerFluidBlock(DRAGON_WATER_FLUID, new BlockDragonWater(DRAGON_WATER_FLUID), "dragon_water");

    BLACK_WATER_FLUID = getOrRegisterFluid("black_water");
    BLACK_WATER_BLOCK = registerFluidBlock(BLACK_WATER_FLUID, new BlockBlackWater(BLACK_WATER_FLUID), "black_water");

    YELLOW_WATER_FLUID = getOrRegisterFluid("yellow_water");
    YELLOW_WATER_BLOCK = registerFluidBlock(YELLOW_WATER_FLUID, new BlockYellowWater(YELLOW_WATER_FLUID), "yellow_water");

    SOLID_BLACK_WATER_FLUID = getOrRegisterFluid("solid_black_water");
    SOLID_BLACK_WATER_BLOCK = registerFluidBlock(SOLID_BLACK_WATER_FLUID, new BlockSolidBlackWater(SOLID_BLACK_WATER_FLUID), "solid_black_water");

    PINK_WATER_FLUID = getOrRegisterFluid("pink_water");
    PINK_WATER_BLOCK = registerFluidBlock(PINK_WATER_FLUID, new BlockPinkWater(PINK_WATER_FLUID), "pink_water");

    PURPLE_WATER_FLUID = getOrRegisterFluid("purple_water");
    PURPLE_WATER_BLOCK = registerFluidBlock(PURPLE_WATER_FLUID, new BlockPurpleWater(PURPLE_WATER_FLUID), "purple_water");

    DARK_GREEN_WATER_FLUID = getOrRegisterFluid("dark_green_water");
    DARK_GREEN_WATER_BLOCK = registerFluidBlock(DARK_GREEN_WATER_FLUID, new BlockDarkGreenWater(DARK_GREEN_WATER_FLUID), "dark_green_water");
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
