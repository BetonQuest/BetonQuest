package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestException;

import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class TitleNotifyIO extends NotifyIO {
    private final int fadeIn;

    private final int stay;

    private final int fadeOut;

    public TitleNotifyIO(final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);

        fadeIn = getIntegerData("fadein", 10);
        stay = getIntegerData("stay", 70);
        fadeOut = getIntegerData("fadeout", 20);
    }

    @Override
    protected void notifyPlayer(final String message, final OnlineProfile onlineProfile) {
        final String[] messageParts = message.split("\n");
        final String title = messageParts[0].isEmpty() ? " " : messageParts[0];
        final String subtitle = messageParts.length > 1 ? messageParts[1] : "";
        onlineProfile.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
}
