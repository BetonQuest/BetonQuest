package org.betonquest.betonquest.quest.variable.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.BetonQuestAPI;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ConditionID;

/**
 * Factory to create {@link ConditionVariable}s from {@link Instruction}s.
 */
public class ConditionVariableFactory implements PlayerVariableFactory {

    /**
     * BetonQuest API.
     */
    private final BetonQuestAPI questAPI;

    /**
     * Create the Condition Variable Factory.
     *
     * @param questAPI the BetonQuest API
     */
    public ConditionVariableFactory(final BetonQuestAPI questAPI) {
        this.questAPI = questAPI;
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        final ConditionID conditionId = instruction.getCondition();
        final boolean papiMode = instruction.hasArgument("papiMode");
        return new ConditionVariable(conditionId, papiMode, questAPI);
    }
}
