package org.betonquest.betonquest.database;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.PlayerTagAddEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerTagRemoveEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerUpdatePointEvent;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.conversation.PlayerConversationState;
import org.betonquest.betonquest.database.Saver.Record;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.feature.journal.JournalFactory;
import org.betonquest.betonquest.feature.journal.Pointer;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents an object storing all profile-related data, which can load and save it.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidSynchronizedStatement", "PMD.CouplingBetweenObjects"})
public class PlayerData implements TagData, PointData {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The database saver for player data.
     */
    private final Saver saver;

    /**
     * The server to determine if an event should be async.
     */
    private final Server server;

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

    /**
     * Factory to create a new Journal.
     */
    private final JournalFactory journalFactory;

    /**
     * Identifier registry to resolve identifiers.
     */
    private final Identifiers identifierRegistry;

    /**
     * The profile this data belongs to.
     */
    private final Profile profile;

    /**
     * The profileID of the data.
     */
    private final String profileID;

    /**
     * List of tags the player has.
     */
    private final Set<String> tags = ConcurrentHashMap.newKeySet();

    /**
     * List of journal entries the player has.
     */
    private final List<Pointer> entries = new CopyOnWriteArrayList<>();

    /**
     * List of points the player has.
     */
    private final Map<String, Point> points = new ConcurrentHashMap<>();

    /**
     * List of not loaded objectiveIDs and their data instructions.
     */
    private final Map<String, String> objectives = new ConcurrentHashMap<>();

    /**
     * The journal of the player, which contains all journal entries.
     */
    @Nullable
    private Journal journal;

    /**
     * The player's backpack, which is a list of itemstacks.
     */
    private List<ItemStack> backpack = new CopyOnWriteArrayList<>();

    /**
     * The state of a conversation the player is in.
     */
    @Nullable
    private PlayerConversationState activeConversation;

    /**
     * The language for the profile.
     */
    @Nullable
    private String profileLanguage;

    /**
     * Loads the PlayerData of the given {@link Profile}.
     *
     * @param log                the custom logger for this class
     * @param saver              the saver to persist data changes
     * @param server             the server to determine if an event should be stated as async
     * @param identifierRegistry the identifier registry to resolve identifiers
     * @param objectiveManager   the objective manager
     * @param journalFactory     the factory to create a new journal
     * @param profile            the profile to load the data for
     */
    public PlayerData(final BetonQuestLogger log, final Saver saver, final Server server,
                      final Identifiers identifierRegistry, final ObjectiveManager objectiveManager,
                      final JournalFactory journalFactory, final Profile profile) {
        this.log = log;
        this.identifierRegistry = identifierRegistry;
        this.objectiveManager = objectiveManager;
        this.saver = saver;
        this.server = server;
        this.journalFactory = journalFactory;
        this.profile = profile;
        this.profileID = profile.getProfileUUID().toString();
        try {
            loadAllPlayerData();
        } catch (final IllegalStateException e) {
            log.error("Could not load player data for profile '%s'. %s".formatted(profileID, e.getMessage()), e);
        }
    }

    private void loadAllPlayerData() {
        final Connector con = BetonQuest.getInstance().getDBConnector();
        final Arguments args = new Arguments(profileID);

        con.querySQL(QueryType.SELECT_OBJECTIVES, args, resultSet -> {
            while (resultSet.next()) {
                objectives.put(resultSet.getString("objective"), resultSet.getString("instructions"));
            }
        }, "Could not load objectives.");
        con.querySQL(QueryType.SELECT_TAGS, args, resultSet -> {
            while (resultSet.next()) {
                tags.add(resultSet.getString("tag"));
            }
        }, "Could not load tags.");
        con.querySQL(QueryType.SELECT_JOURNAL, args, resultSet -> {
            while (resultSet.next()) {
                loadJournalPointer(resultSet.getString("pointer"), resultSet.getTimestamp("date").getTime());
            }
        }, "Could not load journal entries.");
        con.querySQL(QueryType.SELECT_POINTS, args, resultSet -> {
            while (resultSet.next()) {
                final String category = resultSet.getString("category");
                points.put(category, new Point(category, resultSet.getInt("count")));
            }
        }, "Could not load points.");
        con.querySQL(QueryType.SELECT_BACKPACK, args, resultSet -> {
            while (resultSet.next()) {
                addItemToBackpack(resultSet);
            }
        }, "Could not load backpack.");
        con.querySQL(QueryType.SELECT_PLAYER, args, resultSet -> {
            if (resultSet.next()) {
                profileLanguage = resultSet.getString("language");
                loadActiveConversation(resultSet);
            } else {
                setupProfile();
            }
        }, "Could not load player data.");

        log.debug("Loaded %d objectives, %d tags, %d points, %d journal entries and %d items for %s"
                .formatted(objectives.size(), tags.size(), points.size(), entries.size(), backpack.size(), profile));
    }

