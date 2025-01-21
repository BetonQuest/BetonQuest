package org.betonquest.betonquest.quest.condition.biome;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.bukkit.block.Biome;

/**
 * Factory for {@link BiomeCondition}s.
 */
public class BiomeConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the biome factory.
     *
     * @param loggerFactory the logger factory
     * @param data          the data used for checking the condition on the main thread
     */
    public BiomeConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Biome biome;
        try {
            biome = Biome.valueOf(instruction.next());
        } catch (final IllegalStateException e) {
            throw new QuestException("Invalid biome name: " + instruction.current(), e);
        }
        final BetonQuestLogger log = loggerFactory.create(BiomeCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new BiomeCondition(biome), log, instruction.getPackage()), data
        );
    }
}
