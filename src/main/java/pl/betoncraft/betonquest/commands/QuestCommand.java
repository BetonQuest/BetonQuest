/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
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
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.ConditionID;
import pl.betoncraft.betonquest.EventID;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.ItemID;
import pl.betoncraft.betonquest.Journal;
import pl.betoncraft.betonquest.ObjectNotFoundException;
import pl.betoncraft.betonquest.ObjectiveID;
import pl.betoncraft.betonquest.Point;
import pl.betoncraft.betonquest.Pointer;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigAccessor;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.database.Connector.UpdateType;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.database.Saver.Record;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Updater;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Main admin command for quest editing.
 *
 * @author Jakub Sapalski
 */
public class QuestCommand implements CommandExecutor,SimpleTabCompleter {

	private BetonQuest instance = BetonQuest.getInstance();
	private String defaultPack = Config.getString("config.default_package");

	/**
	 * Registers a new executor and a new tab completer of the /betonquest command
	 */
	public QuestCommand() {
		BetonQuest.getInstance().getCommand("betonquest").setExecutor(this);
		BetonQuest.getInstance().getCommand("betonquest").setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {

		if (cmd.getName().equalsIgnoreCase("betonquest")) {
			Debug.info("Executing /betonquest command for user " + sender.getName() + " with arguments: "
					+ Arrays.toString(args));
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
			case "delete":
			case "del":
			case "d":
				Debug.info("Loading data asynchronously");
				final CommandSender finalSender5 = sender;
				final String[] finalArgs5 = args;
				new BukkitRunnable() {
					@Override
					public void run() {
						handleDeleting(finalSender5, finalArgs5);
					}
				}.runTaskAsynchronously(instance);
				break;
			case "rename":
			case "r":
				Debug.info("Loading data asynchronously");
				final CommandSender finalSender6 = sender;
				final String[] finalArgs6 = args;
				new BukkitRunnable() {
					@Override
					public void run() {
						handleRenaming(finalSender6, finalArgs6);
					}
				}.runTaskAsynchronously(instance);
				break;
			case "vector":
			case "vec":
			case "v":
				handleVector(sender, args);
				break;
			case "purge":
				Debug.info("Loading data asynchronously");
				final CommandSender finalSender7 = sender;
				final String[] finalArgs7 = args;
				new BukkitRunnable() {
					@Override
					public void run() {
						purgePlayer(finalSender7, finalArgs7);
					}
				}.runTaskAsynchronously(instance);
				break;
			case "update":
				Updater upd = BetonQuest.getInstance().getUpdater();
				if (args.length > 1 && args[1].equalsIgnoreCase("--dev")) {
					if (upd.updateDevBuild(sender)) {
						sender.sendMessage("§2Downloading development version " + upd.getRemoteDevBuild());
					} else {
						sender.sendMessage("§cThere is no new development version available.");
					}
				} else {
					if (upd.updateNewRelease(sender)) {
						sender.sendMessage("§2Downloading new release " + upd.getRemoteRelease());
					} else {
						sender.sendMessage("§cThere is no new release available.");
					}
				}
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
			default:
				// there was an unknown argument, so handle this
				sendMessage(sender, "unknown_argument");
				break;
			}
			Debug.info("Command executing done");
			return true;
		}
		return false;
	}

