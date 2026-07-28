package org.darkoro.zerosmod.world;

import net.minecraftforge.common.DimensionManager;
import org.darkoro.zerosmod.ZeroSMod;

public final class ModDimensions {

  public static final int PHYLACTERY_PROVIDER_TYPE_ID = 69;
  public static final int DRAGON_REALM_PROVIDER_TYPE_ID = 70;

  private ModDimensions() {}

  public static void registerAll() {
    registerProviderType(PHYLACTERY_PROVIDER_TYPE_ID, WorldProviderPhylactery.class, "Phylactery");
    registerProviderType(DRAGON_REALM_PROVIDER_TYPE_ID, WorldProviderDragonRealm.class, "Dragon Realm");
  }

  private static void registerProviderType(int providerTypeId, Class<? extends net.minecraft.world.WorldProvider> provider,
      String name) {
    if (DimensionManager.registerProviderType(providerTypeId, provider, false)) {
      logInfo("Registered " + name + " provider type " + providerTypeId + ".");
    } else {
      logWarn("Could not register " + name + " provider type " + providerTypeId + "; ID is already in use.");
    }
  }

  private static void logInfo(String message) {
    if (ZeroSMod.LOGGER != null) {
      ZeroSMod.LOGGER.info(message);
    }
  }

  private static void logWarn(String message) {
    if (ZeroSMod.LOGGER != null) {
      ZeroSMod.LOGGER.warn(message);
    }
  }
}
