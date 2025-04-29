package org.betonquest.betonquest.quest.objective.tame;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.types.EnumParser;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Tameable;

/**
 * Factory for creating {@link TameObjective} instances from {@link Instruction}s.
 */
public class TameObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new TameObjectiveFactory instance.
     */
    public TameObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<EntityType> type = instruction.get(new EntityTypeParser());
        final Variable<Number> targetAmount = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ONE);
        return new TameObjective(instruction, targetAmount, type);
    }

    /**
     * Parser for {@link EntityType} enums.
     */
    private static class EntityTypeParser extends EnumParser<EntityType> {
        /**
         * Creates a new parser for enums.
         */
        public EntityTypeParser() {
            super(EntityType.class);
        }

        @Override
        public EntityType apply(final String string) throws QuestException {
            final EntityType type = super.apply(string);
            if (type.getEntityClass() == null || !Tameable.class.isAssignableFrom(type.getEntityClass())) {
                throw new QuestException("Entity cannot be tamed: " + type);
            }
            return type;
        }
    }
}
