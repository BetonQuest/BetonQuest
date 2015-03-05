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
package pl.betoncraft.betonquest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.commands.JournalCommand;
import pl.betoncraft.betonquest.commands.QuestCommand;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.conditions.AlternativeCondition;
import pl.betoncraft.betonquest.conditions.ArmorCondition;
import pl.betoncraft.betonquest.conditions.ArmorRatingCondition;
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
import pl.betoncraft.betonquest.conditions.RandomCondition;
import pl.betoncraft.betonquest.conditions.SneakCondition;
import pl.betoncraft.betonquest.conditions.TagCondition;
import pl.betoncraft.betonquest.conditions.TimeCondition;
import pl.betoncraft.betonquest.conditions.WeatherCondition;
import pl.betoncraft.betonquest.config.ConfigHandler;
import pl.betoncraft.betonquest.config.ConfigUpdater;
import pl.betoncraft.betonquest.core.CubeNPCListener;
import pl.betoncraft.betonquest.core.GlobalLocations;
import pl.betoncraft.betonquest.core.JoinQuitListener;
import pl.betoncraft.betonquest.core.QuestItemHandler;
import pl.betoncraft.betonquest.database.Database;
import pl.betoncraft.betonquest.database.DatabaseHandler;
import pl.betoncraft.betonquest.database.MySQL;
import pl.betoncraft.betonquest.database.SQLite;
import pl.betoncraft.betonquest.events.CommandEvent;
import pl.betoncraft.betonquest.events.ConversationEvent;
import pl.betoncraft.betonquest.events.DeleteObjectiveEvent;
import pl.betoncraft.betonquest.events.EffectEvent;
import pl.betoncraft.betonquest.events.ExplosionEvent;
import pl.betoncraft.betonquest.events.FolderEvent;
import pl.betoncraft.betonquest.events.GiveEvent;
import pl.betoncraft.betonquest.events.JournalEvent;
import pl.betoncraft.betonquest.events.KillEvent;
import pl.betoncraft.betonquest.events.LightningEvent;
import pl.betoncraft.betonquest.events.MessageEvent;
import pl.betoncraft.betonquest.events.ObjectiveEvent;
import pl.betoncraft.betonquest.events.PointEvent;
import pl.betoncraft.betonquest.events.SetBlockEvent;
import pl.betoncraft.betonquest.events.SpawnMobEvent;
import pl.betoncraft.betonquest.events.TagEvent;
import pl.betoncraft.betonquest.events.TakeEvent;
import pl.betoncraft.betonquest.events.TeleportEvent;
import pl.betoncraft.betonquest.events.TimeEvent;
import pl.betoncraft.betonquest.events.WeatherEvent;
import pl.betoncraft.betonquest.objectives.ActionObjective;
import pl.betoncraft.betonquest.objectives.BlockObjective;
import pl.betoncraft.betonquest.objectives.CraftingObjective;
import pl.betoncraft.betonquest.objectives.DelayObjective;
import pl.betoncraft.betonquest.objectives.DieObjective;
import pl.betoncraft.betonquest.objectives.LocationObjective;
import pl.betoncraft.betonquest.objectives.MobKillObjective;
import pl.betoncraft.betonquest.objectives.SmeltingObjective;
import pl.betoncraft.betonquest.objectives.TameObjective;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.Metrics;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Updater;
import pl.betoncraft.betonquest.utils.Updater.UpdateResult;
import pl.betoncraft.betonquest.utils.Utils;



/**
 * Represents BetonQuest plugin
 * 
 * @authors Co0sh, Dzejkop, BYK
 */
public final class BetonQuest extends JavaPlugin {
    /**
     * The plugin's instance
     */
    private static BetonQuest instance;
    /**
     * Instance of the Database object, which handles the database connection
     */
    private Database database;
    /**
     * This is true if MySQL is used for data storing
     */
    private boolean isMySQLUsed;
    /**
     * Handler for storing data in the database (does not handle the connection
     * itself)
     */
    private ConcurrentHashMap<String, DatabaseHandler> dbHandlers = new ConcurrentHashMap<>();
    /**
     * Stores all condition types with their corresponding classes
     */
    private static HashMap<String, Class<? extends Condition>> conditions = new HashMap<String, Class<? extends Condition>>();
    /**
     * Stores all event types with their corresponding classes
     */
    private static HashMap<String, Class<? extends QuestEvent>> events = new HashMap<String, Class<? extends QuestEvent>>();
    /**
     * Stores all objective types with their corresponding classes
     */
    private static HashMap<String, Class<? extends Objective>> objectives = new HashMap<String, Class<? extends Objective>>();

