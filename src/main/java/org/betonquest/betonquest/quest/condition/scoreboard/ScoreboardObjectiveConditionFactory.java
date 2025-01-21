package org.betonquest.betonquest.quest.condition.scoreboard;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create scoreboard objective conditions from {@link Instruction}s.
 */
public class ScoreboardObjectiveConditionFactory implements PlayerConditionFactory {

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the scoreboard objective condition factory.
     *
     * @param data the data used for checking the condition on the main thread
     */
    public ScoreboardObjectiveConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final String objective = instruction.next();
        final VariableNumber count = instruction.get(VariableNumber::new);
        return new PrimaryServerThreadPlayerCondition(new ScoreboardObjectiveCondition(objective, count), data);
    }
}
