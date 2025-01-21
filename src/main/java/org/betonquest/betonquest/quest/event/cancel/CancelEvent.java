package org.betonquest.betonquest.quest.event.cancel;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.config.QuestCanceler;

/**
 * The cancel event.
 */
public class CancelEvent implements OnlineEvent {

    /**
     * The canceler to use.
     */
    private final QuestCanceler canceler;

    /**
     * Creates a new cancel event.
     *
     * @param canceler the canceler to use
     */
    public CancelEvent(final QuestCanceler canceler) {
        this.canceler = canceler;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        canceler.cancel(profile);
    }
}
