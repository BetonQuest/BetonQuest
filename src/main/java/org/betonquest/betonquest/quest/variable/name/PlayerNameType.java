package org.betonquest.betonquest.quest.variable.name;

import org.betonquest.betonquest.api.profiles.Profile;

import java.util.function.Function;

/**
 * The type of the {@link PlayerNameVariable}.
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
            .orElseThrow(() -> new IllegalStateException(profile.getPlayer().getName() + " is offline, cannot get display name."))),
    /**
     * The player's UUID.
     */
    UUID(profile -> profile.getPlayer().getUniqueId().toString());

    /**
     * Method to get the variable value from a profile.
     */
    private final Function<Profile, String> valueExtractor;

    PlayerNameType(final Function<Profile, String> valueExtractor) {
        this.valueExtractor = valueExtractor;
    }

    /* default */ String extractValue(final Profile profile) {
        return valueExtractor.apply(profile);
    }
}
