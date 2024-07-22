package org.betonquest.betonquest.api.quest.event.nullable;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.jetbrains.annotations.Nullable;

/**
 * Quest event that can work both with and without a profile.
 */
public interface NullableEvent {
    /**
     * Execute the event with a nullable profile.
     *
     * @param profile profile or null
     * @throws QuestRuntimeException if the event cannot be executed correctly;
     *                               this might indicate that the profile cannot be null
     *                               in this specific case
     */
    void execute(@Nullable Profile profile) throws QuestRuntimeException;
}
