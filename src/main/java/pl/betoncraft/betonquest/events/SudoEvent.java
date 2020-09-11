package pl.betoncraft.betonquest.events;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Forces the player to run commands.
 */
public class SudoEvent extends QuestEvent {

    private final String[] commands;

    public SudoEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        try {
            final String string = instruction.getInstruction();
            commands = string.trim().substring(string.indexOf(" ") + 1).split("\\|");
        } catch (Exception e) {
            throw new InstructionParseException("Could not parse commands", e);
        }
    }

    @Override
    protected Void execute(final String playerID) {
        final Player player = PlayerConverter.getPlayer(playerID);
        for (final String command : commands) {
            player.performCommand(command.replace("%player%", player.getName()));
        }
        return null;
    }

}