    @Override
    public void onEnable() {

        instance = this;

        // initialize debugger
        new Debug();

        // load configuration
        new ConfigHandler();

        // try to connect to database
        Debug.info("Connecting to MySQL database");
        this.database = new MySQL(this, getConfig().getString("mysql.host"), getConfig().getString(
                "mysql.port"), getConfig().getString("mysql.base"), getConfig().getString(
                "mysql.user"), getConfig().getString("mysql.pass"));

        // try to connect to MySQL
        if (database.openConnection() != null) {
            Debug.broadcast("Using MySQL for storing data!");
            isMySQLUsed = true;
            database.closeConnection();
            // if it fails use SQLite
        } else {
            this.database = new SQLite(this, "database.db");
            Debug.broadcast("Using SQLite for storing data!");
            isMySQLUsed = false;
        }

        // create tables in the database
        database.createTables(isMySQLUsed);
        
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

        // start timer for global locations
        new GlobalLocations().runTaskTimer(this, 20, 20);

        getCommand("q").setExecutor(new QuestCommand());
        getCommand("j").setExecutor(new JournalCommand());

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
        registerEvents("folder", FolderEvent.class);
        registerEvents("setblock", SetBlockEvent.class);

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

        // initialize compatibility with other plugins
        new Compatibility();

        // initialize PlayerConverter
        PlayerConverter.getType();

        // load data for all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            String playerID = PlayerConverter.getID(player);
            dbHandlers.put(playerID, new DatabaseHandler(playerID));
        }

        // metrics!
        if (getConfig().getString("metrics").equalsIgnoreCase("true")) {
            try {
                Metrics metrics = new Metrics(this);
                metrics.start();
                Debug.broadcast("Metrics enabled!");
            } catch (IOException e) {
                Debug.broadcast("Metrics faild to enable!");
            }
        } else {
            Debug.broadcast("Metrics are not used!");
        }

        // updater!
        if (getConfig().getString("autoupdate").equalsIgnoreCase("true")) {
            Debug.broadcast("AutoUpdater enabled!");
        } else {
            Debug.broadcast("AutoUpdater disabled!");
        }

