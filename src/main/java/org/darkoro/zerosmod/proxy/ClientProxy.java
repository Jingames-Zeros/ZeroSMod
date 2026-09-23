package org.darkoro.zerosmod.proxy;

import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.client.BiomeFogHandler;
import org.darkoro.zerosmod.client.DimensionVisibilityHandler;
import org.darkoro.zerosmod.client.PhylacterySkyRenderer;
import org.darkoro.zerosmod.client.ZSTabOverlayHandler;
import org.darkoro.zerosmod.input.KeyInputHandler;
import org.darkoro.zerosmod.input.KeybindHandler;
import net.minecraftforge.common.MinecraftForge;
import org.darkoro.zerosmod.ki.KiAttackSafety;
import org.darkoro.zerosmod.network.SyncKiAttackStatePacket;
import org.darkoro.zerosmod.world.WorldProviderPhylactery;
import org.darkoro.zerosmod.zsweapons.client.ClientWeaponHandler;

public class ClientProxy extends CommonProxy {

  @Override
  public void init(FMLInitializationEvent event) {
    super.init(event);
    KeybindHandler.init();
    FMLCommonHandler.instance().bus().register(new KeyInputHandler());
    FMLCommonHandler.instance().bus().register(ClientWeaponHandler.INSTANCE);
    MinecraftForge.EVENT_BUS.register(new ZSTabOverlayHandler());
    MinecraftForge.EVENT_BUS.register(new BiomeFogHandler());
    MinecraftForge.EVENT_BUS.register(new DimensionVisibilityHandler());
    MinecraftForge.EVENT_BUS.register(ClientWeaponHandler.INSTANCE);
  }

  @Override
  public void postInit(FMLPostInitializationEvent event) {
    super.postInit(event);
    String defaultFile = "/assets/zerosmod/lang/%s.lang";
    List<String> langs = Arrays.asList(
        "de_DE", "en_AU", "en_CA", "en_GB", "en_US", "es_AR", "es_ES", "es_MX",
        "fr_FR", "it_IT", "pl_PL", "pt_BR");
    for (String lang : langs) {
      LanguageRegistry.instance().loadLocalization(String.format(defaultFile, lang), lang, false);
      ZeroSMod.LOGGER.info("Loaded language {}", lang);
    }
  }

  @Override
  public void configurePhylacterySky(WorldProviderPhylactery provider) {
    provider.setSkyRenderer(new PhylacterySkyRenderer());
  }

  @Override
  public void applyKiAttackColorSync(int entityId, int color, int color2) {
    World world = Minecraft.getMinecraft().theWorld;
    if (world == null) {
      return;
    }

    Entity entity = world.getEntityByID(entityId);
    if (entity instanceof EntityEnergyAtt && entity instanceof KiAttackSafety.KiAttackInternals) {
      KiAttackSafety.KiAttackInternals internals = (KiAttackSafety.KiAttackInternals)entity;
      internals.zerosmod$setColor(color);
      internals.zerosmod$setColor2(color2);
    }
  }

  @Override
  public void applyKiAttackStateSync(SyncKiAttackStatePacket packet) {
    World world = Minecraft.getMinecraft().theWorld;
    if (world == null || packet == null) {
      return;
    }

    Entity entity = world.getEntityByID(packet.entityId);
    if (!(entity instanceof EntityEnergyAtt)) {
      return;
    }

    EntityEnergyAtt attack = (EntityEnergyAtt)entity;
    attack.setPositionAndRotation(packet.posX, packet.posY, packet.posZ, packet.rotationYaw, packet.rotationPitch);
    attack.lastTickPosX = packet.posX;
    attack.lastTickPosY = packet.posY;
    attack.lastTickPosZ = packet.posZ;
    attack.prevPosX = packet.posX;
    attack.prevPosY = packet.posY;
    attack.prevPosZ = packet.posZ;
    attack.motionX = packet.motionX;
    attack.motionY = packet.motionY;
    attack.motionZ = packet.motionZ;
    attack.motionXStart = packet.motionX;
    attack.motionYStart = packet.motionY;
    attack.motionZStart = packet.motionZ;
    attack.prevRotationYaw = packet.rotationYaw;
    attack.prevRotationPitch = packet.rotationPitch;
    attack.startRotationYaw = packet.rotationYaw;
    attack.startRotationPitch = packet.rotationPitch;
    attack.velocityChanged = true;

    if (attack instanceof KiAttackSafety.KiAttackInternals) {
      KiAttackSafety.KiAttackInternals internals = (KiAttackSafety.KiAttackInternals)attack;
      internals.zerosmod$setStartPosition(packet.startX, packet.startY, packet.startZ);
      internals.zerosmod$setTargetPosition(packet.targetX, packet.targetY, packet.targetZ);
      internals.zerosmod$setTarget(null);
    }
    KiAttackSafety.markRedirected(attack, packet.startX, packet.startY, packet.startZ);
    attack.hadTarget = false;
    attack.shooterHolds = false;
  }
}
