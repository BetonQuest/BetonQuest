package org.betonquest.betonquest.notify.io;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.betonquest.betonquest.api.common.component.ComponentLineWrapper;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.notify.NotifyIO;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class TitleNotifyIO extends NotifyIO {

    private final Duration fadeIn;

    private final Duration stay;

    private final Duration fadeOut;

    public TitleNotifyIO(@Nullable final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);

        fadeIn = Duration.ofMillis(getIntegerData("fadein", 10) * 50L);
        stay = Duration.ofMillis(getIntegerData("stay", 70) * 50L);
        fadeOut = Duration.ofMillis(getIntegerData("fadeout", 20) * 50L);
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) {
        final List<Component> messageComponents = ComponentLineWrapper.splitNewLine(message);
        final int size = messageComponents.size();
        final Component titleComponent = size >= 1 ? messageComponents.get(0) : Component.empty();
        final Component subtitleComponent = size >= 2 ? messageComponents.get(1) : Component.empty();
        final Title title = Title.title(titleComponent, subtitleComponent, Title.Times.times(fadeIn, stay, fadeOut));
        onlineProfile.getPlayer().showTitle(title);
    }
}