	@Override
	public List<String> simpleTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			return Arrays.asList(
					"reload",
					"objective",
					"tag",
					"point",
					"journal",
					"condition",
					"event",
					"item",
					"give",
					"rename",
					"delete",
					"config",
					"vector",
					"backup");
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
			case "v":
				return completeVector(sender, args);
			case "purge":
				if (args.length == 2) return null;
				else return new ArrayList<>();
			case "update":
				return completeUpdate(sender, args);
			case "reload":
			case "backup":
			case "create":
			case "package":
			default:
				return new ArrayList<>();
		}
	}

	/**
	 * Returns a list including all possible options for tab complete of the /betonquest update command
	 * @param sender
	 * @param args
	 * @return
	 */
	private List<String> completeUpdate(CommandSender sender, String[] args) {
		if (args.length == 2) return Arrays.asList("--dev");
		else return new ArrayList<>();
	}

	/**
	 * Returns a list of all packages for the tab completer
	 * @param sender
	 * @param args
	 * @return
	 */
	private List<String> completePackage(CommandSender sender, String[] args) {
		return new ArrayList<>(Config.getPackages().keySet());
	}

	/**
	 * Returns a list including all possible tab complete options for ids
	 * @param sender
	 * @param args
	 * @param type - the type of the Id (item/event/journal/condition/objective), null for unspecific
	 * @return
	 */
	private List<String> completeId(CommandSender sender, String[] args, ConfigAccessor.AccessorType type) {
		String last = args[args.length - 1];
		if (last == null || !last.contains(".")) {
			return completePackage(sender, args);
		} else {
			String pack = last.substring(0,last.indexOf("."));
			ConfigPackage configPack = Config.getPackages().get(pack);
			if (configPack == null) return new ArrayList<>();
			if (type == null) {
				List<String> completations = new ArrayList<>();
				completations.add(pack + ".");
				return completations;
			}
			ConfigAccessor accessor;
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
			FileConfiguration configuration = accessor.getConfig();
			List<String> completations = new ArrayList<>();
			for (String key : configuration.getKeys(false)) {
				completations.add(pack + "." + key);
			}
			return completations;
		}
	}

	/**
	 * Gives an item to the player
	 */
	private void giveItem(CommandSender sender, String[] args) {
		// sender must be a player
		if (!(sender instanceof Player)) {
			Debug.info("Cannot continue, sender must be player");
			return;
		}
		// and the item name must be specified
		if (args.length < 2) {
			Debug.info("Cannot continue, item's name must be supplied");
			sendMessage(sender, "specify_item");
			return;
		}
		try {
			ItemID itemID = new ItemID(null, args[1]);
			QuestItem item = new QuestItem(itemID);
			((Player) sender).getInventory().addItem(item.generate(1));
		} catch (InstructionParseException | ObjectNotFoundException e) {
			sendMessage(sender, "error", new String[]{e.getMessage()});
			Debug.error("Error while creating an item: " + e.getMessage());
		}
	}

	/**
	 * Creates new package
	 */
	private void createNewPackage(CommandSender sender, String[] args) {
		if (args.length < 2) {
			Debug.info("Package name is missing");
			sendMessage(sender, "specify_package");
			return;
		}
		if (Config.createPackage(args[1])) {
			sendMessage(sender, "package_created");
		} else {
			sendMessage(sender, "package_exists");
		}
	}

	/**
	 * Purges player's data
	 */
	private void purgePlayer(CommandSender sender, String[] args) {
		// playerID is required
		if (args.length < 2) {
			Debug.info("Player's name is missing");
			sendMessage(sender, "specify_player");
			return;
		}
		String playerID = PlayerConverter.getID(args[1]);
		PlayerData playerData = instance.getPlayerData(playerID);
		// if the player is offline then get his PlayerData outside of the
		// list
		if (playerData == null) {
			Debug.info("Player is offline, loading his data");
			playerData = new PlayerData(playerID);
		}
		// purge the player
		Debug.info("Purging player " + args[1]);
		playerData.purgePlayer();
		// done
		sendMessage(sender, "purged", new String[] { args[1] });
	}

	/**
	 * Reads, sets or appends strings from/to config files
	 */
	private void handleConfig(CommandSender sender, String[] args) {
		if (args.length < 3) {
			Debug.info("No action specified!");
			sendMessage(sender, "specify_action");
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
			if (args.length < 4) {
				sendMessage(sender, "config_set_error");
				return;
			}
			StringBuilder strBldr = new StringBuilder();
			for (int i = 3; i < args.length; i++) {
				strBldr.append(args[i] + " ");
			}
			if (strBldr.length() < 2) {
				Debug.info("Wrong path!");
				sendMessage(sender, "specify_path");
				return;
			}
			boolean set = Config.setString(path, (args[3].equalsIgnoreCase("null")) ? null : strBldr.toString().trim());
			if (set) {
				Debug.info("Displaying variable at path " + path);
				String message1 = Config.getString(path);
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
			StringBuilder strBldr2 = new StringBuilder();
			for (int i = 3; i < args.length; i++) {
				strBldr2.append(args[i] + " ");
			}
			if (strBldr2.length() < 2) {
				Debug.info("Wrong path!");
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
			boolean set2 = Config.setString(path, oldString + ((space) ? " " : "") + finalString);
			if (set2) {
				Debug.info("Displaying variable at path " + path);
				String message2 = Config.getString(path);
				sender.sendMessage(message2 == null ? "null" : message2);
			} else {
				sendMessage(sender, "config_set_error");
			}
			break;
		default:
			// if there was something else, display error message
			Debug.info("The argument was unknown");
			sendMessage(sender, "unknown_argument");
			break;
		}
	}

	/**
	 * Returns a list including all possible options for tab complete of the /betonquest config command
	 * @param sender
	 * @param args
	 * @return
	 */
	private List<String> completeConfig(CommandSender sender, String[] args) {
		if (args.length == 2) return Arrays.asList("set", "add" , "read");
		return new ArrayList<>();
	}

	/**
	 * Lists, adds or removes journal entries of certain players
	 */
	private void handleJournals(CommandSender sender, String[] args) {
		// playerID is required
		if (args.length < 2) {
			Debug.info("Player's name is missing");
			sendMessage(sender, "specify_player");
			return;
		}
		String playerID = PlayerConverter.getID(args[1]);
		PlayerData playerData = instance.getPlayerData(playerID);
		// if the player is offline then get his PlayerData outside of the
		// list
		if (playerData == null) {
			Debug.info("Player is offline, loading his data");
			playerData = new PlayerData(playerID);
		}
		Journal journal = playerData.getJournal();
		// if there are no arguments then list player's pointers
		if (args.length < 3 || args[2].equalsIgnoreCase("list") || args[2].equalsIgnoreCase("l")) {
			Debug.info("Listing journal pointers");
			sendMessage(sender, "player_journal");
			for (Pointer pointer : journal.getPointers()) {
				String date = new SimpleDateFormat(Config.getString("config.date_format"))
						.format(new Date(pointer.getTimestamp()));
				sender.sendMessage("§b- " + pointer.getPointer() + " §c(§2" + date + "§c)");
			}
			return;
		}
		// if there is not enough arguments, display warning
		if (args.length < 4) {
			Debug.info("Missing pointer");
			sendMessage(sender, "specify_pointer");
			return;
		}
		String pointerName = (args[3].contains(".")) ? args[3] : defaultPack + "." + args[3];
		// if there are arguments, handle them
		switch (args[2].toLowerCase()) {
		case "add":
		case "a":
			Pointer pointer;
			if (args.length < 5) {
				long timestamp = new Date().getTime();
				Debug.info("Adding pointer with current date: " + timestamp);
				pointer = new Pointer(pointerName, timestamp);
			} else {
				Debug.info("Adding pointer with date " + args[4].replaceAll("_", " "));
				try {
					pointer = new Pointer(pointerName, new SimpleDateFormat(Config.getString("config.date_format"))
							.parse(args[4].replaceAll("_", " ")).getTime());
				} catch (ParseException e) {
					Debug.info("Date was in the wrong format");
					sendMessage(sender, "specify_date");
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
			Debug.info("Removing pointer");
			journal.removePointer(pointerName);
			journal.update();
			sendMessage(sender, "pointer_removed");
			break;
		default:
			// if there was something else, display error message
			Debug.info("The argument was unknown");
			sendMessage(sender, "unknown_argument");
			break;
		}
	}

	/**
	 * Returns a list including all possible options for tab complete of the /betonquest journal command
	 * @param sender
	 * @param args
	 * @return
	 */
	private List<String> completeJournals(CommandSender sender, String[] args) {
		if (args.length == 2) return null;
		if (args.length == 3) return Arrays.asList("add", "list", "del");
		if (args.length == 4) return completeId(sender, args, ConfigAccessor.AccessorType.JOURNAL);
		return new ArrayList<>();
	}

	/**
	 * Lists, adds or removes points of certain player
	 */
	private void handlePoints(CommandSender sender, String[] args) {
		// playerID is required
		if (args.length < 2) {
			Debug.info("Player's name is missing");
			sendMessage(sender, "specify_player");
			return;
		}
		String playerID = PlayerConverter.getID(args[1]);
		PlayerData playerData = instance.getPlayerData(playerID);
		// if the player is offline then get his PlayerData outside of the
		// list
		if (playerData == null) {
			Debug.info("Player is offline, loading his data");
			playerData = new PlayerData(playerID);
		}
		// if there are no arguments then list player's points
		if (args.length < 3 || args[2].equalsIgnoreCase("list") || args[2].equalsIgnoreCase("l")) {
			List<Point> points = playerData.getPoints();
			Debug.info("Listing points");
			sendMessage(sender, "player_points");
			for (Point point : points) {
				sender.sendMessage("§b- " + point.getCategory() + "§e: §a" + point.getCount());
			}
			return;
		}
		// if there is not enough arguments, display warning
		if (args.length < 4) {
			Debug.info("Missing category");
			sendMessage(sender, "specify_category");
			return;
		}
		String category = args[3].contains(".") ? args[3] : defaultPack + "." + args[3];
		// if there are arguments, handle them
		switch (args[2].toLowerCase()) {
		case "add":
		case "a":
			if (args.length < 5 || !args[4].matches("-?\\d+")) {
				Debug.info("Missing amount");
				sendMessage(sender, "specify_amount");
				return;
			}
			// add the point
			Debug.info("Adding points");
			playerData.modifyPoints(category, Integer.parseInt(args[4]));
			sendMessage(sender, "points_added");
			break;
		case "remove":
		case "delete":
		case "del":
		case "r":
		case "d":
			// remove the point (this is unnecessary as adding negative amounts
			// subtracts points, but for the sake of users let's leave it here)
			Debug.info("Removing points");
			playerData.removePointsCategory(category);
			sendMessage(sender, "points_removed");
			break;
		default:
			// if there was something else, display error message
			Debug.info("The argument was unknown");
			sendMessage(sender, "unknown_argument");
			break;
		}
	}

	/**
	 * Returns a list including all possible options for tab complete of the /betonquest points command
	 * @param sender
	 * @param args
	 * @return
	 */
	private List<String> completePoints(CommandSender sender, String[] args) {
		if (args.length == 2) return null;
		if (args.length == 3) return Arrays.asList("add", "list", "del");
		if (args.length == 4) return completeId(sender, args, null);
		return new ArrayList<>();
	}

	/**
	 * Adds item held in hand to items.yml file
	 */
	@SuppressWarnings("deprecation")
    private void handleItems(CommandSender sender, String[] args) {
		// sender must be a player
		if (!(sender instanceof Player)) {
			Debug.info("Cannot continue, sender must be player");
			return;
		}
		// and the item name must be specified
		if (args.length < 2) {
			Debug.info("Cannot continue, item's name must be supplied");
			sendMessage(sender, "specify_item");
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
			pack = defaultPack;
			name = itemID;
		}
		Player player = (Player) sender;
		ItemStack item = null;
		try {
		    item = player.getInventory().getItemInMainHand();
		} catch (LinkageError e) {
		    item = player.getItemInHand();
		}
		// if item is air then there is nothing to add to items.yml
		if (item == null || item.getType() == Material.AIR) {
			Debug.info("Cannot continue, item must not be air");
			sendMessage(sender, "no_item");
			return;
		}
		// define parts of the final string
		ConfigPackage configPack = Config.getPackages().get(pack);
		if (configPack == null) {
			Debug.info("Cannot continue, package does not exist");
			sendMessage(sender, "specify_package");
			return;
		}
		ConfigAccessor config = configPack.getItems();
		String instructions = QuestItem.itemToString(item);
		// save it in items.yml
		Debug.info("Saving item to configuration as " + args[1]);
		config.getConfig().set(name, instructions.trim());
		config.saveConfig();
		// done
		sendMessage(sender, "item_created", new String[] { args[1] });

	}

	/**
	 * Returns a list including all possible options for tab complete of the /betonquest item command
	 * @param sender
	 * @param args
	 * @return
	 */
	private List<String> completeItems(CommandSender sender, String[] args) {
		if (args.length == 2) return completeId(sender, args, ConfigAccessor.AccessorType.ITEMS);
		return new ArrayList<>();
	}

	/**
	 * Fires an event for an online player. It cannot work for offline players!
	 */
	private void handleEvents(CommandSender sender, String[] args) {
		// the player has to be specified every time
		if (args.length < 2 || (Bukkit.getPlayer(args[1]) == null && !args[1].equals("-"))) {
			Debug.info("Player's name is missing or he's offline");
			sendMessage(sender, "specify_player");
			return;
		}
		String playerID = (args[1].equals("-")) ? null : PlayerConverter.getID(args[1]);
		if (args.length < 3) {
			Debug.info("Event's ID is missing");
			sendMessage(sender, "specify_event");
			return;
		}
		EventID eventID;
		try {
			eventID = new EventID(null, args[2]);
		} catch (ObjectNotFoundException e) {
			sendMessage(sender, "error", new String[]{ e.getMessage() });
			return;
		}
		// fire the event
		BetonQuest.event(playerID, eventID);
		sendMessage(sender, "player_event", new String[] { eventID.generateInstruction().getInstruction() });
	}

	/**
	 * Returns a list including all possible options for tab complete of the /betonquest event command
	 * @param sender
	 * @param args
	 * @return
	 */
	private List<String> completeEvents(CommandSender sender, String[] args) {
		if (args.length == 2) return null;
		if (args.length == 3) return completeId(sender, args, ConfigAccessor.AccessorType.EVENTS);
		return new ArrayList<>();
	}

	/**
	 * Checks if specified player meets condition described by ID
	 */
	private void handleConditions(CommandSender sender, String[] args) {
		// the player has to be specified every time
		if (args.length < 2 || (Bukkit.getPlayer(args[1]) == null && !args[1].equals("-"))) {
			Debug.info("Player's name is missing or he's offline");
			sendMessage(sender, "specify_player");
			return;
		}
		String playerID = (args[1].equals("-")) ? null : PlayerConverter.getID(args[1]);
		// the condition ID
		if (args.length < 3) {
			Debug.info("Condition's ID is missing");
			sendMessage(sender, "specify_condition");
			return;
		}
		ConditionID conditionID;
		try {
			conditionID = new ConditionID(null, args[2]);
		} catch (ObjectNotFoundException e) {
			sendMessage(sender, "error", new String[]{ e.getMessage() });
			return;
		}
		// display message about condition
		sendMessage(sender, "player_condition", new String[] { (conditionID.inverted() ? "! " : "")
				+ conditionID.generateInstruction().getInstruction(),
				Boolean.toString(BetonQuest.condition(playerID, conditionID)) });
	}

	/**
	 * Returns a list including all possible options for tab complete of the /betonquest condition command
	 * @param sender
	 * @param args
	 * @return
	 */
	private List<String> completeConditions(CommandSender sender, String[] args) {
		if (args.length == 2) return null;
		if (args.length == 3) return completeId(sender, args, ConfigAccessor.AccessorType.CONDITIONS);
		return new ArrayList<>();
	}

	/**
	 * Lists, adds or removes tags
	 */
	private void handleTags(CommandSender sender, String[] args) {
		// playerID is required
		if (args.length < 2) {
			Debug.info("Player's name is missing");
			sendMessage(sender, "specify_player");
			return;
		}
		String playerID = PlayerConverter.getID(args[1]);
		PlayerData playerData = instance.getPlayerData(playerID);
		// if the player is offline then get his PlayerData outside of the
		// list
		if (playerData == null) {
			Debug.info("Player is offline, loading his data");
			playerData = new PlayerData(playerID);
		}
		// if there are no arguments then list player's tags
		if (args.length < 3 || args[2].equalsIgnoreCase("list") || args[2].equalsIgnoreCase("l")) {
			List<String> tags = playerData.getTags();
			Debug.info("Listing tags");
			sendMessage(sender, "player_tags");
			for (String tag : tags) {
				sender.sendMessage("§b- " + tag);
			}
			return;
		}
		// if there is not enough arguments, display warning
		if (args.length < 4) {
			Debug.info("Missing tag name");
			sendMessage(sender, "specify_tag");
			return;
		}
		String tag = args[3].contains(".") ? args[3] : defaultPack + "." + args[3];
		// if there are arguments, handle them
		switch (args[2].toLowerCase()) {
		case "add":
		case "a":
			// add the tag
			Debug.info("Adding tag " + tag + " for player " + PlayerConverter.getName(playerID));
			playerData.addTag(tag);
			sendMessage(sender, "tag_added");
			break;
		case "remove":
		case "delete":
		case "del":
		case "r":
		case "d":
			// remove the tag
			Debug.info("Removing tag " + tag + " for player " + PlayerConverter.getName(playerID));
			playerData.removeTag(tag);
			sendMessage(sender, "tag_removed");
			break;
		default:
			// if there was something else, display error message
			Debug.info("The argument was unknown");
			sendMessage(sender, "unknown_argument");
			break;
		}
	}

	/**
	 * Returns a list including all possible options for tab complete of the /betonquest tags command
	 * @param sender
	 * @param args
	 * @return
	 */
	private List<String> completeTags(CommandSender sender, String[] args) {
		if (args.length == 2) return null;
		if (args.length == 3) return Arrays.asList("list", "add", "del");
		if (args.length == 4) return completeId(sender, args, null);
		return new ArrayList<>();
	}

	/**
	 * Lists, adds or removes objectives.
	 */
	private void handleObjectives(CommandSender sender, String[] args) {
		// playerID is required
		if (args.length < 2) {
			Debug.info("Player's name is missing");
			sendMessage(sender, "specify_player");
			return;
		}
		String playerID = PlayerConverter.getID(args[1]);
		boolean isOnline = !(PlayerConverter.getPlayer(playerID) == null);
		PlayerData playerData = instance.getPlayerData(playerID);
		// if the player is offline then get his PlayerData outside of the
		// list
		if (playerData == null) {
			Debug.info("Player is offline, loading his data");
			playerData = new PlayerData(playerID);
		}
		// if there are no arguments then list player's objectives
		if (args.length < 3 || args[2].equalsIgnoreCase("list") || args[2].equalsIgnoreCase("l")) {
			List<String> tags;
			if (!isOnline) {
				// if player is offline then convert his raw objective strings
				// to tags
				tags = new ArrayList<>();
				for (String string : playerData.getRawObjectives().keySet()) {
					tags.add(string);
				}
			} else {
				// if the player is online then just retrieve tags from his
				// active
				// objectives
				tags = new ArrayList<>();
				for (Objective objective : BetonQuest.getInstance().getPlayerObjectives(playerID)) {
					tags.add(objective.getLabel());
				}
			}
			// display objectives
			Debug.info("Listing objectives");
			sendMessage(sender, "player_objectives");
			for (String tag : tags) {
				sender.sendMessage("§b- " + tag);
			}
			return;
		}
		// if there is not enough arguments, display warning
		if (args.length < 4) {
			Debug.info("Missing objective instruction string");
			sendMessage(sender, "specify_objective");
			return;
		}
		// get the objective
		ObjectiveID objectiveID;
		try {
			objectiveID = new ObjectiveID(null, args[3]);
		} catch (ObjectNotFoundException e) {
			sendMessage(sender, "error", new String[]{ e.getMessage() });
			return;
		}
		Objective objective = BetonQuest.getInstance().getObjective(objectiveID);
		if (objective == null) {
			sendMessage(sender, "specify_objective");
			return;
		}
		switch (args[2].toLowerCase()) {
		case "start":
		case "s":
		case "add":
		case "a":
			Debug.info("Adding new objective " + objectiveID + " for player " + PlayerConverter.getName(playerID));
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
			Debug.info("Deleting objective " + objectiveID + " for player " + PlayerConverter.getName(playerID));
			objective.removePlayer(playerID);
			playerData.removeRawObjective(objectiveID);
			sendMessage(sender, "objective_removed");
			break;
		case "complete":
		case "c":
			Debug.info("Completing objective " + objectiveID + " for player " + PlayerConverter.getName(playerID));
			if (isOnline) {
				objective.completeObjective(playerID);
			} else {
				playerData.removeRawObjective(objectiveID);
			}
			sendMessage(sender, "objective_completed");
			break;
		default:
			// if there was something else, display error message
			Debug.info("The argument was unknown");
			sendMessage(sender, "unknown_argument");
			break;
		}
	}

	/**
	 * Returns a list including all possible options for tab complete of the /betonquest objectives command
	 * @param sender
	 * @param args
	 * @return
	 */
	private List<String> completeObjectives(CommandSender sender, String[] args) {
		if (args.length == 2) return null;
		if (args.length == 3) return Arrays.asList("list", "add", "del", "complete");
		if (args.length == 4) return completeId(sender, args, ConfigAccessor.AccessorType.OBJECTIVES);
		return new ArrayList<>();
	}

	/**
	 * Creates a vector variable
	 *
	 * @param sender
	 * @param args
	 */
	private void handleVector(CommandSender sender, String[] args) {
		if (!(sender instanceof Player))
			return;
		Player player = ((Player) sender);
		if (args.length != 3) {
			player.sendMessage("§4ERROR");
			return;
		}
		String pack = null, name = null;
		if (args[1].contains(".")) {
			String[] parts = args[1].split("\\.");
			pack = parts[0];
			name = parts[1];
		} else {
			pack = defaultPack;
			name = args[1];
		}
		String origin = Config.getString(pack + ".main.variables." + name);
		if (origin == null) {
			player.sendMessage("§4ERROR");
			return;
		}
		String[] parts = origin.split(";");
		if (parts.length < 3) {
			player.sendMessage("§4ERROR");
			return;
		}
		double x, y, z;
		try {
			x = Double.parseDouble(parts[0]);
			y = Double.parseDouble(parts[1]);
			z = Double.parseDouble(parts[2]);
		} catch (NumberFormatException e) {
			player.sendMessage("§4ERROR");
			return;
		}
		Location loc = player.getLocation();
		x = loc.getX() - x;
		y = loc.getY() - y;
		z = loc.getZ() - z;
		Config.setString(pack + ".main.variables.vectors." + args[2],
				String.format("$%s$->(%.2f,%.2f,%.2f)", name, x, y, z));
		player.sendMessage("§2OK");
	}

	/**
	 * Returns a list including all possible options for tab complete of the /betonquest vector command
	 * @param sender
	 * @param args
	 * @return
	 */
	private List<String> completeVector(CommandSender sender, String[] args) {
		if (args.length == 2) {
			if (args[1] == null || !args[1].contains(".")) {
				return completePackage(sender, args);
			}
			String pack = args[1].substring(0,args[1].indexOf("."));
			ConfigPackage configPack = Config.getPackages().get(pack);
			if (configPack == null) return new ArrayList<>();
			ConfigurationSection section = configPack.getMain().getConfig().getConfigurationSection("variables");
			Collection<String> keys = section.getKeys(false);
			if (keys.isEmpty()) return new ArrayList<>();
			return new ArrayList<>(keys);
		}
		return new ArrayList<>();
	}

	/**
	 * Renames stuff.
	 */
	private void handleRenaming(CommandSender sender, String[] args) {
		if (args.length < 4) {
			sendMessage(sender, "arguments");
			return;
		}
		String type = args[1].toLowerCase(), name = args[2], rename = args[3];
		if (!name.contains(".")) {
			name = defaultPack + "." + name;
		}
		if (!rename.contains(".")) {
			rename = defaultPack + "." + rename;
		}
		UpdateType updateType;
		switch (type) {
		case "tags":
		case "tag":
		case "t":
			updateType = UpdateType.RENAME_ALL_TAGS;
			for (Player player : Bukkit.getOnlinePlayers()) {
				PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
				playerData.removeTag(name);
				playerData.addTag(rename);
			}
			break;
		case "points":
		case "point":
		case "p":
			updateType = UpdateType.RENAME_ALL_POINTS;
			for (Player player : Bukkit.getOnlinePlayers()) {
				PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
				int points = 0;
				for (Point point : playerData.getPoints()) {
					if (point.getCategory().equals(name)) {
						points = point.getCount();
						break;
					}
				}
				playerData.removePointsCategory(name);
				playerData.modifyPoints(rename, points);
			}
			break;
		case "objectives":
		case "objective":
		case "o":
			updateType = UpdateType.RENAME_ALL_OBJECTIVES;
			// get ID and package
			ObjectiveID nameID;
			try {
				nameID = new ObjectiveID(null, name);
			} catch (ObjectNotFoundException e) {
				sendMessage(sender, "error", new String[]{ e.getMessage() });
				return;
			}
			// rename objective in the file
			nameID.getPackage().getObjectives().getConfig().set(rename.split("\\.")[1], nameID.generateInstruction().getInstruction());
			nameID.getPackage().getObjectives().saveConfig();
			// rename objective instance
			ObjectiveID renameID;
			try {
				renameID = new ObjectiveID(null, rename);
			} catch (ObjectNotFoundException e) {
				// this should not happen
				e.printStackTrace();
				return;
			}
			BetonQuest.getInstance().renameObjective(nameID, renameID);
			BetonQuest.getInstance().getObjective(renameID).setLabel(renameID);
			// renaming an active objective probably isn't needed
			for (Player player : Bukkit.getOnlinePlayers()) {
				String playerID = PlayerConverter.getID(player);
				boolean found = false;
				String data = null;
				for (Objective obj : BetonQuest.getInstance().getPlayerObjectives(playerID)) {
					if (obj.getLabel().equals(name)) {
						found = true;
						data = obj.getData(PlayerConverter.getID(player));
						break;
					}
				}
				// skip the player if he does not have this objective
				if (found == false)
					continue;
				if (data == null)
					data = "";
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
			for (Player player : Bukkit.getOnlinePlayers()) {
				Journal journal = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player)).getJournal();
				Pointer p = null;
				for (Pointer pointer : journal.getPointers()) {
					if (pointer.getPointer().equals(name)) {
						p = pointer;
					}
				}
				// skip the player if he does not have this entry
				if (p == null)
					continue;
				journal.removePointer(name);
				journal.addPointer(new Pointer(rename, p.getTimestamp()));
				journal.update();
			}
			break;
		default:
			sendMessage(sender, "unknown_argument");
			return;
		}
		BetonQuest.getInstance().getSaver().add(new Record(updateType, new String[] { rename, name }));
		sendMessage(sender, "everything_renamed");
	}

	/**
	 * Returns a list including all possible options for tab complete of the /betonquest rename command
	 * @param sender
	 * @param args
	 * @return
	 */
	private List<String> completeRenaming(CommandSender sender, String[] args) {
		if (args.length <= 3) return completeDeleting(sender, args);
		if (args.length == 4) return completeId(sender, args, null);
		return new ArrayList<>();
	}

	/**
	 * Deleted stuff.
	 */
	private void handleDeleting(CommandSender sender, String[] args) {
		if (args.length < 3) {
			sendMessage(sender, "arguments");
			return;
		}
		String type = args[1].toLowerCase(), name = args[2];
		if (!name.contains(".")) {
			name = defaultPack + "." + name;
		}
		UpdateType updateType;
		switch (type) {
		case "tags":
		case "tag":
		case "t":
			updateType = UpdateType.REMOVE_ALL_TAGS;
			for (Player player : Bukkit.getOnlinePlayers()) {
				PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
				playerData.removeTag(name);
			}
			break;
		case "points":
		case "point":
		case "p":
			updateType = UpdateType.REMOVE_ALL_POINTS;
			for (Player player : Bukkit.getOnlinePlayers()) {
				PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
				playerData.removePointsCategory(name);
			}
			break;
		case "objectives":
		case "objective":
		case "o":
			updateType = UpdateType.REMOVE_ALL_OBJECTIVES;
			ObjectiveID objectiveID;
			try {
				objectiveID = new ObjectiveID(null, name);
			} catch (ObjectNotFoundException e) {
				sendMessage(sender, "error", new String[]{ e.getMessage() });
				return;
			}
			for (Player player : Bukkit.getOnlinePlayers()) {
				String playerID = PlayerConverter.getID(player);
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
			for (Player player : Bukkit.getOnlinePlayers()) {
				Journal journal = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player)).getJournal();
				journal.removePointer(name);
				journal.update();
			}
			break;
		default:
			sendMessage(sender, "unknown_argument");
			return;
		}
		BetonQuest.getInstance().getSaver().add(new Record(updateType, new String[] { name }));
		sendMessage(sender, "everything_removed");
	}

	/**
	 * Returns a list including all possible options for tab complete of the /betonquest delete command
	 * @param sender
	 * @param args
	 * @return
	 */
	private List<String> completeDeleting(CommandSender sender, String[] args) {
		if (args.length == 2) return Arrays.asList("tag", "point", "objective", "entry");
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
		cmds.put("give", "give <name>");
		cmds.put("rename", "rename <tag/point/objective/journal> <old> <new>");
		cmds.put("delete", "delete <tag/point/objective/journal> <name>");
		cmds.put("config", "config <read/set/add> <path> [string]");
		cmds.put("vector", "vector <pack.varname> <vectorname>");
		cmds.put("purge", "purge <player>");
		if (!(sender instanceof Player))
			cmds.put("backup", "backup");
		// display them
		sender.sendMessage("§e----- §aBetonQuest §e-----");
		if (sender instanceof Player) {
			String lang = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID((Player) sender)).getLanguage();
			for (String command : cmds.keySet()) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						"tellraw " + sender.getName() + " {\"text\":\"\",\"extra\":[{\"text\":\"§c/" + alias + " "
								+ cmds.get(command) + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§b"
								+ Config.getMessage(lang, "command_" + command) + "\"}}]}");
			}
		} else {
			for (String command : cmds.keySet()) {
				sender.sendMessage("§c/" + alias + " " + cmds.get(command));
				sender.sendMessage("§b- " + Config.getMessage(Config.getLanguage(), "command_" + command));
			}
		}
	}

	private void sendMessage(CommandSender sender, String messageName) {
		sendMessage(sender, messageName, null);
	}

	private void sendMessage(CommandSender sender, String messageName, String[] variables) {
		if (sender instanceof Player) {
			Config.sendMessage(PlayerConverter.getID((Player) sender), messageName, variables);
		} else {
			String message = Config.getMessage(Config.getLanguage(), messageName, variables);
			sender.sendMessage(message);
		}
	}
}
