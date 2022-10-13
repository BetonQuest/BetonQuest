package org.betonquest.betonquest.database;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.Point;
import org.betonquest.betonquest.Pointer;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.PlayerTagAddEvent;
import org.betonquest.betonquest.api.PlayerTagRemoveEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.database.Saver.Record;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents an object storing all profile-related data, which can load and save it.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.CommentRequired"})
@CustomLog
public class PlayerData implements TagData {

    @SuppressWarnings("PMD.DoNotUseThreads")
    private final Saver saver = BetonQuest.getInstance().getSaver();

    private final Profile profile;

    private final List<String> tags = new CopyOnWriteArrayList<>();
    private final List<Pointer> entries = new CopyOnWriteArrayList<>();
    private final List<Point> points = new CopyOnWriteArrayList<>();
    private final Map<String, String> objectives = new ConcurrentHashMap<>();
    private Journal journal;
    private List<ItemStack> backpack = new CopyOnWriteArrayList<>();
    private String conv;

    private String lang; // the profile's language

    /**
     * Creates new PlayerData for the player represented by the {@link Profile} of the player.
     *
     * @param profile - the {@link Profile} of the player
     */
    public PlayerData(final Profile profile) {
        this.profile = profile;
        // load data from the database
        loadAllPlayerData();
    }

    /**
     * Loads all data for the profile and puts it in appropriate lists.
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    public final void loadAllPlayerData() {
        try {
            final Connector con = new Connector();

            try (ResultSet res1 = con.querySQL(QueryType.SELECT_OBJECTIVES, profile.getProfileUUID().toString());
                 ResultSet res2 = con.querySQL(QueryType.SELECT_TAGS, profile.getProfileUUID().toString());
                 ResultSet res3 = con.querySQL(QueryType.SELECT_JOURNAL, profile.getProfileUUID().toString());
                 ResultSet res4 = con.querySQL(QueryType.SELECT_POINTS, profile.getProfileUUID().toString());
                 ResultSet res5 = con.querySQL(QueryType.SELECT_BACKPACK, profile.getProfileUUID().toString());
                 ResultSet res6 = con.querySQL(QueryType.SELECT_PLAYER, profile.getProfileUUID().toString())) {

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
                        LOG.warn("Could not load backpack item for player " + profile.getProfileName()
                                + ", with instruction '" + instruction + "', because: " + e.getMessage(), e);
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
                    saver.add(new Record(UpdateType.ADD_PLAYER, profile.getProfileUUID().toString(), "default"));
                }

                // log data to debugger
                LOG.debug("There are " + objectives.size() + " objectives, " + tags.size() + " tags, " + points.size()
                        + " points, " + entries.size() + " journal entries and " + backpack.size()
                        + " items loaded for player " + profile.getProfileName());
            }
        } catch (final SQLException e) {
            LOG.error("There was an exception with SQL", e);
        }
    }

    /**
     * Returns the List of Tags for this profile.
     *
     * @return the List of Tags
     */
    @Override
    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /**
     * Checks if the profile has specified tag.
     *
     * @param tag tag to check
     * @return true if the player has this tag
     */
    @Override
    public boolean hasTag(final String tag) {
        return tags.contains(tag);
    }

    /**
     * Adds the specified tag to profile's list. It won't double it however.
     *
     * @param tag tag to add
     */
    @Override
    public void addTag(final String tag) {
        synchronized (tags) {
            if (!tags.contains(tag)) {
                tags.add(tag);
                saver.add(new Record(UpdateType.ADD_TAGS, profile.getProfileUUID().toString(), tag));
                BetonQuest.getInstance()
                        .callSyncBukkitEvent(new PlayerTagAddEvent(profile, tag));
            }
        }
    }

    /**
     * Removes the specified tag from profile's list. If there is no tag, nothing
     * happens.
     *
     * @param tag tag to remove
     */
    @Override
    public void removeTag(final String tag) {
        synchronized (tags) {
            if (tags.contains(tag)) {
                tags.remove(tag);
                saver.add(new Record(UpdateType.REMOVE_TAGS, profile.getProfileUUID().toString(), tag));
                BetonQuest.getInstance()
                        .callSyncBukkitEvent(new PlayerTagRemoveEvent(profile, tag));
            }
        }
    }

