package org.betonquest.betonquest.api.text;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * A text is the abstract representation of a specific text that a player can receive.
 * The actual content can depend on the player.
 *
 * @since 3.0.0
 */
@SuppressWarnings("PMD.ShortClassName")
public interface Text {

    /**
     * Returns the text as a component for the given profile.
     *
     * @param profile the profile to get the text for
     * @return the text as a component
     * @throws QuestException if an error occurs while getting the text
     * @since 3.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    Component asComponent(@Nullable Profile profile) throws QuestException;

    /**
     * Returns the raw text for the given profile.
     *
     * @param profile the profile to get the text for
     * @return the text as unparsed string
     * @throws QuestException if an error occurs while getting the text
     * @since 3.1.0
     */
    @Contract(value = "_ -> new", pure = true)
    String asRaw(@Nullable Profile profile) throws QuestException;
}
