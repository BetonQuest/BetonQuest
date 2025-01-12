package org.betonquest.betonquest.api.quest.condition.online;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestException;

import java.util.Optional;

/**
 * Adapter to run an {@link OnlineCondition} via the {@link PlayerCondition} interface.
 * It supports a fallback if the player is not online.
 */
public final class OnlineConditionAdapter implements PlayerCondition {
    /**
     * Condition to check with the online profile.
     */
    private final OnlineCondition onlineCondition;

    /**
     * Fallback condition to check if the player is not online.
     */
    private final PlayerCondition fallbackCondition;

    /**
     * Create a condition that checks the given online condition.
     * If the player is not online, it logs a message into the debug log
     * and returns 'false'.
     *
     * @param onlineCondition condition to run for online players
     * @param log             log to write to if the player is not online
     * @param questPackage    quest package to reference in the log
     */
    public OnlineConditionAdapter(final OnlineCondition onlineCondition, final BetonQuestLogger log, final QuestPackage questPackage) {
        this(onlineCondition, profile -> {
            log.debug(
                    questPackage,
                    profile + " is offline, cannot check condition because it's not persistent."
            );
            return false;
        });
    }

    /**
     * Create a condition that checks the given online condition if the player is online
     * and falls back to the fallback condition otherwise.
     *
     * @param onlineCondition   condition to check for online players
     * @param fallbackCondition fallback condition to check for offline players
     */
    public OnlineConditionAdapter(final OnlineCondition onlineCondition, final PlayerCondition fallbackCondition) {
        this.onlineCondition = onlineCondition;
        this.fallbackCondition = fallbackCondition;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final Optional<OnlineProfile> onlineProfile = profile.getOnlineProfile();
        if (onlineProfile.isPresent()) {
            return onlineCondition.check(onlineProfile.get());
        } else {
            return fallbackCondition.check(profile);
        }
    }
}
