package org.betonquest.betonquest.api.message;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * A Message is the abstract representation of a specific text that a player can receive.
 * The actual content can depend on the player.
 */
@FunctionalInterface
public interface Message {
    /**
     * Returns the message as a component for the given profile.
     *
     * @param profile the profile to get the message for
     * @return the message as a component
     * @throws QuestException if an error occurs while getting the message
     */
    Component asComponent(@Nullable Profile profile) throws QuestException;
}
