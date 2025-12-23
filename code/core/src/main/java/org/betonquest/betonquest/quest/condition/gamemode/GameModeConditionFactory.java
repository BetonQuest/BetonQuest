package org.betonquest.betonquest.quest.condition.gamemode;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.bukkit.GameMode;

/**
 * Factory for {@link GameModeCondition}s.
 */
public class GameModeConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the game mode factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public GameModeConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<GameMode> gameMode = instruction.get(instruction.getParsers().forEnum(GameMode.class));
        final BetonQuestLogger log = loggerFactory.create(GameModeCondition.class);
        return new OnlineConditionAdapter(new GameModeCondition(gameMode), log, instruction.getPackage());
    }
}
