package org.darkoro.zerosmod.guis;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.ResourceLocation;

/**
 * Registry for GUI background textures. Mods can register custom textures
 * with an integer ID, then pass that ID as the {@code y} parameter when
 * opening a GUI to use that texture instead of the default.
 */
public class GuiTextureRegistry {

  private static final Map<Integer, ResourceLocation> chestTextures = new HashMap<>();
  private static final Map<Integer, ResourceLocation> anvilTextures = new HashMap<>();

  private static final ResourceLocation DEFAULT_CHEST = new ResourceLocation(
      "zerosmod", "textures/gui/container/generic_54-6.png");
  private static final ResourceLocation DEFAULT_ANVIL = new ResourceLocation(
      "minecraft", "textures/gui/container/anvil.png");

  /**
   * Register a custom chest GUI texture.
   *
   * @param id      unique integer ID for this texture
   * @param texture the ResourceLocation of the texture
   */
  public static void registerChestTexture(int id, ResourceLocation texture) {
    chestTextures.put(id, texture);
  }

  /**
   * Register a custom anvil GUI texture.
   *
   * @param id      unique integer ID for this texture
   * @param texture the ResourceLocation of the texture
   */
  public static void registerAnvilTexture(int id, ResourceLocation texture) {
    anvilTextures.put(id, texture);
  }

  /**
   * Get the chest texture for the given ID, or the default if not found.
   */
  public static ResourceLocation getChestTexture(int id) {
    return chestTextures.getOrDefault(id, DEFAULT_CHEST);
  }

  /**
   * Get the anvil texture for the given ID, or the default if not found.
   */
  public static ResourceLocation getAnvilTexture(int id) {
    return anvilTextures.getOrDefault(id, DEFAULT_ANVIL);
  }
}