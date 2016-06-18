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
package pl.betoncraft.betonquest.conversation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.ConversationOptionEvent;
import pl.betoncraft.betonquest.api.PlayerConversationEndEvent;
import pl.betoncraft.betonquest.api.PlayerConversationStartEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.conversation.ConversationData.OptionType;
import pl.betoncraft.betonquest.conversation.ConversationData.RequestType;
import pl.betoncraft.betonquest.database.Connector.UpdateType;
import pl.betoncraft.betonquest.database.Saver.Record;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Represents a conversation between player and NPC
 * 
 * @author Jakub Sapalski
 */
public class Conversation implements Listener {

	private static ConcurrentHashMap<String, Conversation> list = new ConcurrentHashMap<>();

	private final String playerID;
	private final Player player;
	private final String packName;
	private final String language;
	private ConversationData data;
	private final Location location;
	private final String convID;
	private final List<String> blacklist;
	private ConversationIO inOut;
	private String option;
	private final Conversation conv;
	private final BetonQuest plugin;
	private boolean ended = false;
	private boolean messagesDelaying = false;
	private ArrayList<String> messages = new ArrayList<>();

	private HashMap<Integer, String> current = new HashMap<>();


	/**
	 * Starts a new conversation between player and npc at given location.
	 * 
	 * @param playerID
	 *            ID of the player
	 * @param packName
	 *            name of the package in which this conversation is defined
	 * @param conversationID
	 *            name of the conversation
	 * @param location
	 *            location where the conversation has been started
	 */
	public Conversation(String playerID, String packName, String conversationID, Location location) {
		this(playerID, packName, conversationID, location, null);
	}

	/**
	 * Starts a new conversation between player and npc at given location,
	 * starting with the given option. If the option is null, then it will start
	 * rom the beginning.
	 * 
	 * @param playerID
	 *            ID of the player
	 * @param packName
	 *            name of the package in which this conversation is defined
	 * @param conversationID
	 *            name of the conversation
	 * @param location
	 *            location where the conversation has been started
	 * @param option
	 *            ID of the option from where to start
	 */
	public Conversation(final String playerID, final String packName, final String conversationID,
			final Location location, String option) {

		this.conv = this;
		this.plugin = BetonQuest.getInstance();
		this.playerID = playerID;
		this.player = PlayerConverter.getPlayer(playerID);
		this.packName = packName;
		this.language = plugin.getPlayerData(playerID).getLanguage();
		this.location = location;
		this.convID = packName + "." + conversationID;
		this.data = plugin.getConversation(convID);
		this.blacklist = plugin.getConfig().getStringList("cmd_blacklist");
		this.messagesDelaying = plugin.getConfig().getString("display_chat_after_conversation").equalsIgnoreCase("true");

		// check if data is present
		if (data == null) {
			Debug.error("Conversation doesn't exist: " + packName + "." + conversationID);
			return;
		}

		// if the player has active conversation, terminate this one
		if (list.containsKey(playerID)) {
			Debug.info("Player " + PlayerConverter.getName(playerID) + " is in conversation right now, returning.");
			return;
		}

		// add the player to the list of active conversations
		list.put(playerID, conv);

		String[] options;
		if (option == null) {
			options = null;
		} else {
			if (!option.contains("."))
				option = conversationID + "." + option;
			options = new String[] { option };
		}

		new Starter(options).runTaskAsynchronously(plugin);
	}

	/**
	 * Chooses the first available option.
	 * 
	 * @param options
	 *            list of option pointers separated by commas
	 * @param force
	 *            setting it to true will force the first option, even if
	 *            conditions are not met
	 */
	private void selectOption(String[] options, boolean force) {

		if (force) {
			options = new String[] { options[0] };
		}
		// get npc's text
		option = null;
		options: for (String option : options) {
			String convName, optionName;
			if (option.contains(".")) {
				String[] parts = option.split("\\.");
				convName = parts[0];
				optionName = parts[1];
			} else {
				convName = data.getName();
				optionName = option;
			}
			ConversationData currentData = plugin.getConversation(packName + "." + convName);
			if (!force)
				for (String condition : currentData.getData(optionName, OptionType.NPC, RequestType.CONDITION)) {
					if (!BetonQuest.condition(this.playerID, condition)) {
						continue options;
					}
				}
			this.option = optionName;
			data = currentData;
			break;
		}
	}

	/**
	 * Sends to the player the text said by NPC. It uses the selected option and
	 * displays it. Note: this method now requires a prior call to
	 * selectOption()
	 */
	private void printNPCText() {

		// if there are no possible options, end conversation
		if (option == null) {
			new ConversationEnder().runTask(plugin);
			return;
		}
		String text = data.getText(language, option, OptionType.NPC);
		// resolve variables
		for (String variable : BetonQuest.resolveVariables(text)) {
			text = text.replace(variable, plugin.getVariableValue(data.getPackName(), variable, playerID));
		}
		// print option to the player
		inOut.setNpcResponse(data.getQuester(language), text);

		new NPCEventRunner(option).runTask(plugin);
	}

