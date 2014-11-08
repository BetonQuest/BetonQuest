package pl.betoncraft.betonquest;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.conditions.AlternativeCondition;
import pl.betoncraft.betonquest.conditions.ArmorCondition;
import pl.betoncraft.betonquest.conditions.ConjunctionCondition;
import pl.betoncraft.betonquest.conditions.EffectCondition;
import pl.betoncraft.betonquest.conditions.ExperienceCondition;
import pl.betoncraft.betonquest.conditions.HandCondition;
import pl.betoncraft.betonquest.conditions.HealthCondition;
import pl.betoncraft.betonquest.conditions.HeightCondition;
import pl.betoncraft.betonquest.conditions.ItemCondition;
import pl.betoncraft.betonquest.conditions.LocationCondition;
import pl.betoncraft.betonquest.conditions.PermissionCondition;
import pl.betoncraft.betonquest.conditions.PointCondition;
import pl.betoncraft.betonquest.conditions.TagCondition;
import pl.betoncraft.betonquest.conditions.TimeCondition;
import pl.betoncraft.betonquest.conditions.WeatherCondition;
import pl.betoncraft.betonquest.core.Condition;
import pl.betoncraft.betonquest.core.Journal;
import pl.betoncraft.betonquest.core.JournalRes;
import pl.betoncraft.betonquest.core.Objective;
import pl.betoncraft.betonquest.core.ObjectiveRes;
import pl.betoncraft.betonquest.core.Point;
import pl.betoncraft.betonquest.core.PointRes;
import pl.betoncraft.betonquest.core.Pointer;
import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.core.StringRes;
import pl.betoncraft.betonquest.events.CommandEvent;
import pl.betoncraft.betonquest.events.ConversationEvent;
import pl.betoncraft.betonquest.events.DeleteObjectiveEvent;
import pl.betoncraft.betonquest.events.EffectEvent;
import pl.betoncraft.betonquest.events.ExplosionEvent;
import pl.betoncraft.betonquest.events.GiveEvent;
import pl.betoncraft.betonquest.events.JournalEvent;
import pl.betoncraft.betonquest.events.KillEvent;
import pl.betoncraft.betonquest.events.LightningEvent;
import pl.betoncraft.betonquest.events.MessageEvent;
import pl.betoncraft.betonquest.events.ObjectiveEvent;
import pl.betoncraft.betonquest.events.PointEvent;
import pl.betoncraft.betonquest.events.SpawnMobEvent;
import pl.betoncraft.betonquest.events.TagEvent;
import pl.betoncraft.betonquest.events.TakeEvent;
import pl.betoncraft.betonquest.events.TeleportEvent;
import pl.betoncraft.betonquest.events.TimeEvent;
import pl.betoncraft.betonquest.events.WeatherEvent;
import pl.betoncraft.betonquest.inout.ConfigInput;
import pl.betoncraft.betonquest.inout.GlobalLocations;
import pl.betoncraft.betonquest.inout.JoinQuitListener;
import pl.betoncraft.betonquest.inout.JournalBook;
import pl.betoncraft.betonquest.inout.JournalCommand;
import pl.betoncraft.betonquest.inout.NPCListener;
import pl.betoncraft.betonquest.inout.ObjectiveSaving;
import pl.betoncraft.betonquest.inout.QuestCommand;
import pl.betoncraft.betonquest.objectives.ActionObjective;
import pl.betoncraft.betonquest.objectives.BlockObjective;
import pl.betoncraft.betonquest.objectives.CraftingObjective;
import pl.betoncraft.betonquest.objectives.DieObjective;
import pl.betoncraft.betonquest.objectives.LocationObjective;
import pl.betoncraft.betonquest.objectives.MobKillObjective;
import pl.betoncraft.betonquest.objectives.SmeltingObjective;
import pl.betoncraft.betonquest.objectives.TameObjective;

/**
 * Represents BetonQuest plugin
 * @authors Co0sh, Dzejkop, BYK
 */
public final class BetonQuest extends JavaPlugin {
	
