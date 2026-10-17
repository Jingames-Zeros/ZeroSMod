package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.controllers.data.PlayerData;
import org.darkoro.zerosmod.screen.PlayerScreenData;

/** Client report of the values exposed by the player screen script methods. */
public final class SyncPlayerScreenPacket implements IMessage {

  private int width;
  private int height;
  private int guiScale;

  public SyncPlayerScreenPacket() {}

  public SyncPlayerScreenPacket(int width, int height, int guiScale) {
    this.width = width;
    this.height = height;
    this.guiScale = guiScale;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    width = buf.readInt();
    height = buf.readInt();
    guiScale = buf.readInt();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(width);
    buf.writeInt(height);
    buf.writeInt(guiScale);
  }

  public static final class Handler implements IMessageHandler<SyncPlayerScreenPacket, IMessage> {

    @Override
    public IMessage onMessage(SyncPlayerScreenPacket message, MessageContext context) {
      EntityPlayerMP player = context.getServerHandler().playerEntity;
      int width = message.width > 0 ? message.width : -1;
      int height = message.height > 0 ? message.height : -1;
      int guiScale = message.guiScale >= 0 ? message.guiScale : -1;
      ServerTaskScheduler.schedule(() -> {
        PlayerData data = PlayerData.get(player);
        ((PlayerScreenData)data).zerosmod$setScreenData(width, height, guiScale);
      });
      return null;
    }
  }
}