    private void loadJournalPointer(final String pointer, final long date) {
        try {
            final JournalEntryIdentifier entryIdentifier = identifierRegistry.getFactory(JournalEntryIdentifier.class).parseIdentifier(null, pointer);
            entries.add(new Pointer(entryIdentifier, date));
        } catch (final QuestException e) {
            log.warn("Loaded '" + pointer
                    + "' journal entry from the database, but it is not defined in configuration. Skipping.", e);
        }
    }

    private void loadActiveConversation(final ResultSet playerResult) throws SQLException {
        final String fullInstruction = playerResult.getString("conversation");

        try {
            final Optional<PlayerConversationState> playerConversationState = PlayerConversationState.fromString(fullInstruction,
                    identifierRegistry.getFactory(ConversationIdentifier.class));
            playerConversationState.ifPresent(conversationState -> activeConversation = conversationState);
        } catch (final QuestException e) {
            log.debug("The profile" + profile + " is in a conversation that does not exist anymore ("
                    + fullInstruction + ").", e);
            saver.add(new Record(UpdateType.UPDATE_CONVERSATION, "null", profileID));
        }
    }

    private void setupProfile() {
        saver.add(new Record(UpdateType.ADD_PROFILE, profileID));
        saver.add(new Record(UpdateType.ADD_PLAYER, profile.getPlayer().getUniqueId().toString(),
                profileID, "default"));
        saver.add(new Record(UpdateType.ADD_PLAYER_PROFILE, profile.getPlayer().getUniqueId().toString(),
                profileID, BetonQuest.getInstance().getPluginConfig().getString("profile.initial_name", "default")));
    }

    private void addItemToBackpack(final ResultSet backpackResults) throws SQLException {
        final String serialized = backpackResults.getString("serialized");
        final int amount = backpackResults.getInt("amount");
        final byte[] bytes = Base64.getDecoder().decode(serialized);
        final ItemStack item = ItemStack.deserializeBytes(bytes).asQuantity(amount);
        backpack.add(item);
    }

    @Override
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public boolean hasTag(final String tag) {
        return tags.contains(tag);
    }

    @Override
    public void addTag(final String tag) {
        synchronized (tags) {
            if (tags.add(tag)) {
                saver.add(new Record(UpdateType.ADD_TAGS, profileID, tag));
                new PlayerTagAddEvent(profile, !server.isPrimaryThread(), tag).callEvent();
            }
        }
    }

    @Override
    public void removeTag(final String tag) {
        synchronized (tags) {
            if (tags.contains(tag)) {
                tags.remove(tag);
                saver.add(new Record(UpdateType.REMOVE_TAGS, profileID, tag));
                new PlayerTagRemoveEvent(profile, !server.isPrimaryThread(), tag).callEvent();
            }
        }
    }

    @Override
    public Set<Point> getPoints() {
        return Set.copyOf(points.values());
    }

    @Override
    public Optional<Integer> getPointsFromCategory(final String category) {
        synchronized (points) {
            final Point point = points.get(category);
            if (point != null) {
                return Optional.of(point.getCount());
            }
            return Optional.empty();
        }
    }

    @Override
    public void modifyPoints(final String category, final int count) {
        synchronized (points) {
            saver.add(new Record(UpdateType.REMOVE_POINTS, profileID, category));
            final Point point = points.computeIfAbsent(category, cat -> new Point(cat, 0));
            point.addPoints(count);
            saver.add(new Record(UpdateType.ADD_POINTS, profileID, category, String.valueOf(point.getCount())));
            new PlayerUpdatePointEvent(profile, !server.isPrimaryThread(), category, point.getCount()).callEvent();
        }
    }

    @Override
    public void setPoints(final String category, final int count) {
        synchronized (points) {
            saver.add(new Record(UpdateType.REMOVE_POINTS, profileID, category));
            points.put(category, new Point(category, count));
            saver.add(new Record(UpdateType.ADD_POINTS, profileID, category, String.valueOf(count)));
            new PlayerUpdatePointEvent(profile, !server.isPrimaryThread(), category, count).callEvent();
        }
    }

    @Override
    public void removePointsCategory(final String category) {
        synchronized (points) {
            final Point removed = points.remove(category);
            if (removed != null) {
                new PlayerUpdatePointEvent(profile, !server.isPrimaryThread(), category, 0).callEvent();
            }
            saver.add(new Record(UpdateType.REMOVE_POINTS, profileID, category));
        }
    }

