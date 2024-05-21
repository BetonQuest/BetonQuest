package org.betonquest.betonquest.api.quest.variable;

import org.betonquest.betonquest.api.profiles.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link PlayerVariable} which can be executed with a profile or static.
 * <p>
 * Common usage is when containing {@link PlayerVariable}s can require a {@link Profile}.
 */
public interface Variable extends PlayerVariable, PlayerlessVariable {
    @Override
    String getValue(@Nullable Profile profile);

    @Override
    default String getValue() {
        return getValue(null);
    }
}
