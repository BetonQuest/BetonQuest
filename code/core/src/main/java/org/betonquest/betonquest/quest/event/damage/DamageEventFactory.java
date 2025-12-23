package org.betonquest.betonquest.quest.event.damage;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;

/**
 * Factory to create damage events from {@link Instruction}s.
 */
public class DamageEventFactory implements PlayerEventFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the damage event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     */
    public DamageEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Number> damage = instruction.number().get();
        return new OnlineEventAdapter(new DamageEvent(damage),
                loggerFactory.create(DamageEvent.class), instruction.getPackage());
    }
}
