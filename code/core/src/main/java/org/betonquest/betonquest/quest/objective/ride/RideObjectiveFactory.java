package org.betonquest.betonquest.quest.objective.ride;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.bukkit.entity.EntityType;

import java.util.Optional;

/**
 * Factory for creating {@link RideObjective} instances from {@link Instruction}s.
 */
public class RideObjectiveFactory implements ObjectiveFactory {

    /**
     * Any property for the entity type.
     */
    private static final String ANY_PROPERTY = "any";

    /**
     * Creates a new instance of the RideObjectiveFactory.
     */
    public RideObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Optional<EntityType>> vehicle = instruction.enumeration(EntityType.class)
                .prefilterOptional(ANY_PROPERTY, null).get();
        return new RideObjective(instruction, vehicle);
    }
}
