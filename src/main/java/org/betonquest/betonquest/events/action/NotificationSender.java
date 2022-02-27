package org.betonquest.betonquest.events.action;

/**
 * Allows sending notifications to a player.
 */
public interface NotificationSender {

    /**
     * Send the notification.
     *
     * @param playerID player to receive the notification
     */
    void sendNotification(String playerID);
}