	private boolean disabling = false;

	private static BetonQuest instance;
	private MySQL MySQL;
	
	private HashMap<String,Class<? extends Condition>> conditions = new HashMap<String,Class<? extends Condition>>();
	private HashMap<String,Class<? extends QuestEvent>> events = new HashMap<String,Class<? extends QuestEvent>>();
	private HashMap<String,Class<? extends Objective>> objectives = new HashMap<String,Class<? extends Objective>>();
	
	private ConcurrentHashMap<String,ObjectiveRes> objectiveRes = new ConcurrentHashMap<String,ObjectiveRes>();
	private ConcurrentHashMap<String,StringRes> stringsRes = new ConcurrentHashMap<String,StringRes>();
	private ConcurrentHashMap<String,JournalRes> journalRes = new ConcurrentHashMap<String,JournalRes>();
	private ConcurrentHashMap<String,PointRes> pointRes	= new ConcurrentHashMap<String,PointRes>();
	
	private HashMap<String,List<String>> playerStrings = new HashMap<String,List<String>>();
	private HashMap<String,Journal> journals = new HashMap<String,Journal>();
	private HashMap<String,List<Point>> points = new HashMap<String,List<Point>>();
	
	private List<ObjectiveSaving> saving = new ArrayList<ObjectiveSaving>();
	
