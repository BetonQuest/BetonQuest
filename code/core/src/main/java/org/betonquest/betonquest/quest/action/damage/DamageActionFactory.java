package org.betonquest.betonquest.quest.action.damage;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;

/**
 * Factory to create damage events from {@link Instruction}s.
 */
public class DamageActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the damage event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     */
    public DamageActionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> damage = instruction.number().get();
        return new OnlineActionAdapter(new DamageAction(damage),
                loggerFactory.create(DamageAction.class), instruction.getPackage());
    }
}
