package org.betonquest.betonquest.api.quest.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * A validated wrapper for a {@link Npc}.
 *
 * @param <T> the original npc type
 * @since 3.0.0
 */
public interface NpcWrapper<T> {

    /**
     * Gets the single npc represented by this wrapper.
     * If multiple npcs match the definition, a quest exception is thrown.
     *
     * @param profile the profile to resolve the npc
     * @return the npc ready to use
     * @throws QuestException if the npc cannot be found, or multiple npcs match the definition
     * @since 3.0.0
     */
    Npc<T> getNpc(@Nullable Profile profile) throws QuestException;

    /**
     * Gets all npcs represented by this wrapper.
     *
     * @param profile the profile to resolve the npcs
     * @return the npcs ready to use
     * @throws QuestException if no npc can be found
     * @since 3.0.0
     */
    Set<Npc<T>> getNpcs(@Nullable Profile profile) throws QuestException;
}
