package org.betonquest.betonquest.quest.objective.logout;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

/**
 * Factory for creating {@link LogoutObjective} instances from {@link DefaultInstruction}s.
 */
public class LogoutObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the LogoutObjectiveFactory.
     */
    public LogoutObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final DefaultInstruction instruction) throws QuestException {
        return new LogoutObjective(instruction);
    }
}
