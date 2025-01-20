package org.betonquest.betonquest.api.quest.variable.nullable;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Quest variable that can work both with and without a profile.
 */
public interface NullableVariable {
    /**
     * Resolve the variable with a nullable profile.
     *
     * @param profile profile or null
     * @return the resolved variable value; or an empty string if the variable
     * cannot be resolved, this might indicate that the profile cannot be null
     * in this specific case
     * @throws QuestException when the value could not be retrieved
     */
    String getValue(@Nullable Profile profile) throws QuestException;
}
