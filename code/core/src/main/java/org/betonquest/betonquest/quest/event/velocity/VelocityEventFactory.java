package org.betonquest.betonquest.quest.event.velocity;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
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
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the velocity event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param data          the data for primary server thread access
     */
    public VelocityEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Vector> vector = instruction.getValue("vector", DefaultArgumentParsers.VECTOR);
        if (vector == null) {
            throw new QuestException("A 'vector' is required");
        }
        final Variable<VectorDirection> direction = instruction.getValue("direction", DefaultArgumentParsers.forEnumeration(VectorDirection.class), VectorDirection.ABSOLUTE);
        final Variable<VectorModification> modification = instruction.getValue("modification", DefaultArgumentParsers.forEnumeration(VectorModification.class), VectorModification.SET);
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new VelocityEvent(vector, direction, modification),
                loggerFactory.create(VelocityEvent.class),
                instruction.getPackage()
        ), data);
    }
}
