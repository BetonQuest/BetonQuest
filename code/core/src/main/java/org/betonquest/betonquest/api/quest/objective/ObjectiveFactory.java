package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;

/**
 * Factory to create a specific {@link DefaultObjective} from {@link Instruction}s.
 */
@FunctionalInterface
public interface ObjectiveFactory {

    /**
     * Parses an instruction to create a {@link DefaultObjective}.
     *
     * @param instruction  instruction to parse
     * @param eventService the event service to register events with
     * @return objective referenced by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    DefaultObjective parseInstruction(Instruction instruction, ObjectiveFactoryService eventService) throws QuestException;
}
