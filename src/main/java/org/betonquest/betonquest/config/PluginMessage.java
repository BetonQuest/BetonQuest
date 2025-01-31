package org.betonquest.betonquest.config;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.api.config.ConfigurationFileFactory;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.notify.Notify;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Loads and sends messages from the plugins messages.yml file and messages-internal.yml file.
 */
public class PluginMessage {
    /**
     * The messages configuration file.
     */
    private final ConfigurationFile messages;

    /**
     * The internal messages configuration file.
     */
    private final ConfigAccessor internal;

    /**
     * The storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Creates a new instance of the PluginMessage handler.
     *
     * @param configurationFileFactory the configuration file factory
     * @param configAccessorFactory    the config accessor factory
     * @param plugin                   the plugin instance
     * @param playerDataStorage        the storage for player data
     * @throws QuestException thrown if the messages could not be loaded
     */
    public PluginMessage(final ConfigurationFileFactory configurationFileFactory,
                         final ConfigAccessorFactory configAccessorFactory, final Plugin plugin, final PlayerDataStorage playerDataStorage) throws QuestException {
        this.playerDataStorage = playerDataStorage;
        final File root = plugin.getDataFolder();

        try {
            messages = configurationFileFactory.create(new File(root, "messages.yml"), plugin, "messages.yml");
            internal = configAccessorFactory.create(plugin, "messages-internal.yml");
        } catch (InvalidConfigurationException | FileNotFoundException e) {
            throw new QuestException("Failed to load messages", e);
        }
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
     * Retrieves the message from the configuration in specified language and replaces the variables.
     *
     * @param lang      language in which the message should be retrieved
     * @param message   name of the message to retrieve
     * @param variables array of variables to replace
     * @return message in that language, or message in English, or null if it
     * does not exist
     */
    public String getMessage(final String lang, final String message, final Replacement... variables) {
        String result = messages.getString(lang + "." + message);
        if (result == null) {
            result = messages.getString(Config.getLanguage() + "." + message);
        }
        if (result == null) {
            result = messages.getString("en." + message);
        }
        if (result == null) {
            result = internal.getConfig().getString(lang + "." + message);
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
     * Retrieve's a message in the language of the player from the {@link OnlineProfile}, replacing variables.
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param messageName   name of the message to retrieve
     * @param variables     Variables to replace in the message
     * @return The parsed message.
     */
    public String parseMessage(final OnlineProfile onlineProfile, final String messageName, final Replacement... variables) {
        final PlayerData playerData = playerDataStorage.get(onlineProfile);
        final String language = playerData.getLanguage();
        return getMessage(language, messageName, variables);
    }

    /**
     * Sends a notification to player in his chosen language or default or English
     * (if previous not found). It will replace all {x} sequences with the
     * variables and play the sound. It will also add a prefix to the message.
     *
     * @param pack          the pack
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param messageName   ID of the message
     * @param variables     array of variables which will be inserted into the message
     * @param category      notification category
     * @throws QuestException thrown if it is not possible to send the notification
     */
    public void sendNotify(@Nullable final QuestPackage pack, final OnlineProfile onlineProfile,
                           final String messageName, @Nullable final String category, final Replacement... variables)
            throws QuestException {
        final String message = parseMessage(onlineProfile, messageName, variables);
        Notify.get(pack, category, null).sendNotify(message, onlineProfile);
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
