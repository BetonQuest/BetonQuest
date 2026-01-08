package org.betonquest.betonquest.compatibility.itemsadder.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;

/**
 * A factory class responsible for parsing and creating instances of {@link ItemsAdderPlayAnimationAction}.
 *
 * <p>This factory extracts the animation name from the BetonQuest instruction and
 * wraps the resulting online action in an {@link OnlineActionAdapter} to ensure
 * compatibility with general player action execution.</p>
 */
public class ItemsAdderPlayAnimationActionFactory implements PlayerActionFactory {

    /**
     * The logger factory used to provide logging capabilities for the action adapter.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new ItemsAdderPlayAnimationActionFactory.
     *
     * @param loggerFactory the factory to create loggers for the adapted actions
     */
    public ItemsAdderPlayAnimationActionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> animation = instruction.string().get();
        return new OnlineActionAdapter(
                new ItemsAdderPlayAnimationAction(animation),
                loggerFactory.create(ItemsAdderPlayAnimationAction.class),
                instruction.getPackage()
        );
    }
}
