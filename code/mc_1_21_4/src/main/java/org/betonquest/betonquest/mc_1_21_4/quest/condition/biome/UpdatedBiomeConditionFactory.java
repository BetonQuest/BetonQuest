package org.betonquest.betonquest.mc_1_21_4.quest.condition.biome;

import io.papermc.paper.registry.RegistryKey;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.mc_1_21_4.api.instruction.argument.type.RegistryKeyParser;
import org.betonquest.betonquest.quest.condition.biome.BiomeCondition;
import org.bukkit.block.Biome;

/**
 * Updated Factory for {@link BiomeCondition}s.
 */
public class UpdatedBiomeConditionFactory implements PlayerConditionFactory {

    /**
     * Parser for {@link Biome}s.
     */
    private static final RegistryKeyParser<Biome> BIOME_PARSER = new RegistryKeyParser<>(RegistryKey.BIOME);

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the biome factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public UpdatedBiomeConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Biome> biome = instruction.parse(BIOME_PARSER).get();
        final BetonQuestLogger log = loggerFactory.create(BiomeCondition.class);
        return new OnlineConditionAdapter(new BiomeCondition(biome), log, instruction.getPackage());
    }
}
