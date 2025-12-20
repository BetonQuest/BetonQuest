package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.kernel.TypeFactory;

/**
 * Factory to create a specific {@link Objective} from {@link DefaultInstruction}s.
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
    Objective parseInstruction(DefaultInstruction instruction) throws QuestException;
}
