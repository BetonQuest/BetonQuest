package org.betonquest.betonquest.kernel.processor.quest;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.registry.FactoryTypeRegistry;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores Objectives and starts/stops/resumes them.
 */
public class ObjectiveProcessor extends TypedQuestProcessor<ObjectiveID, Objective> {

    /**
     * Manager to register listener.
     */
    private final PluginManager pluginManager;

    /**
     * Plugin instance to associate registered listener with.
     */
    private final Plugin plugin;

    /**
     * Loaded global objectives.
     */
    private final Set<ObjectiveID> globalObjectiveIds;

    /**
     * Create a new Objective Processor to store Objectives and starts/stops/resumes them.
     *
     * @param log            the custom logger for this class
     * @param packManager    the quest package manager to get quest packages from
     * @param objectiveTypes the available objective types
     * @param pluginManager  the manager to register listener
     * @param plugin         the plugin instance to associate registered listener with
     */
    public ObjectiveProcessor(final BetonQuestLogger log, final QuestPackageManager packManager,
                              final FactoryTypeRegistry<Objective> objectiveTypes,
                              final PluginManager pluginManager, final Plugin plugin) {
        super(log, packManager, objectiveTypes, "Objective", "objectives");
        this.pluginManager = pluginManager;
        this.plugin = plugin;
        globalObjectiveIds = new HashSet<>();
    }

    /**
     * Get the tag used to mark an already started global objective.
     *
     * @param objectiveID the id of a global objective
     * @return the tag which marks that the given global objective has already been started for the player
     */
    public static String getTag(final ObjectiveID objectiveID) {
        return PackageArgument.IDENTIFIER.apply(objectiveID.getPackage(), "global-" + objectiveID.get());
    }

    @Override
    public void clear() {
        globalObjectiveIds.clear();
        for (final Objective objective : values.values()) {
            objective.close();
            if (objective instanceof Listener) {
                HandlerList.unregisterAll((Listener) objective);
            }
        }
        super.clear();
    }

    @Override
    protected ObjectiveID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new ObjectiveID(packManager, pack, identifier);
    }

    @Override
    protected void postCreation(final ObjectiveID identifier, final Objective value) {
        if (identifier.getInstruction().hasArgument("global")) {
            globalObjectiveIds.add(identifier);
        }
        if (value instanceof Listener) {
            pluginManager.registerEvents((Listener) value, plugin);
        }
    }

    /**
     * Creates new objective for given player.
     *
     * @param profile     the {@link Profile} of the player
     * @param objectiveID ID of the objective
     */
    public void start(final Profile profile, final ObjectiveID objectiveID) {
        final Objective objective = values.get(objectiveID);
        if (objective == null) {
            log.error("Tried to start objective '" + objectiveID + "' but it is not loaded! Check for errors on /bq reload!");
            return;
        }
        if (objective.containsPlayer(profile)) {
            log.debug(objectiveID.getPackage(), profile + " already has the " + objectiveID + " objective");
            return;
        }
        objective.newPlayer(profile);
    }

    /**
     * Resumes the existing objective for given player.
     *
     * @param profile     the {@link Profile} of the player
     * @param objectiveID ID of the objective
     * @param instruction data instruction string
     */
    public void resume(final Profile profile, final ObjectiveID objectiveID, final String instruction) {
        final Objective objective = values.get(objectiveID);
        if (objective == null) {
            log.warn(objectiveID.getPackage(), "Objective " + objectiveID + " does not exist");
            return;
        }
        if (objective.containsPlayer(profile)) {
            log.debug(objectiveID.getPackage(), profile + " already has the " + objectiveID + " objective!");
            return;
        }
        objective.resumeObjectiveForPlayer(profile, instruction);
    }

    /**
     * Returns the list of objectives of this player.
     *
     * @param profile the {@link Profile} of the player
     * @return list of this player's active objectives
     */
    public List<Objective> getActive(final Profile profile) {
        final List<Objective> list = new ArrayList<>();
        for (final Objective objective : values.values()) {
            if (objective.containsPlayer(profile)) {
                list.add(objective);
            }
        }
        return list;
    }

    /**
     * Renames the objective instance.
     *
     * @param name   the current name
     * @param rename the name it should have now
     */
    public void renameObjective(final ObjectiveID name, final ObjectiveID rename) {
        final Objective objective = values.remove(name);
        values.put(rename, objective);
        if (objective != null) {
            objective.setLabel(rename);
        }
    }

    /**
     * Starts all unstarted global objectives for the player.
     *
     * @param profile     the {@link Profile} of the player
     * @param dataStorage the storage providing player data
     */
    public void startAll(final Profile profile, final PlayerDataStorage dataStorage) {
        final PlayerData data = dataStorage.get(profile);
        for (final ObjectiveID id : globalObjectiveIds) {
            final Objective objective = values.get(id);
            final String tag = getTag(id);
            if (objective == null || data.hasTag(tag)) {
                continue;
            }
            if (objective.containsPlayer(profile)) {
                log.debug(id.getPackage(), profile + " already has the " + id + " objective, adding tag");
            } else {
                objective.newPlayer(profile);
            }
            data.addTag(tag);
        }
    }

    /**
     * Get all global objectives.
     *
     * @return a new list of all loaded global objectives
     */
    public List<ObjectiveID> getGlobalObjectives() {
        return new ArrayList<>(globalObjectiveIds);
    }
}
