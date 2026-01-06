package org.betonquest.betonquest.quest.event.hunger;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;

/**
 * Factory for the hunger event.
 */
public class HungerEventFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the hunger event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     */
    public HungerEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Hunger> hunger = instruction.enumeration(Hunger.class).get();
        final Argument<Number> amount = instruction.number().get();
        return new OnlineActionAdapter(new HungerEvent(hunger, amount),
                loggerFactory.create(HungerEvent.class), instruction.getPackage());
    }
}
