package org.darkoro.guiapi.callbacks;

import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayer;

public interface IAnvilGuiCallbacks extends IGuiContextProvider {

  String getGuiTitle(EntityPlayer ply);
  String getInitialMessage(EntityPlayer ply);
  Function<String, String> getInputValidator(EntityPlayer ply);
  Function<String, Boolean> getOutputHandler(EntityPlayer ply);

}
