package org.betonquest.betonquest.notify.io;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.notify.NotifyIO;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class SubTitleNotifyIO extends NotifyIO {

    private final Variable<Number> variableFadeIn;

    private final Variable<Number> variableStay;

    private final Variable<Number> variableFadeOut;

    public SubTitleNotifyIO(@Nullable final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);

        variableFadeIn = getNumberData("fadein", 10);
        variableStay = getNumberData("stay", 70);
        variableFadeOut = getNumberData("fadeout", 20);
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) throws QuestException {
        final Duration fadeIn = Duration.ofMillis(variableFadeIn.getValue(onlineProfile).longValue() * 50L);
        final Duration stay = Duration.ofMillis(variableStay.getValue(onlineProfile).longValue() * 50L);
        final Duration fadeOut = Duration.ofMillis(variableFadeOut.getValue(onlineProfile).longValue() * 50L);

        final Title title = Title.title(Component.empty(), message, Title.Times.times(fadeIn, stay, fadeOut));
        onlineProfile.getPlayer().showTitle(title);
    }
}
