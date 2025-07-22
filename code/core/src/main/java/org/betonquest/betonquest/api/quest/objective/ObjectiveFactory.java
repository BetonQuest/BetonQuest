package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.kernel.registry.TypeFactory;

/**
 * Factory to create a specific {@link Objective} from {@link Instruction}s.
 */
@FunctionalInterface
public interface ObjectiveFactory extends TypeFactory<Objective> {
    /**
     * Parses an instruction to create a {@link Objective}.
     *
     * @param instruction instruction to parse
     * @return objective referenced by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    @Override
    Objective parseInstruction(Instruction instruction) throws QuestException;
}
