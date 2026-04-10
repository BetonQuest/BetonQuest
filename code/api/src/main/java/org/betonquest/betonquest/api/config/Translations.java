package org.betonquest.betonquest.api.config;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Offers access to the messages in configurations and its translations as well as available languages.
 */
public interface Translations {

    /**
     * Retrieves the languages available in the message configurations.
     *
     * @return the {@link Set} of languages
     */
    Set<String> getLanguages();

    /**
     * Retrieves the message from the configuration in the profile's language and replaces the placeholders.
     *
     * @param profile      the profile to get the message for
     * @param message      name of the message to retrieve
     * @param replacements array of placeholders to replace
     * @return message with replaced placeholders in the profile's language or the default language or in English
     * @throws IllegalArgumentException if the message could not be found in the configuration
     * @throws QuestException           if the message could not be parsed
     */
    Component getMessage(@Nullable Profile profile, String message, VariableReplacement... replacements) throws QuestException;
}
