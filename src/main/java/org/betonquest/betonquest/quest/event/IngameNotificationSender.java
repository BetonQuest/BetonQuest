package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Notification sender that sends notifications in the error category.
 */
public class IngameNotificationSender implements NotificationSender {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The notification level.
     */
    private final NotificationLevel level;

    /**
     * Message package to send the message from.
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
     * @param log          the logger that will be used for logging
     * @param questPackage quest package to send the message from
     * @param fullId       full ID of the message sending object
     * @param level        the notification level
     * @param messageName  message package to send the message from
     * @param categories   categories to send the message to
     */
    public IngameNotificationSender(final BetonQuestLogger log, final QuestPackage questPackage, final String fullId, final NotificationLevel level, final String messageName, final String... categories) {
        this.log = log;
        this.level = level;
        this.messageName = messageName;
        this.questPackage = questPackage;
        this.fullId = fullId;
        this.categories = categories;
    }

    @Override
    public void sendNotification(final Profile profile, final String... variables) {
        profile.getOnlineProfile().ifPresent(onlineProfile -> {
            try {
                final String fullCategoryList = String.join(",", messageName, String.join(",", categories), level.category());
                Config.sendNotify(questPackage.getQuestPath(), onlineProfile, messageName, variables, fullCategoryList);
            } catch (final QuestRuntimeException e) {
                log.warn(questPackage, "The notify system was unable to play a sound for the '" + messageName + "' message in '" + fullId + "'. Error was: '" + e.getMessage() + "'", e);
            }
        });
    }
}
