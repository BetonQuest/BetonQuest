package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profile.Profile;

/**
 * Allows sending notifications to a player.
 */
public interface NotificationSender {

    /**
     * Send the notification.
     *
     * @param profile   the {@link Profile} of the player to receive the notification
     * @param variables the variables to use in the notification
     */
    void sendNotification(Profile profile, String... variables);
}
