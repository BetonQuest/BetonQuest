package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.kernel.TypeFactory;

/**
 * Factory to create a specific {@link DefaultObjective} from {@link Instruction}s.
 */
@FunctionalInterface
public interface ObjectiveFactory extends TypeFactory<DefaultObjective> {

    /**
     * Parses an instruction to create a {@link DefaultObjective}.
     *
     * @param instruction instruction to parse
     * @return objective referenced by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    @Override
    DefaultObjective parseInstruction(Instruction instruction) throws QuestException;
}
