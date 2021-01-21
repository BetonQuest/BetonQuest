package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.entity.Player;

import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class SubTitleNotifyIO extends NotifyIO {

    private final int fadeIn;
    private final int stay;
    private final int fadeOut;


    public SubTitleNotifyIO(final Map<String, String> data) throws InstructionParseException {
        super(data);

        fadeIn = getIntegerData("fadein", 10);
        stay = getIntegerData("stay", 70);
        fadeOut = getIntegerData("fadeout", 20);
    }

    @Override
    protected void notifyPlayer(final String message, final Player player) {
        player.sendTitle("", message, fadeIn, stay, fadeOut);
    }
}
