package org.betonquest.betonquest.notify;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;

import java.time.Duration;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class TitleNotifyIO extends NotifyIO {
    private final Duration fadeIn;

    private final Duration stay;

    private final Duration fadeOut;

    public TitleNotifyIO(final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);

        fadeIn = Duration.ofMillis(getIntegerData("fadein", 10) * 50L);
        stay = Duration.ofMillis(getIntegerData("stay", 70) * 50L);
        fadeOut = Duration.ofMillis(getIntegerData("fadeout", 20) * 50L);
    }

    @Override
    protected void notifyPlayer(final String message, final OnlineProfile onlineProfile) {
        final String[] messageParts = message.split("\n");
        final String title = messageParts[0].isEmpty() ? " " : messageParts[0];
        final String subtitle = messageParts.length > 1 ? messageParts[1] : "";
        onlineProfile.getPlayer().sendTitle(title, subtitle, (int) (fadeIn.toMillis() / 50), (int) (stay.toMillis() / 50), (int) (fadeOut.toMillis() / 50));
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) {
        final Title title = Title.title(message, Component.empty(), Title.Times.times(fadeIn, stay, fadeOut));
        onlineProfile.getPlayer().showTitle(title);
    }
}
