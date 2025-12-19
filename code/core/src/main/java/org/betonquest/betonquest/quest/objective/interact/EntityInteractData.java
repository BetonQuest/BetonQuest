package org.betonquest.betonquest.quest.objective.interact;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.bukkit.entity.Entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The entity counting data for the objective.
 */
public class EntityInteractData extends CountingObjective.CountingData {
    /**
     * The set of entities that have been interacted with.
     */
    private final Set<UUID> entities;

    /**
     * Creates a new instance of the EntityInteractData.
     *
     * @param instruction the instruction that created this objective
     * @param profile     the profile of the player
     * @param objID       the ID of the objective
     * @throws QuestException when the instruction data is malformed
     */
    public EntityInteractData(final String instruction, final Profile profile, final ObjectiveID objID) throws QuestException {
        super(instruction, profile, objID);
        entities = new HashSet<>();
        final String[] entityInstruction = instruction.split(";", 3);
        if (entityInstruction.length >= 2 && !entityInstruction[1].isEmpty()) {
            Arrays.stream(entityInstruction[1].split("/"))
                    .map(UUID::fromString)
                    .forEach(entities::add);
        }
    }

    /**
     * Checks if the interaction with a given entity progresses the objective.
     *
     * @param entity the entity to try to progress with
     * @return true if the entity was added to the set, false otherwise
     */
    public boolean tryProgressWithEntity(final Entity entity) {
        final boolean success = entities.add(entity.getUniqueId());
        if (success) {
            progress();
        }
        return success;
    }

    @Override
    public String toString() {
        return super.toString() + ";" + entities.stream().map(UUID::toString).collect(Collectors.joining("/"));
    }
}
