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
package pl.betoncraft.betonquest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.commands.BackpackCommand;
import pl.betoncraft.betonquest.commands.CancelQuestCommand;
import pl.betoncraft.betonquest.commands.CompassCommand;
import pl.betoncraft.betonquest.commands.JournalCommand;
import pl.betoncraft.betonquest.commands.LangCommand;
import pl.betoncraft.betonquest.commands.QuestCommand;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.conditions.AchievementCondition;
import pl.betoncraft.betonquest.conditions.AlternativeCondition;
import pl.betoncraft.betonquest.conditions.ArmorCondition;
import pl.betoncraft.betonquest.conditions.ArmorRatingCondition;
import pl.betoncraft.betonquest.conditions.CheckCondition;
import pl.betoncraft.betonquest.conditions.ChestItemCondition;
import pl.betoncraft.betonquest.conditions.ConjunctionCondition;
import pl.betoncraft.betonquest.conditions.EffectCondition;
import pl.betoncraft.betonquest.conditions.EmptySlotsCondition;
import pl.betoncraft.betonquest.conditions.ExperienceCondition;
import pl.betoncraft.betonquest.conditions.GameModeCondition;
import pl.betoncraft.betonquest.conditions.HandCondition;
import pl.betoncraft.betonquest.conditions.HealthCondition;
import pl.betoncraft.betonquest.conditions.HeightCondition;
import pl.betoncraft.betonquest.conditions.ItemCondition;
import pl.betoncraft.betonquest.conditions.JournalCondition;
import pl.betoncraft.betonquest.conditions.LocationCondition;
import pl.betoncraft.betonquest.conditions.MonstersCondition;
import pl.betoncraft.betonquest.conditions.ObjectiveCondition;
import pl.betoncraft.betonquest.conditions.PartyCondition;
import pl.betoncraft.betonquest.conditions.PermissionCondition;
import pl.betoncraft.betonquest.conditions.PointCondition;
import pl.betoncraft.betonquest.conditions.RandomCondition;
import pl.betoncraft.betonquest.conditions.ScoreboardCondition;
import pl.betoncraft.betonquest.conditions.SneakCondition;
import pl.betoncraft.betonquest.conditions.TagCondition;
import pl.betoncraft.betonquest.conditions.TestForBlockCondition;
import pl.betoncraft.betonquest.conditions.TimeCondition;
import pl.betoncraft.betonquest.conditions.VariableCondition;
import pl.betoncraft.betonquest.conditions.VehicleCondition;
import pl.betoncraft.betonquest.conditions.WeatherCondition;
import pl.betoncraft.betonquest.conditions.WorldCondition;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.config.ConfigUpdater;
import pl.betoncraft.betonquest.conversation.CombatTagger;
import pl.betoncraft.betonquest.conversation.Conversation;
import pl.betoncraft.betonquest.conversation.ConversationColors;
import pl.betoncraft.betonquest.conversation.ConversationData;
import pl.betoncraft.betonquest.conversation.ConversationIO;
import pl.betoncraft.betonquest.conversation.ConversationResumer;
import pl.betoncraft.betonquest.conversation.CubeNPCListener;
import pl.betoncraft.betonquest.conversation.InventoryConvIO;
import pl.betoncraft.betonquest.conversation.SimpleConvIO;
import pl.betoncraft.betonquest.conversation.TellrawConvIO;
import pl.betoncraft.betonquest.database.Database;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.database.MySQL;
import pl.betoncraft.betonquest.database.SQLite;
import pl.betoncraft.betonquest.database.Saver;
import pl.betoncraft.betonquest.events.CancelEvent;
import pl.betoncraft.betonquest.events.ChestClearEvent;
import pl.betoncraft.betonquest.events.ChestGiveEvent;
import pl.betoncraft.betonquest.events.ChestTakeEvent;
import pl.betoncraft.betonquest.events.ClearEvent;
import pl.betoncraft.betonquest.events.CommandEvent;
import pl.betoncraft.betonquest.events.CompassEvent;
import pl.betoncraft.betonquest.events.ConversationEvent;
import pl.betoncraft.betonquest.events.DamageEvent;
import pl.betoncraft.betonquest.events.DoorEvent;
import pl.betoncraft.betonquest.events.EffectEvent;
import pl.betoncraft.betonquest.events.ExplosionEvent;
import pl.betoncraft.betonquest.events.FolderEvent;
import pl.betoncraft.betonquest.events.GiveEvent;
import pl.betoncraft.betonquest.events.GiveJournalEvent;
import pl.betoncraft.betonquest.events.IfElseEvent;
import pl.betoncraft.betonquest.events.JournalEvent;
import pl.betoncraft.betonquest.events.KillEvent;
import pl.betoncraft.betonquest.events.LightningEvent;
import pl.betoncraft.betonquest.events.MessageEvent;
import pl.betoncraft.betonquest.events.ObjectiveEvent;
import pl.betoncraft.betonquest.events.PartyEvent;
import pl.betoncraft.betonquest.events.PointEvent;
import pl.betoncraft.betonquest.events.RunEvent;
import pl.betoncraft.betonquest.events.ScoreboardEvent;
import pl.betoncraft.betonquest.events.SetBlockEvent;
import pl.betoncraft.betonquest.events.SpawnMobEvent;
import pl.betoncraft.betonquest.events.LeverEvent;
import pl.betoncraft.betonquest.events.SudoEvent;
import pl.betoncraft.betonquest.events.TagEvent;
import pl.betoncraft.betonquest.events.TakeEvent;
import pl.betoncraft.betonquest.events.TeleportEvent;
import pl.betoncraft.betonquest.events.TimeEvent;
import pl.betoncraft.betonquest.events.WeatherEvent;
import pl.betoncraft.betonquest.objectives.ActionObjective;
import pl.betoncraft.betonquest.objectives.ArrowShootObjective;
import pl.betoncraft.betonquest.objectives.BlockObjective;
import pl.betoncraft.betonquest.objectives.ChestPutObjective;
import pl.betoncraft.betonquest.objectives.ConsumeObjective;
import pl.betoncraft.betonquest.objectives.CraftingObjective;
import pl.betoncraft.betonquest.objectives.DelayObjective;
import pl.betoncraft.betonquest.objectives.DieObjective;
import pl.betoncraft.betonquest.objectives.EnchantObjective;
import pl.betoncraft.betonquest.objectives.ExperienceObjective;
import pl.betoncraft.betonquest.objectives.FishObjective;
import pl.betoncraft.betonquest.objectives.KillPlayerObjective;
import pl.betoncraft.betonquest.objectives.LocationObjective;
import pl.betoncraft.betonquest.objectives.LogoutObjective;
import pl.betoncraft.betonquest.objectives.MobKillObjective;
import pl.betoncraft.betonquest.objectives.PasswordObjective;
import pl.betoncraft.betonquest.objectives.PotionObjective;
import pl.betoncraft.betonquest.objectives.ShearObjective;
import pl.betoncraft.betonquest.objectives.SmeltingObjective;
import pl.betoncraft.betonquest.objectives.StepObjective;
import pl.betoncraft.betonquest.objectives.TameObjective;
import pl.betoncraft.betonquest.objectives.VariableObjective;
import pl.betoncraft.betonquest.objectives.VehicleObjective;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.Metrics;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Updater;
import pl.betoncraft.betonquest.utils.Utils;
import pl.betoncraft.betonquest.variables.ItemAmountVariable;
import pl.betoncraft.betonquest.variables.NpcNameVariable;
import pl.betoncraft.betonquest.variables.ObjectivePropertyVariable;
import pl.betoncraft.betonquest.variables.PlayerNameVariable;
import pl.betoncraft.betonquest.variables.PointVariable;
import pl.betoncraft.betonquest.variables.VersionVariable;

