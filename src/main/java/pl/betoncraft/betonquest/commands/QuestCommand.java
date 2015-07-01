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
import java.util.HashMap;
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
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigAccessor;
import pl.betoncraft.betonquest.config.ConfigPackage;
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
     * Keeps the pointer to BetonQuest's instance.
     */
    private BetonQuest instance = BetonQuest.getInstance();
    
    /**
     * Registers a new executor of the /q command
     */
    public QuestCommand() {
        BetonQuest.getInstance().getCommand("q").setExecutor(this);
    }

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
                    Debug.info("Loading data asynchronously");
                    final CommandSender finalSender1 = sender;
                    final String[] finalArgs1 = args;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            handleObjectives(finalSender1, finalArgs1);
                        }
                    }.runTaskAsynchronously(instance);
                    break;
                case "tags":
                case "tag":
                case "t":
                    Debug.info("Loading data asynchronously");
                    final CommandSender finalSender2 = sender;
                    final String[] finalArgs2 = args;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            handleTags(finalSender2, finalArgs2);
                        }
                    }.runTaskAsynchronously(instance);
                    break;
                case "points":
                case "point":
                case "p":
                    Debug.info("Loading data asynchronously");
                    final CommandSender finalSender3 = sender;
                    final String[] finalArgs3 = args;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            handlePoints(finalSender3, finalArgs3);
                        }
                    }.runTaskAsynchronously(instance);
                    break;
                case "journals":
                case "journal":
                case "j":
                    Debug.info("Loading data asynchronously");
                    final CommandSender finalSender4 = sender;
                    final String[] finalArgs4 = args;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            handleJournals(finalSender4, finalArgs4);
                        }
                    }.runTaskAsynchronously(instance);
                    break;
                case "purge":
                    Debug.info("Loading data asynchronously");
                    final CommandSender finalSender5 = sender;
                    final String[] finalArgs5 = args;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            purgePlayer(finalSender5, finalArgs5);
                        }
                    }.runTaskAsynchronously(instance);
                    break;
                case "reload":
                    // just reloading
                    reloadPlugin();
                    sender.sendMessage(Config.getMessage("reloaded"));
                    break;
                case "backup":
                    // do a full plugin backup
                    if (sender instanceof Player || Bukkit.getOnlinePlayers().size() > 0) {
                        sender.sendMessage(Config.getMessage("offline"));
                        break;
                    }
                    Utils.backup();
                    break;
                case "create":
                case "package":
                    createNewPackage(sender, args);
                    break;
                default:
                    // there was an unknown argument, so handle this
                    sender.sendMessage(Config.getMessage("unknown_argument"));
                    break;
            }
            Debug.info("Command executing done");
            return true;
        }
        return false;
    }
    
    /**
     * Creates new package
     */
    public void createNewPackage(CommandSender sender, String[] args) {
        if (args.length < 2) {
            Debug.info("Package name is missing");
            sender.sendMessage(Config.getMessage("specify_package"));
            return;
        }
        if (Config.createPackage(args[1])) {
            sender.sendMessage(Config.getMessage("package_created"));
        } else {
            sender.sendMessage(Config.getMessage("package_exists"));
        }
    }

    /**
     * Purges player's data
     */
    private void purgePlayer(CommandSender sender, String[] args) {
        // playerID is required
        if (args.length < 2) {
            Debug.info("Player's name is missing");
            sender.sendMessage(Config.getMessage("specify_player"));
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
        sender.sendMessage(Config.getMessage("purged").replaceAll("%player%", args[1]));
    }
    
    /**
     * Reads, sets or appends strings from/to config files
     */
    private void handleConfig(CommandSender sender, String[] args) {
        if (args.length < 3) {
            Debug.info("No action specified!");
            sender.sendMessage(Config.getMessage("specify_action"));
            return;
        }
        String action = args[1];
        String path = args[2];
        switch (action) {
            case "read":
            case "r":
                Debug.info("Displaying variable at path " + path);
                String message = Config.getString(path);
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
                    sender.sendMessage(Config.getMessage("specify_path"));
                    return;
                }
                boolean set = Config.setString(path, (args[3].equals("null")) ? null : strBldr.toString().trim());
                if (set) {
                    Debug.info("Displaying variable at path " + path);
                    String message1 = Config.getString(path);
                    sender.sendMessage(message1 == null ? "null" : message1);
                } else {
                    sender.sendMessage(Config.getMessage("config_set_error"));
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
                    sender.sendMessage(Config.getMessage("specify_path"));
                    return;
                }
                String finalString = strBldr2.toString().trim();
                boolean space = false;
                if (finalString.startsWith("_")) {
                    finalString = finalString.substring(1, finalString.length());
                    space = true;
                }
                String oldString = Config.getString(path);
                if (oldString == null) {
                    oldString = "";
                }
                boolean set2 = Config.setString(path, oldString + ((space) ? " " : "") + finalString);
                if (set2) {
                    Debug.info("Displaying variable at path " + path);
                    String message2 = Config.getString(path);
                    sender.sendMessage(message2 == null ? "null" : message2);
                } else {
                    sender.sendMessage(Config.getMessage("config_set_error"));
                }
                break;
            default:
                // if there was something else, display error message
                Debug.info("The argument was unknown");
                sender.sendMessage(Config.getMessage("unknown_argument"));
                break;
        }
    }
    
    /**
     * Lists, adds or removes journal entries of certain players
     */
    private void handleJournals(CommandSender sender, String[] args) {
        // playerID is required
        if (args.length < 2) {
            Debug.info("Player's name is missing");
            sender.sendMessage(Config.getMessage("specify_player"));
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
            sender.sendMessage(Config.getMessage("player_journal"));
            for (Pointer pointer : journal.getPointers()) {
                String date = new SimpleDateFormat(Config.getString("messages.global.date_format"))
                        .format(new Date(pointer.getTimestamp()));
                sender.sendMessage("§b- " + pointer.getPointer() + " §c(§2" + date + "§c)");
            }
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            Debug.info("Missing pointer");
            sender.sendMessage(Config.getMessage("specify_pointer"));
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
                        journal.addPointer(new Pointer(args[3], new SimpleDateFormat(
                                Config.getString("messages.global.date_format"))
                                .parse(args[4].replaceAll("_", " ")).getTime()));
                    } catch (ParseException e) {
                        Debug.info("Date was in the wrong format");
                        sender.sendMessage(Config.getMessage("specify_date"));
                        return;
                    }
                }
                // add the pointer
                if (!isOnline) {
                    dbHandler.saveData();
                    dbHandler.removeData();
                } else {
                    journal.updateJournal();
                }
                sender.sendMessage(Config.getMessage("pointer_added"));
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
                    dbHandler.removeData();
                } else {
                    journal.updateJournal();
                }
                sender.sendMessage(Config.getMessage("pointer_removed"));
                break;
            default:
                // if there was something else, display error message
                Debug.info("The argument was unknown");
                sender.sendMessage(Config.getMessage("unknown_argument"));
                break;
        }
    }

    /**
     * Lists, adds or removes points of certain player
     */
    private void handlePoints(CommandSender sender, String[] args) {
        // playerID is required
        if (args.length < 2) {
            Debug.info("Player's name is missing");
            sender.sendMessage(Config.getMessage("specify_player"));
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
            sender.sendMessage(Config.getMessage("player_points"));
            for (Point point : points) {
                sender.sendMessage("§b- " + point.getCategory() + "§e: §a" + point.getCount());
            }
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            Debug.info("Missing category");
            sender.sendMessage(Config.getMessage("specify_category"));
            return;
        }
        // if there are arguments, handle them
        switch (args[2].toLowerCase()) {
            case "add":
            case "a":
                if (args.length < 5 || !args[4].matches("-?\\d+")) {
                    Debug.info("Missing amount");
                    sender.sendMessage(Config.getMessage("specify_amount"));
                    return;
                }
                // add the point
                Debug.info("Adding points");
                dbHandler.addPoints(args[3], Integer.parseInt(args[4]));
                if (!isOnline) {
                    dbHandler.saveData();
                    dbHandler.removeData();
                }
                sender.sendMessage(Config.getMessage("points_added"));
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
                if (!isOnline) {
                    dbHandler.saveData();
                    dbHandler.removeData();
                }
                sender.sendMessage(Config.getMessage("points_removed"));
                break;
            default:
                // if there was something else, display error message
                Debug.info("The argument was unknown");
                sender.sendMessage(Config.getMessage("unknown_argument"));
                break;
        }
    }

    /**
     * Adds item held in hand to items.yml file
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
            sender.sendMessage(Config.getMessage("specify_item"));
            return;
        }
        String itemID = args[1];
        String pack;
        String name;
        if (itemID.contains(".")) {
            String[] parts = itemID.split("\\.");
            pack = parts[0];
            name = parts[1];
        } else {
            pack = Config.getString("config.default_package");
            name = itemID;
        }
        if (!args[1].contains(".")) {
            Debug.info("Cannot continue, package must be specified");
            sender.sendMessage(Config.getMessage("specify_package"));
            return;
        }
        Player player = (Player) sender;
        ItemStack item = player.getItemInHand();
        // if item is air then there is nothing to add to items.yml
        if (item == null) {
            Debug.info("Cannot continue, item must not be air");
            sender.sendMessage(Config.getMessage("no_item"));
            return;
        }
        // define parts of the final string
        ConfigPackage configPack = Config.getPackage(pack);
        if (configPack == null) {
            Debug.info("Cannot continue, package does not exist");
            sender.sendMessage(Config.getMessage("specify_package"));
            return;
        }
        ConfigAccessor config = configPack.getItems();
        String instructions = Utils.itemToString(item);
        // save it in items.yml
        Debug.info("Saving item to configuration as " + args[1]);
        config.getConfig().set(name, instructions.trim());
        config.saveConfig();
        // done
        sender.sendMessage(Config.getMessage("item_created").replace("%item%", args[1]));
    }

    /**
     * Fires an event for an online player. It cannot work for offline players!
     */
    private void handleEvents(CommandSender sender, String[] args) {
        String playerID = PlayerConverter.getID(args[1]);
        // the player has to be specified every time
        if (args.length < 2 || PlayerConverter.getPlayer(playerID) == null) {
            Debug.info("Player's name is missing or he's offline");
            sender.sendMessage(Config.getMessage("specify_player"));
            return;
        }
        String eventID = args[2];
        String pack;
        String name;
        if (eventID.contains(".")) {
            String[] parts = eventID.split("\\.");
            pack = parts[0];
            name = parts[1];
        } else {
            pack = Config.getString("config.default_package");
            name = eventID;
            eventID = pack + "." + name;
        }
        ConfigPackage configPack = Config.getPackage(pack);
        if (configPack == null) {
            Debug.info("Cannot continue, package does not exist");
            sender.sendMessage(Config.getMessage("specify_package"));
            return;
        }
        // the event ID
        if (args.length < 3 || configPack.getEvents().getConfig().getString(name) == null) {
            Debug.info("Event's ID is missing or it's not defined");
            sender.sendMessage(Config.getMessage("specify_event"));
            return;
        }
        // fire the event
        BetonQuest.event(playerID, eventID);
        sender.sendMessage(Config.getMessage("player_event").replaceAll("%event%",
                configPack.getString("events." + name)));
    }

    /**
     * Checks if specified player meets condition described by ID
     */
    private void handleConditions(CommandSender sender, String[] args) {
        String playerID = PlayerConverter.getID(args[1]);
        // the player has to be specified every time
        if (args.length < 2 || PlayerConverter.getPlayer(playerID) == null) {
            Debug.info("Player's name is missing or he's offline");
            sender.sendMessage(Config.getMessage("specify_player"));
            return;
        }
        // the condition ID
        if (args.length < 3) {
            Debug.info("Condition's ID is missing");
            sender.sendMessage(Config.getMessage("specify_condition"));
            return;
        }
        String conditionID = args[2].replace("!", "");
        boolean inverted = args[2].contains("!");
        String pack;
        String name;
        if (conditionID.contains(".")) {
            String[] parts = conditionID.split("\\.");
            pack = parts[0];
            name = parts[1];
        } else {
            pack = Config.getString("config.default_package");
            name = conditionID;
            conditionID = pack + "." + name;
        }
        ConfigPackage configPack = Config.getPackage(pack);
        if (configPack == null) {
            Debug.info("Cannot continue, package does not exist");
            sender.sendMessage(Config.getMessage("specify_package"));
            return;
        }
        if (configPack.getConditions().getConfig().getString(name) == null) {
            Debug.info("Condition is not defined");
            sender.sendMessage(Config.getMessage("specify_condition"));
            return;
        }
        // display message about condition
        sender.sendMessage(Config.getMessage("player_condition").replaceAll("%condition%", (inverted ? "! " : "")
                + configPack.getString("conditions." + name)).replaceAll("%outcome%", BetonQuest
                .condition(playerID, conditionID) + ""));
    }

    /**
     * Lists, adds or removes tags
     */
    private void handleTags(CommandSender sender, String[] args) {
        // playerID is required
        if (args.length < 2) {
            Debug.info("Player's name is missing");
            sender.sendMessage(Config.getMessage("specify_player"));
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
            sender.sendMessage(Config.getMessage("player_tags"));
            for (String tag : tags) {
                sender.sendMessage("§b- " + tag);
            }
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            Debug.info("Missing tag name");
            sender.sendMessage(Config.getMessage("specify_tag"));
            return;
        }
        // if there are arguments, handle them
        switch (args[2].toLowerCase()) {
            case "add":
            case "a":
                // add the point
                Debug.info("Adding tag " + args[3] + " for player " + PlayerConverter.getName(playerID));
                dbHandler.addTag(args[3]);
                if (!isOnline) {
                    dbHandler.saveData();
                    dbHandler.removeData();
                }
                sender.sendMessage(Config.getMessage("tag_added"));
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                // remove the point (this is unnecessary as adding negative
                // amounts
                // subtracts points, but for the sake of users leave it be)
                Debug.info("Removing tag " + args[3] + " for player " + PlayerConverter.getName(playerID));
                dbHandler.removeTag(args[3]);
                if (!isOnline) {
                    dbHandler.saveData();
                    dbHandler.removeData();
                }
                sender.sendMessage(Config.getMessage("tag_removed"));
                break;
            default:
                // if there was something else, display error message
                Debug.info("The argument was unknown");
                sender.sendMessage(Config.getMessage("unknown_argument"));
                break;
        }
    }

    /**
     * Lists, adds or removes objectives.
     */
    private void handleObjectives(CommandSender sender, String[] args) {
        // playerID is required
        if (args.length < 2) {
            Debug.info("Player's name is missing");
            sender.sendMessage(Config.getMessage("specify_player"));
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
                // if player is offline then convert his raw objective strings to tags
                tags = new ArrayList<>();
                for (String string : dbHandler.getRawObjectives()) {
                    for (String part : string.split(" ")) {
                        if (part.startsWith("label:")) {
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
            sender.sendMessage(Config.getMessage("player_objectives"));
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
                Debug.info("Adding new objective for player " + PlayerConverter.getName(playerID));
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
                    dbHandler.removeData();
                }
                sender.sendMessage(Config.getMessage("objective_added"));
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                // remove the objective
                Debug.info("Deleting objective with tag " + args[3] + " for player " + PlayerConverter.getName(playerID));
                dbHandler.deleteObjective(args[3]);
                if (!isOnline) {
                    // if the player is offline then save the data
                    dbHandler.saveData();
                    dbHandler.removeData();
                }
                sender.sendMessage(Config.getMessage("objective_removed"));
                break;
            default:
                // if there was something else, display error message
                Debug.info("The argument was unknown");
                sender.sendMessage(Config.getMessage("unknown_argument"));
                break;
        }
    }

    /**
     * Reloads the configuration.
     */
    private void reloadPlugin() {
        // reload the configuration
        Debug.info("Reloading configuration");
        new Config();
        // load new static events
        new StaticEvents();
        // reload tellraw command executor
        new TellrawCommand();
        // stop current global locations listener
        // and start new one with reloaded configs
        Debug.info("Restarting global locations");
        GlobalLocations.stop();
        new GlobalLocations().runTaskTimer(instance, 0, 20);
        // load all events and conditions
        instance.loadData();
        // update journals for every online player
        for (Player player : Bukkit.getOnlinePlayers()) {
            String playerID = PlayerConverter.getID(player);
            Debug.info("Updating journal for player " + PlayerConverter.getName(playerID));
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
     */
    private void displayHelp(CommandSender sender, String alias) {
        Debug.info("Just displaying help");
        // specify all commands
        HashMap<String, String> cmds = new HashMap<>();
        cmds.put("reload", "reload");
        cmds.put("objectives", "objective <player> [list/add/del] [objective]");
        cmds.put("tags", "tag <player> [list/add/del] [tag]");
        cmds.put("points", "point <player> [list/add/del] [category] [amount]");
        cmds.put("journal", "journal <player> [list/add/del] [entry] [date]");
        cmds.put("condition", "condition <player> <condition>");
        cmds.put("event", "event <player> <event>");
        cmds.put("item", "item <name>");
        cmds.put("config", "config <read/set/add> <path> [string]");
        cmds.put("purge", "purge <player>");
        if (!(sender instanceof Player)) cmds.put("backup", "backup");
        // display them
        sender.sendMessage("§e----- §aBetonQuest §e-----");
        if (Config.getString("config.tellraw").equalsIgnoreCase("true") && sender instanceof Player) {
            for (String command : cmds.keySet()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
                        "tellraw " + sender.getName() + " {\"text\":\"\",\"extra\":[{\"text\":\""
                        + "§c/" + alias + " " + cmds.get(command)
                        + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\""
                        + "§b" + Config.getMessage("command_" + command) + "\"}}]}");
            }
        } else {
            for (String command : cmds.keySet()) {
                sender.sendMessage("§c/" + alias + " " + cmds.get(command));
                sender.sendMessage("§b- " + Config.getMessage("command_" + command));
            }
        }
    }
}
