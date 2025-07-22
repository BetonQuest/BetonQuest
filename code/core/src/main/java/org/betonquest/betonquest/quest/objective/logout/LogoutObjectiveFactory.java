package org.betonquest.betonquest.quest.objective.logout;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory for creating {@link LogoutObjective} instances from {@link Instruction}s.
 */
public class LogoutObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new instance of the LogoutObjectiveFactory.
     */
    public LogoutObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        return new LogoutObjective(instruction);
    }
}
