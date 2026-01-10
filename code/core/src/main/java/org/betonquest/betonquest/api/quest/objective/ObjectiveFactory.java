package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

/**
 * Factory to create a specific {@link Objective} from {@link Instruction}s.
 */
@FunctionalInterface
public interface ObjectiveFactory {

    /**
     * Parses an instruction to create a {@link Objective}.
     *
     * @param instruction  instruction to parse
     * @param eventService the event service to register events with
     * @return objective referenced by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    Objective parseInstruction(Instruction instruction, ObjectiveService eventService) throws QuestException;
}
