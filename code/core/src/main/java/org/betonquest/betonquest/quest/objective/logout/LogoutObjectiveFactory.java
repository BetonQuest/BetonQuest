package org.betonquest.betonquest.quest.objective.logout;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

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
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        return new LogoutObjective(instruction);
    }
}
