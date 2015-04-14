/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.commands;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.ConfigAccessor;
import pl.betoncraft.betonquest.config.ConfigHandler;
import pl.betoncraft.betonquest.core.Conversation;
import pl.betoncraft.betonquest.core.GlobalLocations;
import pl.betoncraft.betonquest.core.Journal;
import pl.betoncraft.betonquest.core.Point;
import pl.betoncraft.betonquest.core.Pointer;
import pl.betoncraft.betonquest.core.StaticEvents;
import pl.betoncraft.betonquest.database.DatabaseHandler;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Main admin command for quest editing.
 * 
 * @author Co0sh
 */
public class QuestCommand implements CommandExecutor {
    /**
     * Language string.
     */
    private String lang = ConfigHandler.getString("config.language");
    /**
     * Keeps the pointer to BetonQuest's instance.
     */
    private BetonQuest instance = BetonQuest.getInstance();
    /**
     * Tells if MySQL is being used (if database calls need to be done in an
     * async thread.
     */
    private boolean isMySQLUsed = instance.isMySQLUsed();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {

        if (cmd.getName().equalsIgnoreCase("q")) {
            Debug.info("Executing /q command for user " + sender.getName()
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
                    handleConditions(sender, args);
                    break;
                case "events":
                case "event":
                case "e":
                    // the same goes for events
                    handleEvents(sender, args);
                    break;
                case "items":
                case "item":
                case "i":
                    // and items, which only use configuration files (they
                    // should be sync)
                    handleItems(sender, args);
                    break;
                case "config":
                    // config is also only synchronous
                    handleConfig(sender, args);
                    break;
                case "objectives":
                case "objective":
                case "o":
                    // objectives may have to be read from database, so run them
                    // async in
                    // case of MySQL usage
                    if (isMySQLUsed) {
                        Debug.info("MySQL connection may be required, continuing in async thread");
                        final CommandSender finalSender = sender;
                        final String[] finalArgs = args;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                handleObjectives(finalSender, finalArgs);
                            }
                        }.runTask(instance);
                    } else {
                        handleObjectives(sender, args);
                    }
                    break;
                case "tags":
                case "tag":
                case "t":
                    // tags also may have to be read from database
                    if (isMySQLUsed) {
                        Debug.info("MySQL connection may be required, continuing in async thread");
                        final CommandSender finalSender = sender;
                        final String[] finalArgs = args;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                handleTags(finalSender, finalArgs);
                            }
                        }.runTask(instance);
                    } else {
                        handleTags(sender, args);
                    }
                    break;
                case "points":
                case "point":
                case "p":
                    // the same situation is with points
                    if (isMySQLUsed) {
                        Debug.info("MySQL connection may be required, continuing in async thread");
                        final CommandSender finalSender = sender;
                        final String[] finalArgs = args;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                handlePoints(finalSender, finalArgs);
                            }
                        }.runTask(instance);
                    } else {
                        handlePoints(sender, args);
                    }
                    break;
                case "journals":
                case "journal":
                case "j":
                    // and journal entries
                    if (isMySQLUsed) {
                        Debug.info("MySQL connection may be required, continuing in async thread");
                        final CommandSender finalSender = sender;
                        final String[] finalArgs = args;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                handleJournals(finalSender, finalArgs);
                            }
                        }.runTask(instance);
                    } else {
                        handleJournals(sender, args);
                    }
                    break;
                case "purge":
                    // purging an offline player also may require database
                    // connection
                    if (isMySQLUsed) {
                        Debug.info("MySQL connection may be required, continuing in async thread");
                        final CommandSender finalSender = sender;
                        final String[] finalArgs = args;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                purgePlayer(finalSender, finalArgs);
                            }
                        }.runTask(instance);
                    } else {
                        purgePlayer(sender, args);
                    }
                    break;
                case "reload":
                    // just reloading
                    reloadPlugin();
                    sender.sendMessage(getMessage("reloaded"));
                    break;
                case "backup":
                    // do a full plugin backup
                    if (sender instanceof Player || Bukkit.getOnlinePlayers().length > 0) {
                        sender.sendMessage(getMessage("offline"));
                        break;
                    }
                    Utils.backup();
                    break;
                default:
                    // there was an unknown argument, so handle this
                    sender.sendMessage(getMessage("unknown_argument"));
                    break;
            }
            Debug.info("Command executing done");
            return true;
        }
        return false;
    }

    /**
     * Purges player's data
     * 
     * @param sender
     *            CommandSender
     * @param args
     *            command arguments
     */
    private void purgePlayer(CommandSender sender, String[] args) {
        // playerID is required
        if (args.length < 2) {
            Debug.info("Player's name is missing");
            sender.sendMessage(getMessage("specify_player"));
            return;
        }
        String playerID = PlayerConverter.getID(args[1]);
        DatabaseHandler dbHandler = instance.getDBHandler(playerID);
        // if the player is offline then get his DatabaseHandler outside of the
        // list
        if (dbHandler == null) {
            Debug.info("Player is offline, loading his data");
            dbHandler = new DatabaseHandler(playerID);
        }
        // purge the player
        Debug.info("Purging player " + args[1]);
        dbHandler.purgePlayer();
        // done
        sender.sendMessage(getMessage("purged").replaceAll("%player%", args[1]));
    }
    
    /**
     * Handles config command
     * <p/>
     * Reads, sets or appends strings from/to config files
     * 
     * @param sender
     * @param args
     */
    private void handleConfig(CommandSender sender, String[] args) {
        if (args.length < 3) {
            Debug.info("No action specified!");
            sender.sendMessage(getMessage("specify_action"));
            return;
        }
        String action = args[1];
        String path = args[2];
        switch (action) {
            case "read":
            case "r":
                Debug.info("Displaying variable at path " + path);
                String message = ConfigHandler.getString(path);
                sender.sendMessage(message == null ? "null" : message);
                break;
            case "set":
            case "s":
                StringBuilder strBldr = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    strBldr.append(args[i] + " ");
                }
                if (strBldr.length() < 2) {
                    Debug.info("Wrong path!");
                    sender.sendMessage(getMessage("specify_path"));
                    return;
                }
                boolean set = ConfigHandler.setString(path, (args[3].equals("null")) ? null : 
                        strBldr.toString().trim());
                if (set) {
                    Debug.info("Displaying variable at path " + path);
                    String message1 = ConfigHandler.getString(path);
                    sender.sendMessage(message1 == null ? "null" : message1);
                } else {
                    sender.sendMessage(getMessage("config_set_error"));
                }
                break;
            case "add":
            case "a":
                StringBuilder strBldr2 = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    strBldr2.append(args[i] + " ");
                }
                if (strBldr2.length() < 2) {
                    Debug.info("Wrong path!");
                    sender.sendMessage(getMessage("specify_path"));
                    return;
                }
                String finalString = strBldr2.toString().trim();
                boolean space = false;
                if (finalString.startsWith("_")) {
                    finalString = finalString.substring(1, finalString.length());
                    space = true;
                }
                String oldString = ConfigHandler.getString(path);
                if (oldString == null) {
                    oldString = "";
                }
                boolean set2 = ConfigHandler.setString(path, oldString + ((space) ? " " : "")
                        + finalString);
                if (set2) {
                    Debug.info("Displaying variable at path " + path);
                    String message2 = ConfigHandler.getString(path);
                    sender.sendMessage(message2 == null ? "null" : message2);
                } else {
                    sender.sendMessage(getMessage("config_set_error"));
                }
                break;
            default:
                // if there was something else, display error message
                Debug.info("The argument was unknown");
                sender.sendMessage(getMessage("unknown_argument"));
                break;
        }
    }
    
    /**
     * Handles journal command
     * <p/>
     * Lists, adds or removes journal entries of certain players
     * 
     * @param sender
     *          CommandSender
     * @param args
     *          list of this command's arguments
     */
    private void handleJournals(CommandSender sender, String[] args) {
        // playerID is required
        if (args.length < 2) {
            Debug.info("Player's name is missing");
            sender.sendMessage(getMessage("specify_player"));
            return;
        }
        String playerID = PlayerConverter.getID(args[1]);
        boolean isOnline = (PlayerConverter.getPlayer(playerID) != null);
        DatabaseHandler dbHandler = instance.getDBHandler(playerID);
        // if the player is offline then get his DatabaseHandler outside of the
        // list
        if (dbHandler == null) {
            Debug.info("Player is offline, loading his data");
            dbHandler = new DatabaseHandler(playerID);
        }
        Journal journal = dbHandler.getJournal();
        // if there are no arguments then list player's pointers
        if (args.length < 3 || args[2].equalsIgnoreCase("list") || args[2].equalsIgnoreCase("l")) {
            Debug.info("Listing journal pointers");
            sender.sendMessage(getMessage("player_journal"));
            for (Pointer pointer : journal.getPointers()) {
                String date = new SimpleDateFormat(ConfigHandler.getString(
                        "messages.global.date_format")).format(new Date(pointer.getTimestamp()));
                sender.sendMessage("§b- " + pointer.getPointer() + " §c(§2" + date + "§c)");
            }
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            Debug.info("Missing pointer");
            sender.sendMessage(getMessage("specify_pointer"));
            return;
        }
        // if there are arguments, handle them
        switch (args[2].toLowerCase()) {
            case "add":
            case "a":
                if (args.length < 5) {
                    long timestamp = new Date().getTime();
                    Debug.info("Adding pointer with current date: " + timestamp);
                    journal.addPointer(new Pointer(args[3], timestamp));
                } else {
                    Debug.info("Adding pointer with date " + args[4].replaceAll("_", " "));
                    try {
                        journal.addPointer(new Pointer(args[3], new SimpleDateFormat(ConfigHandler
                                .getString("messages.global.date_format")).parse(args[4].replaceAll("_", " ")).getTime()));
                    } catch (ParseException e) {
                        Debug.info("Date was in the wrong format");
                        sender.sendMessage(getMessage("specify_date"));
                        return;
                    }
                }
                // add the pointer
                if (!isOnline) {
                    dbHandler.saveData();
                } else {
                    journal.updateJournal();
                }
                sender.sendMessage(getMessage("pointer_added"));
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                // remove the pointer (this is unnecessary as adding negativ
                Debug.info("Removing pointer");
                journal.removePointer(args[3]);
                if (!isOnline) {
                    dbHandler.saveData();
                } else {
                    journal.updateJournal();
                }
                sender.sendMessage(getMessage("pointer_removed"));
                break;
            default:
                // if there was something else, display error message
                Debug.info("The argument was unknown");
                sender.sendMessage(getMessage("unknown_argument"));
                break;
        }
    }

    /**
     * Handles points command
     * <p/>
     * Lists, adds or removes points of certain player
     * 
     * @param sender
     *            CommandSender
     * @param args
     *            list of this command's arguments
     */
    private void handlePoints(CommandSender sender, String[] args) {
        // playerID is required
        if (args.length < 2) {
            Debug.info("Player's name is missing");
            sender.sendMessage(getMessage("specify_player"));
            return;
        }
        String playerID = PlayerConverter.getID(args[1]);
        boolean isOnline = (PlayerConverter.getPlayer(playerID) != null);
        DatabaseHandler dbHandler = instance.getDBHandler(playerID);
        // if the player is offline then get his DatabaseHandler outside of the
        // list
        if (dbHandler == null) {
            Debug.info("Player is offline, loading his data");
            dbHandler = new DatabaseHandler(playerID);
        }
        // if there are no arguments then list player's points
        if (args.length < 3 || args[2].equalsIgnoreCase("list") || args[2].equalsIgnoreCase("l")) {
            List<Point> points = dbHandler.getPoints();
            Debug.info("Listing points");
            sender.sendMessage(getMessage("player_points"));
            for (Point point : points) {
                sender.sendMessage("§b- " + point.getCategory() + "§e: §a" + point.getCount());
            }
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            Debug.info("Missing category");
            sender.sendMessage(getMessage("specify_category"));
            return;
        }
        // if there are arguments, handle them
        switch (args[2].toLowerCase()) {
            case "add":
            case "a":
                if (args.length < 5 || !args[4].matches("-?\\d+")) {
                    Debug.info("Missing amount");
                    sender.sendMessage(getMessage("specify_amount"));
                    return;
                }
                // add the point
                Debug.info("Adding points");
                dbHandler.addPoints(args[3], Integer.parseInt(args[4]));
                if (!isOnline) dbHandler.saveData();
                sender.sendMessage(getMessage("points_added"));
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                // remove the point (this is unnecessary as adding negative
                // amounts
                // subtracts points, but for the sake of users leave it be)
                Debug.info("Removing points");
                dbHandler.removePointsCategory(args[3]);
                if (!isOnline) dbHandler.saveData();
                sender.sendMessage(getMessage("points_removed"));
                break;
            default:
                // if there was something else, display error message
                Debug.info("The argument was unknown");
                sender.sendMessage(getMessage("unknown_argument"));
                break;
        }
    }

    /**
     * Handles items command
     * <p/>
     * Adds item held in hand to items.yml file
     * 
     * @param sender
     *            CommandSender
     * @param args
     *            List of command's arguments
     */
    private void handleItems(CommandSender sender, String[] args) {
        // sender must be a player
        if (!(sender instanceof Player)) {
            Debug.info("Cannot continue, sender must be player");
            return;
        }
        // and the item name must be specified
        if (args.length < 2) {
            Debug.info("Cannot continue, item's name must be supplied");
            sender.sendMessage(getMessage("specify_item"));
            return;
        }
        Player player = (Player) sender;
        ItemStack item = player.getItemInHand();
        // if item is air then there is nothing to add to items.yml
        if (item == null) {
            Debug.info("Cannot continue, item must not be air");
            sender.sendMessage(getMessage("no_item"));
            return;
        }
        // define parts of the final string
        ConfigAccessor config = ConfigHandler.getConfigs().get("items");
        String instructions = Utils.itemToString(item);
        // save it in items.yml
        Debug.info("Saving item to configuration as " + args[1]);
        config.getConfig().set(args[1], instructions.trim());
        config.saveConfig();
        // done
        sender.sendMessage(getMessage("item_created").replace("%item%", args[1]));
    }

    /**
     * Handler events command
     * <p/>
     * Fires an event for an online player. It cannot work for offline players!
     * 
     * @param sender
     *            CommandSender
     * @param args
     *            list of command's arguments
     */
    private void handleEvents(CommandSender sender, String[] args) {
        String playerID = PlayerConverter.getID(args[1]);
        // the player has to be specified every time
        if (args.length < 2 || PlayerConverter.getPlayer(playerID) == null) {
            Debug.info("Player's name is missing or he's offline");
            sender.sendMessage(getMessage("specify_player"));
            return;
        }
        // the event ID
        if (args.length < 3 || ConfigHandler.getString("events." + args[2]) == null) {
            Debug.info("Event's ID is missing or it's not defined");
            sender.sendMessage(getMessage("specify_event"));
            return;
        }
        // fire the event
        BetonQuest.event(playerID, args[2]);
        sender.sendMessage(getMessage("player_event").replaceAll("%event%",
                ConfigHandler.getString("events." + args[2])));
    }

    /**
     * Handles conditions command
     * <p/>
     * Checks if specified player meets condition described by ID
     * 
     * @param sender
     *            CommandSender
     * @param args
     *            list of arguments
     */
    private void handleConditions(CommandSender sender, String[] args) {
        String playerID = PlayerConverter.getID(args[1]);
        // the player has to be specified every time
        if (args.length < 2 || PlayerConverter.getPlayer(playerID) == null) {
            Debug.info("Player's name is missing or he's offline");
            sender.sendMessage(getMessage("specify_player"));
            return;
        }
        // the condition ID
        if (args.length < 3) {
            Debug.info("Condition's ID is missing");
            sender.sendMessage(getMessage("specify_condition"));
            return;
        }
        String conditionID = (args[2].startsWith("!")) ? args[2].substring(1, args[2].length()): args[2];
        if (ConfigHandler.getString("conditions." + conditionID) == null) {
            Debug.info("Condition is not defined");
            sender.sendMessage(getMessage("specify_condition"));
            return;
        }
        // display message about condition
        sender.sendMessage(getMessage("player_condition")
                .replaceAll("%condition%", 
                        ((args[2].startsWith("!")) ? "! " : "")
                        + ConfigHandler.getString("conditions." + conditionID))
                .replaceAll("%outcome%",
                        BetonQuest.condition(playerID, args[2]) + ""));
    }

    /**
     * Handles tags command
     * <p/>
     * Lists, adds or removes tags
     * 
     * @param sender
     */
    private void handleTags(CommandSender sender, String[] args) {
        // playerID is required
        if (args.length < 2) {
            Debug.info("Player's name is missing");
            sender.sendMessage(getMessage("specify_player"));
            return;
        }
        String playerID = PlayerConverter.getID(args[1]);
        boolean isOnline = (PlayerConverter.getPlayer(playerID) != null);
        DatabaseHandler dbHandler = instance.getDBHandler(playerID);
        // if the player is offline then get his DatabaseHandler outside of the
        // list
        if (dbHandler == null) {
            Debug.info("Player is offline, loading his data");
            dbHandler = new DatabaseHandler(playerID);
        }
        // if there are no arguments then list player's tags
        if (args.length < 3 || args[2].equalsIgnoreCase("list") || args[2].equalsIgnoreCase("l")) {
            List<String> tags = dbHandler.getTags();
            Debug.info("Listing tags");
            sender.sendMessage(getMessage("player_tags"));
            for (String tag : tags) {
                sender.sendMessage("§b- " + tag);
            }
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            Debug.info("Missing tag name");
            sender.sendMessage(getMessage("specify_tag"));
            return;
        }
        // if there are arguments, handle them
        switch (args[2].toLowerCase()) {
            case "add":
            case "a":
                // add the point
                Debug.info("Adding tag " + args[3] + " for player " + playerID);
                dbHandler.addTag(args[3]);
                if (!isOnline) dbHandler.saveData();
                sender.sendMessage(getMessage("tag_added"));
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                // remove the point (this is unnecessary as adding negative
                // amounts
                // subtracts points, but for the sake of users leave it be)
                Debug.info("Removing tag " + args[3] + " for player " + playerID);
                dbHandler.removeTag(args[3]);
                if (!isOnline) dbHandler.saveData();
                sender.sendMessage(getMessage("tag_removed"));
                break;
            default:
                // if there was something else, display error message
                Debug.info("The argument was unknown");
                sender.sendMessage(getMessage("unknown_argument"));
                break;
        }
    }

    /**
     * Handles objectives command
     * <p/>
     * Lists, adds or removes objectives.
     * 
     * @param sender
     *            CommandSender
     * @param args
     *            list of command's arguments
     */
    private void handleObjectives(CommandSender sender, String[] args) {
        // playerID is required
        if (args.length < 2) {
            Debug.info("Player's name is missing");
            sender.sendMessage(getMessage("specify_player"));
            return;
        }
        String playerID = PlayerConverter.getID(args[1]);
        boolean isOnline = !(PlayerConverter.getPlayer(playerID) == null);
        DatabaseHandler dbHandler = instance.getDBHandler(playerID);
        // if the player is offline then get his DatabaseHandler outside of the
        // list
        if (dbHandler == null) {
            Debug.info("Player is offline, loading his data");
            dbHandler = new DatabaseHandler(playerID);
        }
        // if there are no arguments then list player's objectives
        if (args.length < 3 || args[2].equalsIgnoreCase("list") || args[2].equalsIgnoreCase("l")) {
            List<String> tags;
            if (!isOnline) {
                // if player is offline then convert his raw objective strings
                // to tags
                tags = new ArrayList<>();
                for (String string : dbHandler.getRawObjectives()) {
                    for (String part : string.split(" ")) {
                        if (part.startsWith("tag:")) {
                            tags.add(part.substring(4));
                        }
                    }
                }
            } else {
                // if the player is online then just retrieve tags from his
                // active
                // objectives
                tags = new ArrayList<>();
                for (Objective objective : dbHandler.getObjectives()) {
                    tags.add(objective.getTag());
                }
            }
            // display objectives
            Debug.info("Listing objectives");
            sender.sendMessage(getMessage("player_objectives"));
            for (String tag : tags) {
                sender.sendMessage("§b- " + tag);
            }
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            Debug.info("Missing objective instruction string");
            sender.sendMessage("specify_objective");
            return;
        }
        // if there are arguments, handle them
        switch (args[2].toLowerCase()) {
            case "add":
            case "a":
                // get the instruction
                Debug.info("Adding new objective for player " + playerID);
                StringBuilder instruction = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    instruction.append(args[i] + " ");
                }
                String objectiveInstruction = instruction.toString().trim();
                // add the objective
                if (isOnline) {
                    BetonQuest.objective(playerID, objectiveInstruction);
                } else {
                    dbHandler.addRawObjective(objectiveInstruction);
                    dbHandler.saveData();
                }
                sender.sendMessage(getMessage("objective_added"));
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                // remove the objective
                Debug.info("Deleting objective with tag " + args[3] + " for player " + playerID);
                dbHandler.deleteObjective(args[3]);
                if (!isOnline) {
                    // if the player is offline then save the data
                    dbHandler.saveData();
                }
                sender.sendMessage(getMessage("objective_removed"));
                break;
            default:
                // if there was something else, display error message
                Debug.info("The argument was unknown");
                sender.sendMessage(getMessage("unknown_argument"));
                break;
        }
    }

    /**
     * Reloads the configuration.
     */
    private void reloadPlugin() {
        // reload the configuration
        Debug.info("Reloading configuration");
        ConfigHandler.reload();
        // update language
        lang = ConfigHandler.getString("config.language");
        // load new static events
        new StaticEvents();
        // stop current global locations listener
        // and start new one with reloaded configs
        Debug.info("Restarting global locations");
        GlobalLocations.stop();
        new GlobalLocations().runTaskTimer(instance, 0, 20);
        // update journals for every online player
        for (Player player : Bukkit.getOnlinePlayers()) {
            String playerID = PlayerConverter.getID(player);
            Debug.info("Updating journal for player " + playerID);
            Journal journal = instance.getDBHandler(playerID).getJournal();
            journal.generateTexts();
            journal.updateJournal();
        }
        // kill all conversation
        Conversation.clear();
        // initialize new debugger
        new Debug();
    }

    /**
     * Displays help to the user.
     * 
     * @param sender
     *            CommandSender
     * @param alias
     *            used command alias
     */
    private void displayHelp(CommandSender sender, String alias) {
        Debug.info("Just displaying help");
        sender.sendMessage("§e----- §aBetonQuest §e-----");
        sender.sendMessage("§c/" + alias + " reload §b- " + getMessage("command_reload"));
        sender.sendMessage("§c/" + alias + " objective <player> [list/add/del] [objective] §b- " + getMessage("command_objectives"));
        sender.sendMessage("§c/" + alias + " tag <player> [list/add/del] [tag] §b- " + getMessage("command_tags"));
        sender.sendMessage("§c/" + alias + " point <player> [list/add/del] [category] [amount] §b- " + getMessage("command_points"));
        sender.sendMessage("§c/" + alias + " journal <player> [list/add/del] [entry] [date] §b- " + getMessage("command_journal"));
        sender.sendMessage("§c/" + alias + " condition <player> <condition> §b- " + getMessage("command_condition"));
        sender.sendMessage("§c/" + alias + " event <player> <event> §b- " + getMessage("command_event"));
        sender.sendMessage("§c/" + alias + " item <name> §b- " + getMessage("command_item"));
        sender.sendMessage("§c/" + alias + " config <read/set/add> <path> [string] §b- " + getMessage("command_config"));
        sender.sendMessage("§c/" + alias + " purge <player> §b- " + getMessage("command_purge"));
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c/" + alias + " backup §b- " + getMessage("command_backup"));
        }
    }

    /**
     * Gets the specified message from messages.yml in user's language.
     * 
     * @param name
     *            message's name
     * @return the message
     */
    private String getMessage(String name) {
        return ConfigHandler.getString("messages." + lang + "." + name).replaceAll("&", "§");
    }
}
