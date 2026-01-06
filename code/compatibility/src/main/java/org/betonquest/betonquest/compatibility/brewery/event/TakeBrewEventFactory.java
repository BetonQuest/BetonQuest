package org.betonquest.betonquest.compatibility.brewery.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;
import org.betonquest.betonquest.compatibility.brewery.IdentifierType;

/**
 * Factory to create {@link GiveBrewEvent}s from {@link Instruction}s.
 */
public class TakeBrewEventFactory implements PlayerActionFactory {

    /**
     * The logger factory.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new Factory to create Give Brew Events.
     *
     * @param loggerFactory the logger factory.
     */
    public TakeBrewEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> count = instruction.number().atLeast(1).get();
        final Argument<String> brew = instruction.string().get();
        final Argument<IdentifierType> mode = instruction.enumeration(IdentifierType.class).get("mode", IdentifierType.NAME);
        final BetonQuestLogger logger = loggerFactory.create(TakeBrewEvent.class);
        return new OnlineActionAdapter(new TakeBrewEvent(count, brew, mode), logger, instruction.getPackage());
    }
}
