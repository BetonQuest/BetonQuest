package org.betonquest.betonquest.quest.action;

/**
 * The level of a notification.
 */
public enum NotificationLevel {
    /**
     * The notification is an info.
     */
    INFO("info"),

    /**
     * The notification is an error.
     */
    ERROR("error");

    /**
     * The category of the notification.
     */
    private final String category;

    NotificationLevel(final String category) {
        this.category = category;
    }

    /**
     * Get the category of the notification.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }
}
