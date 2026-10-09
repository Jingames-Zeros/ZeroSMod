package org.darkoro.zerosmod.command;

import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import kamkeel.npcdbc.scripted.ScriptDBCAddon;
import noppes.npcs.Server;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.item.ScriptItemStack;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.api.ScriptZSWeapon;
import org.darkoro.zerosmod.api.UnknownWeaponTypeException;
import org.darkoro.zerosmod.config.*;
import org.darkoro.zerosmod.event.SaiyanMasteryMergeEvent;
import org.darkoro.zerosmod.finisher.FinisherCamera;
import org.darkoro.zerosmod.finisher.FinisherCameraPreset;
import org.darkoro.zerosmod.network.BiomeVisualSyncUtil;
import org.darkoro.zerosmod.network.RaceStatEditorServer;
import org.darkoro.zerosmod.network.SyncDimensionConfigPacket;
import org.darkoro.zerosmod.scripted.ZeroSAPI;
import org.darkoro.zerosmod.zsweapons.ZSWeaponUtils;
import org.darkoro.zerosmod.zsweapons.network.packets.ReloadToClientPacket;
import org.darkoro.zerosmod.zsweapons.network.packets.WeaponTypesToClientPacket;

import java.util.*;

public class CommandZSMod extends CommandBase {

  private static final String PREFIX = EnumChatFormatting.AQUA + "" + EnumChatFormatting.BOLD + "["
      + EnumChatFormatting.DARK_PURPLE + EnumChatFormatting.BOLD + "Zero"
      + EnumChatFormatting.GREEN + EnumChatFormatting.BOLD + "S"
      + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "Mod"
      + EnumChatFormatting.AQUA + EnumChatFormatting.BOLD + "] "
      + EnumChatFormatting.RESET;

  private final Map<String, ZSSubCommand> subCommands = new LinkedHashMap<String, ZSSubCommand>();

  public CommandZSMod() {
    registerSubCommand(new HelpSubCommand());
    registerSubCommand(new ReloadSubCommand());
    registerSubCommand(new RaceStatsSubCommand());
    registerSubCommand(new SaiyanMergeSubCommand());
    registerSubCommand(new MasteryDebugSubCommand());
    registerSubCommand(new SetItemTypeCommand());
    registerSubCommand(new FinisherCamSubCommand());
  }

  @Override
  public String getCommandName() {
    return "zsmod";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/zsmod [help|reload|racestats|saiyanmerge]";
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }

