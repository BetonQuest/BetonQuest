package org.betonquest.betonquest.compatibility.brewery.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.compatibility.brewery.IdentifierType;

/**
 * Factory to create {@link GiveBrewEvent}s from {@link Instruction}s.
 */
public class GiveBrewEventFactory implements PlayerEventFactory {

    /**
     * The logger factory.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new Factory to create Give Brew Events.
     *
     * @param loggerFactory the logger factory.
     */
    public GiveBrewEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> amountVar = instruction.number().atLeast(1).get();
        final Argument<Number> qualityVar = instruction.number().get();
        final Argument<String> nameVar = instruction.string().get();
        final Argument<IdentifierType> mode = instruction.enumeration(IdentifierType.class).get("mode", IdentifierType.NAME);
        final BetonQuestLogger logger = loggerFactory.create(GiveBrewEvent.class);
        return new OnlineEventAdapter(new GiveBrewEvent(amountVar, qualityVar, nameVar, mode), logger, instruction.getPackage());
    }
}
