package org.darkoro.zerosmod.network;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.config.RaceStatConfig;

public final class RaceStatEditorServer {
  private static RaceStatConfig config;
  private static final Set<UUID> PENDING = ConcurrentHashMap.newKeySet();

  private RaceStatEditorServer() {}

  public static void initialize(Path configDirectory) { config = new RaceStatConfig(configDirectory); }

  public static void open(EntityPlayerMP player) {
    process(player, new RaceStatRequest(0, 0, 0, null, null), true);
  }

  public static void enqueue(EntityPlayerMP player, RaceStatRequest request) {
    UUID id = player.getUniqueID();
    if (!PENDING.add(id)) return;
    ServerTaskScheduler.schedule(() -> {
      try {
        if (!player.playerNetServerHandler.netManager.isChannelOpen()) return;
        process(player, request, false);
      } finally {
        PENDING.remove(id);
      }
    });
  }

  private static void process(EntityPlayerMP player, RaceStatRequest request, boolean open) {
    RaceStatConfig.Snapshot snapshot = null;
    String message;
    try {
      if (!player.canCommandSenderUseCommand(2, "zsmod")) {
        throw new RaceStatConfig.InvalidConfigException("Operator permission is required.");
      }
      RaceStatConfig.validateSelection(request.race, request.classId);
      if (request.save) {
        snapshot = config.save(request.race, request.classId, request.revision, request.values);
        message = "Saved. Server restart required.";
        String selection = RaceStatConfig.RACES[request.race] + " / " + RaceStatConfig.CLASSES[request.classId];
        player.addChatMessage(new ChatComponentText("[ZeroSMod] " + selection + ": " + message));
        ZeroSMod.LOGGER.info("{} saved race stat multipliers for {} (restart required).",
            player.getCommandSenderName(), selection);
      } else {
        snapshot = config.read(request.race, request.classId);
        message = "Saved values loaded. Changes require a server restart.";
      }
    } catch (RaceStatConfig.InvalidConfigException e) {
      message = e.getMessage();
    } catch (IOException e) {
      message = "Could not read/save main.cfg. See the server log.";
      ZeroSMod.LOGGER.error("Race stat config access failed", e);
    }
    ZeroSMod.network.sendTo(new RaceStatResponse(request.requestId, request.race, request.classId,
        open, message, snapshot), player);
  }
}
