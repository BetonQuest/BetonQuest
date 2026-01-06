package org.betonquest.betonquest.quest.action.velocity;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;
import org.bukkit.util.Vector;

/**
 * Factory to create velocity actions from {@link Instruction}s.
 */
public class VelocityActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the velocity action factory.
     *
     * @param loggerFactory the logger factory to create a logger for the actions
     */
    public VelocityActionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Vector> vector = instruction.vector().get("vector").orElse(null);
        if (vector == null) {
            throw new QuestException("A 'vector' is required");
        }
        final Argument<VectorDirection> direction = instruction.enumeration(VectorDirection.class)
                .get("direction", VectorDirection.ABSOLUTE);
        final Argument<VectorModification> modification = instruction.enumeration(VectorModification.class)
                .get("modification", VectorModification.SET);
        return new OnlineActionAdapter(new VelocityAction(vector, direction, modification),
                loggerFactory.create(VelocityAction.class), instruction.getPackage());
    }
}
