package org.betonquest.betonquest.quest.condition.biome;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.bukkit.block.Biome;

/**
 * Requires the player to be in a specified biome.
 */
public class BiomeCondition implements OnlineCondition {

    /**
     * The biome to check for.
     */
    private final Argument<Biome> biome;

    /**
     * Creates a new BiomeCondition.
     *
     * @param biome the biome to check for
     */
    public BiomeCondition(final Argument<Biome> biome) {
        this.biome = biome;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return profile.getPlayer().getLocation().getBlock().getBiome() == biome.getValue(profile);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