  @Override
  public boolean canCommandSenderUseCommand(ICommandSender sender) {
    return true;
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) {
    if (args == null || args.length == 0) {
      sendHelp(sender);
      return;
    }

    ZSSubCommand subCommand = getSubCommand(args[0]);
    if (subCommand == null) {
      sender.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.RED + "Unknown subcommand: " + args[0]));
      sendHelp(sender);
      return;
    }

    if (!subCommand.canUse(sender)) {
      throw new CommandException("You do not have permission to use /zsmod " + subCommand.getName());
    }

    subCommand.process(sender, withoutFirstArg(args));
  }

  @Override
  public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
    if (args == null || args.length == 0) {
      return Collections.emptyList();
    }

    if (args.length == 1) {
      return getListOfStringsMatchingLastWord(args, getUsableSubCommandNames(sender));
    }

    ZSSubCommand subCommand = getSubCommand(args[0]);
    if (subCommand == null || !subCommand.canUse(sender)) {
      return Collections.emptyList();
    }

    return subCommand.addTabCompletionOptions(sender, withoutFirstArg(args));
  }

  @Override
  public boolean isUsernameIndex(String[] args, int index) {
    if (args == null || args.length == 0 || index == 0) {
      return false;
    }

    ZSSubCommand subCommand = getSubCommand(args[0]);
    return subCommand != null && subCommand.isUsernameIndex(withoutFirstArg(args), index - 1);
  }

  private void registerSubCommand(ZSSubCommand subCommand) {
    subCommands.put(subCommand.getName().toLowerCase(), subCommand);
  }

  private ZSSubCommand getSubCommand(String name) {
    if (name == null) {
      return null;
    }

    return subCommands.get(name.toLowerCase());
  }

  private String[] getUsableSubCommandNames(ICommandSender sender) {
    List<String> names = new ArrayList<String>();
    for (ZSSubCommand subCommand : subCommands.values()) {
      if (subCommand.canUse(sender)) {
        names.add(subCommand.getName());
      }
    }

    return names.toArray(new String[names.size()]);
  }

  private String[] withoutFirstArg(String[] args) {
    return Arrays.copyOfRange(args, 1, args.length);
  }

  private void sendHelp(ICommandSender sender) {
    sender.addChatMessage(createHelpHeader());

    for (ZSSubCommand subCommand : subCommands.values()) {
      sender.addChatMessage(createHelpLine(subCommand));
    }
  }

  private IChatComponent createHelpHeader() {
    IChatComponent header = chat("-----", EnumChatFormatting.AQUA, true);
    header.appendSibling(chat("Zero", EnumChatFormatting.DARK_PURPLE, true));
    header.appendSibling(chat(" ", EnumChatFormatting.GRAY, true));
    header.appendSibling(chat("S", EnumChatFormatting.GREEN, true));
    header.appendSibling(chat(" ", EnumChatFormatting.GRAY, true));
    header.appendSibling(chat("Mod", EnumChatFormatting.GOLD, true));
    header.appendSibling(chat(" Commands", EnumChatFormatting.GOLD, true));
    header.appendSibling(chat("-----", EnumChatFormatting.AQUA, true));
    return header;
  }

  private IChatComponent createHelpLine(ZSSubCommand subCommand) {
    IChatComponent line = chat("> ", EnumChatFormatting.GRAY);
    line.appendSibling(chat(getHelpUsage(subCommand), EnumChatFormatting.GREEN));

    line.appendSibling(chat(": ", EnumChatFormatting.DARK_GRAY));
    line.appendSibling(chat(subCommand.getDescription(), EnumChatFormatting.GRAY));
    return line;
  }

  private String getHelpUsage(ZSSubCommand subCommand) {
    String usage = subCommand.getUsage();
    return usage.startsWith("/zsmod ") ? usage.substring("/zsmod ".length()) : usage;
  }

  private IChatComponent chat(String text, EnumChatFormatting color) {
    return chat(text, color, false);
  }

  private IChatComponent chat(String text, EnumChatFormatting color, boolean bold) {
    ChatComponentText component = new ChatComponentText(text);
    component.setChatStyle(new ChatStyle().setColor(color).setBold(Boolean.valueOf(bold)));
    return component;
  }

  private abstract class ZSSubCommand {

    private final String name;
    private final String usage;
    private final String description;
    private final int permissionLevel;

    private ZSSubCommand(String name, String usage, String description, int permissionLevel) {
      this.name = name;
      this.usage = usage;
      this.description = description;
      this.permissionLevel = permissionLevel;
    }

    protected String getName() {
      return name;
    }

    protected String getUsage() {
      return usage;
    }

    protected String getDescription() {
      return description;
    }

    private boolean canUse(ICommandSender sender) {
      return permissionLevel <= 0 || sender.canCommandSenderUseCommand(permissionLevel, getCommandName());
    }

    private boolean requiresOp() {
      return permissionLevel > 0;
    }

    protected List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
      return Collections.emptyList();
    }

    protected boolean isUsernameIndex(String[] args, int index) {
      return false;
    }

    protected abstract void process(ICommandSender sender, String[] args);
  }

  private class HelpSubCommand extends ZSSubCommand {

    private HelpSubCommand() {
      super("help", "/zsmod help", "Shows the ZeroSMod command menu.", 0);
    }

    @Override
    protected void process(ICommandSender sender, String[] args) {
      if (args.length > 0) {
        throw new WrongUsageException(getUsage());
      }

      sendHelp(sender);
    }
  }

  private class RaceStatsSubCommand extends ZSSubCommand {
    private RaceStatsSubCommand() {
      super("racestats", "/zsmod racestats", "Edits saved DBC race stat multipliers; restart required.", 2);
    }

    @Override protected void process(ICommandSender sender, String[] args) {
      if (args.length != 0) throw new WrongUsageException(getUsage());
      RaceStatEditorServer.open(getCommandSenderAsPlayer(sender));
    }
  }

  private class ReloadSubCommand extends ZSSubCommand {

    private ReloadSubCommand() {
      super("reload", "/zsmod reload", "Reloads ZeroSMod configs and syncs clients.", 2);
    }

    @Override
    protected void process(ICommandSender sender, String[] args) {
      if (args.length > 0) {
        throw new WrongUsageException(getUsage());
      }

      PathConfig.reload();
      BiomeConfig.reload();
      DimensionConfig.reload();
      KiAttackConfig.reload();
      ServerWeaponConfig.reload();

      IMessage pkt = BiomeVisualSyncUtil.buildFullPacket();
      ZeroSMod.network.sendToAll(pkt);
      ZeroSMod.network.sendToAll(SyncDimensionConfigPacket.buildCurrent());

      WeaponTypesToClientPacket weaponTypesPacket = new WeaponTypesToClientPacket(ServerWeaponConfig.loadedWeaponStats);
      ZeroSMod.network.sendToAll(weaponTypesPacket);
      ZeroSMod.network.sendToAll(new ReloadToClientPacket());

      int players = MinecraftServer.getServer().getConfigurationManager().playerEntityList.size();
      sender.addChatMessage(new ChatComponentText(
          PREFIX + EnumChatFormatting.GRAY + "ZeroSMod configs reloaded and synced to " + players + " player(s)."));
    }
  }

  private class SetItemTypeCommand extends ZSSubCommand {

    private SetItemTypeCommand() {
      super("weapons", "/zsmod weapons [types, setitemtype] [TYPE]", "Sets item type of currently held item.", 2);
    }

    @Override
    protected List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
      if (args.length == 1) {
        List<String> options = new ArrayList<>();
        options.add("types");
        options.add("setitemtype");
        return options;
      } else if (args.length == 2) {
        return new ArrayList<>(ZSWeaponUtils.getLoadedStats().keySet());
      }

      return Collections.emptyList();
    }

    @Override
    protected void process(ICommandSender sender, String[] args) {
      if (args.length < 1) {
        throw new WrongUsageException(getUsage());
      }

      switch(args[0].toLowerCase()) {
        case "types":
          StringBuilder message = new StringBuilder(PREFIX + EnumChatFormatting.WHITE + "Loaded weapon types: ");
          Set<String> loadedStats = ZSWeaponUtils.getLoadedStats().keySet();
          for(String key : loadedStats) {
            message.append(key).append(", ");
          }
          sender.addChatMessage(new ChatComponentText(message.toString()));
          break;

        case "setitemtype":
          EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(sender.getCommandSenderName());
          if(player == null) {
            sender.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.DARK_RED + "PLAYER NOT FOUND"));
            return;
          }
          ItemStack heldItem = player.getHeldItem();
          if(heldItem == null) {
            sender.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.RED + "Please hold the item you wish to change"));
            return;
          }

          StringBuilder typeBuilder = new StringBuilder();
          for(int i = 1; i < args.length; i++ ) {
            typeBuilder.append(args[i]).append(' ');
          }
          typeBuilder.deleteCharAt(typeBuilder.length() - 1);
          String type = typeBuilder.toString();

          try {
            ScriptZSWeapon weapon = ZeroSAPI.Instance().getZSWeapon(new ScriptItemStack(heldItem));
            weapon.setType(type);
            ZeroSAPI.Instance().getPlayerCombatState((IPlayer) NpcAPI.Instance().getIEntity(player)).setCurrentZSWeapon(weapon, true);
            sender.addChatMessage(new ChatComponentText(
                    PREFIX + EnumChatFormatting.GRAY + "Item type successfully set to " + type));
          } catch (UnknownWeaponTypeException e) {
            sender.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.RED + "Unknown weapon type: " + type));
          }
          break;
      }
    }
  }

  private class FinisherCamSubCommand extends ZSSubCommand {

    private FinisherCamSubCommand() {
      super("finishercam", "/zsmod finishercam <ticks> [preset] [player] [animation] | stop [player]",
          "Plays the finisher camera, plus an optional CNPC+ animation on the player.", 2);
    }

    @Override
    protected List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
      if (args.length == 1) {
        return getListOfStringsMatchingLastWord(args, "60", "100", "stop");
      }
      if (isUsernameIndex(args, args.length - 1)) {
        return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
      }
      if (args.length == 2) {
        return getListOfStringsMatchingLastWord(args, FinisherCameraPreset.names());
      }
      if (args.length == 4 && !isStop(args)) {
        return getListOfStringsMatchingLastWord(args, FinisherCamera.animationNames());
      }

      return Collections.emptyList();
    }

    @Override
    protected boolean isUsernameIndex(String[] args, int index) {
      return index == (isStop(args) ? 1 : 2);
    }

    @Override
    protected void process(ICommandSender sender, String[] args) {
      if (isStop(args)) {
        if (args.length > 2) {
          throw new WrongUsageException(getUsage());
        }

        EntityPlayerMP player = args.length == 2 ? getPlayer(sender, args[1]) : getCommandSenderAsPlayer(sender);
        FinisherCamera.stop(player);
        ZeroSMod.LOGGER.debug("Finisher camera stopped for {}", player.getCommandSenderName());
        return;
      }
      if (args.length < 1 || args.length > 4) {
        throw new WrongUsageException(getUsage());
      }

      int ticks = parseIntBounded(sender, args[0], 1, FinisherCamera.MAX_TICKS);
      FinisherCameraPreset preset = args.length > 1 ? FinisherCameraPreset.byName(args[1]) : FinisherCameraPreset.ORBIT;
      if (preset == null) {
        throw new WrongUsageException(getUsage());
      }
      String animation = args.length == 4 ? args[3] : null;
      if (animation != null && !FinisherCamera.hasAnimation(animation)) {
        throw new CommandException("Unknown CNPC+ animation: " + animation);
      }

      EntityPlayerMP player = args.length >= 3 ? getPlayer(sender, args[2]) : getCommandSenderAsPlayer(sender);
      EntityLivingBase target = FinisherCamera.findLookTarget(player, 16.0D);
      FinisherCamera.play(player, target, ticks, preset, animation);
      ZeroSMod.LOGGER.debug("Finisher camera {} for {} ticks on {}{}{}", preset.name().toLowerCase(), ticks,
          player.getCommandSenderName(), target == null ? "" : " and " + target.getCommandSenderName(),
          animation == null ? "" : " with animation " + animation);
    }

    private boolean isStop(String[] args) {
      return args.length > 0 && "stop".equalsIgnoreCase(args[0]);
    }
  }

  private class SaiyanMergeSubCommand extends ZSSubCommand {

    private SaiyanMergeSubCommand() {
      super("saiyanmerge", "/zsmod saiyanmerge [player]", "Manually forces the Saiyan merge for a player if it does not work automatically.", 2);
    }

    @Override
    protected List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
      if (args.length == 1) {
        return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
      }

      return Collections.emptyList();
    }

    @Override
    protected boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
    }

    @Override
    protected void process(ICommandSender sender, String[] args) {
      if (args.length > 1) {
        throw new WrongUsageException(getUsage());
      }

      EntityPlayerMP player = args.length == 1
          ? getPlayer(sender, args[0])
          : getCommandSenderAsPlayer(sender);
      String result = SaiyanMasteryMergeEvent.forceMerge(player);
      sender.addChatMessage(new ChatComponentText(
          PREFIX + EnumChatFormatting.GRAY + "Saiyan merge for " + player.getCommandSenderName() + ": " + result));
    }
  }

  private class MasteryDebugSubCommand extends ZSSubCommand {

    private MasteryDebugSubCommand() {
      super("masterydebug", "/zsmod masterydebug [player]", "Reports racial mastery values read by CNPC+ and DBCAddon.", 2);
    }

    @Override
    protected List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
      if (args.length == 1) {
        return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
      }

      return Collections.emptyList();
    }

    @Override
    protected boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
    }

    @Override
    protected void process(ICommandSender sender, String[] args) {
      if (args.length > 1) {
        throw new WrongUsageException(getUsage());
      }

      EntityPlayerMP player = args.length == 1
          ? getPlayer(sender, args[0])
          : getCommandSenderAsPlayer(sender);
      NBTTagCompound nbt = JRMCoreH.nbt(player);
      byte race = nbt.getByte(JRMCoreH.race);
      String currentKey = JRMCoreH.getNBTFormMasteryRacialKey(race);
      String currentData = nbt.getString(currentKey);
      String firstEntry = currentData.length() == 0 ? "<empty>" : currentData.split(";", 2)[0];
      String firstFormName = race >= 0 && race < JRMCoreH.trans.length && JRMCoreH.trans[race].length > 0
          ? JRMCoreH.trans[race][0]
          : "<unknown>";
      ScriptDBCAddon addon = new ScriptDBCAddon(player);

      sender.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.GRAY
          + "Race NBT=" + race + ", current key=" + currentKey));
      sender.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.GRAY
          + "CNPC getRacialFormMastery(0)=" + addon.getRacialFormMastery((byte) 0)
          + ", DBCAddon getDBCMasteryValue(Base)=" + addon.getDBCMasteryValue("Base")));
      sender.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.GRAY
          + "Race form index 0=" + firstFormName + ", raw first entry=" + firstEntry
          + ", raw Base entry=" + findMasteryValue(currentData, "Base")));

      String saiyanKey = JRMCoreH.getNBTFormMasteryRacialKey(JRMCoreH.RACE_SAIYAN);
      String halfSaiyanKey = JRMCoreH.getNBTFormMasteryRacialKey(JRMCoreH.RACE_HALF_SAIYAN);
      sender.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.GRAY
          + "Saiyan Base=" + findMasteryValue(nbt.getString(saiyanKey), "Base")
          + ", Half-Saiyan Base=" + findMasteryValue(nbt.getString(halfSaiyanKey), "Base")
          + ", legacy racial key=" + findMasteryValue(nbt.getString("jrmcFormMasteryRacial"), "Base")));
    }
  }

  private static String findMasteryValue(String data, String name) {
    String[] entries = data == null ? new String[0] : data.split(";");
    for (int i = 0; i < entries.length; i++) {
      String[] fields = entries[i].split(",", 2);
      if (fields.length == 2 && name.equalsIgnoreCase(fields[0])) {
        return fields[1];
      }
    }

    return "<missing>";
  }
}