        // done
        Debug.broadcast("BetonQuest succesfully enabled!");
    }

    @Override
    public void onDisable() {
        // stop global location listener
        GlobalLocations.stop();
        // save players' data
        for (Player player : Bukkit.getOnlinePlayers()) {
            dbHandlers.get(PlayerConverter.getID(player)).saveData();
        }
        // update if needed
        if (getConfig().getString("autoupdate").equalsIgnoreCase("true")) {
            Updater updater = new Updater(this, 86448, this.getFile(), Updater.UpdateType.DEFAULT,
                    false);
            if (updater.getResult().equals(UpdateResult.SUCCESS)) {
                Debug.broadcast("Found "
                    + updater.getLatestName()
                    + " update on DBO and downloaded it! Plugin will be automatically updated on next restart.");
            }
        }
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

    /**
     * Checks if MySQL is used or not
     * 
     * @return if MySQL is used (false means that SQLite is being used)
     */
    public boolean isMySQLUsed() {
        return isMySQLUsed;
    }

    /**
     * Stores the DatabaseHandler in a map, so it can be retrieved using
     * getDBHandler(String playerID)
     * 
     * @param playerID
     *            ID of the player
     * @param handler
     *            DatabaseHandler object to store
     */
    public void putDBHandler(String playerID, DatabaseHandler handler) {
        Debug.info("Inserting data for " + playerID);
        dbHandlers.put(playerID, handler);
    }

    /**
     * Retrieves DatabaseHandler object for specified player
     * 
     * @param playerID
     *            ID of the player
     * @return DatabaseHandler object for the player
     */
    public DatabaseHandler getDBHandler(String playerID) {
        return dbHandlers.get(playerID);
    }
    
    /**
     * Removes the database handler from the map
     * 
     * @param playerID
     *          ID of the player whose handler is to be removed
     */
    public void removeDBHandler(String playerID) {
        dbHandlers.remove(playerID);
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
        conditions.put(name, conditionClass);
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
        events.put(name, eventClass);
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
        objectives.put(name, objectiveClass);
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
        if (playerID == null || conditionID == null) {
            Debug.info("Null arguments for the condition!");
            return false;
        }
        // check for online player
        if (PlayerConverter.getPlayer(playerID) == null) {
            Debug.info("Player was offline, returning false");
            return false;
        }
        // check for inverted condition
        boolean inverted = false;
        if (conditionID.startsWith("!")) {
            conditionID = conditionID.substring(1);
            inverted = true;
        }
        // get instruction string
        String conditionInstruction = ConfigHandler.getString("conditions." + conditionID);
        // if it doesn't exist log an error
        if (conditionInstruction == null) {
            Debug.error("Tried to access condition with ID \"" + conditionID
                + "\", but it isn't defined! The condition will be false for now.");
            return false;
        }
        // get condition's class
        String[] parts = conditionInstruction.split(" ");
        Class<? extends Condition> condition = conditions.get(parts[0]);
        Condition instance = null;
        // if it's null then there is no such type registered, log an error
        if (condition == null) {
            Debug.error("Condition type \"" + parts[0]
                + "\" is not registered, check if it's spelled " + "correctly in \"" + conditionID
                + "\" condition.");
            return false;
        }
        // instantiate condition
        try {
            instance = condition.getConstructor(String.class, String.class).newInstance(playerID,
                    conditionInstruction);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            // return false for safety
            return false;
        }
        // and check if it's met or not
        boolean isMet = (instance.isMet() && !inverted) || (!instance.isMet() && inverted);
        Debug.info((isMet ? "TRUE" : "FALSE") + ": " + (inverted ? "inverted" : "") + " condition " + conditionID + " for player "
            + playerID);
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
        if (playerID == null || eventID == null) {
            Debug.info("Null arguments for the event!");
            return;
        }
        // get instruction string
        String eventInstruction = ConfigHandler.getString("events." + eventID);
        // if it's null then log an error
        if (eventInstruction == null) {
            Debug.error("Tried to access event with ID \"" + eventID
                + "\", but it isn't defined! The event won't fire.");
            return;
        }
        // check conditions
        String[] parts = eventInstruction.split(" ");
        for (String part : parts) {
            if (part.startsWith("event_conditions:")) {
                String[] conditions = part.substring(17).split(",");
                for (String condition : conditions) {
                    if (!condition(playerID, condition)) {
                        Debug.info("Conditions for event " + eventID + " were not met for "
                            + playerID);
                        return;
                    }
                }
                break;
            }
        }
        // get event's class
        Class<? extends QuestEvent> event = events.get(parts[0]);
        if (event == null) {
            // if it's null then there is no such type registered, log an error
            Debug.error("Event type \"" + parts[0]
                + "\" is not registered, check if it's spelled correctly in \"" + eventID
                + "\" event.");
            return;
        }
        try {
            // fire an event
            event.getConstructor(String.class, String.class)
                    .newInstance(playerID, eventInstruction);
            Debug.info("Event " + eventID + " fired for " + playerID);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates new objective for given player
     * 
     * @param playerID
     *            ID of the player who should receive this objective
     * @param instruction
     *            instruction string to instantiate the objective
     */
    public static void objective(String playerID, String instruction) {
        // null check
        if (playerID == null || instruction == null) {
            Debug.info("Null arguments for the objective!");
            return;
        }
        // get tag
        String[] parts = instruction.split(" ");
        String tag = null;
        for (String part : parts) {
            if (part.contains("tag:")) {
                tag = part.substring(4);
                break;
            }
        }
        if (tag == null) {
            // the tag is required, so log an error if it's not supplied
            Debug.error("Tag was not found in an objective, it's required. Player: " + playerID
                + ", instruction: " + instruction);
            return;
        }
        // get objective's class
        Class<? extends Objective> objective = objectives.get(parts[0]);
        if (objective == null) {
            // if it's null then objective type has not been registered, log an
            // error
            Debug.error("Objective type \"" + parts[0]
                + "\" is not registered, check if it's spelled correctly in \"" + instruction
                + "\".");
            return;
        }
        try {
            // start the objective
            getInstance().getDBHandler(playerID).addObjective(
                    objective.getConstructor(String.class, String.class).newInstance(playerID,
                            instruction));
            Debug.info("Created new objective from instruction \"" + instruction + "\" with \""
                + tag + "\" tag for player " + playerID);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }
}