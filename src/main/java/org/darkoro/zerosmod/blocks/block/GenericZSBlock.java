package org.darkoro.zerosmod.blocks.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import org.darkoro.zerosmod.ZeroSMod;

public class GenericZSBlock extends Block {

  private final String name;

  public GenericZSBlock(String name) {
    super(Material.rock);
    this.name = name;
    setBlockName(name);
    setBlockTextureName("zerosmod:block/" + name);
    setCreativeTab(ZeroSMod.ZeroSModTab);
    setHardness(3.0F);
    setResistance(15.0F);
    setStepSound(Block.soundTypePiston);
  }

  public String getRegistryName() {
    return name;
  }
}
