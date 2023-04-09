package org.betonquest.betonquest.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.modules.config.QuestManager;
import org.betonquest.betonquest.notify.Notify;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Handles the configuration of the plugin
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.GodClass", "PMD.TooManyMethods", "PMD.UseObjectForClearerAPI",
        "PMD.CommentRequired", "PMD.AvoidLiteralsInIfCondition", "PMD.AvoidFieldNameMatchingTypeName",
        "PMD.ClassNamingConventions"})
public final class Config {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(Config.class);
    private static final Set<String> LANGUAGES = new LinkedHashSet<>();
    private static QuestManager questManager;
    private static BetonQuest plugin;
    private static ConfigurationFile messages;
    private static ConfigAccessor internal;
    private static String lang;

    private Config() {
    }

    /**
     * Creates new instance of the Config handler
     */
    @SuppressWarnings({"PMD.AssignmentToNonFinalStatic", "PMD.CognitiveComplexity", "PMD.NPathComplexity"})
    @SuppressFBWarnings({"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", "EI_EXPOSE_STATIC_REP2"})
    public static void setup(final BetonQuest plugin) {
        Config.plugin = plugin;
        LANGUAGES.clear();

        final File root = plugin.getDataFolder();
        try {
            messages = ConfigurationFile.create(new File(root, "messages.yml"), plugin, "messages.yml");
            internal = ConfigAccessor.create(plugin, "messages-internal.yml");
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            LOG.warn(e.getMessage(), e);
            return;
        }

        lang = plugin.getPluginConfig().getString("language");
        for (final String key : messages.getKeys(false)) {
            if (!"global".equals(key)) {
                LOG.debug("Loaded " + key + " language");
                LANGUAGES.add(key);
            }
        }

        questManager = new QuestManager(root);
    }

    /**
     * Retrieves the message from the configuration in specified language and
     * replaces the variables
     *
     * @param lang      language in which the message should be retrieved
     * @param message   name of the message to retrieve
     * @param variables array of variables to replace
     * @return message in that language, or message in English, or null if it
     * does not exist
     */
    public static String getMessage(final String lang, final String message, final String... variables) {
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
        if (result != null) {
            if (variables != null) {
                for (int i = 0; i < variables.length; i++) {
                    result = result.replace("{" + (i + 1) + "}", variables[i]);
                }
            }
            result = ChatColor.translateAlternateColorCodes('&', result);
        }
        return result;
    }

    /**
     * Retrieves the message from the configuration in specified language
     *
     * @param message name of the message to retrieve
     * @param lang    language in which the message should be retrieved
     * @return message in that language, or message in English, or null if it
     * does not exist
     */
    public static String getMessage(final String lang, final String message) {
        return getMessage(lang, message, (String[]) null);
    }

    /**
     * @return the map of packages and their names
     */
    public static Map<String, QuestPackage> getPackages() {
        return questManager.getPackages();
    }

    /**
     * Retrieves the string from across all configuration. The variables are not
     * replaced! To replace variables automatically just call getString() method
     * on {@link QuestPackage}.
     *
     * @param address address of the string
     * @return the requested string
     */
    public static String getString(final String address) {
        if (address == null) {
            return null;
        }
        final String[] parts = address.split("\\.");
        if (parts.length < 2) {
            return null;
        }
        final String main = parts[0];
        if ("config".equals(main)) {
            return plugin.getPluginConfig().getString(address.substring(7));
        } else if ("messages".equals(main)) {
            return messages.getString(address.substring(9));
        } else {
            final QuestPackage pack = getPackages().get(main);
            if (pack == null) {
                return null;
            }
            return pack.getRawString(address.substring(main.length() + 1));
        }
    }

    /**
     * @return messages configuration
     */
    public static ConfigurationFile getMessages() {
        return messages;
    }

    /**
     * @return the default language
     */
    public static String getLanguage() {
        return lang;
    }

