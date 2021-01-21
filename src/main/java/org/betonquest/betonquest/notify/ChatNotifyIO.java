package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.entity.Player;

import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class ChatNotifyIO extends NotifyIO {

    public ChatNotifyIO(final Map<String, String> data) throws InstructionParseException {
        super(data);
    }

    @Override
    protected void notifyPlayer(final String message, final Player player) {
        player.sendMessage(message);
    }
}
