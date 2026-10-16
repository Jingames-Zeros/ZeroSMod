package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import org.darkoro.zerosmod.transformation.CustomFormTransformationEventHooks;

public final class BeginCustomTransformationPacket implements IMessage {

  private int targetFormId;

  public BeginCustomTransformationPacket() {}

  public BeginCustomTransformationPacket(int targetFormId) {
    this.targetFormId = targetFormId;
  }

  @Override public void fromBytes(ByteBuf buf) {
    this.targetFormId = buf.readInt();
  }

  @Override public void toBytes(ByteBuf buf) {
    buf.writeInt(this.targetFormId);
  }

  public static final class Handler implements IMessageHandler<BeginCustomTransformationPacket, IMessage> {

    @Override public IMessage onMessage(BeginCustomTransformationPacket message, MessageContext context) {
      final EntityPlayerMP player = context.getServerHandler().playerEntity;
      final int targetFormId = message.targetFormId;
      ServerTaskScheduler.schedule(() -> CustomFormTransformationEventHooks.onBegin(player, targetFormId));
      return null;
    }
  }
}
