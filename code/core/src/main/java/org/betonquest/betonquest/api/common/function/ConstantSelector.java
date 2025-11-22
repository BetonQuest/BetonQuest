package org.betonquest.betonquest.api.common.function;

import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * Selector that always selects a fixed target.
 *
 * @param <T> target to select
 */
public class ConstantSelector<T> implements Selector<T> {
    /**
     * The target that should always be used.
     */
    private final T target;

    /**
     * Creates a selector that will always select the provided target.
     *
     * @param target target to be selected
     */
    public ConstantSelector(final T target) {
        this.target = target;
    }

    @Override
    public T selectFor(@Nullable final Profile profile) {
        return target;
    }
}
