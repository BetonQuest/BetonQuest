package org.betonquest.betonquest.compatibility.itemsadder.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory to {@link IAPlayAnimationAction}s from {@link Instruction}s.
 */
public class IAPlayAnimationActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the play animation action factory.
     *
     * @param loggerFactory logger factory to use
     */
    public IAPlayAnimationActionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> animation = instruction.string().get();
        return new OnlineActionAdapter(
                new IAPlayAnimationAction(animation),
                loggerFactory.create(IAPlayAnimationAction.class),
                instruction.getPackage()
        );
    }
}