    /**
     * Returns the ID of a conversation assigned to specified NPC, across all
     * packages. If there are multiple assignments for the same value, the first
     * one will be returned.
     *
     * @param value the name of the NPC (as defined in <i>package.yml</i>)
     * @return the ID of the conversation assigned to this NPC or null if there
     * isn't one
     */
    public static String getNpc(final String value) {
        // load npc assignments from all packages
        for (final Map.Entry<String, QuestPackage> entry : getPackages().entrySet()) {
            final QuestPackage pack = entry.getValue();
            final ConfigurationSection assignments = pack.getConfig().getConfigurationSection("npcs");
            if (assignments != null) {
                for (final String assignment : assignments.getKeys(false)) {
                    if (assignment.equalsIgnoreCase(value)) {
                        return entry.getKey() + "." + assignments.getString(assignment);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Sends a message to player from the {@link OnlineProfile} in his chosen language or default or English
     * (if previous not found).
     *
     * @param packName      ID of the pack
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param messageName   ID of the message
     */
    public static void sendMessage(final String packName, final OnlineProfile onlineProfile, final String messageName) {
        sendMessage(packName, onlineProfile, messageName, (String[]) null, null, null);
    }

    /**
     * Sends a message to player from the {@link OnlineProfile} in his chosen language or default or English
     * (if previous not found). It will replace all {x} sequences with the
     * variables.
     *
     * @param packName      ID of the pack
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param messageName   ID of the message
     * @param variables     array of variables which will be inserted into the string
     */
    public static void sendMessage(final String packName, final OnlineProfile onlineProfile, final String messageName, final String... variables) {
        sendMessage(packName, onlineProfile, messageName, variables, null, null, (String) null);
    }

    /**
     * Sends a message to player from the {@link OnlineProfile} in his chosen language or default or English
     * (if previous not found). It will replace all {x} sequences with the
     * variables and play the sound.
     *
     * @param packName      ID of the pack
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param messageName   ID of the message
     * @param variables     array of variables which will be inserted into the string
     * @param soundName     name of the sound to play to the player
     */
    public static void sendMessage(final String packName, final OnlineProfile onlineProfile, final String messageName, final String[] variables, final String soundName) {
        sendMessage(packName, onlineProfile, messageName, variables, soundName, null, (String) null);
    }

    /**
     * Sends a message to player from the {@link OnlineProfile} in his chosen language or default or English
     * (if previous not found). It will replace all {x} sequences with the
     * variables and play the sound. It will also add a prefix to the message.
     *
     * @param packName        ID of the pack
     * @param onlineProfile   the {@link OnlineProfile} of the player
     * @param messageName     ID of the message
     * @param variables       array of variables which will be inserted into the message
     * @param soundName       name of the sound to play to the player
     * @param prefixName      ID of the prefix
     * @param prefixVariables array of variables which will be inserted into the prefix
     */
    public static void sendMessage(final String packName, final OnlineProfile onlineProfile, final String messageName, final String[] variables, final String soundName,
                                   final String prefixName, final String... prefixVariables) {
        final String message = parseMessage(packName, onlineProfile, messageName, variables, prefixName, prefixVariables);
        if (message == null || message.length() == 0) {
            return;
        }

        final Player player = onlineProfile.getPlayer();
        player.sendMessage(message);
        if (soundName != null) {
            playSound(onlineProfile, soundName);
        }
    }

    public static void sendNotify(final String packName, final OnlineProfile onlineProfile, final String messageName, final String category) throws QuestRuntimeException {
        sendNotify(packName, onlineProfile, messageName, null, category);
    }

    public static void sendNotify(final String packName, final OnlineProfile onlineProfile, final String messageName, final String[] variables, final String category) throws QuestRuntimeException {
        sendNotify(packName, onlineProfile, messageName, variables, category, null);
    }

    /**
     * Sends a notification to player in his chosen language or default or English
     * (if previous not found). It will replace all {x} sequences with the
     * variables and play the sound. It will also add a prefix to the message.
     *
     * @param packName      ID of the pack
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param messageName   ID of the message
     * @param variables     array of variables which will be inserted into the message
     * @param category      notification category
     * @param data          custom notifyIO data
     * @throws QuestRuntimeException thrown if it is not possible to send the notification
     */
    public static void sendNotify(final String packName, final OnlineProfile onlineProfile, final String messageName, final String[] variables, final String category, final Map<String, String> data) throws QuestRuntimeException {
        final String message = parseMessage(packName, onlineProfile, messageName, variables);
        if (message == null || message.length() == 0) {
            return;
        }

        Notify.get(Config.getPackages().get(packName), category, data).sendNotify(message, onlineProfile);
    }

    public static String parseMessage(final String packName, final OnlineProfile onlineProfile, final String messageName, final String... variables) {
        return parseMessage(packName, onlineProfile, messageName, variables, null, (String) null);
    }

    /**
     * Retrieve's a message in the language of the player from the {@link OnlineProfile}, replacing variables
     *
     * @param packName        ID of the pack
     * @param onlineProfile   the {@link OnlineProfile} of the player
     * @param messageName     name of the message to retrieve
     * @param variables       Variables to replace in message
     * @param prefixName      ID of the prefix
     * @param prefixVariables array of variables which will be inserted into the prefix
     * @return The parsed message.
     */
    public static String parseMessage(final String packName, final OnlineProfile onlineProfile, final String messageName, final String[] variables, final String prefixName,
                                      final String... prefixVariables) {
        final PlayerData playerData = plugin.getPlayerData(onlineProfile);
        if (playerData == null) {
            return null;
        }
        final String language = playerData.getLanguage();
        String message = getMessage(language, messageName, variables);
        if (message == null || message.length() == 0) {
            return null;
        }
        if (prefixName != null) {
            final String prefix = getMessage(language, prefixName, prefixVariables);
            if (prefix.length() > 0) {
                message = prefix + message;
            }
        }
        if (packName != null) {
            for (final String variable : BetonQuest.resolveVariables(message)) {
                final String replacement = BetonQuest.getInstance().getVariableValue(packName, variable, onlineProfile);
                message = message.replace(variable, replacement);
            }
        }
        return message;
    }

    /**
     * Plays a sound specified in the plugin's config to the player
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
                LOG.warn("Unknown sound type: " + rawSound, e);
            }
        }
    }

    /**
     * @return the languages defined for this plugin
     */
    public static Set<String> getLanguages() {
        return LANGUAGES;
    }
}
