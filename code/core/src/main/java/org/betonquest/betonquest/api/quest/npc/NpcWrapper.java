package org.betonquest.betonquest.api.quest.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * A validated wrapper for a {@link Npc}.
 *
 * @param <T> the original npc type
 */
@FunctionalInterface
public interface NpcWrapper<T> {
    /**
     * Gets the Npc represented by this Wrapper.
     *
     * @param profile the profile to resolve the Npc
     * @return the npc ready to use
     * @throws QuestException when the Npc cannot be found
     */
    Npc<T> getNpc(@Nullable Profile profile) throws QuestException;
}
