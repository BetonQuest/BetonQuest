package org.betonquest.betonquest.quest.event.scoreboard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.point.PointType;

/**
 * Factory to create scoreboard events from {@link DefaultInstruction}s.
 */
public class ScoreboardObjectiveEventFactory implements PlayerEventFactory {

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the scoreboard event factory.
     *
     * @param data the data for primary server thread access
     */
    public ScoreboardObjectiveEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final DefaultInstruction instruction) throws QuestException {
        final Variable<String> objective = instruction.get(Argument.STRING);
        final Variable<Number> number = instruction.get(Argument.NUMBER);
        final PointType action = instruction.getValue("action", Argument.ENUM(PointType.class), PointType.ADD).getValue(null);
        return new PrimaryServerThreadEvent(
                new ScoreboardObjectiveEvent(objective, number, action),
                data);
    }
}