	/**
	 * Passes given string as answer from player in a conversation.
	 * 
	 * @param number
	 *            the message player has sent on chat
	 */
	public void passPlayerAnswer(int number) {

		inOut.clear();

		new PlayerEventRunner(current.get(number)).runTask(plugin);

		// clear hashmap
		current.clear();
	}

	/**
	 * Prints answers the player can choose.
	 * 
	 * @param options
	 *            list of pointers to player options separated by commas
	 */
	private void printOptions(String[] options) {
		// i is for counting replies, like 1. something, 2. something else
		int i = 0;
		answers: for (String option : options) {
			for (String condition : data.getData(option, OptionType.PLAYER, RequestType.CONDITION)) {
				if (!BetonQuest.condition(playerID, condition)) {
					continue answers;
				}
			}
			i++;
			// print reply and put it to the hashmap
			current.put(Integer.valueOf(i), option);
			// replace variables with their values
			String text = data.getText(language, option, OptionType.PLAYER);
			for (String variable : BetonQuest.resolveVariables(text)) {
				text = text.replace(variable, plugin.getVariableValue(data.getPackName(), variable, playerID));
			}
			inOut.addPlayerOption(text);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				inOut.display();
			}
		}.runTask(plugin);
		// end conversations if there are no possible options
		if (current.isEmpty()) {
			new ConversationEnder().runTask(plugin);
			return;
		}
	}

	/**
	 * Ends conversation, firing final events and removing it from the list of
	 * active conversations
	 */
	public void endConversation() {
		if (ended)
			return;
		ended = true;
		inOut.end();
		// fire final events
		for (String event : data.getFinalEvents()) {
			BetonQuest.event(playerID, event);
		}
		// print message
		Config.sendMessage(playerID, "conversation_end", new String[] { data.getQuester(language) }, "end");
		// delete conversation
		list.remove(playerID);
		HandlerList.unregisterAll(this);
		displayStoredMessages();
		Bukkit.getServer().getPluginManager().callEvent(new PlayerConversationEndEvent(player, this));
	}

	private void displayStoredMessages() {
		for (String message : messages) {
			player.sendMessage(message);
		}
	}

	/**
	 * Checks if the movement of the player should be blocked.
	 * 
	 * @return true if the movement should be blocked, false otherwise
	 */
	public boolean isMovementBlock() {
		return data.isMovementBlocked();
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (!event.getPlayer().equals(player)) {
			return;
		}
		if (event.getMessage() == null)
			return;
		String cmdName = event.getMessage().split(" ")[0].substring(1);
		if (blacklist.contains(cmdName)) {
			event.setCancelled(true);
			Config.sendMessage(PlayerConverter.getID(event.getPlayer()), "command_blocked");
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		// prevent damage to (or from) player while in conversation
		if ((event.getEntity() instanceof Player && PlayerConverter.getID((Player) event.getEntity()).equals(playerID))
				|| (event.getDamager() instanceof Player
						&& PlayerConverter.getID((Player) event.getDamager()).equals(playerID))) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		// if player quits, end conversation (why keep listeners running?)
		if (event.getPlayer().equals(player)) {
			if (isMovementBlock()) {
				suspend();
			} else {
				endConversation();
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent event) {
		// store all messages so they can be displayed to the player
		// once the conversation is finished
		if (!messagesDelaying) {
			return;
		}
		if (event.isCancelled()) {
			return;
		}
		if (event.getPlayer() != player && event.getRecipients().contains(player)) {
			event.getRecipients().remove(player);
			addMessage(String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage()));
		}
	}

	/**
	 * This method prevents concurrent list modification
	 */
	private synchronized void addMessage(String message) {
		messages.add(message);
	}

	/**
	 * Instead of ending the conversation it saves it to the database, from
	 * where it will be resumed after the player logs in again.
	 */
	public void suspend() {
		inOut.end();
		// save the conversation to the database
		String loc = location.getX() + ";" + location.getY() + ";" + location.getZ() + ";"
				+ location.getWorld().getName();
		plugin.getSaver().add(new Record(UpdateType.UPDATE_CONVERSATION,
				new String[] { convID + " " + option + " " + loc, playerID }));
		// delete conversation
		list.remove(playerID);
		HandlerList.unregisterAll(this);
		Bukkit.getServer().getPluginManager().callEvent(new PlayerConversationEndEvent(player, this));
	}

	/**
	 * Checks if the player is in a conversation
	 * 
	 * @param playerID
	 *            ID of the player
	 * @return if the player is on the list of active conversations
	 */
	public static boolean containsPlayer(String playerID) {
		return list.containsKey(playerID);
	}

	/**
	 * Gets this player's active conversation.
	 * 
	 * @param playerID
	 *            ID of the player
	 * @return player's active conversation or null if there is no conversation
	 */
	public static Conversation getConversation(String playerID) {
		return list.get(playerID);
	}

	/**
	 * @return the location where the conversation has been started
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @return the ConversationIO object used by this conversation
	 */
	public ConversationIO getIO() {
		return inOut;
	}

	/**
	 * @return the data of the conversation
	 */
	public ConversationData getData() {
		return data;
	}
	
	/**
	 * @return the package containing this conversation
	 */
	public String getPackage() {
		return packName;
	}
	
	/**
	 * @return the ID of the conversation
	 */
	public String getID() {
		return convID;
	}

	/**
	 * Starts the conversation, should be called asynchronously.
	 * 
	 * @author Jakub Sapalski
	 */
	private class Starter extends BukkitRunnable {

		private String[] options;

		public Starter(String[] options) {
			this.options = options;
		}

		public void run() {
			// the conversation start event must be run on next tick
			PlayerConversationStartEvent event = new PlayerConversationStartEvent(player, conv);
			Bukkit.getServer().getPluginManager().callEvent(event);

			// stop the conversation if it's canceled
			if (event.isCancelled())
				return;

			// now the conversation should start no matter what;
			// the inOut can be safely instantiated; doing it before
			// would leave it active while the conversation is not
			// started, causing it to display "null" all the time
			try {
				String name = plugin.getConfig().getString("default_conversation_IO");
				Class<? extends ConversationIO> c = plugin.getConvIO(name);
				if (c == null) {
					Debug.error("Conversation IO " + name + " is not registered!");
					return;
				}
				conv.inOut = c.getConstructor(Conversation.class, String.class).newInstance(conv, playerID);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				Debug.error("Error when loading conversation IO");
				return;
			}

			// register listener for immunity, blocking commands and storing chat messages
			Bukkit.getPluginManager().registerEvents(conv, BetonQuest.getInstance());

			if (options == null) {
				options = data.getStartingOptions();

				// first select the option before sending message, so it
				// knows which is used
				selectOption(options, false);

				// check whether to add a prefix
				String prefix = data.getPrefix(language, option);
				String prefixName = null;
				String[] prefixVariables = null;
				if (prefix != null) {
					prefixName = "conversation_prefix";
					prefixVariables = new String[] { prefix };
				}

				// print message about starting a conversation only if it
				// is started, not resumed
				Config.sendMessage(playerID, "conversation_start", new String[] { data.getQuester(language) }, "start",
						prefixName, prefixVariables);
			} else {
				// don't forget to select the option prior to printing its text
				selectOption(options, true);
			}

			// print NPC's text
			printNPCText();
			ConversationOptionEvent e = new ConversationOptionEvent(player, conv, option, conv.option);
			Bukkit.getPluginManager().callEvent(e);
		}
	}

	/**
	 * Fires events from the option. Should be called in the main thread.
	 * 
	 * @author Jakub Sapalski
	 */
	private class NPCEventRunner extends BukkitRunnable {

		private String option;

		public NPCEventRunner(String option) {
			this.option = option;
		}

		public void run() {
			// fire events
			for (String event : data.getData(option, OptionType.NPC, RequestType.EVENT)) {
				BetonQuest.event(playerID, event);
			}
			new OptionPrinter(option).runTaskAsynchronously(plugin);
		}
	}

	/**
	 * Fires events from the option. Should be called in the main thread.
	 * 
	 * @author Jakub Sapalski
	 */
	private class PlayerEventRunner extends BukkitRunnable {

		private String option;

		public PlayerEventRunner(String option) {
			this.option = option;
		}

		public void run() {
			// fire events
			for (String event : data.getData(option, OptionType.PLAYER, RequestType.EVENT)) {
				BetonQuest.event(playerID, event);
			}
			new ResponsePrinter(option).runTaskAsynchronously(plugin);
		}
	}

	/**
	 * Prints the NPC response to the player. Should be called asynchronously.
	 * 
	 * @author Jakub Sapalski
	 */
	private class ResponsePrinter extends BukkitRunnable {

		private String option;

		public ResponsePrinter(String option) {
			this.option = option;
		}

		public void run() {
			// don't forget to select the option prior to printing its text
			selectOption(data.getData(option, OptionType.PLAYER, RequestType.POINTER), false);
			// print to player npc's answer
			printNPCText();
			ConversationOptionEvent event = new ConversationOptionEvent(player, conv, option, conv.option);
			Bukkit.getPluginManager().callEvent(event);
		}
	}

	/**
	 * Prints the options to the player. Should be called asynchronously.
	 * 
	 * @author Jakub Sapalski
	 */
	private class OptionPrinter extends BukkitRunnable {

		private String option;

		public OptionPrinter(String option) {
			this.option = option;
		}

		public void run() {
			// print options
			printOptions(data.getData(option, OptionType.NPC, RequestType.POINTER));
		}
	}

	/**
	 * Ends the conversation. Should be called in the main thread.
	 * 
	 * @author Jakub Sapalski
	 */
	private class ConversationEnder extends BukkitRunnable {
		public void run() {
			endConversation();
		}
	}
}
