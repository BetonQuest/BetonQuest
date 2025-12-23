package org.betonquest.betonquest.quest.condition.world;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.bukkit.World;

/**
 * Factory to create world conditions from {@link Instruction}s.
 */
public class WorldConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the test for block condition factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public WorldConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<World> world = instruction.get(instruction.getParsers().world());
        final BetonQuestLogger logger = loggerFactory.create(WorldCondition.class);
        return new OnlineConditionAdapter(new WorldCondition(world), logger, instruction.getPackage());
    }
}
