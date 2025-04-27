package org.betonquest.betonquest.quest.event.velocity;

import org.betonquest.betonquest.BetonQuest;
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
        final String rawVector = instruction.getOptional("vector");
        if (rawVector == null) {
            throw new QuestException("A 'vector' is required");
        }
        final Variable<Vector> vector = new Variable<>(BetonQuest.getInstance().getVariableProcessor(), instruction.getPackage(), rawVector, Argument.VECTOR);
        final Variable<VectorDirection> direction = instruction.getVariable(instruction.getOptional("direction"), Argument.ENUM(VectorDirection.class), VectorDirection.ABSOLUTE);
        final Variable<VectorModification> modification = instruction.getVariable(instruction.getOptional("modification"), Argument.ENUM(VectorModification.class), VectorModification.SET);
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new VelocityEvent(vector, direction, modification),
                loggerFactory.create(VelocityEvent.class),
                instruction.getPackage()
        ), data);
    }
}
