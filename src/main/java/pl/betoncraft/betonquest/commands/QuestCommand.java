/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016 Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pl.betoncraft.betonquest.*;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigAccessor;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.database.Connector.UpdateType;
import pl.betoncraft.betonquest.database.GlobalData;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.database.Saver.Record;
import pl.betoncraft.betonquest.events.GiveEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.id.EventID;
import pl.betoncraft.betonquest.id.ObjectiveID;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * Main admin command for quest editing.
 *
 * @author Jakub Sapalski
 */
public class QuestCommand implements CommandExecutor, SimpleTabCompleter {

    private BetonQuest instance = BetonQuest.getInstance();
    private String defaultPack = Config.getString("config.default_package");

    /**
     * Registers a new executor and a new tab completer of the /betonquest
     * command
     */
    public QuestCommand() {
        BetonQuest.getInstance().getCommand("betonquest").setExecutor(this);
        BetonQuest.getInstance().getCommand("betonquest").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String alias, final String[] args) {

        if (cmd.getName().equalsIgnoreCase("betonquest")) {
            LogUtils.getLogger().log(Level.FINE, "Executing /betonquest command for user " + sender.getName()
                    + " with arguments: " + Arrays.toString(args));
            // if the command is empty, display help message
            if (args.length < 1) {
                displayHelp(sender, alias);
                return true;
            }
            // if there are arguments handle them
            // toLowerCase makes switch case-insensitive
            switch (args[0].toLowerCase()) {
                case "conditions":
                case "condition":
                case "c":
                    // conditions are only possible for online players, so no
                    // MySQL async
                    // access is required
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleConditions(sender, args);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "events":
                case "event":
                case "e":
                    // the same goes for events
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleEvents(sender, args);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "items":
                case "item":
                case "i":
                    // and items, which only use configuration files (they
                    // should be sync)
                    handleItems(sender, args);
                    break;
                case "give":
                case "g":
                    giveItem(sender, args);
                    break;
                case "config":
                    // config is also only synchronous
                    handleConfig(sender, args);
                    break;
                case "objectives":
                case "objective":
                case "o":
                    LogUtils.getLogger().log(Level.FINE, "Loading data asynchronously");
                    final CommandSender finalSender1 = sender;
                    final String[] finalArgs1 = args;
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleObjectives(finalSender1, finalArgs1);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "globaltags":
                case "globaltag":
                case "gtag":
                case "gtags":
                case "gt":
                    LogUtils.getLogger().log(Level.FINE, "Loading data asynchronously");
                    final CommandSender finalSender8 = sender;
                    final String[] finalArgs8 = args;
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleGlobalTags(finalSender8, finalArgs8);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "globalpoints":
                case "globalpoint":
                case "gpoints":
                case "gpoint":
                case "gp":
                    LogUtils.getLogger().log(Level.FINE, "Loading data asynchronously");
                    final CommandSender finalSender9 = sender;
                    final String[] finalArgs9 = args;
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleGlobalPoints(finalSender9, finalArgs9);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "tags":
                case "tag":
                case "t":
                    LogUtils.getLogger().log(Level.FINE, "Loading data asynchronously");
                    final CommandSender finalSender2 = sender;
                    final String[] finalArgs2 = args;
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleTags(finalSender2, finalArgs2);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "points":
                case "point":
                case "p":
                    LogUtils.getLogger().log(Level.FINE, "Loading data asynchronously");
                    final CommandSender finalSender3 = sender;
                    final String[] finalArgs3 = args;
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handlePoints(finalSender3, finalArgs3);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "journals":
                case "journal":
                case "j":
                    LogUtils.getLogger().log(Level.FINE, "Loading data asynchronously");
                    final CommandSender finalSender4 = sender;
                    final String[] finalArgs4 = args;
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleJournals(finalSender4, finalArgs4);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "delete":
                case "del":
                case "d":
                    LogUtils.getLogger().log(Level.FINE, "Loading data asynchronously");
                    final CommandSender finalSender5 = sender;
                    final String[] finalArgs5 = args;
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleDeleting(finalSender5, finalArgs5);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "rename":
                case "r":
                    LogUtils.getLogger().log(Level.FINE, "Loading data asynchronously");
                    final CommandSender finalSender6 = sender;
                    final String[] finalArgs6 = args;
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleRenaming(finalSender6, finalArgs6);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "vector":
                case "vec":
                    handleVector(sender, args);
                    break;
                case "version":
                case "ver":
                case "v":
                    displayVersionInfo(sender);
                    break;
                case "purge":
                    LogUtils.getLogger().log(Level.FINE, "Loading data asynchronously");
                    final CommandSender finalSender7 = sender;
                    final String[] finalArgs7 = args;
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            purgePlayer(finalSender7, finalArgs7);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "update":
                    BetonQuest.getInstance().getUpdater().update(sender);
                    break;
                case "reload":
                    // just reloading
                    defaultPack = Config.getString("config.default_package");
                    instance.reload();
                    sendMessage(sender, "reloaded");
                    break;
                case "backup":
                    // do a full plugin backup
                    if (sender instanceof Player || Bukkit.getOnlinePlayers().size() > 0) {
                        sendMessage(sender, "offline");
                        break;
                    }
                    Utils.backup();
                    break;
                case "create":
                case "package":
                    createNewPackage(sender, args);
                    break;
                case "debug":
                    handleDebug(sender, args);
                    break;
                default:
                    // there was an unknown argument, so handle this
                    sendMessage(sender, "unknown_argument");
                    break;
            }
            LogUtils.getLogger().log(Level.FINE, "Command executing done");
            return true;
        }
        return false;
    }

