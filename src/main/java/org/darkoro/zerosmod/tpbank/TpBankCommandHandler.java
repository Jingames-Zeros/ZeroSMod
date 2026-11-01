package org.darkoro.zerosmod.tpbank;

import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.CommandEvent;

/**
 * Banks the TP that {@code /jrmctp} would clamp away, before the command runs.
 *
 * <p>The command is {@code /jrmctp amount [playerName] [(nbtdata)]}. For a plain positive amount
 * the part above DBC's cap is banked and the amount is rewritten to what still fits, so DBC then
 * gives exactly that. Amounts computed from NBT data are left to DBC untouched.</p>
 */
public final class TpBankCommandHandler {

  public static final TpBankCommandHandler INSTANCE = new TpBankCommandHandler();

  /** DBC's power type whose {@code /jrmctp} grants experience instead of TP. */
  private static final byte SAO_POWER_TYPE = 3;

  private TpBankCommandHandler() {}

  @SubscribeEvent(priority = EventPriority.LOW)
  public void onCommand(CommandEvent event) {
    String[] args = event.parameters;
    if (event.isCanceled() || args == null || args.length == 0 || args.length > 2
        || !event.command.canCommandSenderUseCommand(event.sender)
        || !"jrmctp".equals(event.command.getCommandName())) {
      return;
    }
    long amount;
    try {
      amount = Long.parseLong(args[0].replace(" ", ""));
    } catch (NumberFormatException e) {
      return;
    }
    if (amount <= 0) {
      return;
    }
    EntityPlayerMP target;
    try {
      target = args.length > 1
          ? CommandBase.getPlayer(event.sender, args[1])
          : CommandBase.getCommandSenderAsPlayer(event.sender);
    } catch (Exception e) {
      return; // DBC reports the bad target itself
    }
    if (target == null || JRMCoreH.nbt(target, "pres").getByte("jrmcPwrtyp") == SAO_POWER_TYPE) {
      return;
    }
    int before = Math.max(0, TpBank.getCurrentTP(target));
    long total = amount > Long.MAX_VALUE - before ? Long.MAX_VALUE : before + amount;
    if (TpBank.captureOverflow(target, before, total) < total) {
      args[0] = Long.toString(Math.max(0, TpBank.getMaxTP() - before));
    }
  }
}
