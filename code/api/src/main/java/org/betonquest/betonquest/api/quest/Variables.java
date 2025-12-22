package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * Creates and Resolves {@link org.betonquest.betonquest.api.quest.variable Variables}.
 */
public interface Variables {

    /**
     * Generates a new instance of a {@link Variable} of type {@link String}. If a similar one was already
     * created, it will return it instead of creating a new one.
     *
     * @param pack        package in which the variable is defined
     * @param instruction instruction of the variable, including both % characters.
     * @return the Variable instance
     * @throws QuestException when the variable parsing fails
     */
    Variable<String> create(@Nullable QuestPackage pack, String instruction) throws QuestException;

    /**
     * Resolves the variable for specified player. If the variable is not loaded, it will create it.
     *
     * @param pack    the {@link QuestPackage} in which the variable is defined
     * @param name    name of the variable (instruction, with % characters)
     * @param profile the {@link Profile} of the player
     * @return the value of this variable for given player
     * @throws QuestException if the variable could not be created
     */
    String getValue(QuestPackage pack, String name, @Nullable Profile profile) throws QuestException;

    /**
     * Resolves the variable for specified player from string format.
     *
     * @param variable the package with the variable, in {@code <package>:<variable>} format
     * @param profile  the {@link Profile} of the player
     * @return the value of parsed variable for given player
     * @throws QuestException if the package cannot be parsed, is not present or the variable could not be created
     * @see #getValue(QuestPackage, String, Profile)
     */
    String getValue(String variable, @Nullable Profile profile) throws QuestException;
}
