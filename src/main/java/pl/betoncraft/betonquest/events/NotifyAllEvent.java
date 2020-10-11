package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class NotifyAllEvent extends NotifyEvent {

    public NotifyAllEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            super.execute(PlayerConverter.getID(player));
        }
        return null;
    }
}
