package pl.betoncraft.betonquest.events;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.logging.Level;

public class OpSudoEvent extends QuestEvent {

    private final String[] commands;

    public OpSudoEvent(final Instruction instruction) throws InstructionParseException {
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
        final boolean previousOp = player.isOp();
        try {
            player.setOp(true);
            for (final String command : commands) {
                player.performCommand(command.replace("%player%", player.getName()));
            }
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, "Couldn't run OpSudoEvent.", e);
            LogUtils.logThrowable(e);
        } finally {
            player.setOp(previousOp);
        }
        return null;
    }

}
