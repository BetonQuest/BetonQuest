package org.betonquest.betonquest.quest.event.velocity;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.location.VariableVector;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create velocity events from {@link Instruction}s.
 */
public class VelocityEventFactory implements EventFactory {
    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the velocity event factory.
     *
     * @param loggerFactory logger factory to use
     * @param data          the data for primary server thread access
     */
    public VelocityEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final String rawVector = instruction.getOptional("vector");
        if (rawVector == null) {
            throw new QuestException("A 'vector' is required");
        }
        final VariableVector vector = new VariableVector(BetonQuest.getInstance().getVariableProcessor(), instruction.getPackage(), rawVector);
        final VectorDirection direction = instruction.getEnum(instruction.getOptional("direction"), VectorDirection.class, VectorDirection.ABSOLUTE);
        final VectorModification modification = instruction.getEnum(instruction.getOptional("modification"), VectorModification.class, VectorModification.SET);
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new VelocityEvent(vector, direction, modification),
                loggerFactory.create(VelocityEvent.class),
                instruction.getPackage()
        ), data);
    }
}
