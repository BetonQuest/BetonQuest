package org.betonquest.betonquest.compatibility.worldguard;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

/**
 * Checks if the player is in specified region
 */
@SuppressWarnings("PMD.CommentRequired")
public class RegionCondition extends Condition {

    private final String name;

    public RegionCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        name = instruction.next();
    }

    @Override
    protected Boolean execute(final String playerID) {
        final Player player = PlayerConverter.getPlayer(playerID);
        return WorldGuardIntegrator.isInsideRegion(player.getLocation(), name);
    }

}
