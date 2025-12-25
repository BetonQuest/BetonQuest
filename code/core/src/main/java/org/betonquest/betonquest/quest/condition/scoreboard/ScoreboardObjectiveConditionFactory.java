package org.betonquest.betonquest.quest.condition.scoreboard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
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
        final Argument<String> objective = instruction.string().get();
        final Argument<Number> count = instruction.number().get();
        return new ScoreboardObjectiveCondition(objective, count);
    }
}
