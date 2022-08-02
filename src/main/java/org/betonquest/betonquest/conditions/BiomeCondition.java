package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

/**
 * Requires the player to be in a specified biome
 */
@SuppressWarnings("PMD.CommentRequired")
public class BiomeCondition extends Condition {

    private final Biome biome;

    public BiomeCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        biome = instruction.getEnum(Biome.class);
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        if (profile.getPlayer().isEmpty()) {
            throw new QuestRuntimeException("Player is offline");
        }
        final Player player = profile.getPlayer().get();
        return player.getLocation().getBlock().getBiome() == this.biome;
    }
}
