package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Notification sender that sends notifications in the info category.
 */
public class InfoNotificationSender implements NotificationSender {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(InfoNotificationSender.class);

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
     * Create the info-category notification sender.
     *
     * @param messageName  message package to send the message from
     * @param questPackage quest package to send the message from
     * @param fullId       full ID of the message sending object
     */
    public InfoNotificationSender(final String messageName, final QuestPackage questPackage, final String fullId) {
        this.messageName = messageName;
        this.questPackage = questPackage;
        this.fullId = fullId;
    }

    @Override
    public void sendNotification(final Profile profile) {
        profile.getOnlineProfile().ifPresent(onlineProfile -> {
            try {
                Config.sendNotify(questPackage.getQuestPath(), onlineProfile, messageName, null, messageName + ",info");
            } catch (final QuestRuntimeException e) {
                LOG.warn(questPackage, "The notify system was unable to play a sound for the '" + messageName + "' category in '" + fullId + "'. Error was: '" + e.getMessage() + "'", e);
            }
        });
    }
}
