package org.betonquest.betonquest.api.quest.placeholder.nullable;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.NullablePlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;

/**
 * An adapter to handle both the {@link PlayerPlaceholder} and {@link PlayerlessPlaceholder}
 * with one common implementation of the {@link NullablePlaceholder}.
 */
public final class NullablePlaceholderAdapter implements PlayerPlaceholder, PlayerlessPlaceholder {

    /**
     * Common null-safe placeholder implementation.
     */
    private final NullablePlaceholder placeholder;

    /**
     * Create an adapter that resolves placeholders via the given common implementation.
     *
     * @param placeholder common null-safe placeholder implementation
     */
    public NullablePlaceholderAdapter(final NullablePlaceholder placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        return placeholder.getValue(profile);
    }

    @Override
    public String getValue() throws QuestException {
        return placeholder.getValue(null);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return placeholder.isPrimaryThreadEnforced();
    }
}
