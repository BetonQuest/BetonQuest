package org.betonquest.betonquest.api.text;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * A text is the abstract representation of a specific text that a player can receive.
 * The actual content can depend on the player.
 */
@SuppressWarnings("PMD.ShortClassName")
@FunctionalInterface
public interface Text {

    /**
     * Returns the text as a component for the given profile.
     *
     * @param profile the profile to get the text for
     * @return the text as a component
     * @throws QuestException if an error occurs while getting the text
     */
    Component asComponent(@Nullable Profile profile) throws QuestException;
}
