package org.betonquest.betonquest.quest.condition.biome;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.bukkit.block.Biome;

/**
 * Factory for {@link BiomeCondition}s.
 */
public class BiomeConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the biome factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public BiomeConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Biome> biomeVariable = instruction.enumeration(Biome.class).get();
        final BetonQuestLogger log = loggerFactory.create(BiomeCondition.class);
        return new OnlineConditionAdapter(new BiomeCondition(biomeVariable), log, instruction.getPackage());
    }
}
