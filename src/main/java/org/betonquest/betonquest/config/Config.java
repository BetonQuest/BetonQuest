package org.betonquest.betonquest.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.api.config.ConfigurationFileFactory;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.modules.config.QuestManager;
import org.betonquest.betonquest.notify.Notify;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Handles the configuration of the plugin.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.CommentRequired", "NullAway.Init"})
public final class Config {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuest.getInstance().getLoggerFactory().create(Config.class);

    private static final Set<String> LANGUAGES = new LinkedHashSet<>();

    private static QuestManager questManager;

    private static BetonQuest plugin;

    private static ConfigurationFile messages;

    private static ConfigAccessor internal;

    private static String lang;

    private Config() {
    }

    /**
     * Creates new instance of the Config handler.
     *
     * @param plugin the {@link BetonQuest} plugin instance
     * @param config the {@link ConfigurationFile} to load from
     */
    @SuppressFBWarnings("EI_EXPOSE_STATIC_REP2")
    public static void setup(final BetonQuest plugin, final ConfigurationFile config) {
        Config.plugin = plugin;
        LANGUAGES.clear();
        final ConfigAccessorFactory configAccessorFactory = plugin.getConfigAccessorFactory();
        final ConfigurationFileFactory configurationFileFactory = plugin.getConfigurationFileFactory();

        final File root = plugin.getDataFolder();
        try {
            messages = configurationFileFactory.create(new File(root, "messages.yml"), plugin, "messages.yml");
            internal = configAccessorFactory.create(plugin, "messages-internal.yml");
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            LOG.warn(e.getMessage(), e);
            return;
        }

        lang = config.getString("language");
        for (final String key : messages.getKeys(false)) {
            if (!"global".equals(key)) {
                LOG.debug("Loaded " + key + " language");
                LANGUAGES.add(key);
            }
        }

        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();
        questManager = new QuestManager(loggerFactory, loggerFactory.create(QuestManager.class), configAccessorFactory, root);
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
    @Nullable
    public static String getMessage(final String lang, final String message, @Nullable final String... variables) {
        String result = messages.getString(lang + "." + message);
        if (result == null) {
            result = messages.getString(getLanguage() + "." + message);
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
            return null;
        }
        if (variables != null) {
            for (int i = 0; i < variables.length; i++) {
                result = result.replace("{" + (i + 1) + "}", variables[i]);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', result);
    }

    /**
     * Retrieves the message from the configuration in the specified language.
     *
     * @param message name of the message to retrieve
     * @param lang    language in which the message should be retrieved
     * @return message in that language, or message in English, or null if it
     * does not exist
     */
    @SuppressWarnings("NullAway")
    public static String getMessage(final String lang, final String message) {
        return getMessage(lang, message, (String[]) null);
    }

    /**
     * Get all the packages loaded by the plugin.
     *
     * @return the map of packages and their names
     */
    public static Map<String, QuestPackage> getPackages() {
        return questManager.getPackages();
    }

    /**
     * Retrieves the string from the configuration.
     *
     * @param address address of the string without leading {@code config.}
     * @return the requested string
     */
    public static String getConfigString(final String address) {
        return plugin.getPluginConfig().getString(address);
    }

    /**
     * Get {@link ConfigurationFile} containing all the messages.
     *
     * @return messages configuration
     */
    public static ConfigurationFile getMessages() {
        return messages;
    }

    /**
     * Get the default language.
     *
     * @return the default language
     */
    public static String getLanguage() {
        return lang;
    }

    /**
     * Sends a message to player from the {@link OnlineProfile} in his chosen language or default or English
     * (if previous not found).
     *
     * @param pack          the pack
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param messageName   ID of the message
     */
    public static void sendMessage(@Nullable final QuestPackage pack, final OnlineProfile onlineProfile, final String messageName) {
        sendMessage(pack, onlineProfile, messageName, (String[]) null, null, null);
    }

    /**
     * Sends a message to player from the {@link OnlineProfile} in his chosen language or default or English
     * (if previous not found). It will replace all {x} sequences with the
     * variables.
     *
     * @param pack          the pack
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param messageName   ID of the message
     * @param variables     array of variables which will be inserted into the string
     */
    public static void sendMessage(@Nullable final QuestPackage pack, final OnlineProfile onlineProfile, final String messageName, @Nullable final String... variables) {
        sendMessage(pack, onlineProfile, messageName, variables, null, null, (String) null);
    }

    /**
     * Sends a message to player from the {@link OnlineProfile} in his chosen language or default or English
     * (if previous not found). It will replace all {x} sequences with the
     * variables and play the sound.
     *
     * @param pack          the pack
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param messageName   ID of the message
     * @param variables     array of variables which will be inserted into the string
     * @param soundName     name of the sound to play to the player
     */
    public static void sendMessage(@Nullable final QuestPackage pack, final OnlineProfile onlineProfile, final String messageName, final String[] variables, final String soundName) {
        sendMessage(pack, onlineProfile, messageName, variables, soundName, null, (String) null);
    }

    /**
     * Sends a message to player from the {@link OnlineProfile} in his chosen language or default or English
     * (if previous not found). It will replace all {x} sequences with the
     * variables and play the sound. It will also add a prefix to the message.
     *
     * @param pack            the pack
     * @param onlineProfile   the {@link OnlineProfile} of the player
     * @param messageName     ID of the message
     * @param variables       array of variables which will be inserted into the message
     * @param soundName       name of the sound to play to the player
     * @param prefixName      ID of the prefix
     * @param prefixVariables array of variables which will be inserted into the prefix
     */
    public static void sendMessage(@Nullable final QuestPackage pack, final OnlineProfile onlineProfile, final String messageName, @Nullable final String[] variables, @Nullable final String soundName,
                                   @Nullable final String prefixName, @Nullable final String... prefixVariables) {
        final String message = parseMessage(pack, onlineProfile, messageName, variables, prefixName, prefixVariables);
        if (message == null || message.isEmpty()) {
            return;
        }

        final Player player = onlineProfile.getPlayer();
        player.sendMessage(message);
        if (soundName != null) {
            playSound(onlineProfile, soundName);
        }
    }

    public static void sendNotify(@Nullable final QuestPackage pack, final OnlineProfile onlineProfile, final String messageName, @Nullable final String category) throws QuestException {
        sendNotify(pack, onlineProfile, messageName, null, category);
    }

    public static void sendNotify(@Nullable final QuestPackage pack, final OnlineProfile onlineProfile, final String messageName, @Nullable final String[] variables, @Nullable final String category) throws QuestException {
        sendNotify(pack, onlineProfile, messageName, variables, category, null);
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
     * @param data          custom notifyIO data
     * @throws QuestException thrown if it is not possible to send the notification
     */
    @SuppressWarnings("NullAway")
    public static void sendNotify(@Nullable final QuestPackage pack, final OnlineProfile onlineProfile, final String messageName, @Nullable final String[] variables, @Nullable final String category, @Nullable final Map<String, String> data) throws QuestException {
        final String message = parseMessage(pack, onlineProfile, messageName, variables);
        if (message == null || message.isEmpty()) {
            return;
        }

        Notify.get(pack, category, data).sendNotify(message, onlineProfile);
    }

    @Nullable
    public static String parseMessage(@Nullable final QuestPackage pack, final OnlineProfile onlineProfile, final String messageName, @Nullable final String... variables) {
        return parseMessage(pack, onlineProfile, messageName, variables, null, (String) null);
    }

    /**
     * Retrieve's a message in the language of the player from the {@link OnlineProfile}, replacing variables.
     *
     * @param pack            the pack
     * @param onlineProfile   the {@link OnlineProfile} of the player
     * @param messageName     name of the message to retrieve
     * @param variables       Variables to replace in the message
     * @param prefixName      ID of the prefix
     * @param prefixVariables array of variables which will be inserted into the prefix
     * @return The parsed message.
     */
    @SuppressWarnings("NullAway")
    @Nullable
    public static String parseMessage(@Nullable final QuestPackage pack, final OnlineProfile onlineProfile,
                                      final String messageName, @Nullable final String[] variables,
                                      @Nullable final String prefixName, @Nullable final String... prefixVariables) {
        final PlayerData playerData = plugin.getPlayerDataStorage().get(onlineProfile);
        final String language = playerData.getLanguage();
        String message = getMessage(language, messageName, variables);
        if (message == null || message.isEmpty()) {
            return null;
        }
        if (prefixName != null) {
            final String prefix = getMessage(language, prefixName, prefixVariables);
            if (prefix != null && !prefix.isEmpty()) {
                message = prefix + message;
            }
        }
        if (pack != null) {
            try {
                message = new VariableString(pack, message).getString(onlineProfile);
            } catch (final QuestException e) {
                LOG.warn("Could not parse message: " + message, e);
            }
        }
        return message;
    }

    /**
     * Plays a sound specified in the plugin's config to the player.
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param soundName     the name of the sound to play to the player
     */
    public static void playSound(final OnlineProfile onlineProfile, final String soundName) {
        final Player player = onlineProfile.getPlayer();
        final String rawSound = plugin.getPluginConfig().getString("sounds." + soundName);
        if (!"false".equalsIgnoreCase(rawSound)) {
            try {
                player.playSound(player.getLocation(), Sound.valueOf(rawSound), 1F, 1F);
            } catch (final IllegalArgumentException e) {
                player.playSound(player.getLocation(), rawSound.toLowerCase(Locale.ROOT), 1F, 1F);
            }
        }
    }

    /**
     * Get the languages defined for this plugin.
     *
     * @return the languages defined for this plugin
     */
    public static Set<String> getLanguages() {
        return LANGUAGES;
    }
}
