package org.betonquest.betonquest.quest.event.velocity;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.bukkit.util.Vector;

/**
 * Factory to create velocity events from {@link Instruction}s.
 */
public class VelocityEventFactory implements PlayerEventFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the velocity event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     */
    public VelocityEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Vector> vector = instruction.vector().get("vector").orElse(null);
        if (vector == null) {
            throw new QuestException("A 'vector' is required");
        }
        final Argument<VectorDirection> direction = instruction.enumeration(VectorDirection.class)
                .get("direction", VectorDirection.ABSOLUTE);
        final Argument<VectorModification> modification = instruction.enumeration(VectorModification.class)
                .get("modification", VectorModification.SET);
        return new OnlineEventAdapter(new VelocityEvent(vector, direction, modification),
                loggerFactory.create(VelocityEvent.class), instruction.getPackage());
    }
}
