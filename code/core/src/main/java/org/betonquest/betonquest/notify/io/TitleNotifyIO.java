package org.betonquest.betonquest.notify.io;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.betonquest.betonquest.api.common.component.ComponentLineWrapper;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.notify.NotifyIO;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Displays the message as title and subtitle after line break.
 */
public class TitleNotifyIO extends NotifyIO {

    /**
     * Time in ticks the title fades in.
     */
    private final Variable<Number> variableFadeIn;

    /**
     * Time in ticks the title stays.
     */
    private final Variable<Number> variableStay;

    /**
     * Time in ticks the title fades out.
     */
    private final Variable<Number> variableFadeOut;

    /**
     * Create a new Title Notify IO.
     *
     * @param pack the source pack to resolve variables
     * @param data the customization data for notifications
     * @throws QuestException when the data could not be parsed
     */
    public TitleNotifyIO(@Nullable final QuestPackage pack, final Map<String, String> data) throws QuestException {
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

        final List<Component> messageComponents = ComponentLineWrapper.splitNewLine(message);
        final int size = messageComponents.size();
        final Component titleComponent = size >= 1 ? messageComponents.get(0) : Component.empty();
        final Component subtitleComponent = size >= 2 ? messageComponents.get(1) : Component.empty();
        final Title title = Title.title(titleComponent, subtitleComponent, Title.Times.times(fadeIn, stay, fadeOut));
        onlineProfile.getPlayer().showTitle(title);
    }
}
