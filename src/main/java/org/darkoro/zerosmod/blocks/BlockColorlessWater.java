package org.darkoro.zerosmod.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import org.darkoro.zerosmod.ZeroSMod;

public class BlockColorlessWater extends BlockFluidClassic {

  @SideOnly(Side.CLIENT) private IIcon stillIcon;
  @SideOnly(Side.CLIENT) private IIcon flowIcon;

  public BlockColorlessWater(Fluid fluid) {
    super(fluid, Material.water);
    setBlockName("colorless_water");
    setLightOpacity(3);
    setCreativeTab(ZeroSMod.Zero_S_Mod_TAB);
  }

  @Override @SideOnly(Side.CLIENT) public void registerBlockIcons(IIconRegister register) {
    stillIcon = register.registerIcon("zerosmod:colorless_water_still");
    flowIcon = register.registerIcon("zerosmod:colorless_water_flow");

    Fluid f = getFluid();
    if (f != null) {
      f.setIcons(stillIcon, flowIcon);
    }
  }

  @Override @SideOnly(Side.CLIENT) public IIcon getIcon(int side, int meta) {
    return (side == 0 || side == 1) ? stillIcon : flowIcon;
  }
}
