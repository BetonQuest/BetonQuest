package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * Creates and Resolves {@link Placeholders}.
 */
public interface Placeholders {

    /**
     * Generates a new instance of a {@link Argument} of type {@link String}. If a similar one was already
     * created, it will return it instead of creating a new one.
     *
     * @param pack        package in which the placeholder is defined
     * @param instruction instruction of the placeholder, including start and end % characters.
     * @return the placeholder instance
     * @throws QuestException when the placeholder parsing fails
     */
    Argument<String> create(@Nullable QuestPackage pack, String instruction) throws QuestException;

    /**
     * Resolves the placeholder for specified player. If the placeholder is not loaded, it will create it.
     *
     * @param pack    the {@link QuestPackage} in which the placeholder is defined
     * @param name    name of the placeholder, including start and end % characters
     * @param profile the {@link Profile} of the player
     * @return the value of this placeholder for given player
     * @throws QuestException if the placeholder could not be created
     */
    String getValue(QuestPackage pack, String name, @Nullable Profile profile) throws QuestException;

    /**
     * Resolves the placeholder for specified player from string format.
     *
     * @param placeholder the package with the placeholder, in {@code <package>:<placeholder>} format
     * @param profile     the {@link Profile} of the player
     * @return the value of parsed placeholder for given player
     * @throws QuestException if the package cannot be parsed, is not present or the placeholder could not be created
     * @see #getValue(QuestPackage, String, Profile)
     */
    String getValue(String placeholder, @Nullable Profile profile) throws QuestException;
}
