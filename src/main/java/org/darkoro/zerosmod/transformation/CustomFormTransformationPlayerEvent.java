package org.darkoro.zerosmod.transformation;

import kamkeel.npcdbc.api.form.IForm;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.event.player.PlayerEvent;
import org.darkoro.zerosmod.api.event.IZeroSEvent;

public abstract class CustomFormTransformationPlayerEvent extends PlayerEvent {

  protected CustomFormTransformationPlayerEvent(IPlayer player) {
    super(player);
  }

  public static final class BeginsTransforming extends CustomFormTransformationPlayerEvent
      implements IZeroSEvent.BeginsTransformingEvent {

    private final IForm targetForm;

    public BeginsTransforming(EntityPlayerMP player, IForm targetForm) {
      super((IPlayer)NpcAPI.Instance().getIEntity(player));
      this.targetForm = targetForm;
    }

    @Override public String getHookName() {
      return CustomFormTransformationEventHooks.BEGINS_TRANSFORMING_HOOK;
    }

    @Override public IForm getTargetForm() {
      return this.targetForm;
    }

    @Override public int getTargetFormId() {
      return this.targetForm.getID();
    }

    @Override public String getTargetFormName() {
      return this.targetForm.getName();
    }
  }
}
