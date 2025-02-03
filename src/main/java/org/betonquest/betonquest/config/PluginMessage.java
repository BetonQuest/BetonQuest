package org.betonquest.betonquest.config;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.api.config.ConfigurationFileFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

/**
 * Loads and sends messages from the plugins messages.yml file and messages-internal.yml file.
 */
public class PluginMessage {
    /**
     * The {@link PlayerDataStorage} instance to get the language from.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * The messages configuration file.
     */
    private final ConfigurationFile messages;

    /**
     * The internal messages configuration file.
     */
    private final ConfigAccessor internal;

    /**
     * Creates a new instance of the PluginMessage handler.
     *
     * @param log                      the logger that will be used for logging
     * @param playerDataStorage        the {@link PlayerDataStorage} instance for the language
     * @param configurationFileFactory the configuration file factory
     * @param configAccessorFactory    the config accessor factory
     * @param plugin                   the plugin instance
     * @throws QuestException thrown if the messages could not be loaded
     */
    public PluginMessage(final BetonQuestLogger log, final PlayerDataStorage playerDataStorage,
                         final ConfigurationFileFactory configurationFileFactory,
                         final ConfigAccessorFactory configAccessorFactory, final Plugin plugin) throws QuestException {
        this.playerDataStorage = playerDataStorage;
        final File root = plugin.getDataFolder();

        try {
            messages = configurationFileFactory.create(new File(root, "messages.yml"), plugin, "messages.yml");
            internal = configAccessorFactory.create(plugin, "messages-internal.yml");
        } catch (InvalidConfigurationException | FileNotFoundException e) {
            throw new QuestException("Failed to load messages", e);
        }

        for (final String language : getLanguages()) {
            log.debug("Loaded " + language + " language");
        }
    }

    /**
     * Retrieves the languages available in the messages configuration.
     *
     * @return the {@link Set} of languages
     */
    public final Set<String> getLanguages() {
        return messages.getKeys(false);
    }

    /**
     * Reloads the configuration.
     *
     * @throws IOException if the configuration could not be reloaded
     */
    public void reload() throws IOException {
        messages.reload();
        internal.reload();
    }

    /**
     * Retrieves the message from the configuration in the profile's language and replaces the variables.
     *
     * @param profile   the profile to get the language from
     * @param message   name of the message to retrieve
     * @param variables array of variables to replace
     * @return message with replaced variables in the profile's language or the default language or in english
     * @throws IllegalArgumentException if the message could not be found in the configuration
     */
    public String getMessage(final Profile profile, final String message, final Replacement... variables) {
        final String language = playerDataStorage.get(profile).getLanguage();
        return getMessage(language, message, variables);
    }

    /**
     * Retrieves the message from the configuration and replaces the variables.
     *
     * @param message   the message to retrieve
     * @param variables the variables to replace
     * @return the message with replaced variables
     * @throws IllegalArgumentException if the message could not be found in the configuration
     */
    public String getMessage(final String message, final Replacement... variables) {
        return getMessage(Config.getLanguage(), message, variables);
    }

    private String getMessage(final String language, final String message, final Replacement... variables) {
        String result = messages.getString(language + "." + message);
        if (result == null) {
            result = messages.getString(Config.getLanguage() + "." + message);
        }
        if (result == null) {
            result = messages.getString("en." + message);
        }
        if (result == null) {
            result = internal.getConfig().getString(language + "." + message);
        }
        if (result == null) {
            result = internal.getConfig().getString("en." + message);
        }
        if (result == null) {
            throw new IllegalArgumentException("Message " + message + " not found in the configuration");
        }
        for (final Replacement variable : variables) {
            result = result.replace("{" + variable.variable + "}", variable.replacement);
        }
        return ChatColor.translateAlternateColorCodes('&', result);
    }

    /**
     * Represents a replacement of a variable in a message.
     *
     * @param variable    the variable to replace
     * @param replacement the replacement
     */
    public record Replacement(String variable, String replacement) {
    }
}
