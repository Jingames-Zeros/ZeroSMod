package org.darkoro.zerosmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import org.darkoro.zerosmod.ZeroSMod;

public class GenericZSSolidBlock extends Block {

  public GenericZSSolidBlock(String name, String textureName) {
    super(Material.rock);
    setBlockName(name);
    setBlockTextureName(textureName);
    setCreativeTab(ZeroSMod.ZeroSModTab);
    setHardness(3.0F);
    setResistance(15.0F);
    setStepSound(Block.soundTypePiston);
  }
}
