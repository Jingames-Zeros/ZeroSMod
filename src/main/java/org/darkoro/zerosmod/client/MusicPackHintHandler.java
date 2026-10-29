package org.darkoro.zerosmod.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import noppes.npcs.CustomNpcs;
import org.darkoro.zerosmod.ZeroSMod;

@SideOnly(Side.CLIENT) public class MusicPackHintHandler {

  public static final MusicPackHintHandler INSTANCE = new MusicPackHintHandler();

  private volatile String pendingUrl;

  private MusicPackHintHandler() {}

  public void requestCheck(String url) {
    pendingUrl = url;
  }

  @SubscribeEvent
  public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
    pendingUrl = null;
  }

  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {
    Minecraft mc = Minecraft.getMinecraft();
    String url = pendingUrl;
    if (event.phase != TickEvent.Phase.END || url == null || mc.thePlayer == null) {
      return;
    }
    pendingUrl = null;

    if (!url.isEmpty() && isMusicPackMissing()) {
      ChatComponentText link = new ChatComponentText(url);
      link.getChatStyle()
          .setColor(EnumChatFormatting.AQUA)
          .setUnderlined(true)
          .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
      mc.thePlayer.addChatMessage(new ChatComponentTranslation("chat.zerosmod.music_pack_missing", link));
    }
  }

  private static boolean isMusicPackMissing() {
    try {
      File soundsDir = new File(CustomNpcs.Dir, "assets/customnpcs");
      if (!soundsDir.isDirectory()) {
        return false;
      }
      File soundsFile = new File(soundsDir, "sounds.json");
      if (!soundsFile.exists()) {
        return true;
      }
      // CustomNPC+ creates an empty sounds.json on startup, so treat that as missing too.
      String content = new String(Files.readAllBytes(soundsFile.toPath()), StandardCharsets.UTF_8);
      JsonElement sounds = new JsonParser().parse(content);
      return sounds.isJsonObject() && sounds.getAsJsonObject().entrySet().isEmpty();
    } catch (Exception e) {
      ZeroSMod.LOGGER.debug("Could not check the CustomNPC sounds.json", e);
      return false;
    }
  }
}
