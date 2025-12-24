package org.betonquest.betonquest.quest.condition.scoreboard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create scoreboard objective conditions from {@link Instruction}s.
 */
public class ScoreboardObjectiveConditionFactory implements PlayerConditionFactory {

    /**
     * Create the scoreboard objective condition factory.
     */
    public ScoreboardObjectiveConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> objective = instruction.string().get();
        final Variable<Number> count = instruction.number().get();
        return new ScoreboardObjectiveCondition(objective, count);
    }
}
