package org.darkoro.zerosmod.blocks.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import org.darkoro.zerosmod.ZeroSMod;

public class GenericZSBlock extends Block {

  private final String name;
  @SideOnly(Side.CLIENT) private IIcon[] sideIcons;

  public GenericZSBlock(String name) {
    super(Material.rock);
    this.name = name;
    setBlockName(name);
    setCreativeTab(ZeroSMod.ZeroSModTab);
    setHardness(3.0F);
    setResistance(15.0F);
    setStepSound(Block.soundTypePiston);
  }

  @Override @SideOnly(Side.CLIENT) public void registerBlockIcons(IIconRegister register) {
    sideIcons = new IIcon[6];
    sideIcons[0] = register.registerIcon("zerosmod:block/" + name + "_bottom");
    sideIcons[1] = register.registerIcon("zerosmod:block/" + name + "_top");
    sideIcons[2] = register.registerIcon("zerosmod:block/" + name + "_north");
    sideIcons[3] = register.registerIcon("zerosmod:block/" + name + "_south");
    sideIcons[4] = register.registerIcon("zerosmod:block/" + name + "_west");
    sideIcons[5] = register.registerIcon("zerosmod:block/" + name + "_east");
  }

  @Override @SideOnly(Side.CLIENT) public IIcon getIcon(int side, int meta) {
    if (sideIcons == null || side < 0 || side >= sideIcons.length || sideIcons[side] == null) {
      return super.getIcon(side, meta);
    }
    return sideIcons[side];
  }

  public String getRegistryName() {
    return name;
  }
}
