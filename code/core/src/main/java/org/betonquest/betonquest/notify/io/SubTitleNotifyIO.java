package org.betonquest.betonquest.notify.io;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.Variables;
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
     * Create a new Sub Title Notify IO.
     *
     * @param variables the variable processor to create and resolve variables
     * @param pack      the source pack to resolve variables
     * @param data      the customization data for notifications
     * @throws QuestException when the data could not be parsed
     */
    public SubTitleNotifyIO(final Variables variables, @Nullable final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(variables, pack, data);

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
