package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profiles.OnlineProfile;

/**
 * Allows sending notifications to a player.
 */
public interface NotificationSender {

    /**
     * Send the notification.
     *
     * @param onlineProfile the {@link OnlineProfile} of the player to receive the notification
     */
    void sendNotification(OnlineProfile onlineProfile);
}
