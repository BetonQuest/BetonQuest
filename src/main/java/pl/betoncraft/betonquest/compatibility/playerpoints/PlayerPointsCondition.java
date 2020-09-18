package pl.betoncraft.betonquest.compatibility.playerpoints;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.UUID;

/**
 * Checks if the player has specified amount of PlayerPoints points.
 */
public class PlayerPointsCondition extends Condition {

    private final VariableNumber count;
    private final PlayerPointsAPI api;

    public PlayerPointsCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        count = instruction.getVarNum();
        api = ((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI();
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final UUID uuid = PlayerConverter.getPlayer(playerID).getUniqueId();
        return api.look(uuid) >= count.getInt(playerID);
    }

}
