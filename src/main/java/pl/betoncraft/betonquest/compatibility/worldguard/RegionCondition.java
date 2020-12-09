package pl.betoncraft.betonquest.compatibility.worldguard;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

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
