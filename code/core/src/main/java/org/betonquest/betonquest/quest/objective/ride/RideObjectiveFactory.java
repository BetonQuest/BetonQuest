package org.betonquest.betonquest.quest.objective.ride;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.entity.EntityType;

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
        final String name = instruction.next();
        final Variable<EntityType> vehicle;
        if (ANY_PROPERTY.equalsIgnoreCase(name)) {
            vehicle = null;
        } else {
            vehicle = instruction.get(name, Argument.ENUM(EntityType.class));
        }
        return new RideObjective(instruction, vehicle);
    }
}
