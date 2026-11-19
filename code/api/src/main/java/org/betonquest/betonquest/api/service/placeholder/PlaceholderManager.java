package org.betonquest.betonquest.api.service.placeholder;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * The PlaceholderManager is responsible for resolving placeholders within strings
 * to their corresponding values.
 *
 * @since 3.0.0
 */
public interface PlaceholderManager {

    /**
     * Returns an {@link Argument} instance for the given placeholder instruction.
     * <br><br>
     * If an identical argument was previously created, it is reused.
     * The instruction must include the leading and trailing {@code %} characters.
     *
     * @param pack        the package in which the placeholder is defined, or {@code null}
     * @param instruction the instruction of the placeholder, including leading and trailing {@code %} characters
     * @return the placeholder argument instance
     * @throws QuestException if parsing the placeholder fails
     * @since 3.0.0
     */
    @Contract(pure = true, value = "_, !null -> new")
    Argument<String> create(@Nullable QuestPackage pack, String instruction) throws QuestException;

    /**
     * Resolves the placeholder for the specified profile.
     * <br><br>
     * If the placeholder is not yet loaded, it is created.
     * The name must include the leading and trailing {@code %} characters.
     *
     * @param pack    the {@link QuestPackage} in which the placeholder is defined
     * @param name    the name of the placeholder, including leading and trailing {@code %} characters
     * @param profile the {@link Profile} of the player, or {@code null} if no profile is involved
     * @return the resolved value of the placeholder for the given profile
     * @throws QuestException if the placeholder cannot be created or resolved
     * @since 3.0.0
     */
    @Contract(pure = true, value = "!null, !null, _ -> new")
    String getValue(QuestPackage pack, String name, @Nullable Profile profile) throws QuestException;

    /**
     * Resolves a placeholder for the specified profile from its string representation.
     * <br><br>
     * The placeholder string must follow the {@code <package>:<placeholder>} format.
     *
     * @param placeholder the placeholder string in {@code <package>:<placeholder>} format
     * @param profile     the {@link Profile} of the player, or {@code null} if no profile is involved
     * @return the resolved value of the placeholder for the given profile
     * @throws QuestException if the package cannot be parsed, is not present, or resolution fails
     * @see #getValue(QuestPackage, String, Profile)
     * @since 3.0.0
     */
    @Contract(pure = true, value = "!null, _ -> new")
    String getValue(String placeholder, @Nullable Profile profile) throws QuestException;
}