/**
 * Represents BetonQuest plugin
 * 
 * @author Jakub Sapalski
 */
public final class BetonQuest extends JavaPlugin {

	private final static String ERROR = "There was some error. Please send it to the"
			+ " developer: <coosheck@gmail.com>";

	private static BetonQuest instance;

	private Database database;
	private boolean isMySQLUsed;
	private Saver saver;
	private BukkitRunnable keeper;
	private Compatibility compatibility;
	private Updater updater;

	private ConcurrentHashMap<String, PlayerData> playerDataMap = new ConcurrentHashMap<>();

	private static HashMap<String, Class<? extends Condition>> conditionTypes = new HashMap<>();
	private static HashMap<String, Class<? extends QuestEvent>> eventTypes = new HashMap<>();
	private static HashMap<String, Class<? extends Objective>> objectiveTypes = new HashMap<>();
	private static HashMap<String, Class<? extends ConversationIO>> convIOTypes = new HashMap<>();
	private static HashMap<String, Class<? extends Variable>> variableTypes = new HashMap<>();

	private static HashMap<String, Condition> conditions = new HashMap<>();
	private static HashMap<String, QuestEvent> events = new HashMap<>();
	private static HashMap<String, Objective> objectives = new HashMap<>();
	private static HashMap<String, ConversationData> conversations = new HashMap<>();
	private static HashMap<String, Variable> variables = new HashMap<>();

