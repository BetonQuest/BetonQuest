package org.betonquest.betonquest.quest.event.scoreboard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.quest.event.point.PointType;

/**
 * Factory to create scoreboard events from {@link Instruction}s.
 */
public class ScoreboardObjectiveActionFactory implements PlayerActionFactory {

    /**
     * Create the scoreboard event factory.
     */
    public ScoreboardObjectiveActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> objective = instruction.string().get();
        final Argument<Number> number = instruction.number().get();
        final PointType action = instruction.enumeration(PointType.class).get("action", PointType.ADD).getValue(null);
        return new ScoreboardObjectiveAction(objective, number, action);
    }
}
