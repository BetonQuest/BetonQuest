package org.betonquest.betonquest.quest.objective.login;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

/**
 * Factory for creating {@link LoginObjective} instances from {@link Instruction}s.
 */
public class LoginObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the LoginObjectiveFactory.
     */
    public LoginObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        return new LoginObjective(instruction);
    }
}
