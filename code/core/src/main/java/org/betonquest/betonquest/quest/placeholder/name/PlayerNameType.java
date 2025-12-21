package org.betonquest.betonquest.quest.placeholder.name;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.profile.Profile;

/**
 * The type of the {@link PlayerNamePlaceholder}.
 */
public enum PlayerNameType {
    /**
     * The player's name.
     */
    NAME(profile -> profile.getPlayer().getName()),
    /**
     * The player's display name.
     */
    DISPLAY(profile -> profile.getOnlineProfile()
            .map(online -> online.getPlayer().getDisplayName())
            .orElseThrow(() -> new QuestException(profile.getPlayer().getName() + " is offline, cannot get display name."))),
    /**
     * The player's UUID.
     */
    UUID(profile -> profile.getPlayer().getUniqueId().toString());

    /**
     * Method to get the placeholder value from a profile.
     */
    private final QuestFunction<Profile, String> valueExtractor;

    PlayerNameType(final QuestFunction<Profile, String> valueExtractor) {
        this.valueExtractor = valueExtractor;
    }

    /* default */ String extractValue(final Profile profile) throws QuestException {
        return valueExtractor.apply(profile);
    }
}
