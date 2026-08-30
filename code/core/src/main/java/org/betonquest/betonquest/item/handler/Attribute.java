package org.betonquest.betonquest.item.handler;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * Values to be set on an item meta which may be resolved per {@link Profile}.
 *
 */
@FunctionalInterface
public interface Attribute {

    /**
     * Resolves all placeholders into a definit state.
     *
     * @param profile the profile to resolve placeholders with
     * @return the fully resolved argument
     * @throws QuestException if argument resolving for the profile fails
     */
    ResolvedAttribute<?> resolve(@Nullable Profile profile) throws QuestException;
}