	@Override
	public void onEnable() {

		instance = this;

		// initialize debugger
		new Debug();

		// load configuration
		new Config();

		// try to connect to database
		Debug.info("Connecting to MySQL database");
		this.database = new MySQL(this, getConfig().getString("mysql.host"), getConfig().getString("mysql.port"),
				getConfig().getString("mysql.base"), getConfig().getString("mysql.user"),
				getConfig().getString("mysql.pass"));

		// try to connect to MySQL
		Connection con = database.getConnection();
		if (con != null) {
			Debug.broadcast("Using MySQL for storing data!");
			isMySQLUsed = true;
			// if it fails use SQLite
		} else {
			this.database = new SQLite(this, "database.db");
			Debug.broadcast("Using SQLite for storing data!");
			isMySQLUsed = false;
		}

		// create tables in the database
		database.createTables(isMySQLUsed);

		// create and start the saver object, which handles correct asynchronous
		// saving to the database
		saver = new Saver();
		saver.start();

		// load database backup
		Utils.loadDatabaseFromBackup();

		// update configuration if needed
		new ConfigUpdater();

		// if it's a first start of the plugin, debug option is not there
		// add it so debug option is turned off after first start
		if (getConfig().getString("debug", null) == null) {
			getConfig().set("debug", "false");
			saveConfig();
		}

		// instantiating of these important things
		new JoinQuitListener();

		// instantiate default conversation start listener
		new CubeNPCListener();

		// instantiate journal handler
		new QuestItemHandler();

		// initialize static events
		new StaticEvents();

		// initialize combat tagging
		new CombatTagger();

		// load colors for conversations
		new ConversationColors();

		// start timer for global locations
		new GlobalLocations().runTaskTimer(this, 20, 20);

		// start mob kill listener
		new MobKillListener();

		// register commands
		new QuestCommand();
		new JournalCommand();
		new BackpackCommand();
		new CancelQuestCommand();
		new CompassCommand();
		new LangCommand();

		// register conditions
		registerConditions("health", HealthCondition.class);
		registerConditions("permission", PermissionCondition.class);
		registerConditions("experience", ExperienceCondition.class);
		registerConditions("tag", TagCondition.class);
		registerConditions("point", PointCondition.class);
		registerConditions("and", ConjunctionCondition.class);
		registerConditions("or", AlternativeCondition.class);
		registerConditions("time", TimeCondition.class);
		registerConditions("weather", WeatherCondition.class);
		registerConditions("height", HeightCondition.class);
		registerConditions("item", ItemCondition.class);
		registerConditions("hand", HandCondition.class);
		registerConditions("location", LocationCondition.class);
		registerConditions("armor", ArmorCondition.class);
		registerConditions("effect", EffectCondition.class);
		registerConditions("rating", ArmorRatingCondition.class);
		registerConditions("sneak", SneakCondition.class);
		registerConditions("random", RandomCondition.class);
		registerConditions("journal", JournalCondition.class);
		registerConditions("testforblock", TestForBlockCondition.class);
		registerConditions("empty", EmptySlotsCondition.class);
		registerConditions("party", PartyCondition.class);
		registerConditions("monsters", MonstersCondition.class);
		registerConditions("objective", ObjectiveCondition.class);
		registerConditions("check", CheckCondition.class);
		registerConditions("chestitem", ChestItemCondition.class);
		registerConditions("score", ScoreboardCondition.class);
		registerConditions("riding", VehicleCondition.class);
		registerConditions("world", WorldCondition.class);
		registerConditions("gamemode", GameModeCondition.class);
		registerConditions("achievement", AchievementCondition.class);
		registerConditions("variable", VariableCondition.class);

		// register events
		registerEvents("message", MessageEvent.class);
		registerEvents("objective", ObjectiveEvent.class);
		registerEvents("command", CommandEvent.class);
		registerEvents("tag", TagEvent.class);
		registerEvents("journal", JournalEvent.class);
		registerEvents("teleport", TeleportEvent.class);
		registerEvents("explosion", ExplosionEvent.class);
		registerEvents("lightning", LightningEvent.class);
		registerEvents("point", PointEvent.class);
		registerEvents("give", GiveEvent.class);
		registerEvents("take", TakeEvent.class);
		registerEvents("conversation", ConversationEvent.class);
		registerEvents("kill", KillEvent.class);
		registerEvents("effect", EffectEvent.class);
		registerEvents("spawn", SpawnMobEvent.class);
		registerEvents("time", TimeEvent.class);
		registerEvents("weather", WeatherEvent.class);
		registerEvents("folder", FolderEvent.class);
		registerEvents("setblock", SetBlockEvent.class);
		registerEvents("damage", DamageEvent.class);
		registerEvents("party", PartyEvent.class);
		registerEvents("clear", ClearEvent.class);
		registerEvents("run", RunEvent.class);
		registerEvents("givejournal", GiveJournalEvent.class);
		registerEvents("sudo", SudoEvent.class);
		registerEvents("chestgive", ChestGiveEvent.class);
		registerEvents("chesttake", ChestTakeEvent.class);
		registerEvents("chestclear", ChestClearEvent.class);
		registerEvents("compass", CompassEvent.class);
		registerEvents("cancel", CancelEvent.class);
		registerEvents("score", ScoreboardEvent.class);
		registerEvents("lever", LeverEvent.class);
		registerEvents("door", DoorEvent.class);
		registerEvents("if", IfElseEvent.class);

		// register objectives
		registerObjectives("location", LocationObjective.class);
		registerObjectives("block", BlockObjective.class);
		registerObjectives("mobkill", MobKillObjective.class);
		registerObjectives("action", ActionObjective.class);
		registerObjectives("die", DieObjective.class);
		registerObjectives("craft", CraftingObjective.class);
		registerObjectives("smelt", SmeltingObjective.class);
		registerObjectives("tame", TameObjective.class);
		registerObjectives("delay", DelayObjective.class);
		registerObjectives("arrow", ArrowShootObjective.class);
		registerObjectives("experience", ExperienceObjective.class);
		registerObjectives("step", StepObjective.class);
		registerObjectives("logout", LogoutObjective.class);
		registerObjectives("password", PasswordObjective.class);
		registerObjectives("fish", FishObjective.class);
		registerObjectives("enchant", EnchantObjective.class);
		registerObjectives("shear", ShearObjective.class);
		registerObjectives("chestput", ChestPutObjective.class);
		registerObjectives("potion", PotionObjective.class);
		registerObjectives("vehicle", VehicleObjective.class);
		registerObjectives("consume", ConsumeObjective.class);
		registerObjectives("variable", VariableObjective.class);
		registerObjectives("kill", KillPlayerObjective.class);

		// register conversation IO types
		registerConversationIO("simple", SimpleConvIO.class);
		registerConversationIO("tellraw", TellrawConvIO.class);
		registerConversationIO("chest", InventoryConvIO.class);

		// register variable types
		registerVariable("player", PlayerNameVariable.class);
		registerVariable("npc", NpcNameVariable.class);
		registerVariable("objective", ObjectivePropertyVariable.class);
		registerVariable("point", PointVariable.class);
		registerVariable("item", ItemAmountVariable.class);
		registerVariable("version", VersionVariable.class);

		// initialize compatibility with other plugins
		compatibility = new Compatibility();

		// schedule quest data loading on the first tick, so all other
		// plugins can register their types
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				// Load all events and conditions
				loadData();
				// load data for all online players
				for (Player player : Bukkit.getOnlinePlayers()) {
					String playerID = PlayerConverter.getID(player);
					PlayerData playerData = new PlayerData(playerID);
					playerDataMap.put(playerID, playerData);
					playerData.startObjectives();
					playerData.getJournal().update();
					if (playerData.getConversation() != null)
						new ConversationResumer(playerID, playerData.getConversation());
				}
			}
		});

		// schedule database pinging to keep connection
		keeper = new BukkitRunnable() {
			@Override
			public void run() {
				try {
					database.getConnection().prepareStatement("SELECT 1").executeQuery();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		};
		keeper.runTaskTimerAsynchronously(this, 60 * 20, 60 * 20);

		// block betonquestanswer logging (it's just a spam)
		try {
			Class.forName("org.apache.logging.log4j.core.Filter");
			Logger coreLogger = (Logger) LogManager.getRootLogger();
			coreLogger.addFilter(new AnswerFilter());
		} catch (ClassNotFoundException | NoClassDefFoundError e) {
			Debug.info("Could not disable /betonquestanswer logging");
		}

		// metrics
		try {
			Metrics metrics = new Metrics(this);
			if (metrics.start()) {
				Debug.broadcast("Metrics enabled!");
			} else {
				Debug.broadcast("Metrics disabled!");
			}
		} catch (IOException e) {
			Debug.broadcast("Metrics faild to enable!");
		}

		// updater
		updater = new Updater(this.getFile());

		// done
		Debug.broadcast("BetonQuest succesfully enabled!");
	}

	/**
	 * Loads events and conditions to the maps
	 */
	public void loadData() {
		// save data of all objectives to the players
		for (Objective objective : objectives.values()) {
			objective.close();
		}
		// clear previously loaded data
		events.clear();
		conditions.clear();
		conversations.clear();
		objectives.clear();
		variables.clear();
		// load new data
		for (String packName : Config.getPackageNames()) {
			Debug.info("Loading stuff in package " + packName);
			ConfigPackage pack = Config.getPackage(packName);
			FileConfiguration eConfig = Config.getPackage(packName).getEvents().getConfig();
			for (String key : eConfig.getKeys(false)) {
				if (key.contains(" ")) {
					Debug.error("Event name cannot contain spaces: '" + key + "' (in " + packName + " package)");
					continue;
				}
				String ID = packName + "." + key;
				String instruction = pack.getString("events." + key);
				if (instruction == null) {
					continue;
				}
				String[] parts = instruction.split(" ");
				if (parts.length < 1) {
					Debug.error("Not enough arguments in event " + ID);
					continue;
				}
				Class<? extends QuestEvent> eventClass = eventTypes.get(parts[0]);
				if (eventClass == null) {
					// if it's null then there is no such type registered, log
					// an error
					Debug.error("Event type " + parts[0] + " is not registered, check if it's"
							+ " spelled correctly in " + ID + " event.");
					continue;
				}
				try {
					QuestEvent event = eventClass.getConstructor(String.class, String.class).newInstance(packName,
							instruction);
					events.put(ID, event);
					Debug.info("  Event " + ID + " loaded");
				} catch (InvocationTargetException e) {
					if (e.getCause() instanceof InstructionParseException) {
						Debug.error("Error in " + ID + " event: " + e.getCause().getMessage());
					} else {
						e.printStackTrace();
						Debug.error(ERROR);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Debug.error(ERROR);
				}
			}
			FileConfiguration cConfig = pack.getConditions().getConfig();
			for (String key : cConfig.getKeys(false)) {
				if (key.contains(" ")) {
					Debug.error("Condition name cannot contain spaces: '" + key + "' (in " + packName + " package)");
					continue;
				}
				String ID = packName + "." + key;
				String instruction = pack.getString("conditions." + key);
				if (instruction == null) {
					continue;
				}
				String[] parts = instruction.split(" ");
				if (parts.length < 1) {
					Debug.error("Not enough arguments in condition " + ID);
					continue;
				}
				Class<? extends Condition> conditionClass = conditionTypes.get(parts[0]);
				// if it's null then there is no such type registered, log an
				// error
				if (conditionClass == null) {
					Debug.error("Condition type " + parts[0] + " is not registered,"
							+ " check if it's spelled correctly in " + ID + " condition.");
					continue;
				}
				try {
					Condition condition = conditionClass.getConstructor(String.class, String.class)
							.newInstance(packName, instruction);
					conditions.put(ID, condition);
					Debug.info("  Condition " + ID + " loaded");
				} catch (InvocationTargetException e) {
					if (e.getCause() instanceof InstructionParseException) {
						Debug.error("Error in " + ID + " condition: " + e.getCause().getMessage());
					} else {
						e.printStackTrace();
						Debug.error(ERROR);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Debug.error(ERROR);
				}
			}
			FileConfiguration oConfig = pack.getObjectives().getConfig();
			for (String key : oConfig.getKeys(false)) {
				if (key.contains(" ")) {
					Debug.error("Objective name cannot contain spaces: '" + key + "' (in " + packName + " package)");
					continue;
				}
				String ID = packName + "." + key;
				String instruction = pack.getString("objectives." + key);
				if (instruction == null) {
					continue;
				}
				String[] parts = instruction.split(" ");
				if (parts.length < 1) {
					Debug.error("Not enough arguments in objectives " + ID);
					continue;
				}
				Class<? extends Objective> objectiveClass = objectiveTypes.get(parts[0]);
				// if it's null then there is no such type registered, log an
				// error
				if (objectiveClass == null) {
					Debug.error("Objective type " + parts[0] + " is not registered, check if it's"
							+ " spelled correctly in " + ID + " objective.");
					continue;
				}
				try {
					Objective objective = objectiveClass.getConstructor(String.class, String.class, String.class)
							.newInstance(packName, ID, instruction);
					objectives.put(ID, objective);
					Debug.info("  Objective " + ID + " loaded");
				} catch (InvocationTargetException e) {
					if (e.getCause() instanceof InstructionParseException) {
						Debug.error("Error in " + ID + " objective: " + e.getCause().getMessage());
					} else {
						e.printStackTrace();
						Debug.error(ERROR);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Debug.error(ERROR);
				}
			}
			for (String convName : pack.getConversationNames()) {
				if (convName.contains(" ")) {
					Debug.error("Conversation name cannot contain spaces: '" + convName + "' (in " + packName
							+ " package)");
					continue;
				}
				try {
					conversations.put(pack.getName() + "." + convName, new ConversationData(packName, convName));
				} catch (InstructionParseException e) {
					Debug.error(
							"Error in " + convName + " conversation from " + packName + " package: " + e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					Debug.error(ERROR);
				}
			}
			// check external pointers
			ConversationData.postEnableCheck();
			Debug.info("Everything in package " + packName + " loaded");
		}
		Debug.broadcast("There are " + conditions.size() + " conditions, " + events.size() + " events, "
				+ objectives.size() + " objectives and " + conversations.size() + " conversations loaded from "
				+ Config.getPackageNames().size() + " packages.");
		// start those freshly loaded objectives for all players
		for (PlayerData playerData : playerDataMap.values()) {
			playerData.startObjectives();
		}
	}

	@Override
	public void onDisable() {
		// suspend all conversations
		for (Player player : Bukkit.getOnlinePlayers()) {
			Conversation conv = Conversation.getConversation(PlayerConverter.getID(player));
			if (conv != null)
				conv.suspend();
			player.closeInventory();
		}
		// cancel database saver
		saver.end();
		keeper.cancel();
		compatibility.disable();
		// stop global location listener
		GlobalLocations.stop();
		database.closeConnection();
		// cancel static events (they are registered outside of Bukkit so it
		// won't happen automatically)
		StaticEvents.stop();
		// update if needed
		updater.updateBugfixes();
		// done
		Debug.broadcast("BetonQuest succesfully disabled!");
	}

	/**
	 * Returns the plugin's instance
	 * 
	 * @return the plugin's instance
	 */
	public static BetonQuest getInstance() {
		return instance;
	}

	/**
	 * Returns the database instance
	 * 
	 * @return Database instance
	 */
	public Database getDB() {
		return database;
	}

	public Updater getUpdater() {
		return updater;
	}

	/**
	 * Checks if MySQL is used or not
	 * 
	 * @return if MySQL is used (false means that SQLite is being used)
	 */
	public boolean isMySQLUsed() {
		return isMySQLUsed;
	}

	/**
	 * Stores the PlayerData in a map, so it can be retrieved using
	 * getPlayerData(String playerID)
	 * 
	 * @param playerID
	 *            ID of the player
	 * @param playerData
	 *            PlayerData object to store
	 */
	public void putPlayerData(String playerID, PlayerData playerData) {
		Debug.info("Inserting data for " + PlayerConverter.getName(playerID));
		playerDataMap.put(playerID, playerData);
	}

	/**
	 * Retrieves PlayerData object for specified player. If the playerData
	 * does not exist but the player is online, it will create new playerData on
	 * the main thread and put it into the map.
	 * 
	 * @param playerID
	 *            ID of the player
	 * @return PlayerData object for the player
	 */
	public PlayerData getPlayerData(String playerID) {
		PlayerData playerData = playerDataMap.get(playerID);
		if (playerData == null && PlayerConverter.getPlayer(playerID) != null) {
			playerData = new PlayerData(playerID);
			putPlayerData(playerID, playerData);
		}
		return playerData;
	}

	/**
	 * Removes the database playerData from the map
	 * 
	 * @param playerID
	 *            ID of the player whose playerData is to be removed
	 */
	public void removePlayerData(String playerID) {
		playerDataMap.remove(playerID);
	}

	/**
	 * Registers new condition classes by their names
	 * 
	 * @param name
	 *            name of the condition type
	 * @param conditionClass
	 *            class object for the condition
	 */
	public void registerConditions(String name, Class<? extends Condition> conditionClass) {
		Debug.info("Registering " + name + " condition type");
		conditionTypes.put(name, conditionClass);
	}

	/**
	 * Registers new event classes by their names
	 * 
	 * @param name
	 *            name of the event type
	 * @param eventClass
	 *            class object for the condition
	 */
	public void registerEvents(String name, Class<? extends QuestEvent> eventClass) {
		Debug.info("Registering " + name + " event type");
		eventTypes.put(name, eventClass);
	}

	/**
	 * Registers new objective classes by their names
	 * 
	 * @param name
	 *            name of the objective type
	 * @param objectiveClass
	 *            class object for the objective
	 */
	public void registerObjectives(String name, Class<? extends Objective> objectiveClass) {
		Debug.info("Registering " + name + " objective type");
		objectiveTypes.put(name, objectiveClass);
	}

	/**
	 * Registers new conversation input/output class.
	 * 
	 * @param name
	 *            name of the IO type
	 * @param convIOClass
	 *            class object to register
	 */
	public void registerConversationIO(String name, Class<? extends ConversationIO> convIOClass) {
		Debug.info("Registering " + name + " conversation IO type");
		convIOTypes.put(name, convIOClass);
	}

	/**
	 * Registers new variable type.
	 * 
	 * @param name
	 *            name of the variable type
	 * @param variable
	 *            class object of this type
	 */
	public void registerVariable(String name, Class<? extends Variable> variable) {
		Debug.info("Registering " + name + " variable type");
		variableTypes.put(name, variable);
	}

	/**
	 * Checks if the condition described by conditionID is met
	 * 
	 * @param conditionID
	 *            ID of the condition to check, as defined in conditions.yml
	 * @param playerID
	 *            ID of the player which should be checked
	 * @return if the condition is met
	 */
	public static boolean condition(String playerID, String conditionID) {
		// null check
		if (conditionID == null) {
			Debug.info("Null arguments for the condition!");
			return false;
		}
		// check for inverted condition
		boolean inverted = false;
		if (conditionID.contains("!")) {
			conditionID = conditionID.replace("!", "");
			inverted = true;
		}
		// get the condition
		Condition condition = conditions.get(conditionID);
		if (condition == null) {
			Debug.error("The condition " + conditionID + " is not defined!");
			return false;
		}
		// check for null player
		if (playerID == null && !condition.isStatic()) {
			Debug.info("Cannot check non-static condition without a player, returning false");
			return false;
		}
		// check for online player
		if (playerID != null && PlayerConverter.getPlayer(playerID) == null && !condition.isPersistent()) {
			Debug.info("Player was offline, condition is not persistent, returning false");
			return false;
		}
		// and check if it's met or not
		boolean outcome = false;
		try {
			outcome = condition.check(playerID);
		} catch (QuestRuntimeException e) {
			Debug.error("Error while checking '" + conditionID + "' condition: " + e.getMessage());
			return false;
		}
		boolean isMet = (outcome && !inverted) || (!outcome && inverted);
		Debug.info((isMet ? "TRUE" : "FALSE") + ": " + (inverted ? "inverted" : "") + " condition " + conditionID
				+ " for player " + PlayerConverter.getName(playerID));
		return isMet;
	}

	/**
	 * Fires the event described by eventID
	 * 
	 * @param eventID
	 *            ID of the event to fire, as defined in events.yml
	 * @param playerID
	 *            ID of the player who the event is firing for
	 */
	public static void event(String playerID, String eventID) {
		// null check
		if (eventID == null) {
			Debug.info("Null argument for the event!");
			return;
		}
		// get the event
		QuestEvent event = events.get(eventID);
		if (event == null) {
			Debug.error("Event " + eventID + " is not defined");
			return;
		}
		// fire the event
		if (playerID == null) {
			Debug.info("Firing static event " + eventID);
		} else {
			Debug.info("Firing event " + eventID + " for " + PlayerConverter.getName(playerID));
		}
		try {
			event.fire(playerID);
		} catch (QuestRuntimeException e) {
			Debug.error("Error while firing '" + eventID + "' event: " + e.getMessage());
		}
	}

	/**
	 * Creates new objective for given player
	 * 
	 * @param playerID
	 *            ID of the player
	 * @param objectiveID
	 *            ID of the objective
	 */
	public static void newObjective(String playerID, String objectiveID) {
		// null check
		if (playerID == null || objectiveID == null) {
			Debug.info("Null arguments for the objective!");
			return;
		}
		Objective objective = objectives.get(objectiveID);
		if (objective.containsPlayer(playerID)) {
			Debug.info("Player " + PlayerConverter.getName(playerID) + " already has the " + objectiveID +
					" objective");
			return;
		}
		objective.newPlayer(playerID);
	}

	/**
	 * Resumes the existing objective for given player
	 * 
	 * @param playerID
	 *            ID of the player
	 * @param objectiveID
	 *            ID of the objective
	 * @param instruction
	 *            data instruction string
	 */
	public static void resumeObjective(String playerID, String objectiveID, String instruction) {
		// null check
		if (playerID == null || objectiveID == null || instruction == null) {
			Debug.info("Null arguments for the objective!");
			return;
		}
		Objective objective = objectives.get(objectiveID);
		if (objective == null) {
			Debug.error("Objective " + objectiveID + " does not exist");
			return;
		}
		if (objective.containsPlayer(playerID)) {
			Debug.info(
					"Player " + PlayerConverter.getName(playerID) + " already has the " + objectiveID + " objective!");
			return;
		}
		objective.addPlayer(playerID, instruction);
	}

	/**
	 * Creates a new instance of a variable and puts it into the hashmap.
	 * 
	 * @param pack
	 * 
	 * @param instruction
	 *            instruction of the variable, including both % characters.
	 */
	public static Variable createVariable(ConfigPackage pack, String instruction) {
		String ID = pack.getName() + "-" + instruction;
		// no need to create duplicated variables
		if (variables.containsKey(ID))
			return variables.get(ID);
		String[] parts = instruction.replace("%", "").split("\\.");
		if (parts.length < 1) {
			Debug.error("Not enough arguments in variable " + ID);
			return null;
		}
		Class<? extends Variable> variableClass = variableTypes.get(parts[0]);
		// if it's null then there is no such type registered, log an error
		if (variableClass == null) {
			Debug.error("Variable type " + parts[0] + " is not registered, check if it's spelled correctly in "
					+ ID + " variable.");
			return null;
		}
		try {
			Variable variable = variableClass.getConstructor(String.class, String.class).newInstance(pack.getName(),
					instruction);
			variables.put(ID, variable);
			Debug.info("Variable " + ID + " loaded");
			return variable;
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof InstructionParseException) {
				Debug.error("Error in " + ID + " variable: " + e.getCause().getMessage());
			} else {
				e.printStackTrace();
				Debug.error(ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.error(ERROR);
		}
		return null;
	}

	/**
	 * Resolves variables in the supplied text and returns them as a list of
	 * instruction strings, including % characters. Variables are unique, so if
	 * the user uses the same variables multiple times, the list will contain
	 * only one occurence of this variable.
	 * 
	 * @param text
	 *            text from which the variables will be resolved
	 * @return the list of unique variable instructions
	 */
	public static ArrayList<String> resolveVariables(String text) {
		ArrayList<String> variables = new ArrayList<>();
		boolean inside = false;
		char[] charArr = text.toCharArray();
		StringBuilder variable = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			if (inside) {
				if (charArr[i] == ' ') {
					// it's not a variable if it contains a space
					inside = false;
					variable = new StringBuilder();
				}
				variable.append(charArr[i]);
				if (charArr[i] == '%') {
					// end of the variable
					inside = false;
					String finalVariable = variable.toString();
					variable = new StringBuilder();
					if (!variables.contains(finalVariable)) {
						variables.add(finalVariable);
					}
				}
			} else {
				if (charArr[i] == '%') {
					inside = true;
					variable.append('%');
				}
			}
		}
		return variables;
	}

	/**
	 * Returns the list of objectives of this player
	 * 
	 * @param playerID
	 *            ID of the player
	 * @return list of this player's active objectives
	 */
	public ArrayList<Objective> getPlayerObjectives(String playerID) {
		ArrayList<Objective> list = new ArrayList<>();
		for (String objective : objectives.keySet()) {
			if (objectives.get(objective).containsPlayer(playerID)) {
				list.add(objectives.get(objective));
			}
		}
		return list;
	}

	/**
	 * @param name
	 *            package name, dot and name of the conversation
	 * @return ConversationData object for this conversation or null if it does
	 *         not exist
	 */
	public ConversationData getConversation(String name) {
		return conversations.get(name);
	}

	/**
	 * @param objectiveID
	 *            package name, dot and ID of the objective
	 * @return Objective object or null if it does not exist
	 */
	public Objective getObjective(String objectiveID) {
		return objectives.get(objectiveID);
	}

	/**
	 * Returns the instance of Saver
	 * 
	 * @return the Saver
	 */
	public Saver getSaver() {
		return saver;
	}

	/**
	 * @param name
	 *            name of the conversation IO type
	 * @return the class object for this conversation IO type
	 */
	public Class<? extends ConversationIO> getConvIO(String name) {
		return convIOTypes.get(name);
	}

	/**
	 * Resoles the variable for specified player. If the variable is not loaded
	 * yet it will load it on the main thread.
	 * 
	 * @param packName
	 *            name of the package
	 * @param name
	 *            name of the variable (instruction, with % characters)
	 * @param playerID
	 *            ID of the player
	 * @return the value of this variable for given player
	 */
	public String getVariableValue(String packName, String name, String playerID) {
		Variable var = createVariable(Config.getPackage(packName), name);
		if (var == null)
			return "could not resolve variable";
		return var.getValue(playerID);
	}

	/**
	 * @return the class of the event
	 */
	public Class<? extends QuestEvent> getEventClass(String name) {
		return eventTypes.get(name);
	}

	/**
	 * @return the class of the event
	 */
	public Class<? extends Condition> getConditionClass(String name) {
		return conditionTypes.get(name);
	}

	/**
	 * Renames the objective instance.
	 * 
	 * @param name
	 *            the current name
	 * @param rename
	 *            the name it should have now
	 */
	public void renameObjective(String name, String rename) {
		objectives.put(rename, objectives.remove(name));
	}
}