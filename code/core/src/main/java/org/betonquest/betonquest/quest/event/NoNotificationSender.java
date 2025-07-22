package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.profile.Profile;

/**
 * Notification sender that suppresses notifications instead of sending them.
 */
public class NoNotificationSender implements NotificationSender {

    /**
     * Create the no notification sender.
     */
    public NoNotificationSender() {
    }

    @Override
    public void sendNotification(final Profile profile, final VariableReplacement... variables) {
        // null object pattern
    }
}
