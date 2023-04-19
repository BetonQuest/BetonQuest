package org.betonquest.betonquest.quest.event.notify;

import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.utils.PlayerConverter;

import java.util.Map;

/**
 * {@link Event} the implementation of the notify all event.
 */
public class NotifyAllEvent extends NotifyEvent implements Event {

    /**
     * Creates a new {@link NotifyAllEvent}.
     *
     * @param notifyIO     the {@link NotifyIO} to use
     * @param translations the translations to use
     */
    public NotifyAllEvent(final NotifyIO notifyIO, final Map<String, VariableString> translations) {
        super(notifyIO, translations);
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            super.execute(onlineProfile);
        }
    }
}
