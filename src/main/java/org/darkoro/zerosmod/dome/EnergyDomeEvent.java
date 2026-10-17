package org.darkoro.zerosmod.dome;

import noppes.npcs.api.entity.IEnergyDome;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.scripted.event.EnergyBarrierEvent;
import org.darkoro.zerosmod.api.event.IZeroSEvent;

/** Script events for entities crossing a spawned energy dome's boundary. */
public abstract class EnergyDomeEvent extends EnergyBarrierEvent {

  private final IEnergyDome dome;
  private final IEntity entity;

  protected EnergyDomeEvent(IEnergyDome dome, IEntity entity) {
    super(dome);
    this.dome = dome;
    this.entity = entity;
  }

  public IEnergyDome getDome() {
    return this.dome;
  }

  public IEntity getEntity() {
    return this.entity;
  }

  public static final class Enter extends EnergyDomeEvent
      implements IZeroSEvent.EnergyDomeEnterEvent {

    public Enter(IEnergyDome dome, IEntity entity) {
      super(dome, entity);
    }

    @Override public String getHookName() {
      return EnergyDomeEventHooks.ENTER_HOOK;
    }
  }

  public static final class Exit extends EnergyDomeEvent
      implements IZeroSEvent.EnergyDomeExitEvent {

    public Exit(IEnergyDome dome, IEntity entity) {
      super(dome, entity);
    }

    @Override public String getHookName() {
      return EnergyDomeEventHooks.EXIT_HOOK;
    }
  }
}