    /**
     * Returns a Journal instance or creates it if it does not exist.
     *
     * @return possible new Journal instance
     */
    public Journal getJournal() {
        if (journal == null) {
            journal = journalFactory.createJournal(profile, entries);
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
                final ObjectiveIdentifier objectiveIdentifier = identifierRegistry.getFactory(ObjectiveIdentifier.class).parseIdentifier(null, objective);
                objectiveManager.start(profile, objectiveIdentifier, entry.getValue());
            } catch (final QuestException e) {
                log.warn("Loaded '" + objective
                        + "' objective from the database, but it is not defined in configuration. Skipping.", e);
            }
        }
        objectives.clear();
    }

    /**
     * Get the not initialized objectives.
     *
     * @return the map containing objective IDs and their objective data;
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
    public void addNewRawObjective(final ObjectiveIdentifier objectiveID) {
        final Objective obj;
        try {
            obj = objectiveManager.getObjective(objectiveID);
        } catch (final QuestException e) {
            log.warn(objectiveID.getPackage(), "Cannot add objective to player data: " + e.getMessage(), e);
            return;
        }
        final String data;
        try {
            data = obj.getService().getDefaultData(profile);
        } catch (final QuestException e) {
            log.warn(objectiveID.getPackage(), "Cannot add objective to player data: Could Not get resolved instruction: "
                    + e.getMessage(), e);
            return;
        }
        if (addRawObjective(objectiveID, data)) {
            saver.add(new Record(UpdateType.ADD_OBJECTIVES, profileID, objectiveID.toString(), data));
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
    public boolean addRawObjective(final ObjectiveIdentifier objectiveID, final String data) {
        final String idString = objectiveID.toString();
        if (objectives.containsKey(idString)) {
            return false;
        }
        objectives.put(idString, data);
        return true;
    }

    /**
     * Removes not initialized objective from the plugin and the database.
     *
     * @param objectiveID the ID of the objective
     */
    public void removeRawObjective(final ObjectiveIdentifier objectiveID) {
        objectives.remove(objectiveID.toString());
        removeObjFromDB(objectiveID.toString());
    }

    /**
     * Directly adds specified objectiveID and data string to the database.
     *
     * @param objectiveID the ID of the objective
     * @param data        the data string of this objective (the one associated with ObjectiveData)
     */
    public void addObjToDB(final ObjectiveIdentifier objectiveID, final String data) {
        saver.add(new Record(UpdateType.ADD_OBJECTIVES, profileID, objectiveID.toString(), data));
    }

    /**
     * Directly removes from the database specified objective.
     *
     * @param objectiveID the ID of the objective to remove
     */
    public void removeObjFromDB(final String objectiveID) {
        saver.add(new Record(UpdateType.REMOVE_OBJECTIVES, profileID, objectiveID));
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
        refreshBackpack(list);
    }

    /**
     * Get the journal entries.
     *
     * @return an unmodifiable list of the profiles journal entries
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
                }
                // if the stack will be overflown, set max size and continue
                inputAmount -= itemStack.getMaxStackSize() - itemStack.getAmount();
                itemStack.setAmount(itemStack.getMaxStackSize());
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
        refreshBackpack(backpack);
    }

    private void refreshBackpack(final List<ItemStack> backpack) {
        // quite expensive, should be changed
        saver.add(new Record(UpdateType.DELETE_BACKPACK, profileID));
        for (final ItemStack itemStack : backpack) {
            final byte[] bytes = itemStack.asOne().serializeAsBytes();
            final String serialized = Base64.getEncoder().encodeToString(bytes);
            final String newAmount = String.valueOf(itemStack.getAmount());
            saver.add(new Record(UpdateType.ADD_BACKPACK, profileID, serialized, newAmount));
        }
    }

    /**
     * Gets player's language.
     *
     * @return the language this profile uses
     */
    public Optional<String> getLanguage() {
        return Optional.ofNullable(profileLanguage);
    }

    /**
     * Sets player's language.
     *
     * @param lang language to set
     */
    public void setLanguage(@Nullable final String lang) {
        if (Objects.equals(profileLanguage, lang)) {
            return;
        }
        this.profileLanguage = lang;
        if (journal != null) {
            journal.update();
        }
        saver.add(new Record(UpdateType.UPDATE_PLAYER_LANGUAGE, lang, profileID));
    }

    /**
     * Get conversation id if the profile has an active one or null.
     *
     * @return the id of active conversation
     */
    @Nullable
    public PlayerConversationState getActiveConversation() {
        return activeConversation;
    }

    /**
     * Purges all profile's data from the database and from this object.
     */
    public void purgePlayer() {
        for (final Objective obj : objectiveManager.getForProfile(profile)) {
            objectiveManager.cancel(profile, obj.getObjectiveID());
        }
        // clear all lists
        objectives.clear();
        tags.clear();
        points.clear();
        entries.clear();
        if (journal != null) {
            journal.clear();
        }
        backpack.clear();
        // clear the database
        saver.add(new Record(UpdateType.DELETE_OBJECTIVES, profileID));
        saver.add(new Record(UpdateType.DELETE_JOURNAL, profileID));
        saver.add(new Record(UpdateType.DELETE_POINTS, profileID));
        saver.add(new Record(UpdateType.DELETE_TAGS, profileID));
        saver.add(new Record(UpdateType.DELETE_BACKPACK, profileID));
        saver.add(new Record(UpdateType.UPDATE_CONVERSATION, "null", profileID));
        // update the journal so it's empty
        if (profile.getOnlineProfile().isPresent()) {
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
