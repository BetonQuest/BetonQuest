package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * Notification sender that sends ingame chat notifications to the player if they are online.
 */
public class IngameNotificationSender implements NotificationSender {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Message name to fetch the localized message from the messages.yml config files.
     */
    private final String messageName;

    /**
     * Quest package to send the message from.
     */
    private final QuestPackage questPackage;

    /**
     * Full ID of the message sending object.
     */
    private final String fullId;

    /**
     * Categories to send the message to.
     */
    private final String[] categories;

    /**
     * Create the info-category notification sender.
     *
     * @param log                  the logger that will be used for logging
     * @param questPackage         quest package to send the message from
     * @param fullId               full ID of the message sending object
     * @param level                the notification level
     * @param messageName          identifier of the message to send
     * @param additionalCategories categories to send the message to
     */
    public IngameNotificationSender(final BetonQuestLogger log, final QuestPackage questPackage, final String fullId, final NotificationLevel level, final String messageName, final String... additionalCategories) {
        this.log = log;
        this.messageName = messageName;
        this.questPackage = questPackage;
        this.fullId = fullId;
        this.categories = new String[additionalCategories.length + 2];
        categories[0] = messageName;
        System.arraycopy(additionalCategories, 0, categories, 1, additionalCategories.length);
        categories[categories.length - 1] = level.getCategory();
    }

    @Override
    public void sendNotification(final Profile profile, final String... variables) {
        profile.getOnlineProfile().ifPresent(onlineProfile -> {
            try {
                Config.sendNotify(questPackage, onlineProfile, messageName, variables, String.join(",", categories));
            } catch (final QuestException e) {
                log.warn(questPackage, "The notify system was unable to play a sound for the '" + messageName + "' message in '" + fullId + "'. Error was: '" + e.getMessage() + "'", e);
            }
        });
    }
}
