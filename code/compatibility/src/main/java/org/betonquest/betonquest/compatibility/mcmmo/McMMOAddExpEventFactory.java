package org.betonquest.betonquest.compatibility.mcmmo;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;

/**
 * Factory to create {@link McMMOAddExpEvent}s from {@link Instruction}s.
 */
public class McMMOAddExpEventFactory implements PlayerEventFactory {

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new factory for mc mmo level conditions.
     *
     * @param loggerFactory the logger factory to create new class specific logger
     */
    public McMMOAddExpEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> skillType = instruction.string().get();
        final Argument<Number> exp = instruction.number().get();
        final BetonQuestLogger log = loggerFactory.create(McMMOAddExpEvent.class);
        return new OnlineEventAdapter(new McMMOAddExpEvent(skillType, exp), log, instruction.getPackage());
    }
}
