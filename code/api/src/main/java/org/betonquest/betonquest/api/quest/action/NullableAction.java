package org.betonquest.betonquest.api.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;
import org.jetbrains.annotations.Nullable;

/**
 * Quest action that can work both with and without a profile.
 */
@FunctionalInterface
public interface NullableAction extends PrimaryThreadEnforceable {

    /**
     * Execute the action with a nullable profile.
     *
     * @param profile profile or null
     * @throws QuestException if the action cannot be executed correctly,
     *                        this might indicate that the profile cannot be null
     *                        in this specific case
     */
    void execute(@Nullable Profile profile) throws QuestException;
}
