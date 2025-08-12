package org.betonquest.betonquest.quest.event.notify;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.api.text.Text;
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
    private final Text text;

    /**
     * Creates a new {@link NotifyEvent}.
     *
     * @param notifyIO the {@link NotifyIO} to use
     * @param text     the text to use
     */
    public NotifyEvent(final NotifyIO notifyIO, final Text text) {
        this.notifyIO = notifyIO;
        this.text = text;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        notifyIO.sendNotify(text.asComponent(profile), profile);
    }
}
