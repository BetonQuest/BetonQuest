package org.betonquest.betonquest.compatibility.brewery.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.compatibility.brewery.IdentifierType;

/**
 * Factory to create {@link GiveBrewAction}s from {@link Instruction}s.
 */
public class TakeBrewActionFactory implements PlayerActionFactory {

    /**
     * The logger factory.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new Factory to create Give Brew Actions.
     *
     * @param loggerFactory the logger factory.
     */
    public TakeBrewActionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> count = instruction.number().atLeast(1).get();
        final Argument<String> brew = instruction.string().get();
        final Argument<IdentifierType> mode = instruction.enumeration(IdentifierType.class).get("mode", IdentifierType.NAME);
        final BetonQuestLogger logger = loggerFactory.create(TakeBrewAction.class);
        return new OnlineActionAdapter(new TakeBrewAction(count, brew, mode), logger, instruction.getPackage());
    }
}
