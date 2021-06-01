package pl.betoncraft.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Journal;
import pl.betoncraft.betonquest.Point;
import pl.betoncraft.betonquest.Pointer;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.QuestCanceler;
import pl.betoncraft.betonquest.database.Connector.QueryType;
import pl.betoncraft.betonquest.database.Connector.UpdateType;
import pl.betoncraft.betonquest.database.Saver.Record;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.id.ObjectiveID;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

/**
 * Represents an object storing all player-related data, which can load and save it.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.CommentRequired"})
@SuppressFBWarnings("JLM_JSR166_UTILCONCURRENT_MONITORENTER")
public class PlayerData {

    @SuppressWarnings("PMD.DoNotUseThreads")
    private final Saver saver = BetonQuest.getInstance().getSaver();

    private final String playerID;

    private final List<String> tags = new CopyOnWriteArrayList<>();
    private final List<Pointer> entries = new CopyOnWriteArrayList<>();
    private final List<Point> points = new CopyOnWriteArrayList<>();
    private final Map<String, String> objectives = new ConcurrentHashMap<>();
    private Journal journal;
    private List<ItemStack> backpack = new CopyOnWriteArrayList<>();
    private String conv;

    private String lang; // the player's language

    /**
     * Creates new PlayerData for the player represented by playerID.
     *
     * @param playerID - ID of the player
     */
    public PlayerData(final String playerID) {
        this.playerID = playerID;
        // load data from the database
        loadAllPlayerData();
    }

    /**
     * Loads all data for the player and puts it in appropriate lists.
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    public final void loadAllPlayerData() {
        try {
            final Connector con = new Connector();

            try (ResultSet res1 = con.querySQL(QueryType.SELECT_OBJECTIVES, playerID);
                 ResultSet res2 = con.querySQL(QueryType.SELECT_TAGS, playerID);
                 ResultSet res3 = con.querySQL(QueryType.SELECT_JOURNAL, playerID);
                 ResultSet res4 = con.querySQL(QueryType.SELECT_POINTS, playerID);
                 ResultSet res5 = con.querySQL(QueryType.SELECT_BACKPACK, playerID);
                 ResultSet res6 = con.querySQL(QueryType.SELECT_PLAYER, playerID)) {

                // put them into the list
                while (res1.next()) {
                    objectives.put(res1.getString("objective"), res1.getString("instructions"));
                }

                // put them into the list
                while (res2.next()) {
                    tags.add(res2.getString("tag"));
                }

                // put them into the list
                while (res3.next()) {
                    entries.add(new Pointer(res3.getString("pointer"), res3.getTimestamp("date").getTime()));
                }

                // put them into the list
                while (res4.next()) {
                    points.add(new Point(res4.getString("category"), res4.getInt("count")));
                }

                // put items into the list
                while (res5.next()) {
                    final String instruction = res5.getString("instruction");
                    final int amount = res5.getInt("amount");
                    final ItemStack item;
                    try {
                        item = new QuestItem(instruction).generate(amount);
                    } catch (final InstructionParseException e) {
                        LogUtils.getLogger().log(Level.WARNING, "Could not load backpack item for player " + PlayerConverter.getName(playerID)
                                + ", with instruction '" + instruction + "', because: " + e.getMessage());
                        LogUtils.logThrowable(e);
                        continue;
                    }
                    backpack.add(item);
                }

                // put it there
                if (res6.next()) {
                    lang = res6.getString("language");
                    if ("default".equals(lang)) {
                        lang = Config.getLanguage();
                    }
                    conv = res6.getString("conversation");
                    if (conv == null || conv.equalsIgnoreCase("null")) {
                        conv = null;
                    }
                } else {
                    lang = Config.getLanguage();
                    saver.add(new Record(UpdateType.ADD_PLAYER, playerID, "default"));
                }

                // log data to debugger
                LogUtils.getLogger().log(Level.FINE, "There are " + objectives.size() + " objectives, " + tags.size() + " tags, " + points.size()
                        + " points, " + entries.size() + " journal entries and " + backpack.size()
                        + " items loaded for player " + PlayerConverter.getName(playerID));
            }
        } catch (final SQLException e) {
            LogUtils.getLogger().log(Level.SEVERE, "There was an exception with SQL");
            LogUtils.logThrowable(e);
        }
    }

    /**
     * Returns the List of Tags for this player.
     *
     * @return the List of Tags
     */
    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /**
     * Checks if the player has specified tag.
     *
     * @param tag tag to check
     * @return true if the player has this tag
     */
    public boolean hasTag(final String tag) {
        return tags.contains(tag);
    }

    /**
     * Adds the specified tag to player's list. It won't double it however.
     *
     * @param tag tag to add
     */
    public void addTag(final String tag) {
        synchronized (tags) {
            if (!tags.contains(tag)) {
                tags.add(tag);
                saver.add(new Record(UpdateType.ADD_TAGS, playerID, tag));
            }
        }
    }

    /**
     * Removes the specified tag from player's list. If there is no tag, nothing
     * happens.
     *
     * @param tag tag to remove
     */
    public void removeTag(final String tag) {
        synchronized (tags) {
            tags.remove(tag);
            saver.add(new Record(UpdateType.REMOVE_TAGS, playerID, tag));
        }
    }

    /**
     * Returns the List of Points for this player.
     *
     * @return the List of Points
     */
    public List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }

    /**
     * Returns the amount of point the player has in specified category. If the
     * category does not exist, it will return 0.
     *
     * @param category name of the category
     * @return amount of points
     */
    @SuppressWarnings("PMD.LinguisticNaming")
    public int hasPointsFromCategory(final String category) {
        synchronized (points) {
            for (final Point p : points) {
                if (p.getCategory().equals(category)) {
                    return p.getCount();
                }
            }
            return 0;
        }
    }

    /**
     * Adds or subtracts points to/from specified category. If there is no such category it will
     * be created.
     *
     * @param category points will be added to this category
     * @param count    how much points will be added (or subtracted if negative)
     */
    public void modifyPoints(final String category, final int count) {
        synchronized (points) {
            saver.add(new Record(UpdateType.REMOVE_POINTS, playerID, category));
            // check if the category already exists
            for (final Point point : points) {
                if (point.getCategory().equalsIgnoreCase(category)) {
                    // if it does, add points to it
                    saver.add(new Record(UpdateType.ADD_POINTS,
                            playerID, category, String.valueOf(point.getCount() + count)));
                    point.addPoints(count);
                    return;
                }
            }
            // if not then create new point category with given amount of points
            points.add(new Point(category, count));
            saver.add(new Record(UpdateType.ADD_POINTS, playerID, category, String.valueOf(count)));
        }
    }

    /**
     * Removes the whole category of points.
     *
     * @param category name of a point category
     */
    public void removePointsCategory(final String category) {
        synchronized (points) {
            Point pointToRemove = null;
            for (final Point point : points) {
                if (point.getCategory().equalsIgnoreCase(category)) {
                    pointToRemove = point;
                }
            }
            if (pointToRemove != null) {
                points.remove(pointToRemove);
            }
            saver.add(new Record(UpdateType.REMOVE_POINTS, playerID, category));
        }
    }

    /**
     * Returns a Journal instance or creates it if it does not exist.
     *
     * @return new Journal instance
     */
    public Journal getJournal() {
        if (journal == null) {
            journal = new Journal(playerID, lang, entries);
        }
        return journal;
    }

    /**
     * Starts all Objectives for this player. It takes all "raw" objectives and
     * initializes them. Raw objectives are deleted from their HashMap after
     * this action (so they won't be started twice)
     */
    public void startObjectives() {
        for (final Map.Entry<String, String> entry : objectives.entrySet()) {
            final String objective = entry.getKey();
            try {
                final ObjectiveID objectiveID = new ObjectiveID(null, objective);
                BetonQuest.resumeObjective(playerID, objectiveID, entry.getValue());
            } catch (final ObjectNotFoundException e) {
                LogUtils.getLogger().log(Level.WARNING, "Loaded '" + objective
                        + "' objective from the database, but it is not defined in configuration. Skipping.");
                LogUtils.logThrowable(e);
            }
        }
        objectives.clear();
    }

    /**
     * @return the map containing objective IDs and their objective data; these
     * are not initialized yet
     */
    public Map<String, String> getRawObjectives() {
        return objectives;
    }

    /**
     * Adds new objective to a list of not initialized objectives. It's added to the
     * database and can be started by running {@link #startObjectives()}.
     *
     * @param objectiveID ID of the objective
     */
    public void addNewRawObjective(final ObjectiveID objectiveID) {
        final Objective obj = BetonQuest.getInstance().getObjective(objectiveID);
        if (obj == null) {
            return;
        }
        final String data = obj.getDefaultDataInstruction();
        if (addRawObjective(objectiveID.toString(), data)) {
            saver.add(new Record(UpdateType.ADD_OBJECTIVES, playerID, objectiveID.toString(), data));
        }
    }

    /**
     * Adds objective to a list of not initialized objectives. This does not add
     * the objective to the database because it's not a new objective, hence
     * it's already in the database.
     *
     * @param objectiveID ID of the objective
     * @param data        data instruction string to use
     * @return true if the objective was successfully added, false if it was
     * already there
     */
    public boolean addRawObjective(final String objectiveID, final String data) {
        if (objectives.containsKey(objectiveID)) {
            return false;
        }
        objectives.put(objectiveID, data);
        return true;
    }

    /**
     * Removes not initialized objective from the plugin and the database.
     *
     * @param objectiveID the ID of the objective
     */
    public void removeRawObjective(final ObjectiveID objectiveID) {
        objectives.remove(objectiveID.toString());
        removeObjFromDB(objectiveID.toString());
    }

    /**
     * Directly adds specified objectiveID and data string to the database.
     *
     * @param objectiveID the ID of the objective
     * @param data        the data string of this objective (the one associated with ObjectiveData)
     */
    public void addObjToDB(final String objectiveID, final String data) {
        saver.add(new Record(UpdateType.ADD_OBJECTIVES, playerID, objectiveID, data));
    }

    /**
     * Directly removes from the database specified objective.
     *
     * @param objectiveID the ID of the objective to remove
     */
    public void removeObjFromDB(final String objectiveID) {
        saver.add(new Record(UpdateType.REMOVE_OBJECTIVES, playerID, objectiveID));
    }

    /**
     * Returns player's backpack as the list of itemstacks.
     *
     * @return list of itemstacks
     */
    public List<ItemStack> getBackpack() {
        return (List<ItemStack>) copyItemList(backpack, new ArrayList<>());
    }

    /**
     * Updates the database with a list of backpack items.
     *
     * @param list list of all items in the backpack
     */
    public void setBackpack(final List<ItemStack> list) {
        this.backpack = (List<ItemStack>) copyItemList(list, new CopyOnWriteArrayList<>());

        // update the database (quite expensive way, should be changed)
        saver.add(new Record(UpdateType.DELETE_BACKPACK, playerID));
        for (final ItemStack itemStack : list) {
            final String instruction = QuestItem.itemToString(itemStack);
            final String amount = String.valueOf(itemStack.getAmount());
            saver.add(new Record(UpdateType.ADD_BACKPACK, playerID, instruction, amount));
        }
    }

    /**
     * @return a list of the players journal entries
     */
    public List<Pointer> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Adds the item to backpack. The amount of the itemstack doesn't matter,
     * it's overwritten by amount parameter. Amount can be greater than max
     * stack size.
     *
     * @param item   ItemStack to add to backpack
     * @param amount amount of the items
     */
    public void addItem(final ItemStack item, final int amount) {
        int inputAmount = amount;
        for (final ItemStack itemStack : backpack) {
            if (item.isSimilar(itemStack)) {
                // if items are similar they can be joined in a single itemstack
                if (inputAmount + itemStack.getAmount() <= itemStack.getMaxStackSize()) {
                    // if they will fit all together, then just add them
                    itemStack.setAmount(itemStack.getAmount() + inputAmount);
                    inputAmount = 0; // this will allow for passing the while loop
                    break;
                } else {
                    // if the stack will be overflown, set max size and continue
                    inputAmount -= itemStack.getMaxStackSize() - itemStack.getAmount();
                    itemStack.setAmount(itemStack.getMaxStackSize());
                }
            }
        }
        // every item checked, time to add a new itemstack
        while (inputAmount > 0) {
            // if the amount is greater than max size of the itemstack, create
            // max
            // stacks until it's lower
            final ItemStack newItem = item.clone();
            int maxSize = newItem.getType().getMaxStackSize();
            if (inputAmount > maxSize) {
                if (maxSize == 0) {
                    maxSize = 64;
                }
                newItem.setAmount(maxSize);
                inputAmount -= maxSize;
            } else {
                newItem.setAmount(inputAmount);
                inputAmount = 0;
            }
            backpack.add(newItem);
        }
        // update the database (quite expensive way, should be changed)
        saver.add(new Record(UpdateType.DELETE_BACKPACK, playerID));
        for (final ItemStack itemStack : backpack) {
            final String instruction = QuestItem.itemToString(itemStack);
            final String newAmount = String.valueOf(itemStack.getAmount());
            saver.add(new Record(UpdateType.ADD_BACKPACK, playerID, instruction, newAmount));
        }
    }

    /**
     * Cancels the quest by removing all defined tags, points, objectives etc.
     *
     * @param name name of the canceler
     */
    public void cancelQuest(final String name) {
        final QuestCanceler canceler = Config.getCancelers().get(name);
        if (canceler != null) {
            canceler.cancel(playerID);
        }
    }

    /**
     * @return the language this player uses
     */
    public String getLanguage() {
        return lang;
    }

    /**
     * Sets player's language
     *
     * @param lang language to set
     */
    public void setLanguage(final String lang) {
        if ("default".equalsIgnoreCase(lang)) {
            this.lang = Config.getLanguage();
        } else {
            this.lang = lang;
        }
        saver.add(new Record(UpdateType.DELETE_PLAYER, playerID));
        saver.add(new Record(UpdateType.ADD_PLAYER, playerID, lang));
    }

    /**
     * @return the name of a conversation if the player has active one or
     * null if he does not.
     */
    public String getConversation() {
        return conv;
    }

    /**
     * Purges all player's data from the database and from this object.
     */
    public void purgePlayer() {
        for (final Objective obj : BetonQuest.getInstance().getPlayerObjectives(playerID)) {
            obj.removePlayer(playerID);
        }
        // clear all lists
        objectives.clear();
        tags.clear();
        points.clear();
        entries.clear();
        getJournal().clear(); // journal can be null, so use a method to get it
        backpack.clear();
        // clear the database
        saver.add(new Record(UpdateType.DELETE_OBJECTIVES, playerID));
        saver.add(new Record(UpdateType.DELETE_JOURNAL, playerID));
        saver.add(new Record(UpdateType.DELETE_POINTS, playerID));
        saver.add(new Record(UpdateType.DELETE_TAGS, playerID));
        saver.add(new Record(UpdateType.DELETE_BACKPACK, playerID));
        saver.add(new Record(UpdateType.UPDATE_CONVERSATION, "null", playerID));
        // update the journal so it's empty
        if (PlayerConverter.getPlayer(playerID) != null) {
            getJournal().update();
        }
    }

    private Collection<ItemStack> copyItemList(final Collection<ItemStack> source, final Collection<ItemStack> target) {
        for (final ItemStack itemStack : source) {
            target.add(itemStack.clone());
        }
        return target;
    }
}
