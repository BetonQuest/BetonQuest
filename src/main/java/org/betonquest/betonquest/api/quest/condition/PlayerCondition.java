package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.Optional;

/**
 * Interface for quest-conditions that are checked for a profile. It represents the normal condition as described in the
 * BetonQuest user documentation. It does not represent the playerless variant though, see {@link PlayerlessCondition}.
 */
public interface PlayerCondition {
    /**
     * Checks the condition.
     *
     * @param profile the {@link OnlineProfile} the condition is checked for
     * @return if the condition is fulfilled
     * @throws QuestRuntimeException when the condition check fails
     */
    boolean check(OnlineProfile profile) throws QuestRuntimeException;

    /**
     * Checks the condition.
     *
     * @param profile the {@link Profile} the condition is checked for
     * @return if the condition is fulfilled
     * @throws QuestRuntimeException when the condition check fails
     */
    default boolean check(final Profile profile) throws QuestRuntimeException {
        final Optional<OnlineProfile> onlineProfile = profile.getOnlineProfile();
        if (onlineProfile.isPresent()) {
            return check(onlineProfile.get());
        }
        throw new QuestRuntimeException("PlayerCondition can only be checked for OnlineProfiles");
    }
}
