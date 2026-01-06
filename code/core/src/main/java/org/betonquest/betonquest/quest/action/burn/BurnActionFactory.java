package org.betonquest.betonquest.quest.action.burn;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;

/**
 * Factory to create burn actions from {@link Instruction}s.
 */
public class BurnActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the burn action factory.
     *
     * @param loggerFactory the logger factory to create a logger for the actions
     */
    public BurnActionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> duration = instruction.number().get("duration").orElse(null);
        if (duration == null) {
            throw new QuestException("Missing duration!");
        }
        return new OnlineActionAdapter(new BurnAction(duration), loggerFactory.create(BurnAction.class), instruction.getPackage());
    }
}
