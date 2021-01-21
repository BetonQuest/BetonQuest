package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@SuppressWarnings("PMD.CommentRequired")
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
