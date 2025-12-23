package org.betonquest.betonquest.api.quest.event.nullable;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;
import org.jetbrains.annotations.Nullable;

/**
 * Quest event that can work both with and without a profile.
 */
@FunctionalInterface
public interface NullableEvent extends PrimaryThreadEnforceable {

    /**
     * Execute the event with a nullable profile.
     *
     * @param profile profile or null
     * @throws QuestException if the event cannot be executed correctly,
     *                        this might indicate that the profile cannot be null
     *                        in this specific case
     */
    void execute(@Nullable Profile profile) throws QuestException;
}
