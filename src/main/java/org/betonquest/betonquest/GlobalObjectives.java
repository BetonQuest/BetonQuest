package org.betonquest.betonquest;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.id.ObjectiveID;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handler for global objectives
 */
@SuppressWarnings("PMD.CommentRequired")
public class GlobalObjectives {

    private static GlobalObjectives instance;

    private final Set<ObjectiveID> globalObjectiveIds;

    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    public GlobalObjectives() {
        instance = this;
        globalObjectiveIds = new HashSet<>();

    }

    /**
     * Adds a objective to the list of global objectives.
     * This method should only be called in the constructor of global objectives
     */
    public static void add(final ObjectiveID objectiveID) {
        instance.globalObjectiveIds.add(objectiveID);
    }

    /**
     * Starts all unstarted global objectives for the player
     *
     * @param playerID the id of the player
     */
    public static void startAll(final String playerID) {
        final PlayerData data = BetonQuest.getInstance().getPlayerData(playerID);
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
            objective.newPlayer(playerID);
            //add the tag
            data.addTag(GlobalObjectives.getTag(id));
        }
    }

    /**
     * @param objectiveID the id of a global objective
     * @return the tag which marks that the given global objective has already been started for the player
     */
    public static String getTag(final ObjectiveID objectiveID) {
        return objectiveID.getPackage().getName() + ".global-" + objectiveID.getBaseID();
    }

    /**
     * @return a list of all loaded global objectives
     */
    public static List<ObjectiveID> list() {
        return new ArrayList<>(instance.globalObjectiveIds);
    }
}
