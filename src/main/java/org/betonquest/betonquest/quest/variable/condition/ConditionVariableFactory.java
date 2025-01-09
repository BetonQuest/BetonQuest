package org.betonquest.betonquest.quest.variable.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.modules.data.PlayerDataStorage;

/**
 * Factory to create {@link ConditionVariable}s from {@link Instruction}s.
 */
public class ConditionVariableFactory implements PlayerVariableFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create the Condition Variable Factory.
     *
     * @param dataStorage the storage providing player data
     */
    public ConditionVariableFactory(final PlayerDataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        final ConditionID conditionId = instruction.getCondition();
        final boolean papiMode = instruction.hasArgument("papiMode");
        return new ConditionVariable(conditionId, papiMode, dataStorage);
    }
}
