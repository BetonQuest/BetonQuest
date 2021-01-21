package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.entity.Player;

import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class TitleNotifyIO extends NotifyIO {

    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public TitleNotifyIO(final Map<String, String> data) throws InstructionParseException {
        super(data);

        fadeIn = getIntegerData("fadein", 10);
        stay = getIntegerData("stay", 70);
        fadeOut = getIntegerData("fadeout", 20);
    }

    @Override
    protected void notifyPlayer(final String message, final Player player) {
        final String[] messageParts = message.split("\n");
        final String title = messageParts[0];
        final String subtitle = messageParts.length > 1 ? messageParts[1] : "";
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
}
