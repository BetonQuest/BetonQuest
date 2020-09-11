package pl.betoncraft.betonquest.compatibility.playerpoints;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.UUID;

/**
 * Adds/removes/multiplies/divides PlayerPoints points.
 */
public class PlayerPointsEvent extends QuestEvent {

    private VariableNumber count;
    private boolean multi;
    private PlayerPointsAPI api;

    public PlayerPointsEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        String string = instruction.next();
        if (string.startsWith("*")) {
            multi = true;
            string = string.replace("*", "");
        } else {
            multi = false;
        }
        try {
            count = new VariableNumber(instruction.getPackage().getName(), string);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse point amount", e);
        }
        api = ((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI();
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final UUID uuid = PlayerConverter.getPlayer(playerID).getUniqueId();
        if (multi) {
            api.set(uuid, (int) Math.floor(api.look(uuid) * count.getDouble(playerID)));
        } else {
            final double amount = count.getDouble(playerID);
            if (amount < 0) {
                api.take(uuid, (int) Math.floor(-amount));
            } else {
                api.give(uuid, (int) Math.floor(amount));
            }
        }
        return null;
    }

}
