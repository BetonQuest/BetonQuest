package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class SuppressNotifyIO extends NotifyIO {

    public SuppressNotifyIO(final Map<String, String> data) throws InstructionParseException {
        super(new HashMap<>());
        data.clear();
    }

    @Override
    protected void notifyPlayer(final String message, final Player player) {
        // Empty
    }
}
