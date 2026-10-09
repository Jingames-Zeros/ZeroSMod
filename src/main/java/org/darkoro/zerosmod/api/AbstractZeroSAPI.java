package org.darkoro.zerosmod.api;

import cpw.mods.fml.common.Loader;
import org.darkoro.zerosmod.ZeroSMod;

/**
 * @hidden
 * Script-facing Zero S API.
 * <p>
 * Scripts can access this singleton through the CNPC global object named {@code ZSAPI}.
 */
public abstract class AbstractZeroSAPI implements ZSAPI {

  private static AbstractZeroSAPI instance;

  /**
   * @hidden
   * @return true when Zero S Mod is loaded.
   */
  public static boolean IsAvailable() {
    return Loader.isModLoaded(ZeroSMod.MODID);
  }

  /**
   * @hidden
   * @return the active Zero S API instance, or null if it cannot be created.
   */
  public static AbstractZeroSAPI Instance() {
    if (instance != null) {
      return instance;
    }
    if (!IsAvailable()) {
      return null;
    }

    try {
      Class<?> apiClass = Class.forName("org.darkoro.zerosmod.scripted.ZeroSAPI");
      instance = (AbstractZeroSAPI)apiClass.getMethod("Instance").invoke(null);
    } catch (Exception e) {
      ZeroSMod.LOGGER.error("Could not create the ZSAPI script object", e);
    }
    return instance;
  }

  /**
   * @hidden
   * @return the loaded Zero S Mod version.
   */
  public abstract String getVersion();
}
