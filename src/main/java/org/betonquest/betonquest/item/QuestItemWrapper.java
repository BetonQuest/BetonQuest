package org.betonquest.betonquest.item;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * A validated wrapper for a {@link QuestItem}.
 */
@FunctionalInterface
public interface QuestItemWrapper {
    /**
     * Gets the QuestItem represented by this Wrapper.
     *
     * @param profile the profile to resolve the item
     * @return the item ready to use
     * @throws QuestException when the item (variables) could not be resolved
     */
    QuestItem getItem(@Nullable Profile profile) throws QuestException;
}
