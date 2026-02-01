package org.betonquest.betonquest.api.quest.placeholder;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;

import java.util.Optional;

/**
 * Adapter to resolve an {@link org.betonquest.betonquest.api.quest.placeholder.OnlinePlaceholder} via the {@link PlayerPlaceholder} interface.
 * It supports a fallback if the player is not online.
 */
public final class OnlinePlaceholderAdapter implements PlayerPlaceholder {

    /**
     * Placeholder to resolve with the online profile.
     */
    private final org.betonquest.betonquest.api.quest.placeholder.OnlinePlaceholder onlinePlaceholder;

    /**
     * Fallback placeholder to resolve if the player is not online.
     */
    private final PlayerPlaceholder fallbackPlaceholder;

    /**
     * Create a placeholder that resolves the given online placeholder.
     * If the player is not online it will throw with an info message.
     *
     * @param onlinePlaceholder placeholder to resolve for online players
     */
    public OnlinePlaceholderAdapter(final org.betonquest.betonquest.api.quest.placeholder.OnlinePlaceholder onlinePlaceholder) {
        this(onlinePlaceholder, profile -> {
            throw new QuestException(profile + " is offline, cannot get placeholder value because it's not persistent.");
        });
    }

    /**
     * Create a placeholder that resolves the given online placeholder if the player is online
     * and falls back to the fallback placeholder otherwise.
     *
     * @param onlinePlaceholder   placeholder to resolve for online players
     * @param fallbackPlaceholder fallback placeholder to resolve for offline players
     */
    public OnlinePlaceholderAdapter(final org.betonquest.betonquest.api.quest.placeholder.OnlinePlaceholder onlinePlaceholder, final PlayerPlaceholder fallbackPlaceholder) {
        this.onlinePlaceholder = onlinePlaceholder;
        this.fallbackPlaceholder = fallbackPlaceholder;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        final Optional<OnlineProfile> onlineProfile = profile.getOnlineProfile();
        if (onlineProfile.isPresent()) {
            return onlinePlaceholder.getValue(onlineProfile.get());
        }
        return fallbackPlaceholder.getValue(profile);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return onlinePlaceholder.isPrimaryThreadEnforced() || fallbackPlaceholder.isPrimaryThreadEnforced();
    }
}
