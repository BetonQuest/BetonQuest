package org.betonquest.betonquest.quest.condition;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Decorator for conditions that do not support checking with offline players.
 */
public class OnlineProfileRequiredCondition implements PlayerCondition {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The condition to check.
     */
    private final PlayerCondition condition;

    /**
     * The quest package to use for reporting failed checks.
     */
    private final QuestPackage questPackage;

    /**
     * Wrap the given condition to only be checked if the given player is online.
     *
     * @param log          the logger that will be used for logging
     * @param condition    condition to check
     * @param questPackage quest package to use for reporting check failures
     */
    public OnlineProfileRequiredCondition(final BetonQuestLogger log, final PlayerCondition condition, final QuestPackage questPackage) {
        this.log = log;
        this.condition = condition;
        this.questPackage = questPackage;
    }

    @Override
    public boolean check(final Profile profile) throws QuestRuntimeException {
        if (profile.getOnlineProfile().isPresent()) {
            return condition.check(profile);
        } else {
            log.debug(questPackage, profile + " is offline, cannot check condition because it's not persistent.");
            return false;
        }
    }
}