    @Override
    public List<String> simpleTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 1) {
            return Arrays.asList("condition", "event", "item", "give", "config", "objective", "globaltag",
                    "globalpoint", "tag", "point", "journal", "delete", "rename", "vector", "version", "purge",
                    "update", "reload", "backup", "create", "debug");
        }
        switch (args[0].toLowerCase()) {
            case "conditions":
            case "condition":
            case "c":
                return completeConditions(sender, args);
            case "events":
            case "event":
            case "e":
                return completeEvents(sender, args);
            case "items":
            case "item":
            case "i":
            case "give":
            case "g":
                return completeItems(sender, args);
            case "config":
                return completeConfig(sender, args);
            case "objectives":
            case "objective":
            case "o":
                return completeObjectives(sender, args);
            case "globaltags":
            case "globaltag":
            case "gtag":
            case "gtags":
            case "gt":
                return completeGlobalTags(sender, args);
            case "globalpoints":
            case "globalpoint":
            case "gpoints":
            case "gpoint":
            case "gp":
                return completeGlobalPoints(sender, args);
            case "tags":
            case "tag":
            case "t":
                return completeTags(sender, args);
            case "points":
            case "point":
            case "p":
                return completePoints(sender, args);
            case "journals":
            case "journal":
            case "j":
                return completeJournals(sender, args);
            case "delete":
            case "del":
            case "d":
                return completeDeleting(sender, args);
            case "rename":
            case "r":
                return completeRenaming(sender, args);
            case "vector":
            case "vec":
                return completeVector(sender, args);
            case "purge":
                if (args.length == 2) {
                    return null;
                } else {
                    return new ArrayList<>();
                }
            case "debug":
                return completeDebug(sender, args);
            case "version":
            case "ver":
            case "v":
            case "update":
            case "reload":
            case "backup":
            case "create":
            case "package":
            default:
                return new ArrayList<>();
        }
    }

    /**
     * Returns a list of all packages for the tab completer
     *
     * @param sender
     * @param args
     * @return
     */
    private List<String> completePackage(final CommandSender sender, final String[] args) {
        return new ArrayList<>(Config.getPackages().keySet());
    }

    /**
     * Returns a list including all possible tab complete options for ids
     *
     * @param sender
     * @param args
     * @param type   - the type of the Id (item/event/journal/condition/objective),
     *               null for unspecific
     * @return
     */
    private List<String> completeId(final CommandSender sender, final String[] args, final ConfigAccessor.AccessorType type) {
        final String last = args[args.length - 1];
        if (last == null || !last.contains(".")) {
            return completePackage(sender, args);
        } else {
            final String pack = last.substring(0, last.indexOf("."));
            final ConfigPackage configPack = Config.getPackages().get(pack);
            if (configPack == null) {
                return new ArrayList<>();
            }
            if (type == null) {
                final List<String> completations = new ArrayList<>();
                completations.add(pack + ".");
                return completations;
            }
            final ConfigAccessor accessor;
            switch (type) {
                case ITEMS:
                    accessor = configPack.getItems();
                    break;
                case EVENTS:
                    accessor = configPack.getEvents();
                    break;
                case JOURNAL:
                    accessor = configPack.getJournal();
                    break;
                case CONDITIONS:
                    accessor = configPack.getConditions();
                    break;
                case OBJECTIVES:
                    accessor = configPack.getObjectives();
                    break;
                default:
                    return new ArrayList<>();
            }
            final FileConfiguration configuration = accessor.getConfig();
            final List<String> completations = new ArrayList<>();
            for (final String key : configuration.getKeys(false)) {
                completations.add(pack + "." + key);
            }
            return completations;
        }
    }

    /**
     * Gives an item to the player
     */
    private void giveItem(final CommandSender sender, final String[] args) {
        // sender must be a player
        if (!(sender instanceof Player)) {
            LogUtils.getLogger().log(Level.FINE, "Cannot continue, sender must be player");
            return;
        }
        // and the item name must be specified
        if (args.length < 2) {
            LogUtils.getLogger().log(Level.FINE, "Cannot continue, item's name must be supplied");
            sendMessage(sender, "specify_item");
            return;
        }
        try {
            final int splitIndex = args[1].indexOf(".");
            final String pack = args[1].substring(0, splitIndex);
            final String item = args[1].substring(splitIndex + 1);
            final ConfigPackage configPack = Config.getPackages().get(pack);
            final GiveEvent give = new GiveEvent(new Instruction(configPack, null, "give " + item));
            give.fire(PlayerConverter.getID((Player) sender));
        } catch (InstructionParseException | QuestRuntimeException e) {
            sendMessage(sender, "error", new String[]{
                    e.getMessage()
            });
            LogUtils.getLogger().log(Level.WARNING, "Error while creating an item: " + e.getMessage());
            LogUtils.logThrowable(e);
        }
    }

    /**
     * Creates new package
     */
    private void createNewPackage(final CommandSender sender, final String[] args) {
        if (args.length < 2) {
            LogUtils.getLogger().log(Level.FINE, "Package name is missing");
            sendMessage(sender, "specify_package");
            return;
        }
        if (Config.createDefaultPackage(args[1])) {
            sendMessage(sender, "package_created");
        } else {
            sendMessage(sender, "package_exists");
        }
    }

    /**
     * Purges player's data
     */
    private void purgePlayer(final CommandSender sender, final String[] args) {
        // playerID is required
        if (args.length < 2) {
            LogUtils.getLogger().log(Level.FINE, "Player's name is missing");
            sendMessage(sender, "specify_player");
            return;
        }
        final String playerID = PlayerConverter.getID(args[1]);
        PlayerData playerData = instance.getPlayerData(playerID);
        // if the player is offline then get his PlayerData outside of the
        // list
        if (playerData == null) {
            LogUtils.getLogger().log(Level.FINE, "Player is offline, loading his data");
            playerData = new PlayerData(playerID);
        }
        // purge the player
        LogUtils.getLogger().log(Level.FINE, "Purging player " + args[1]);
        playerData.purgePlayer();
        // done
        sendMessage(sender, "purged", new String[]{
                args[1]
        });
    }

    /**
     * Reads, sets or appends strings from/to config files
     */
    private void handleConfig(final CommandSender sender, final String[] args) {
        if (args.length < 3) {
            LogUtils.getLogger().log(Level.FINE, "No action specified!");
            sendMessage(sender, "specify_action");
            return;
        }
        final String action = args[1];
        final String path = args[2];
        switch (action) {
            case "read":
            case "r":
                LogUtils.getLogger().log(Level.FINE, "Displaying variable at path " + path);
                final String message = Config.getString(path);
                sender.sendMessage(message == null ? "null" : message);
                break;
            case "set":
            case "s":
                if (args.length < 4) {
                    sendMessage(sender, "config_set_error");
                    return;
                }
                final StringBuilder strBldr = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    strBldr.append(args[i] + " ");
                }
                if (strBldr.length() < 2) {
                    LogUtils.getLogger().log(Level.FINE, "Wrong path!");
                    sendMessage(sender, "specify_path");
                    return;
                }
                final boolean set = Config.setString(path, args[3].equalsIgnoreCase("null") ? null : strBldr.toString().trim());
                if (set) {
                    LogUtils.getLogger().log(Level.FINE, "Displaying variable at path " + path);
                    final String message1 = Config.getString(path);
                    sender.sendMessage(message1 == null ? "null" : message1);
                } else {
                    sendMessage(sender, "config_set_error");
                }
                break;
            case "add":
            case "a":
                if (args.length < 4) {
                    sendMessage(sender, "config_set_error");
                    return;
                }
                final StringBuilder strBldr2 = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    strBldr2.append(args[i] + " ");
                }
                if (strBldr2.length() < 2) {
                    LogUtils.getLogger().log(Level.FINE, "Wrong path!");
                    sendMessage(sender, "specify_path");
                    return;
                }
                String finalString = strBldr2.toString().trim();
                boolean space = false;
                if (finalString.startsWith("_")) {
                    finalString = finalString.substring(1);
                    space = true;
                }
                String oldString = Config.getString(path);
                if (oldString == null) {
                    oldString = "";
                }
                final boolean set2 = Config.setString(path, oldString + (space ? " " : "") + finalString);
                if (set2) {
                    LogUtils.getLogger().log(Level.FINE, "Displaying variable at path " + path);
                    final String message2 = Config.getString(path);
                    sender.sendMessage(message2 == null ? "null" : message2);
                } else {
                    sendMessage(sender, "config_set_error");
                }
                break;
            default:
                // if there was something else, display error message
                LogUtils.getLogger().log(Level.FINE, "The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest config command
     *
     * @param sender
     * @param args
     * @return
     */
    private List<String> completeConfig(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return Arrays.asList("set", "add", "read");
        }
        return new ArrayList<>();
    }

    /**
     * Lists, adds or removes journal entries of certain players
     */
    private void handleJournals(final CommandSender sender, final String[] args) {
        // playerID is required
        if (args.length < 2) {
            LogUtils.getLogger().log(Level.FINE, "Player's name is missing");
            sendMessage(sender, "specify_player");
            return;
        }
        final String playerID = PlayerConverter.getID(args[1]);
        PlayerData playerData = instance.getPlayerData(playerID);
        // if the player is offline then get his PlayerData outside of the
        // list
        if (playerData == null) {
            LogUtils.getLogger().log(Level.FINE, "Player is offline, loading his data");
            playerData = new PlayerData(playerID);
        }
        final Journal journal = playerData.getJournal();
        // if there are no arguments then list player's pointers
        if (args.length < 3 || args[2].equalsIgnoreCase("list") || args[2].equalsIgnoreCase("l")) {
            LogUtils.getLogger().log(Level.FINE, "Listing journal pointers");
            sendMessage(sender, "player_journal");
            for (final Pointer pointer : journal.getPointers()) {
                final String date = new SimpleDateFormat(Config.getString("config.date_format"))
                        .format(new Date(pointer.getTimestamp()));
                sender.sendMessage("§b- " + pointer.getPointer() + " §c(§2" + date + "§c)");
            }
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            LogUtils.getLogger().log(Level.FINE, "Missing pointer");
            sendMessage(sender, "specify_pointer");
            return;
        }
        final String pointerName = args[3].contains(".") ? args[3] : defaultPack + "." + args[3];
        // if there are arguments, handle them
        switch (args[2].toLowerCase()) {
            case "add":
            case "a":
                final Pointer pointer;
                if (args.length < 5) {
                    final long timestamp = new Date().getTime();
                    LogUtils.getLogger().log(Level.FINE, "Adding pointer with current date: " + timestamp);
                    pointer = new Pointer(pointerName, timestamp);
                } else {
                    LogUtils.getLogger().log(Level.FINE, "Adding pointer with date " + args[4].replaceAll("_", " "));
                    try {
                        pointer = new Pointer(pointerName, new SimpleDateFormat(Config.getString("config.date_format"))
                                .parse(args[4].replaceAll("_", " ")).getTime());
                    } catch (ParseException e) {
                        sendMessage(sender, "specify_date");
                        LogUtils.getLogger().log(Level.WARNING, "Could not parse date: " + e.getMessage());
                        LogUtils.logThrowable(e);
                        return;
                    }
                }
                // add the pointer
                journal.addPointer(pointer);
                journal.update();
                sendMessage(sender, "pointer_added");
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                // remove the pointer
                LogUtils.getLogger().log(Level.FINE, "Removing pointer");
                journal.removePointer(pointerName);
                journal.update();
                sendMessage(sender, "pointer_removed");
                break;
            default:
                // if there was something else, display error message
                LogUtils.getLogger().log(Level.FINE, "The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest journal command
     *
     * @param sender
     * @param args
     * @return
     */
    private List<String> completeJournals(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return null;
        }
        if (args.length == 3) {
            return Arrays.asList("add", "list", "del");
        }
        if (args.length == 4) {
            return completeId(sender, args, ConfigAccessor.AccessorType.JOURNAL);
        }
        return new ArrayList<>();
    }

    /**
     * Lists, adds or removes points of certain player
     */
    private void handlePoints(final CommandSender sender, final String[] args) {
        // playerID is required
        if (args.length < 2) {
            LogUtils.getLogger().log(Level.FINE, "Player's name is missing");
            sendMessage(sender, "specify_player");
            return;
        }
        final String playerID = PlayerConverter.getID(args[1]);
        PlayerData playerData = instance.getPlayerData(playerID);
        // if the player is offline then get his PlayerData outside of the
        // list
        if (playerData == null) {
            LogUtils.getLogger().log(Level.FINE, "Player is offline, loading his data");
            playerData = new PlayerData(playerID);
        }
        // if there are no arguments then list player's points
        if (args.length < 3 || args[2].equalsIgnoreCase("list") || args[2].equalsIgnoreCase("l")) {
            final List<Point> points = playerData.getPoints();
            LogUtils.getLogger().log(Level.FINE, "Listing points");
            sendMessage(sender, "player_points");
            for (final Point point : points) {
                sender.sendMessage("§b- " + point.getCategory() + "§e: §a" + point.getCount());
            }
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            LogUtils.getLogger().log(Level.FINE, "Missing category");
            sendMessage(sender, "specify_category");
            return;
        }
        final String category = args[3].contains(".") ? args[3] : defaultPack + "." + args[3];
        // if there are arguments, handle them
        switch (args[2].toLowerCase()) {
            case "add":
            case "a":
                if (args.length < 5 || !args[4].matches("-?\\d+")) {
                    LogUtils.getLogger().log(Level.FINE, "Missing amount");
                    sendMessage(sender, "specify_amount");
                    return;
                }
                // add the point
                LogUtils.getLogger().log(Level.FINE, "Adding points");
                playerData.modifyPoints(category, Integer.parseInt(args[4]));
                sendMessage(sender, "points_added");
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                // remove the point (this is unnecessary as adding negative
                // amounts
                // subtracts points, but for the sake of users let's leave it
                // here)
                LogUtils.getLogger().log(Level.FINE, "Removing points");
                playerData.removePointsCategory(category);
                sendMessage(sender, "points_removed");
                break;
            default:
                // if there was something else, display error message
                LogUtils.getLogger().log(Level.FINE, "The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Lists, adds, removes or purges all global points
     *
     * @param sender
     * @param args
     */
    private void handleGlobalPoints(final CommandSender sender, final String[] args) {
        final GlobalData data = instance.getGlobalData();
        // if there are no arguments then list all global points
        if (args.length < 2 || args[1].equalsIgnoreCase("list") || args[1].equalsIgnoreCase("l")) {
            final List<Point> points = data.getPoints();
            LogUtils.getLogger().log(Level.FINE, "Listing global points");
            sendMessage(sender, "global_points");
            for (final Point point : points) {
                sender.sendMessage("§b- " + point.getCategory() + "§e: §a" + point.getCount());
            }
            return;
        }
        // handle purge
        if (args[1].equalsIgnoreCase("purge")) {
            LogUtils.getLogger().log(Level.FINE, "Purging all global points");
            data.purgePoints();
            sendMessage(sender, "global_points_purged");
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 3) {
            LogUtils.getLogger().log(Level.FINE, "Missing category");
            sendMessage(sender, "specify_category");
            return;
        }
        final String category = args[2].contains(".") ? args[2] : defaultPack + "." + args[2];
        // if there are arguments, handle them
        switch (args[1].toLowerCase()) {
            case "add":
            case "a":
                if (args.length < 4 || !args[3].matches("-?\\d+")) {
                    LogUtils.getLogger().log(Level.FINE, "Missing amount");
                    sendMessage(sender, "specify_amount");
                    return;
                }
                // add the point
                LogUtils.getLogger().log(Level.FINE, "Adding global points");
                data.modifyPoints(category, Integer.parseInt(args[3]));
                sendMessage(sender, "points_added");
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                LogUtils.getLogger().log(Level.FINE, "Removing global points");
                data.removePointsCategory(category);
                sendMessage(sender, "points_removed");
                break;
            default:
                // if there was something else, display error message
                LogUtils.getLogger().log(Level.FINE, "The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest points command
     *
     * @param sender
     * @param args
     * @return
     */
    private List<String> completePoints(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return null;
        }
        if (args.length == 3) {
            return Arrays.asList("add", "list", "del");
        }
        if (args.length == 4) {
            return completeId(sender, args, null);
        }
        return new ArrayList<>();
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest globalpoints command
     *
     * @param sender
     * @param args
     * @return
     */
    private List<String> completeGlobalPoints(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return Arrays.asList("add", "list", "del");
        }
        if (args.length == 3) {
            return completeId(sender, args, null);
        }
        return new ArrayList<>();
    }

    /**
     * Adds item held in hand to items.yml file
     */

    private void handleItems(final CommandSender sender, final String[] args) {
        // sender must be a player
        if (!(sender instanceof Player)) {
            LogUtils.getLogger().log(Level.FINE, "Cannot continue, sender must be player");
            return;
        }
        // and the item name must be specified
        if (args.length < 2) {
            LogUtils.getLogger().log(Level.FINE, "Cannot continue, item's name must be supplied");
            sendMessage(sender, "specify_item");
            return;
        }
        final String itemID = args[1];
        final String pack;
        final String name;
        if (itemID.contains(".")) {
            final String[] parts = itemID.split("\\.");
            pack = parts[0];
            name = parts[1];
        } else {
            pack = defaultPack;
            name = itemID;
        }
        final Player player = (Player) sender;
        ItemStack item = null;
        item = player.getInventory().getItemInMainHand();

        // if item is air then there is nothing to add to items.yml
        if (item == null || item.getType() == Material.AIR) {
            LogUtils.getLogger().log(Level.FINE, "Cannot continue, item must not be air");
            sendMessage(sender, "no_item");
            return;
        }
        // define parts of the final string
        final ConfigPackage configPack = Config.getPackages().get(pack);
        if (configPack == null) {
            LogUtils.getLogger().log(Level.FINE, "Cannot continue, package does not exist");
            sendMessage(sender, "specify_package");
            return;
        }
        final ConfigAccessor config = configPack.getItems();
        final String instructions = QuestItem.itemToString(item);
        // save it in items.yml
        LogUtils.getLogger().log(Level.FINE, "Saving item to configuration as " + args[1]);
        config.getConfig().set(name, instructions.trim());
        config.saveConfig();
        // done
        sendMessage(sender, "item_created", new String[]{
                args[1]
        });

    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest item command
     *
     * @param sender
     * @param args
     * @return
     */
    private List<String> completeItems(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return completeId(sender, args, ConfigAccessor.AccessorType.ITEMS);
        }
        return new ArrayList<>();
    }

    /**
     * Fires an event for an online player. It cannot work for offline players!
     */
    private void handleEvents(final CommandSender sender, final String[] args) {
        // the player has to be specified every time
        if (args.length < 2 || Bukkit.getPlayer(args[1]) == null && !args[1].equals("-")) {
            LogUtils.getLogger().log(Level.FINE, "Player's name is missing or he's offline");
            sendMessage(sender, "specify_player");
            return;
        }
        if (args.length < 3) {
            LogUtils.getLogger().log(Level.FINE, "Event's ID is missing");
            sendMessage(sender, "specify_event");
            return;
        }
        final EventID eventID;
        try {
            eventID = new EventID(null, args[2]);
        } catch (ObjectNotFoundException e) {
            sendMessage(sender, "error", new String[]{
                    e.getMessage()
            });
            LogUtils.getLogger().log(Level.WARNING, "Could not find event: " + e.getMessage());
            LogUtils.logThrowable(e);
            return;
        }
        // fire the event
        final String playerID = args[1].equals("-") ? null : PlayerConverter.getID(args[1]);
        BetonQuest.event(playerID, eventID);
        sendMessage(sender, "player_event", new String[]{
                eventID.generateInstruction().getInstruction()
        });
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest event command
     *
     * @param sender
     * @param args
     * @return
     */
    private List<String> completeEvents(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return null;
        }
        if (args.length == 3) {
            return completeId(sender, args, ConfigAccessor.AccessorType.EVENTS);
        }
        return new ArrayList<>();
    }

    /**
     * Checks if specified player meets condition described by ID
     */
    private void handleConditions(final CommandSender sender, final String[] args) {
        // the player has to be specified every time
        if (args.length < 2 || Bukkit.getPlayer(args[1]) == null && !args[1].equals("-")) {
            LogUtils.getLogger().log(Level.FINE, "Player's name is missing or he's offline");
            sendMessage(sender, "specify_player");
            return;
        }
        // the condition ID
        if (args.length < 3) {
            LogUtils.getLogger().log(Level.FINE, "Condition's ID is missing");
            sendMessage(sender, "specify_condition");
            return;
        }
        final ConditionID conditionID;
        try {
            conditionID = new ConditionID(null, args[2]);
        } catch (ObjectNotFoundException e) {
            sendMessage(sender, "error", new String[]{
                    e.getMessage()
            });
            LogUtils.getLogger().log(Level.WARNING, "Could not find condition: " + e.getMessage());
            LogUtils.logThrowable(e);
            return;
        }
        // display message about condition
        final String playerID = args[1].equals("-") ? null : PlayerConverter.getID(args[1]);
        sendMessage(sender, "player_condition", new String[]{
                (conditionID.inverted() ? "! " : "") + conditionID.generateInstruction().getInstruction(),
                Boolean.toString(BetonQuest.condition(playerID, conditionID))
        });
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest condition command
     *
     * @param sender
     * @param args
     * @return
     */
    private List<String> completeConditions(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return null;
        }
        if (args.length == 3) {
            return completeId(sender, args, ConfigAccessor.AccessorType.CONDITIONS);
        }
        return new ArrayList<>();
    }

    /**
     * Lists, adds or removes tags
     */
    private void handleTags(final CommandSender sender, final String[] args) {
        // playerID is required
        if (args.length < 2) {
            LogUtils.getLogger().log(Level.FINE, "Player's name is missing");
            sendMessage(sender, "specify_player");
            return;
        }
        final String playerID = PlayerConverter.getID(args[1]);
        PlayerData playerData = instance.getPlayerData(playerID);
        // if the player is offline then get his PlayerData outside of the
        // list
        if (playerData == null) {
            LogUtils.getLogger().log(Level.FINE, "Player is offline, loading his data");
            playerData = new PlayerData(playerID);
        }
        // if there are no arguments then list player's tags
        if (args.length < 3 || args[2].equalsIgnoreCase("list") || args[2].equalsIgnoreCase("l")) {
            final List<String> tags = playerData.getTags();
            LogUtils.getLogger().log(Level.FINE, "Listing tags");
            sendMessage(sender, "player_tags");
            for (final String tag : tags) {
                sender.sendMessage("§b- " + tag);
            }
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            LogUtils.getLogger().log(Level.FINE, "Missing tag name");
            sendMessage(sender, "specify_tag");
            return;
        }
        final String tag = args[3].contains(".") ? args[3] : defaultPack + "." + args[3];
        // if there are arguments, handle them
        switch (args[2].toLowerCase()) {
            case "add":
            case "a":
                // add the tag
                LogUtils.getLogger().log(Level.FINE,
                        "Adding tag " + tag + " for player " + PlayerConverter.getName(playerID));
                playerData.addTag(tag);
                sendMessage(sender, "tag_added");
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                // remove the tag
                LogUtils.getLogger().log(Level.FINE,
                        "Removing tag " + tag + " for player " + PlayerConverter.getName(playerID));
                playerData.removeTag(tag);
                sendMessage(sender, "tag_removed");
                break;
            default:
                // if there was something else, display error message
                LogUtils.getLogger().log(Level.FINE, "The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Lists, adds or removes global tags
     */
    private void handleGlobalTags(final CommandSender sender, final String[] args) {
        final GlobalData data = instance.getGlobalData();
        // if there are no arguments then list all global tags
        if (args.length < 2 || args[1].equalsIgnoreCase("list") || args[1].equalsIgnoreCase("l")) {
            final List<String> tags = data.getTags();
            LogUtils.getLogger().log(Level.FINE, "Listing global tags");
            sendMessage(sender, "global_tags");
            for (final String tag : tags) {
                sender.sendMessage("§b- " + tag);
            }
            return;
        }
        // handle purge
        if (args[1].equalsIgnoreCase("purge")) {
            LogUtils.getLogger().log(Level.FINE, "Purging all global tags");
            data.purgeTags();
            sendMessage(sender, "global_tags_purged");
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 3) {
            LogUtils.getLogger().log(Level.FINE, "Missing tag name");
            sendMessage(sender, "specify_tag");
            return;
        }
        final String tag = args[2].contains(".") ? args[2] : defaultPack + "." + args[2];
        // if there are arguments, handle them
        switch (args[1].toLowerCase()) {
            case "add":
            case "a":
                // add the tag
                LogUtils.getLogger().log(Level.FINE, "Adding global tag " + tag);
                data.addTag(tag);
                sendMessage(sender, "tag_added");
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                // remove the tag
                LogUtils.getLogger().log(Level.FINE, "Removing global tag " + tag);
                data.removeTag(tag);
                sendMessage(sender, "tag_removed");
                break;
            default:
                // if there was something else, display error message
                LogUtils.getLogger().log(Level.FINE, "The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest tags command
     *
     * @param sender
     * @param args
     * @return
     */
    private List<String> completeTags(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return null;
        }
        if (args.length == 3) {
            return Arrays.asList("list", "add", "del");
        }
        if (args.length == 4) {
            return completeId(sender, args, null);
        }
        return new ArrayList<>();
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest globaltags command
     *
     * @param sender
     * @param args
     * @return
     */
    private List<String> completeGlobalTags(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return Arrays.asList("list", "add", "del");
        }
        if (args.length == 3) {
            return completeId(sender, args, null);
        }
        return new ArrayList<>();
    }

    /**
     * Lists, adds or removes objectives.
     */
    private void handleObjectives(final CommandSender sender, final String[] args) {
        // playerID is required
        if (args.length < 2) {
            LogUtils.getLogger().log(Level.FINE, "Player's name is missing");
            sendMessage(sender, "specify_player");
            return;
        }
        final String playerID = PlayerConverter.getID(args[1]);
        final boolean isOnline = !(PlayerConverter.getPlayer(playerID) == null);
        PlayerData playerData = instance.getPlayerData(playerID);
        // if the player is offline then get his PlayerData outside of the
        // list
        if (playerData == null) {
            LogUtils.getLogger().log(Level.FINE, "Player is offline, loading his data");
            playerData = new PlayerData(playerID);
        }
        // if there are no arguments then list player's objectives
        if (args.length < 3 || args[2].equalsIgnoreCase("list") || args[2].equalsIgnoreCase("l")) {
            final List<String> tags;
            if (isOnline) {
                // if the player is online then just retrieve tags from his
                // active
                // objectives
                tags = new ArrayList<>();
                for (final Objective objective : BetonQuest.getInstance().getPlayerObjectives(playerID)) {
                    tags.add(objective.getLabel());
                }
            } else {
                // if player is offline then convert his raw objective strings
                // to tags
                tags = new ArrayList<>();
                for (final String string : playerData.getRawObjectives().keySet()) {
                    tags.add(string);
                }
            }
            // display objectives
            LogUtils.getLogger().log(Level.FINE, "Listing objectives");
            sendMessage(sender, "player_objectives");
            for (final String tag : tags) {
                sender.sendMessage("§b- " + tag);
            }
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            LogUtils.getLogger().log(Level.FINE, "Missing objective instruction string");
            sendMessage(sender, "specify_objective");
            return;
        }
        // get the objective
        final ObjectiveID objectiveID;
        try {
            objectiveID = new ObjectiveID(null, args[3]);
        } catch (ObjectNotFoundException e) {
            sendMessage(sender, "error", new String[]{
                    e.getMessage()
            });
            LogUtils.getLogger().log(Level.WARNING, "Could not find objective: " + e.getMessage());
            LogUtils.logThrowable(e);
            return;
        }
        final Objective objective = BetonQuest.getInstance().getObjective(objectiveID);
        if (objective == null) {
            sendMessage(sender, "specify_objective");
            return;
        }
        switch (args[2].toLowerCase()) {
            case "start":
            case "s":
            case "add":
            case "a":
                LogUtils.getLogger().log(Level.FINE,
                        "Adding new objective " + objectiveID + " for player " + PlayerConverter.getName(playerID));
                // add the objective
                if (isOnline) {
                    BetonQuest.newObjective(playerID, objectiveID);
                } else {
                    playerData.addNewRawObjective(objectiveID);
                }
                sendMessage(sender, "objective_added");
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                LogUtils.getLogger().log(Level.FINE,
                        "Deleting objective " + objectiveID + " for player " + PlayerConverter.getName(playerID));
                objective.removePlayer(playerID);
                playerData.removeRawObjective(objectiveID);
                sendMessage(sender, "objective_removed");
                break;
            case "complete":
            case "c":
                LogUtils.getLogger().log(Level.FINE,
                        "Completing objective " + objectiveID + " for player " + PlayerConverter.getName(playerID));
                if (isOnline) {
                    objective.completeObjective(playerID);
                } else {
                    playerData.removeRawObjective(objectiveID);
                }
                sendMessage(sender, "objective_completed");
                break;
            default:
                // if there was something else, display error message
                LogUtils.getLogger().log(Level.FINE, "The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest objectives command
     *
     * @param sender
     * @param args
     * @return
     */
    private List<String> completeObjectives(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return null;
        }
        if (args.length == 3) {
            return Arrays.asList("list", "add", "del", "complete");
        }
        if (args.length == 4) {
            return completeId(sender, args, ConfigAccessor.AccessorType.OBJECTIVES);
        }
        return new ArrayList<>();
    }

    /**
     * Creates a vector variable
     *
     * @param sender
     * @param args
     */
    private void handleVector(final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) {
            return;
        }
        final Player player = (Player) sender;
        if (args.length != 3) {
            player.sendMessage("§4ERROR");
            return;
        }
        String pack = null, name = null;
        if (args[1].contains(".")) {
            final String[] parts = args[1].split("\\.");
            pack = parts[0];
            name = parts[1];
        } else {
            pack = defaultPack;
            name = args[1];
        }
        final String origin = Config.getString(pack + ".main.variables." + name);
        if (origin == null) {
            player.sendMessage("§4ERROR");
            return;
        }
        final Vector vector;
        try {
            vector = LocationData.parseVector(origin);
        } catch (InstructionParseException e) {
            player.sendMessage("§4ERROR");
            LogUtils.logThrowable(e);
            return;
        }
        Config.setString(pack + ".main.variables.vectors." + args[2],
                String.format(Locale.US, "$%s$->(%.2f,%.2f,%.2f)", name, vector.getBlockX(), vector.getBlockY(),
                        vector.getBlockZ()));
        player.sendMessage("§2OK");
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest vector command
     *
     * @param sender
     * @param args
     * @return
     */
    private List<String> completeVector(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            if (args[1] == null || !args[1].contains(".")) {
                return completePackage(sender, args);
            }
            final String pack = args[1].substring(0, args[1].indexOf("."));
            final ConfigPackage configPack = Config.getPackages().get(pack);
            if (configPack == null) {
                return new ArrayList<>();
            }
            final ConfigurationSection section = configPack.getMain().getConfig().getConfigurationSection("variables");
            final Collection<String> keys = section.getKeys(false);
            if (keys.isEmpty()) {
                return new ArrayList<>();
            }
            return new ArrayList<>(keys);
        }
        return new ArrayList<>();
    }

    /**
     * Renames stuff.
     */
    private void handleRenaming(final CommandSender sender, final String[] args) {
        if (args.length < 4) {
            sendMessage(sender, "arguments");
            return;
        }
        final String type = args[1].toLowerCase();
        String name = args[2];
        String rename = args[3];
        if (!name.contains(".")) {
            name = defaultPack + "." + name;
        }
        if (!rename.contains(".")) {
            rename = defaultPack + "." + rename;
        }
        final UpdateType updateType;
        switch (type) {
            case "tags":
            case "tag":
            case "t":
                updateType = UpdateType.RENAME_ALL_TAGS;
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
                    playerData.removeTag(name);
                    playerData.addTag(rename);
                }
                break;
            case "points":
            case "point":
            case "p":
                updateType = UpdateType.RENAME_ALL_POINTS;
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
                    int points = 0;
                    for (final Point point : playerData.getPoints()) {
                        if (point.getCategory().equals(name)) {
                            points = point.getCount();
                            break;
                        }
                    }
                    playerData.removePointsCategory(name);
                    playerData.modifyPoints(rename, points);
                }
                break;
            case "globalpoints":
            case "globalpoint":
            case "gpoints":
            case "gpoint":
            case "gp":
                updateType = UpdateType.RENAME_ALL_GLOBAL_POINTS;
                int globalpoints = 0;
                for (final Point globalpoint : BetonQuest.getInstance().getGlobalData().getPoints()) {
                    if (globalpoint.getCategory().equals(name)) {
                        globalpoints = globalpoint.getCount();
                        break;
                    }
                }
                BetonQuest.getInstance().getGlobalData().removePointsCategory(name);
                BetonQuest.getInstance().getGlobalData().modifyPoints(rename, globalpoints);
                break;
            case "objectives":
            case "objective":
            case "o":
                updateType = UpdateType.RENAME_ALL_OBJECTIVES;
                // get ID and package
                final ObjectiveID nameID;
                try {
                    nameID = new ObjectiveID(null, name);
                } catch (ObjectNotFoundException e) {
                    sendMessage(sender, "error", new String[]{
                            e.getMessage()
                    });
                    LogUtils.getLogger().log(Level.WARNING, "Could not find Objective: " + e.getMessage());
                    LogUtils.logThrowable(e);
                    return;
                }
                // rename objective in the file
                nameID.getPackage().getObjectives().getConfig().set(rename.split("\\.")[1],
                        nameID.generateInstruction().getInstruction());
                nameID.getPackage().getObjectives().saveConfig();
                // rename objective instance
                final ObjectiveID renameID;
                try {
                    renameID = new ObjectiveID(null, rename);
                } catch (ObjectNotFoundException e) {
                    sender.sendMessage("§4There was an unexpected error: " + e.getMessage());
                    LogUtils.logThrowableReport(e);
                    return;
                }
                BetonQuest.getInstance().renameObjective(nameID, renameID);
                BetonQuest.getInstance().getObjective(renameID).setLabel(renameID);
                // renaming an active objective probably isn't needed
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final String playerID = PlayerConverter.getID(player);
                    boolean found = false;
                    String data = null;
                    for (final Objective obj : BetonQuest.getInstance().getPlayerObjectives(playerID)) {
                        if (obj.getLabel().equals(name)) {
                            found = true;
                            data = obj.getData(PlayerConverter.getID(player));
                            break;
                        }
                    }
                    // skip the player if he does not have this objective
                    if (found == false) {
                        continue;
                    }
                    if (data == null) {
                        data = "";
                    }
                    BetonQuest.getInstance().getObjective(nameID).removePlayer(playerID);
                    BetonQuest.getInstance().getPlayerData(playerID).removeRawObjective(nameID);
                    BetonQuest.resumeObjective(PlayerConverter.getID(player), renameID, data);
                }
                nameID.getPackage().getObjectives().getConfig().set(nameID.getBaseID(), null);
                nameID.getPackage().getObjectives().saveConfig();
                break;
            case "journals":
            case "journal":
            case "j":
            case "entries":
            case "entry":
            case "e":
                updateType = UpdateType.RENAME_ALL_ENTRIES;
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final Journal journal = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player)).getJournal();
                    Pointer journalPointer = null;
                    for (final Pointer pointer : journal.getPointers()) {
                        if (pointer.getPointer().equals(name)) {
                            journalPointer = pointer;
                        }
                    }
                    // skip the player if he does not have this entry
                    if (journalPointer == null) {
                        continue;
                    }
                    journal.removePointer(name);
                    journal.addPointer(new Pointer(rename, journalPointer.getTimestamp()));
                    journal.update();
                }
                break;
            default:
                sendMessage(sender, "unknown_argument");
                return;
        }
        BetonQuest.getInstance().getSaver().add(new Record(updateType, new String[]{
                rename, name
        }));
        sendMessage(sender, "everything_renamed");
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest rename command
     *
     * @param sender
     * @param args
     * @return
     */
    private List<String> completeRenaming(final CommandSender sender, final String[] args) {
        if (args.length <= 3) {
            return completeDeleting(sender, args);
        }
        if (args.length == 4) {
            return completeId(sender, args, null);
        }
        return new ArrayList<>();
    }

    /**
     * Deleted stuff.
     */
    private void handleDeleting(final CommandSender sender, final String[] args) {
        if (args.length < 3) {
            sendMessage(sender, "arguments");
            return;
        }
        final String type = args[1].toLowerCase();
        String name = args[2];
        if (!name.contains(".")) {
            name = defaultPack + "." + name;
        }
        final UpdateType updateType;
        switch (type) {
            case "tags":
            case "tag":
            case "t":
                updateType = UpdateType.REMOVE_ALL_TAGS;
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
                    playerData.removeTag(name);
                }
                break;
            case "points":
            case "point":
            case "p":
                updateType = UpdateType.REMOVE_ALL_POINTS;
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
                    playerData.removePointsCategory(name);
                }
                break;
            case "objectives":
            case "objective":
            case "o":
                updateType = UpdateType.REMOVE_ALL_OBJECTIVES;
                final ObjectiveID objectiveID;
                try {
                    objectiveID = new ObjectiveID(null, name);
                } catch (ObjectNotFoundException e) {
                    sendMessage(sender, "error", new String[]{
                            e.getMessage()
                    });
                    LogUtils.getLogger().log(Level.WARNING, "Could not fine objective: " + e.getMessage());
                    LogUtils.logThrowable(e);
                    return;
                }
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final String playerID = PlayerConverter.getID(player);
                    BetonQuest.getInstance().getObjective(objectiveID).removePlayer(playerID);
                    BetonQuest.getInstance().getPlayerData(playerID).removeRawObjective(objectiveID);
                }
                break;
            case "journals":
            case "journal":
            case "j":
            case "entries":
            case "entry":
            case "e":
                updateType = UpdateType.REMOVE_ALL_ENTRIES;
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final Journal journal = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player)).getJournal();
                    journal.removePointer(name);
                    journal.update();
                }
                break;
            default:
                sendMessage(sender, "unknown_argument");
                return;
        }
        BetonQuest.getInstance().getSaver().add(new Record(updateType, new String[]{
                name
        }));
        sendMessage(sender, "everything_removed");
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest delete command
     *
     * @param sender
     * @param args
     * @return
     */
    private List<String> completeDeleting(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return Arrays.asList("tag", "point", "objective", "entry");
        }
        if (args.length == 3) {
            switch (args[1].toLowerCase()) {
                case "tags":
                case "tag":
                case "t":
                case "points":
                case "point":
                case "p":
                    return completeId(sender, args, null);
                case "objectives":
                case "objective":
                case "o":
                    return completeId(sender, args, ConfigAccessor.AccessorType.OBJECTIVES);
                case "journals":
                case "journal":
                case "j":
                case "entries":
                case "entry":
                case "e":
                    return completeId(sender, args, ConfigAccessor.AccessorType.JOURNAL);
            }
        }
        return new ArrayList<>();
    }

    /**
     * Displays help to the user.
     */
    private void displayHelp(final CommandSender sender, final String alias) {
        LogUtils.getLogger().log(Level.FINE, "Just displaying help");
        // specify all commands
        final HashMap<String, String> cmds = new HashMap<>();
        cmds.put("reload", "reload");
        cmds.put("objectives", "objective <player> [list/add/del] [objective]");
        cmds.put("globaltags", "globaltags [list/add/del/purge]");
        cmds.put("globalpoints", "globalpoints [list/add/del/purge]");
        cmds.put("tags", "tag <player> [list/add/del] [tag]");
        cmds.put("points", "point <player> [list/add/del] [category] [amount]");
        cmds.put("journal", "journal <player> [list/add/del] [entry] [date]");
        cmds.put("condition", "condition <player> <condition>");
        cmds.put("event", "event <player> <event>");
        cmds.put("item", "item <name>");
        cmds.put("give", "give <name>");
        cmds.put("rename", "rename <tag/point/globalpoint/objective/journal> <old> <new>");
        cmds.put("delete", "delete <tag/point/objective/journal> <name>");
        cmds.put("config", "config <read/set/add> <path> [string]");
        cmds.put("vector", "vector <pack.varname> <vectorname>");
        cmds.put("version", "version");
        cmds.put("purge", "purge <player>");
        cmds.put("debug", "[true/false]");
        if (!(sender instanceof Player)) {
            cmds.put("backup", "backup");
        }
        // display them
        sender.sendMessage("§e----- §aBetonQuest §e-----");
        if (sender instanceof Player) {
            final String lang = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID((Player) sender)).getLanguage();
            for (final String command : cmds.keySet()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "tellraw " + sender.getName() + " {\"text\":\"\",\"extra\":[{\"text\":\"§c/" + alias + " "
                                + cmds.get(
                                command)
                                + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§b"
                                + Config.getMessage(lang, "command_" + command) + "\"}}]}");
            }
        } else {
            for (final String command : cmds.keySet()) {
                sender.sendMessage("§c/" + alias + " " + cmds.get(command));
                sender.sendMessage("§b- " + Config.getMessage(Config.getLanguage(), "command_" + command));
            }
        }
    }

    private void displayVersionInfo(final CommandSender sender) {

        // build clickable tellraw-like message by using bugee api or fall back
        // on unclickable messages
        final ComponentBuilder builder = ComponentBuilder.BugeeCordAPIBuilder.create();

        // get versions
        final String betonquestVersion = BetonQuest.getInstance().getDescription().getVersion();
        final String spigotVersion = Bukkit.getServer().getVersion();

        // get internal messages
        final String lang = sender instanceof Player
                ? BetonQuest.getInstance().getPlayerData(PlayerConverter.getID((Player) sender)).getLanguage()
                : Config.getLanguage();
        final String clickToDownload = "§b" + Config.getMessage(lang, "click_to_download");
        final String clickToCopy = "§b" + Config.getMessage(lang, "click_to_copy");

        // get available updates
        final Updater updater = BetonQuest.getInstance().getUpdater();
        String updatesString = "";
        String updatesCommand = null;
        if (updater.isUpdateAvailable()) {
            updatesCommand = "/q update";
            updatesString = " (version '" + updater.getUpdateVersion() + "' is " + "avialable!)";
        }

        // get hooked Plugins
        final TreeMap<String, String> hooked = new TreeMap<>();
        for (final String plugin : Compatibility.getHooked()) {
            final Plugin plug = Bukkit.getPluginManager().getPlugin(plugin);
            if (plug != null) {
                hooked.put(plugin, plug.getDescription().getVersion());
            }
        }
        final StringJoiner hookedRaw = new StringJoiner(", ");
        for (final String key : hooked.navigableKeySet()) {
            hookedRaw.add(key + " (" + hooked.get(key) + ")");
        }

        // build version info message
        builder.append("- - - - - - - - - - - - - - -\n", ChatColor.YELLOW);
        builder.append("BetonQuest version: ", ChatColor.AQUA).append(betonquestVersion, ChatColor.GRAY)
                .hover(clickToCopy).click(
                ComponentBuilder.ClickEvent.SUGGEST_COMMAND, betonquestVersion);
        if (updatesCommand != null) {
            builder.append("\n        " + updatesString, ChatColor.YELLOW).hover(clickToDownload)
                    .click(ComponentBuilder.ClickEvent.RUN_COMMAND, updatesCommand);
        }
        builder.append("\n", ChatColor.RESET).append("Server version: ", ChatColor.GOLD)
                .append(spigotVersion, ChatColor.GRAY).hover(clickToCopy).click(
                ComponentBuilder.ClickEvent.SUGGEST_COMMAND, spigotVersion)
                .append("\n\n", ChatColor.RESET).append("Hooked into:\n", ChatColor.GREEN);
        if (hooked.isEmpty()) {
            builder.append("  ---", ChatColor.GRAY);
        } else {
            boolean first = true;
            for (final String key : hooked.navigableKeySet()) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ", ChatColor.RESET).hover(clickToCopy)
                            .click(ComponentBuilder.ClickEvent.SUGGEST_COMMAND, hookedRaw.toString());
                }
                builder.append(key, ChatColor.RESET).hover(clickToCopy)
                        .click(ComponentBuilder.ClickEvent.SUGGEST_COMMAND, hookedRaw.toString()).append(" (" + hooked
                        .get(key) + ")", ChatColor.GRAY)
                        .hover(clickToCopy).click(ComponentBuilder.ClickEvent.SUGGEST_COMMAND, hookedRaw.toString());

            }
        }

        // send the message
        builder.send(sender);
    }

    private void handleDebug(final CommandSender sender, final String[] args) {
        if (args.length == 1) {
            sender.sendMessage(
                    "§2Debugging mode is currently " + (LogUtils.isDebugging() ? "enabled" : "disabled") + "!");
            return;
        }
        final Boolean input = args[1].equalsIgnoreCase("true") ? Boolean.TRUE
                : args[1].equalsIgnoreCase("false") ? Boolean.FALSE : null;
        if (input != null && args.length == 2) {

            if (LogUtils.isDebugging() && input || !LogUtils.isDebugging() && !input) {
                sender.sendMessage(
                        "§2Debugging mode is already " + (LogUtils.isDebugging() ? "enabled" : "disabled") + "!");
                return;
            }
            if (input) {
                LogUtils.startDebug();
            } else {
                LogUtils.endDebug();
            }
            sender.sendMessage("§2Debugging mode was " + (LogUtils.isDebugging() ? "enabled" : "disabled") + "!");
            LogUtils.getLogger().log(Level.INFO,
                    "Debuging mode was " + (LogUtils.isDebugging() ? "enabled" : "disabled") + "!");
            return;
        }
        sendMessage(sender, "unknown_argument");
    }

    private List<String> completeDebug(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return Arrays.asList("true", "false");
        }
        return null;
    }

    private void sendMessage(final CommandSender sender, final String messageName) {
        sendMessage(sender, messageName, null);
    }

    private void sendMessage(final CommandSender sender, final String messageName, final String[] variables) {
        if (sender instanceof Player) {
            Config.sendMessage(PlayerConverter.getID((Player) sender), messageName, variables);
        } else {
            final String message = Config.getMessage(Config.getLanguage(), messageName, variables);
            sender.sendMessage(message);
        }
    }
}
