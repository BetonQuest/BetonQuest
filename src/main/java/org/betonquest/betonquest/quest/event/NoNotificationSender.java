package org.betonquest.betonquest.quest.event;

/**
 * Notification sender that suppresses notifications instead of sending them.
 */
public class NoNotificationSender implements NotificationSender {

    /**
     * Create the no notification sender.
     */
    public NoNotificationSender() {}

    @Override
    public void sendNotification(final String playerID) {
        // null object pattern
    }
}
