package org.betonquest.betonquest.api.quest.placeholder.nullable;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;
import org.jetbrains.annotations.Nullable;

/**
 * Quest placeholder that can work both with and without a profile.
 */
@FunctionalInterface
public interface NullablePlaceholder extends PrimaryThreadEnforceable {

    /**
     * Resolve the placeholder with a nullable profile.
     *
     * @param profile profile or null
     * @return the resolved placeholder value; or an empty string if the placeholder
     * cannot be resolved, this might indicate that the profile cannot be null
     * in this specific case
     * @throws QuestException when the value could not be retrieved
     */
    String getValue(@Nullable Profile profile) throws QuestException;
}
