package org.darkoro.zerosmod.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.darkoro.zerosmod.blocks.liquids.GenericZSLiquid;

public class ModBlocks {

  public static final String[] LIQUID_NAMES = {
      "spirit_water",
      "colorless_water",
      "dragon_water",
      "black_water",
      "yellow_water",
      "solid_black_water",
      "pink_water",
      "purple_water",
      "red_water",
      "dark_green_water"
  };

  private static final Map<String, Fluid> LIQUID_FLUIDS = new LinkedHashMap<String, Fluid>();
  private static final Map<String, Block> LIQUID_BLOCKS = new LinkedHashMap<String, Block>();

  public static void registerAll() {
    for (String name : LIQUID_NAMES) {
      Fluid fluid = getOrRegisterFluid(name);
      Block block = new GenericZSLiquid(fluid, name);
      GameRegistry.registerBlock(block, name);
      fluid.setBlock(block);
      LIQUID_FLUIDS.put(name, fluid);
      LIQUID_BLOCKS.put(name, block);
    }
  }

  public static Fluid getLiquidFluid(String name) {
    return LIQUID_FLUIDS.get(name);
  }

  public static Block getLiquidBlock(String name) {
    return LIQUID_BLOCKS.get(name);
  }

  private static Fluid getOrRegisterFluid(String name) {
    Fluid fluid = FluidRegistry.getFluid(name);
    if (fluid == null) {
      fluid = new Fluid(name);
      FluidRegistry.registerFluid(fluid);
    }
    return fluid;
  }
}
