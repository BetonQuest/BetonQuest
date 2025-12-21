package org.betonquest.betonquest.notify.io;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.notify.NotifyIO;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Map;

/**
 * Displays the message as subtitle.
 */
public class SubTitleNotifyIO extends NotifyIO {

    /**
     * Time in ticks the title fades in.
     */
    private final Argument<Number> fadeIn;

    /**
     * Time in ticks the title stays.
     */
    private final Argument<Number> stay;

    /**
     * Time in ticks the title fades out.
     */
    private final Argument<Number> fadeOut;

    /**
     * Create a new Sub Title Notify IO.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param pack         the source pack to resolve placeholders
     * @param data         the customization data for notifications
     * @throws QuestException when the data could not be parsed
     */
    public SubTitleNotifyIO(final Placeholders placeholders, @Nullable final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(placeholders, pack, data);

        fadeIn = getNumberData("fadein", 10);
        stay = getNumberData("stay", 70);
        fadeOut = getNumberData("fadeout", 20);
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) throws QuestException {
        final Duration fadeIn = Duration.ofMillis(this.fadeIn.getValue(onlineProfile).longValue() * 50L);
        final Duration stay = Duration.ofMillis(this.stay.getValue(onlineProfile).longValue() * 50L);
        final Duration fadeOut = Duration.ofMillis(this.fadeOut.getValue(onlineProfile).longValue() * 50L);

        final Title title = Title.title(Component.empty(), message, Title.Times.times(fadeIn, stay, fadeOut));
        onlineProfile.getPlayer().showTitle(title);
    }
}
