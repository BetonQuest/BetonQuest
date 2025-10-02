package org.betonquest.betonquest.quest.event.scoreboard;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;

import java.util.Locale;

/**
 * Factory to create scoreboard events from {@link Instruction}s.
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
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> objective = instruction.get(Argument.STRING);
        final String number = instruction.next();
        final String action = instruction.getValue("action");
        if (action != null) {
            try {
                final ScoreModification type = ScoreModification.valueOf(action.toUpperCase(Locale.ROOT));
                return new PrimaryServerThreadEvent(
                        new ScoreboardObjectiveEvent(objective, instruction.get(number, Argument.NUMBER), type),
                        data);
            } catch (final IllegalArgumentException e) {
                throw new QuestException("Unknown modification action: " + instruction.current(), e);
            }
        }
        if (!number.isEmpty() && number.charAt(0) == '*') {
            return new PrimaryServerThreadEvent(
                    new ScoreboardObjectiveEvent(objective, instruction.get(number.replace("*", ""), Argument.NUMBER), ScoreModification.MULTIPLY),
                    data);
        }
        return new PrimaryServerThreadEvent(
                new ScoreboardObjectiveEvent(objective, instruction.get(number, Argument.NUMBER), ScoreModification.ADD),
                data);
    }
}
