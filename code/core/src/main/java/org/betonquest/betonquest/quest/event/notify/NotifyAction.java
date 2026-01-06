package org.betonquest.betonquest.quest.event.notify;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.notify.NotifyIO;

/**
 * {@link PlayerAction} the implementation of the notify events.
 */
public class NotifyAction implements OnlineAction {

    /**
     * The {@link NotifyIO} to use.
     */
    private final NotifyIO notifyIO;

    /**
     * The translations to use.
     */
    private final Text text;

    /**
     * Creates a new {@link NotifyAction}.
     *
     * @param notifyIO the {@link NotifyIO} to use
     * @param text     the text to use
     */
    public NotifyAction(final NotifyIO notifyIO, final Text text) {
        this.notifyIO = notifyIO;
        this.text = text;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        notifyIO.sendNotify(text.asComponent(profile), profile);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
