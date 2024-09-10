package org.betonquest.betonquest.api.quest.npc;

import org.betonquest.betonquest.api.quest.QuestException;

/**
 * A validated wrapper for a {@link Npc}.
 *
 * @param <T> the original npc type
 */
public interface NpcWrapper<T> {
    /**
     * Gets the Npc represented by this Wrapper.
     *
     * @return the npc ready to use
     * @throws QuestException when the Npc cannot be found
     */
    Npc<T> getNpc() throws QuestException;
}
