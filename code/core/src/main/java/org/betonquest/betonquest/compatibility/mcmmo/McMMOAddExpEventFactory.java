package org.betonquest.betonquest.compatibility.mcmmo;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link McMMOAddExpEvent}s from {@link Instruction}s.
 */
public class McMMOAddExpEventFactory implements PlayerEventFactory {

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for mc mmo level conditions.
     *
     * @param loggerFactory the logger factory to create new class specific logger
     * @param data          the data for primary server thread access
     */
    public McMMOAddExpEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> skillType = instruction.get(Argument.STRING);
        final Variable<Number> exp = instruction.get(Argument.NUMBER);
        final BetonQuestLogger log = loggerFactory.create(McMMOAddExpEvent.class);
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new McMMOAddExpEvent(skillType, exp),
                log, instruction.getPackage()), data);
    }
}
