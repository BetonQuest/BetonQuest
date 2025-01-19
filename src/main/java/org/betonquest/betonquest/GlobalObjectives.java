package org.betonquest.betonquest;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.id.ObjectiveID;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handler for global objectives.
 */
@SuppressWarnings("PMD.CommentRequired")
public class GlobalObjectives {
    @SuppressWarnings("NullAway.Init")
    private static GlobalObjectives instance;

    private final Set<ObjectiveID> globalObjectiveIds;

    /**
     * Sets a new empty {@link GlobalObjectives} instance.
     */
    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    public GlobalObjectives() {
        instance = this;
        globalObjectiveIds = new HashSet<>();
    }

    /**
     * Adds a objective to the list of global objectives.
     * This method should only be called in the constructor of global objectives
     *
     * @param objectiveID The objective to add.
     */
    public static void add(final ObjectiveID objectiveID) {
        instance.globalObjectiveIds.add(objectiveID);
    }

    /**
     * Starts all unstarted global objectives for the player.
     *
     * @param profile     the {@link Profile} of the player
     * @param dataStorage the storage providing player data
     */
    public static void startAll(final Profile profile, final PlayerDataStorage dataStorage) {
        final PlayerData data = dataStorage.get(profile);
        for (final ObjectiveID id : instance.globalObjectiveIds) {
            final Objective objective = BetonQuest.getInstance().getObjective(id);

            if (objective == null) {
                continue;
            }

            //if player already has the tag skip
            if (data.hasTag(getTag(id))) {
                continue;
            }
            //start the objective
            objective.newPlayer(profile);
            //add the tag
            data.addTag(getTag(id));
        }
    }

    /**
     * @param objectiveID the id of a global objective
     * @return the tag which marks that the given global objective has already been started for the player
     */
    public static String getTag(final ObjectiveID objectiveID) {
        return objectiveID.getPackage().getQuestPath() + ".global-" + objectiveID.getBaseID();
    }

    /**
     * @return a list of all loaded global objectives
     */
    public static List<ObjectiveID> list() {
        return new ArrayList<>(instance.globalObjectiveIds);
    }
}
