package org.darkoro.zerosmod.blocks.liquid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import org.darkoro.zerosmod.ZeroSMod;

public class GenericZSLiquid extends BlockFluidClassic {

  private final String name;

  @SideOnly(Side.CLIENT) private IIcon stillIcon;
  @SideOnly(Side.CLIENT) private IIcon flowIcon;

  public GenericZSLiquid(Fluid fluid, String name) {
    super(fluid, Material.water);
    this.name = name;
    setBlockName(name);
    setLightOpacity(3);
    setCreativeTab(ZeroSMod.ZeroSModTab);
  }

  @Override @SideOnly(Side.CLIENT) public void registerBlockIcons(IIconRegister register) {
    stillIcon = register.registerIcon("zerosmod:liquids/" + name + "_still");
    flowIcon = register.registerIcon("zerosmod:liquids/" + name + "_flow");

    Fluid fluid = getFluid();
    if (fluid != null) {
      fluid.setIcons(stillIcon, flowIcon);
    }
  }

  @Override @SideOnly(Side.CLIENT) public IIcon getIcon(int side, int meta) {
    return (side == 0 || side == 1) ? stillIcon : flowIcon;
  }
}
