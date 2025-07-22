package org.betonquest.betonquest.quest.event.notify;

import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.notify.NotifyIO;

/**
 * {@link PlayerEvent} the implementation of the notify events.
 */
public class NotifyEvent implements OnlineEvent {
    /**
     * The {@link NotifyIO} to use.
     */
    private final NotifyIO notifyIO;

    /**
     * The translations to use.
     */
    private final Message messages;

    /**
     * Creates a new {@link NotifyEvent}.
     *
     * @param notifyIO the {@link NotifyIO} to use
     * @param messages the messages to use
     */
    public NotifyEvent(final NotifyIO notifyIO, final Message messages) {
        this.notifyIO = notifyIO;
        this.messages = messages;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        notifyIO.sendNotify(messages.asComponent(profile), profile);
    }
}
