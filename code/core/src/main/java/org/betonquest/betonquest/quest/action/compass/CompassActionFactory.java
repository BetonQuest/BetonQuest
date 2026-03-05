package org.betonquest.betonquest.quest.action.compass;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.data.Persistence;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.service.compass.CompassManager;

/**
 * The compass action factory.
 */
public class CompassActionFactory implements PlayerActionFactory {

    /**
     * The compass manager.
     */
    private final CompassManager compassManager;

    /**
     * Storage to get persistent offline player data.
     */
    private final Persistence persistence;

    /**
     * Create the compass action factory.
     *
     * @param compassManager   the compass manager
     * @param persistence the storage to access persistent offline player data
     */
    public CompassActionFactory(final CompassManager compassManager, final Persistence persistence) {
        this.compassManager = compassManager;
        this.persistence = persistence;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<CompassTargetOperation> action = instruction.enumeration(CompassTargetOperation.class).get();
        final Argument<CompassIdentifier> compassId = instruction.identifier(CompassIdentifier.class).get();
        return new CompassAction(compassManager, persistence, action, compassId);
    }
}
