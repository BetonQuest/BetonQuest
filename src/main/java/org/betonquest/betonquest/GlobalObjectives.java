package org.betonquest.betonquest;

import lombok.CustomLog;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ObjectiveID;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handler for global objectives
 */
@CustomLog(topic = "GlobalObjectives")
@SuppressWarnings("PMD.CommentRequired")
public class GlobalObjectives {

    private static GlobalObjectives instance;

    private final Set<ObjectiveID> globalObjectiveIds;

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
     * Starts all unstarted global objectives for the player
     *
     * @param profile the {@link Profile} of the player
     */
    public static void startAll(final Profile profile) {
        final PlayerData data = BetonQuest.getInstance().getPlayerData(profile);
        for (final ObjectiveID id : instance.globalObjectiveIds) {
            final Objective objective = BetonQuest.getInstance().getObjective(id);

            if (objective == null) {
                continue;
            }

            //if player already has the tag skip
            if (data.hasTag(GlobalObjectives.getTag(id))) {
                continue;
            }
            //start the objective
            objective.newPlayer(profile);
            //add the tag
            try {
                data.addTag(GlobalObjectives.getTag(id));
            } catch (final QuestRuntimeException e) {
                LOG.warn("Couldn't addTag due to: " + e.getMessage(), e);
            }
        }
    }

    /**
     * @param objectiveID the id of a global objective
     * @return the tag which marks that the given global objective has already been started for the player
     */
    public static String getTag(final ObjectiveID objectiveID) {
        return objectiveID.getPackage().getPackagePath() + ".global-" + objectiveID.getBaseID();
    }

    /**
     * @return a list of all loaded global objectives
     */
    public static List<ObjectiveID> list() {
        return new ArrayList<>(instance.globalObjectiveIds);
    }
}
