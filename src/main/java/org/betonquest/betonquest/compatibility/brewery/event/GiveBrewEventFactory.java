package org.betonquest.betonquest.compatibility.brewery.event;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.compatibility.brewery.IdentifierType;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link GiveBrewEvent}s from {@link Instruction}s.
 */
public class GiveBrewEventFactory implements PlayerEventFactory {
    /**
     * The logger factory.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create Give Brew Events.
     *
     * @param loggerFactory the logger factory.
     * @param data          the data used for primary server access.
     */
    public GiveBrewEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Number> amountVar = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ONE);
        final Variable<Number> qualityVar = instruction.get(Argument.NUMBER);
        final Variable<String> nameVar = instruction.get(Argument.STRING);
        final Variable<IdentifierType> mode = instruction.getValue("mode", Argument.ENUM(IdentifierType.class), IdentifierType.NAME);
        final BetonQuestLogger logger = loggerFactory.create(GiveBrewEvent.class);
        return new PrimaryServerThreadEvent(
                new OnlineEventAdapter(new GiveBrewEvent(amountVar, qualityVar, nameVar, mode), logger, instruction.getPackage()), data);
    }
}
