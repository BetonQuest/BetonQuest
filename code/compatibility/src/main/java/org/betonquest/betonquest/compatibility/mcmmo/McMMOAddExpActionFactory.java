package org.betonquest.betonquest.compatibility.mcmmo;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;

/**
 * Factory to create {@link McMMOAddExpAction}s from {@link Instruction}s.
 */
public class McMMOAddExpActionFactory implements PlayerActionFactory {

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new factory for mc mmo level conditions.
     *
     * @param loggerFactory the logger factory to create new class specific logger
     */
    public McMMOAddExpActionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> skillType = instruction.string().get();
        final Argument<Number> exp = instruction.number().get();
        final BetonQuestLogger log = loggerFactory.create(McMMOAddExpAction.class);
        return new OnlineActionAdapter(new McMMOAddExpAction(skillType, exp), log, instruction.getPackage());
    }
}
