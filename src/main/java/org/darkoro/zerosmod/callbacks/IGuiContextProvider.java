package org.darkoro.zerosmod.callbacks;

import net.minecraft.entity.player.EntityPlayer;

public interface IGuiContextProvider {

  // Would have made this abstract - DBCArmor implements GuiContextProvider though so I'm fucked
  default String getGuiTitle(EntityPlayer ply) { return ""; }
  default boolean isEditable(EntityPlayer ply) { return false; }
  default boolean isInventory(EntityPlayer ply) { return false; }

}
