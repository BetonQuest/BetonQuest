package org.betonquest.betonquest.mc_1_21_4.quest.condition.biome;

import io.papermc.paper.registry.RegistryKey;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
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
     * Create the biome factory.
     */
    public UpdatedBiomeConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Biome> biome = instruction.parse(BIOME_PARSER).get();
        return new OnlineConditionAdapter(new BiomeCondition(biome));
    }
}
