package org.darkoro.zerosmod.screen;

/** Internal screen data attached to CNPC+'s per-player data object. */
public interface PlayerScreenData {

  int zerosmod$getScaledScreenWidth();

  int zerosmod$getScaledScreenHeight();

  int zerosmod$getGuiScale();

  void zerosmod$setScreenData(int width, int height, int guiScale);
}