	@Override
	public void onEnable() {
		
		instance = this;

		new ConfigInput();
		
		// try to connect to database
		this.MySQL = new MySQL(this, getConfig().getString("mysql.host"),
				getConfig().getString("mysql.port"), getConfig().getString(
						"mysql.base"), getConfig().getString("mysql.user"),
				getConfig().getString("mysql.pass"));
			
		// create tables if they don't exist
		if (MySQL.openConnection() != null) {
			MySQL.updateSQL("CREATE TABLE IF NOT EXISTS objectives (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, playerID VARCHAR(256) NOT NULL, instructions VARCHAR(2048) NOT NULL, isused BOOLEAN NOT NULL DEFAULT 0);");
			MySQL.updateSQL("CREATE TABLE IF NOT EXISTS strings (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, playerID VARCHAR(256) NOT NULL, string TEXT NOT NULL, isused BOOLEAN NOT NULL DEFAULT 0);");
			MySQL.updateSQL("CREATE TABLE IF NOT EXISTS journal (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, playerID VARCHAR(256) NOT NULL, pointer VARCHAR(256) NOT NULL, date TIMESTAMP NOT NULL);");
			MySQL.updateSQL("CREATE TABLE IF NOT EXISTS points (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, playerID VARCHAR(256) NOT NULL, category VARCHAR(256) NOT NULL, count INT NOT NULL);");
		} else {
			BetonQuest.getInstance().getLogger().info("Couldn't connect to database, fix this and restart the server!");
			disabling = true;
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		new JoinQuitListener();
		new NPCListener();
		new JournalBook();
		new GlobalLocations().runTaskTimer(this, 0, 20);
		
		getCommand("q").setExecutor(new QuestCommand());
		getCommand("j").setExecutor(new JournalCommand());
		
		// register conditions
		registerConditions("health",HealthCondition.class);
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
        registerEvents("delete", DeleteObjectiveEvent.class);
        registerEvents("give", GiveEvent.class);
        registerEvents("take", TakeEvent.class);
        registerEvents("conversation", ConversationEvent.class);
        registerEvents("kill", KillEvent.class);
        registerEvents("effect", EffectEvent.class);
        registerEvents("spawn", SpawnMobEvent.class);
        registerEvents("time", TimeEvent.class);
        registerEvents("weather", WeatherEvent.class);
        
		// register objectives
		registerObjectives("location", LocationObjective.class);
		registerObjectives("block", BlockObjective.class);
		registerObjectives("mobkill", MobKillObjective.class);
		registerObjectives("action", ActionObjective.class);
		registerObjectives("die", DieObjective.class);
		registerObjectives("craft", CraftingObjective.class);
		registerObjectives("smelt", SmeltingObjective.class);
		registerObjectives("tame", TameObjective.class);
		
		// load objectives for all online players (in case of reload)
		for (Player player : Bukkit.getOnlinePlayers()) {
			loadAllPlayerData(player.getName());
			loadObjectives(player.getName());
			loadPlayerStrings(player.getName());
			loadJournal(player.getName());
			loadPlayerPoints(player.getName());
		}

		getLogger().log(Level.INFO, "BetonQuest succesfully enabled!");
	}
	
	@Override
	public void onDisable() {
		if (disabling) {
			return;
		}
		// create array and put there objectives (to avoid concurrent modification exception)
		List<ObjectiveSaving> list = new ArrayList<ObjectiveSaving>();
		// save all active objectives to database
		for (ObjectiveSaving objective : saving) {
			list.add(objective);
		}
		for (ObjectiveSaving objective : list) {
			objective.saveObjective();
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			JournalBook.removeJournal(player.getName());
			saveJournal(player.getName());
			savePlayerStrings(player.getName());
			savePlayerPoints(player.getName());
			BetonQuest.getInstance().getMySQL().updateSQL("DELETE FROM objectives WHERE playerID='" + player.getName() + "' AND isused = 1;");
		}
		getLogger().log(Level.INFO, "BetonQuest succesfully disabled!");
	}

	/**
	 * @return the plugin instance
	 */
	public static BetonQuest getInstance() {
		return instance;
	}

	/**
	 * @return the mySQL object
	 */
	public MySQL getMySQL() {
		return MySQL;
	}
	
	/**
	 * Registers new condition classes by their names
	 * @param name
	 * @param conditionClass
	 */
	public void registerConditions(String name, Class<? extends Condition> conditionClass) {
		conditions.put(name, conditionClass);
	}
	
	/**
	 * Registers new event classes by their names
	 * @param name
	 * @param eventClass
	 */
	public void registerEvents(String name, Class<? extends QuestEvent> eventClass) {
		events.put(name, eventClass);
	}
	
	/**
	 * Registers new objective classes by their names
	 * @param name
	 * @param objectiveClass
	 */
	public void registerObjectives(String name, Class<? extends Objective> objectiveClass) {
		objectives.put(name, objectiveClass);
	}
	
	/**
	 * returns Class object of condition with given name
	 * @param name
	 * @return
	 */
	public Class<? extends Condition> getCondition(String name) {
		return conditions.get(name);
	}
	
	/**
	 * returns Class object of event with given name
	 * @param name
	 * @return
	 */
	public Class<? extends QuestEvent> getEvent(String name) {
		return events.get(name);
	}
	
	/**
	 * returns Class object of objective with given name
	 * @param name
	 * @return
	 */
	public Class<? extends Objective> getObjective(String name) {
		return objectives.get(name);
	}
	
	/**
	 * stores pointer to ObjectiveSaving instance in order to store it on disable
	 * @param object
	 */
	public void putObjectiveSaving(ObjectiveSaving object) {
		saving.add(object);
	}
	
	/**
	 * deletes pointer to ObjectiveSaving instance in case the objective was completed and needs to be deleted
	 * @param object
	 */
	public void deleteObjectiveSaving(ObjectiveSaving object) {
		saving.remove(object);
	}

	/**
	 * loads from database all objectives of given player
	 * @param playerID
	 */
	public void loadObjectives(String playerID) {
		ObjectiveRes res = objectiveRes.get(playerID);
		while (res.next()) {
			BetonQuest.objective(playerID, res.getInstruction());
		}
		objectiveRes.remove(playerID);
	}
	
	/**
	 * returns if the condition described by conditionID is met
	 * @param conditionID
	 * @return
	 */
	public static boolean condition(String playerID, String conditionID) {
		String conditionInstruction = ConfigInput.getString("conditions." + conditionID);
		if (conditionInstruction == null) {
			BetonQuest.getInstance().getLogger().severe("Error while fetching condition: " + conditionID);
			return false;
		}
		String[] parts = conditionInstruction.split(" ");
		Class<? extends Condition> condition = BetonQuest.getInstance().getCondition(parts[0]);
		Condition instance = null;
		if (condition == null) {
			BetonQuest.getInstance().getLogger().severe("Condition type not defined, in: " + conditionID);
			return false;
		}
		try {
			instance = condition.getConstructor(String.class, String.class).newInstance(playerID, conditionInstruction);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			// return false for safety
			return false;
		}
		return instance.isMet();
	}
	
	/**
	 * fires the event described by eventID
	 * @param eventID
	 */
	public static void event(String playerID, String eventID) {
		String eventInstruction = ConfigInput.getString("events." + eventID);
		if (eventInstruction == null) {
			BetonQuest.getInstance().getLogger().severe("Error while fetching event: " + eventID);
			return;
		}
		String[] parts = eventInstruction.split(" ");
		boolean fire = true;
		conditions:
		for (String part : parts) {
			if (part.contains("conditions:")) {
				String[] conditions = part.substring(11).split(",");
				for (String condition : conditions) {
					if (!BetonQuest.condition(playerID, condition)) {
						fire = false;
						break conditions;
					}
				}
			}
		}
		if (!fire) {
			return;
		}
		Class<? extends QuestEvent> event = BetonQuest.getInstance().getEvent(parts[0]);
		if (event == null) {
			BetonQuest.getInstance().getLogger().severe("Event type not defined, in: " + eventID);
			return;
		}
		try {
			event.getConstructor(String.class, String.class).newInstance(playerID, eventInstruction);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * creates new objective for given player
	 * @param playerID
	 * @param instruction
	 */
	public static void objective(String playerID, String instruction) {
		if (instruction == null) {
			BetonQuest.getInstance().getLogger().severe("Error while creating objective.");
			return;
		}
		String[] parts = instruction.split(" ");
		String tag = null;
		for (String part : parts) {
			if (part.contains("tag:")) {
				tag = part.substring(4);
				break;
			}
		}
		if (tag == null) {
			BetonQuest.getInstance().getLogger().severe("Tag not found in: " + instruction);
			return;
		}
		Class<? extends Objective> objective = BetonQuest.getInstance().getObjective(parts[0]);
		if (objective == null) {
			BetonQuest.getInstance().getLogger().severe("Objective type not defined, in: " + tag);
			return;
		}
		try {
			objective.getConstructor(String.class, String.class).newInstance(playerID, instruction);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads point objects for specified player
	 * @param playerID
	 */
	public void loadPlayerPoints(String playerID) {
		PointRes res = pointRes.get(playerID);
		while (res.next()) {
			putPlayerPoints(playerID, res.getPoint());
		}
		pointRes.remove(playerID);
	}
	
	/**
	 * puts points in player's list
	 * @param playerID
	 * @param points
	 */
	public void putPlayerPoints(String playerID, Point points) {
		if (!this.points.containsKey(playerID)) {
			this.points.put(playerID, new ArrayList<Point>());
		}
		this.points.get(playerID).add(points);
	}
	
	/**
	 * Saves player's points to database
	 * @param playerID
	 */
	public void savePlayerPoints(String playerID) {
        List<Point> points = this.points.remove(playerID);
        if (points == null) {
        	return;
        }
        MySQL.updateSQL("DELETE FROM points WHERE playerID = '" + playerID + "'");
        for (Point point : points) {
        	MySQL.updateSQL("INSERT INTO points (playerID, category, count) VALUES ('" + playerID + "', '" + point.getCategory() + "', '" + point.getCount() + "')");
       	}
	}
	
	/**
	 * returns how many points from given category the player has
	 * @param playerID
	 * @param category
	 * @return
	 */
	public int getPlayerPoints(String playerID, String category) {
		List<Point> points = this.points.get(playerID);
		if (points == null) {
			return 0;
		}
		for (Point point : points) {
			if (point.getCategory().equalsIgnoreCase(category)) {
				return point.getCount();
			}
		}
		return 0;
	}
	
	/**
	 * adds points to specified category
	 * @param playerID
	 * @param category
	 * @param count
	 */
	public void addPlayerPoints(String playerID, String category, int count) {
		List<Point> points = this.points.get(playerID);
		if (points == null) {
			this.points.put(playerID, new ArrayList<Point>());
			this.points.get(playerID).add(new Point(category, count));
			return;
		}
		for (Point point : points) {
			if (point.getCategory().equalsIgnoreCase(category)) {
				point.addPoints(count);
				return;
			}
		}
		this.points.get(playerID).add(new Point(category, count));
	}
	
	/**
	 * loads strings (tags) for specified player
	 * @param playerID
	 */
	public void loadPlayerStrings(String playerID) {
		StringRes res = stringsRes.get(playerID);
		while (res.next()) {
			putPlayerString(playerID, res.getString());
		}
		stringsRes.remove(playerID);
	}
	
	/**
	 * Puts a string in player's list
	 * @param playerID
	 * @param string
	 */
	public void putPlayerString(String playerID, String string) {
		if (!playerStrings.containsKey(playerID)) {
			playerStrings.put(playerID, new ArrayList<String>());
		}
		playerStrings.get(playerID).add(string);
	}
	
	/**
	 * @return the playerStrings
	 */
	public HashMap<String, List<String>> getPlayerStrings() {
		return playerStrings;
	}

	/**
	 * Checks if player has specified string in his list
	 * @param playerID
	 * @param string
	 * @return
	 */
	public boolean havePlayerString(String playerID, String string) {
		if (!playerStrings.containsKey(playerID)) {
			return false;
		}
		return playerStrings.get(playerID).contains(string);
	}
	
	/**
	 * Removes specified string from player's list
	 * @param playerID
	 * @param string
	 */
	public void removePlayerString(String playerID, String string) {
		if (playerStrings.containsKey(playerID)) {
			playerStrings.get(playerID).remove(string);
		}
	}
	
	/**
	 * Removes player's list from HashMap and returns it (eg. for storing in database)
	 * @param playerID
	 */
	public void savePlayerStrings(final String playerID) {
        List<String> strings = playerStrings.remove(playerID);
        if (strings == null) {
        	return;
        }
        MySQL.openConnection();
        MySQL.updateSQL("DELETE FROM strings WHERE playerID = '" + playerID + "'");
        for (String string : strings) {
        	MySQL.updateSQL("INSERT INTO strings (playerID, string) VALUES ('" + playerID + "', '" + string + "')");
       	}
	}
	
	/**
	 * loads journal of specified player
	 * @param playerID
	 */
	public void loadJournal(String playerID) {
		journals.put(playerID, new Journal(playerID));
	}
	
	/**
	 * returns journal of specified player
	 * @param playerID
	 * @return
	 */
	public Journal getJournal(String playerID) {
		return journals.get(playerID);
	}
	
	/**
	 * saves player's journal
	 * @param playerID
	 */
	public void saveJournal(final String playerID) {
        MySQL.updateSQL("DELETE FROM journal WHERE playerID = '" + playerID + "'");
        Journal journal = journals.remove(playerID);
        if (journal == null) {
			return;
		}
        List<Pointer> pointers = journal.getPointers();
        for (Pointer pointer : pointers) {
        	MySQL.updateSQL("INSERT INTO journal (playerID, pointer, date) VALUES ('" + playerID + "', '" + pointer.getPointer() + "', '" + pointer.getTimestamp() + "')");
        }
	}

	/**
	 * @return the objectiveRes
	 */
	public ConcurrentHashMap<String,ObjectiveRes> getObjectiveRes() {
		return objectiveRes;
	}
	
	/**
	 * @return the stringsRes
	 */
	public ConcurrentHashMap<String,StringRes> getStringsRes() {
		return stringsRes;
	}
	
	/**
	 * @return the journalRes
	 */
	public ConcurrentHashMap<String,JournalRes> getJournalRes() {
		return journalRes;
	}
	
	/**
	 * @return the pointRes
	 */
	public ConcurrentHashMap<String,PointRes> getPointRes() {
		return pointRes;
	}
	
	/**
	 * loads all player data from database and puts it to concurrent HashMap, so it's safe to call it in async thread
	 * @param playerID
	 */
	public void loadAllPlayerData(String playerID) {
		try {
			// load objectives
			ResultSet res1 = MySQL.querySQL("SELECT instructions FROM objectives WHERE playerID = '" + playerID + "' AND isused= 1;");
			if (!res1.isBeforeFirst()) {
				res1 = MySQL.querySQL("SELECT instructions FROM objectives WHERE playerID = '" + playerID + "' AND isused= 0;");
			}
			getObjectiveRes().put(playerID, new ObjectiveRes(res1));
			MySQL.updateSQL("UPDATE objectives SET isused = 1 WHERE playerID = '" + playerID + "' AND isused = 0;");
			// load strings
			ResultSet res2 = MySQL.querySQL("SELECT string FROM strings WHERE playerID = '" + playerID + "' AND isused = 1;");
			if (!res2.isBeforeFirst()) {
				res2 = MySQL.querySQL("SELECT string FROM strings WHERE playerID = '" + playerID + "' AND isused = 0;");
			}
			getStringsRes().put(playerID, new StringRes(res2));
			MySQL.updateSQL("UPDATE strings SET isused = 1 WHERE playerID = '" + playerID + "' AND isused = 0");
			// load journals
			ResultSet res3 = MySQL.querySQL("SELECT pointer, date FROM journal WHERE playerID = '" + playerID + "'");
			getJournalRes().put(playerID, new JournalRes(res3));
			// load points
			ResultSet res4 = MySQL.querySQL("SELECT category, count FROM points WHERE playerID = '" + playerID + "'");
			getPointRes().put(playerID, new PointRes(res4));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteObjective(String playerID, String tag) {
		for (ObjectiveSaving objective : saving) {
			if (objective.getPlayerID().equals(playerID) && objective.getTag() != null && objective.getTag().equalsIgnoreCase(tag)) {
				objective.deleteThis();
			}
		}
	}
	
	public List<ObjectiveSaving> getObjectives(String playerID) {
		List<ObjectiveSaving> list = new ArrayList<ObjectiveSaving>();
		for (ObjectiveSaving objective : saving) {
			if (objective.getPlayerID().equals(playerID) && objective.getTag() != null) {
				list.add(objective);
			}
		}
		return list;
	}
	
	public void purgePlayer(final String playerID) {
		if (Bukkit.getPlayer(playerID) != null) {
			if (playerStrings.get(playerID) != null) {
				playerStrings.get(playerID).clear();
			}
			if (journals.get(playerID) != null) {
				journals.get(playerID).clear();
			}
			if (points.get(playerID) != null) {
				points.get(playerID).clear();
			}
			List<ObjectiveSaving> list = new ArrayList<ObjectiveSaving>();
			Iterator<ObjectiveSaving> iterator = saving.iterator();
			while (iterator.hasNext()) {
				ObjectiveSaving objective = (ObjectiveSaving) iterator.next();
				if (objective.getPlayerID().equals(playerID)) {
					list.add(objective);
				}
			}
			for (ObjectiveSaving objective : list) {
				objective.saveObjective();
			}
			JournalBook.updateJournal(playerID);
		}
		new BukkitRunnable() {
            @Override
            public void run() {
        		BetonQuest.getInstance().getMySQL().updateSQL("DELETE FROM objectives WHERE playerID='" + playerID + "'");
        		BetonQuest.getInstance().getMySQL().updateSQL("DELETE FROM journal WHERE playerID='" + playerID + "'");
        		BetonQuest.getInstance().getMySQL().updateSQL("DELETE FROM strings WHERE playerID='" + playerID + "'");
        		BetonQuest.getInstance().getMySQL().updateSQL("DELETE FROM points WHERE playerID='" + playerID + "'");
            }
        }.runTaskAsynchronously(BetonQuest.getInstance());
	}
}
