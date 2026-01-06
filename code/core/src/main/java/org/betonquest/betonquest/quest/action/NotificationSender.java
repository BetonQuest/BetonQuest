package org.betonquest.betonquest.quest.action;

import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.profile.Profile;

/**
 * Allows sending notifications to a player.
 */
@FunctionalInterface
public interface NotificationSender {

    /**
     * Send the notification.
     *
     * @param profile      the {@link Profile} of the player to receive the notification
     * @param replacements the placeholders to use in the notification
     */
    void sendNotification(Profile profile, VariableReplacement... replacements);
}