    /**
     * Returns the List of Points for this profile.
     *
     * @return the List of Points
     */
    public List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }

    /**
     * Returns the amount of point the profile has in specified category. If the
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
            saver.add(new Record(UpdateType.REMOVE_POINTS, profile.getProfileUUID().toString(), category));
            // check if the category already exists
            for (final Point point : points) {
                if (point.getCategory().equalsIgnoreCase(category)) {
                    // if it does, add points to it
                    saver.add(new Record(UpdateType.ADD_POINTS,
                            profile.getProfileUUID().toString(), category, String.valueOf(point.getCount() + count)));
                    point.addPoints(count);
                    return;
                }
            }
            // if not then create new point category with given amount of points
            points.add(new Point(category, count));
            saver.add(new Record(UpdateType.ADD_POINTS, profile.getProfileUUID().toString(), category, String.valueOf(count)));
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
            saver.add(new Record(UpdateType.REMOVE_POINTS, profile.getProfileUUID().toString(), category));
        }
    }

    /**
     * Returns a Journal instance or creates it if it does not exist.
     *
     * @return new Journal instance
     */
    public Journal getJournal() {
        if (journal == null) {
            journal = new Journal(profile, lang, entries, BetonQuest.getInstance().getPluginConfig());
        }
        return journal;
    }

    /**
     * Starts all Objectives for this profile. It takes all "raw" objectives and
     * initializes them. Raw objectives are deleted from their HashMap after
     * this action (so they won't be started twice)
     */
    public void startObjectives() {
        for (final Map.Entry<String, String> entry : objectives.entrySet()) {
            final String objective = entry.getKey();
            try {
                final ObjectiveID objectiveID = new ObjectiveID(null, objective);
                BetonQuest.resumeObjective(profile, objectiveID, entry.getValue());
            } catch (final ObjectNotFoundException e) {
                LOG.warn("Loaded '" + objective
                        + "' objective from the database, but it is not defined in configuration. Skipping.", e);
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
        final String data = obj.getDefaultDataInstruction(profile);
        if (addRawObjective(objectiveID.toString(), data)) {
            saver.add(new Record(UpdateType.ADD_OBJECTIVES, profile.getProfileUUID().toString(), objectiveID.toString(), data));
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
        saver.add(new Record(UpdateType.ADD_OBJECTIVES, profile.getProfileUUID().toString(), objectiveID, data));
    }

    /**
     * Directly removes from the database specified objective.
     *
     * @param objectiveID the ID of the objective to remove
     */
    public void removeObjFromDB(final String objectiveID) {
        saver.add(new Record(UpdateType.REMOVE_OBJECTIVES, profile.getProfileUUID().toString(), objectiveID));
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
        saver.add(new Record(UpdateType.DELETE_BACKPACK, profile.getProfileUUID().toString()));
        for (final ItemStack itemStack : list) {
            final String instruction = QuestItem.itemToString(itemStack);
            final String amount = String.valueOf(itemStack.getAmount());
            saver.add(new Record(UpdateType.ADD_BACKPACK, profile.getProfileUUID().toString(), instruction, amount));
        }
    }

    /**
     * @return a list of the profiles journal entries
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
    @SuppressWarnings("PMD.CognitiveComplexity")
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
        saver.add(new Record(UpdateType.DELETE_BACKPACK, profile.getProfileUUID().toString()));
        for (final ItemStack itemStack : backpack) {
            final String instruction = QuestItem.itemToString(itemStack);
            final String newAmount = String.valueOf(itemStack.getAmount());
            saver.add(new Record(UpdateType.ADD_BACKPACK, profile.getProfileUUID().toString(), instruction, newAmount));
        }
    }

    /**
     * @return the language this profile uses
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
        saver.add(new Record(UpdateType.DELETE_PLAYER, profile.getProfileUUID().toString()));
        saver.add(new Record(UpdateType.ADD_PLAYER, profile.getProfileUUID().toString(), lang));
    }

    /**
     * @return the name of a conversation if the profile has active one or
     * null if he does not.
     */
    public String getConversation() {
        return conv;
    }

    /**
     * Purges all profile's data from the database and from this object.
     */
    public void purgePlayer() {
        for (final Objective obj : BetonQuest.getInstance().getPlayerObjectives(profile)) {
            obj.cancelObjectiveForPlayer(profile);
        }
        // clear all lists
        objectives.clear();
        tags.clear();
        points.clear();
        entries.clear();
        getJournal().clear(); // journal can be null, so use a method to get it
        backpack.clear();
        // clear the database
        saver.add(new Record(UpdateType.DELETE_OBJECTIVES, profile.getProfileUUID().toString()));
        saver.add(new Record(UpdateType.DELETE_JOURNAL, profile.getProfileUUID().toString()));
        saver.add(new Record(UpdateType.DELETE_POINTS, profile.getProfileUUID().toString()));
        saver.add(new Record(UpdateType.DELETE_TAGS, profile.getProfileUUID().toString()));
        saver.add(new Record(UpdateType.DELETE_BACKPACK, profile.getProfileUUID().toString()));
        saver.add(new Record(UpdateType.UPDATE_CONVERSATION, "null", profile.getProfileUUID().toString()));
        // update the journal so it's empty
        if (profile.getPlayer().isPresent()) {
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
