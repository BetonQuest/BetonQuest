package org.betonquest.betonquest.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.notify.Notify;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Handles the configuration of the plugin
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.GodClass", "PMD.TooManyMethods", "PMD.UseObjectForClearerAPI",
        "PMD.CommentRequired", "PMD.AvoidLiteralsInIfCondition", "PMD.AvoidFieldNameMatchingTypeName"})
@CustomLog
public final class Config {
    public static final String CONFIG_PACKAGE_SEPARATOR = "-";

    private static final Map<String, QuestPackage> PACKAGES = new HashMap<>();
    private static final Map<String, QuestCanceler> CANCELERS = new HashMap<>();
    private static final List<String> LANGUAGES = new ArrayList<>();
    private static BetonQuest plugin;
    private static ConfigAccessor messages;
    private static ConfigAccessor internal;
    private static String lang;
    private static String defaultPackage = "default";

    private Config() {
    }

    /**
     * Creates new instance of the Config handler
     */
    @SuppressWarnings({"PMD.AssignmentToNonFinalStatic", "PMD.CognitiveComplexity", "PMD.NPathComplexity"})
    @SuppressFBWarnings({"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", "EI_EXPOSE_STATIC_REP2"})
    public static void setup(final BetonQuest plugin) {
        Config.plugin = plugin;
        PACKAGES.clear();
        CANCELERS.clear();
        LANGUAGES.clear();

        final File root = plugin.getDataFolder();

        try {
            ConfigAccessor.create(new File(root, "config.yml"), plugin, "config.yml");
            BetonQuest.getInstance().reloadConfig();
            messages = ConfigAccessor.create(new File(root, "messages.yml"), plugin, "messages.yml");
            internal = ConfigAccessor.create(plugin, "messages-internal.yml");
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            LOG.warn(e.getMessage(), e);
            return;
        }
        lang = BetonQuest.getInstance().getConfig().getString("language");
        for (final String key : messages.getConfig().getKeys(false)) {
            if (!"global".equals(key)) {
                LOG.debug("Loaded " + key + " language");
                LANGUAGES.add(key);
            }
        }

        final File packages = new File(root, "QuestPackages");
        defaultPackage = plugin.getConfig().getString("default_package", defaultPackage);

        // Create QuestPackages folder
        if (!packages.exists() && !packages.mkdir()) {
            LOG.error("It was not possible to create the folder '" + packages.getPath() + "'!");
            return;
        }

        // save example package
        createDefaultPackage(packages, defaultPackage);

        // load packages
        try {
            searchForPackages(packages, packages, "package", ".yml");
        } catch (final IOException e) {
            LOG.error("Error while loading '" + packages.getPath() + "'!", e);
        }

        // load quest cancelers
        for (final QuestPackage pack : PACKAGES.values()) {
            final ConfigurationSection section = pack.getConfig().getConfigurationSection("cancel");
            if (section == null) {
                continue;
            }
            for (final String key : section.getKeys(false)) {
                final String name = pack.getPackagePath() + "." + key;
                try {
                    CANCELERS.put(name, new QuestCanceler(name));
                } catch (final InstructionParseException e) {
                    LOG.warn(pack, "Could not load '" + name + "' quest canceler: " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Creates package with the given name and populates it with default quest
     *
     * @param packName name of the new package
     */
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    public static void createDefaultPackage(final File packages, final String packName) {
        final File def = new File(packages, packName.replace(CONFIG_PACKAGE_SEPARATOR, File.separator));
        if (!def.exists()) {
            LOG.info("Deploying " + packName + " package!");
            createDefaultPackageFile(def, "package.yml");
            createDefaultPackageFile(def, "events.yml");
            createDefaultPackageFile(def, "conditions.yml");
            createDefaultPackageFile(def, "journal.yml");
            createDefaultPackageFile(def, "items.yml");
            createDefaultPackageFile(def, "objectives.yml");
            createDefaultPackageFile(def, "custom.yml");
            createDefaultPackageFile(def, "conversations/innkeeper.yml");
        }
    }

    private static void createDefaultPackageFile(final File root, final String resource) {
        try {
            ConfigAccessor.create(new File(root, resource), plugin, "default/" + resource);
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    /**
     * Returns a map containing all quest cancelers from across all packages.
     *
     * @return the map with quest cancelers
     */
    public static Map<String, QuestCanceler> getCancelers() {
        return CANCELERS;
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
        String result = messages.getConfig().getString(lang + "." + message);
        if (result == null) {
            result = messages.getConfig().getString(Config.getLanguage() + "." + message);
        }
        if (result == null) {
            result = messages.getConfig().getString("en." + message);
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
        return PACKAGES;
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
            return plugin.getConfig().getString(address.substring(7));
        } else if ("messages".equals(main)) {
            return messages.getConfig().getString(address.substring(9));
        } else {
            final QuestPackage pack = PACKAGES.get(main);
            if (pack == null) {
                return null;
            }
            return pack.getRawString(address.substring(main.length() + 1));
        }
    }

    /**
     * @return messages configuration
     */
    public static ConfigAccessor getMessages() {
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
        for (final Map.Entry<String, QuestPackage> entry : PACKAGES.entrySet()) {
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
     * Sends a message to player in his chosen language or default or English
     * (if previous not found).
     *
     * @param packName    ID of the pack
     * @param playerID    ID of the player
     * @param messageName ID of the message
     */
    public static void sendMessage(final String packName, final String playerID, final String messageName) {
        sendMessage(packName, playerID, messageName, (String[]) null, null, null);
    }

    /**
     * Sends a message to player in his chosen language or default or English
     * (if previous not found). It will replace all {x} sequences with the
     * variables.
     *
     * @param packName    ID of the pack
     * @param playerID    ID of the player
     * @param messageName ID of the message
     * @param variables   array of variables which will be inserted into the string
     */
    public static void sendMessage(final String packName, final String playerID, final String messageName, final String... variables) {
        sendMessage(packName, playerID, messageName, variables, null, null, (String) null);
    }

    /**
     * Sends a message to player in his chosen language or default or English
     * (if previous not found). It will replace all {x} sequences with the
     * variables and play the sound.
     *
     * @param packName    ID of the pack
     * @param playerID    ID of the player
     * @param messageName ID of the message
     * @param variables   array of variables which will be inserted into the string
     * @param soundName   name of the sound to play to the player
     */
    public static void sendMessage(final String packName, final String playerID, final String messageName, final String[] variables, final String soundName) {
        sendMessage(packName, playerID, messageName, variables, soundName, null, (String) null);
    }

    /**
     * Sends a message to player in his chosen language or default or English
     * (if previous not found). It will replace all {x} sequences with the
     * variables and play the sound. It will also add a prefix to the message.
     *
     * @param packName        ID of the pack
     * @param playerID        ID of the player
     * @param messageName     ID of the message
     * @param variables       array of variables which will be inserted into the message
     * @param soundName       name of the sound to play to the player
     * @param prefixName      ID of the prefix
     * @param prefixVariables array of variables which will be inserted into the prefix
     */
    public static void sendMessage(final String packName, final String playerID, final String messageName, final String[] variables, final String soundName,
                                   final String prefixName, final String... prefixVariables) {
        final String message = parseMessage(packName, playerID, messageName, variables, prefixName, prefixVariables);
        if (message == null || message.length() == 0) {
            return;
        }

        final Player player = PlayerConverter.getPlayer(playerID);
        player.sendMessage(message);
        if (soundName != null) {
            playSound(playerID, soundName);
        }
    }

    public static void sendNotify(final String packName, final String playerID, final String messageName, final String category) throws QuestRuntimeException {
        sendNotify(packName, playerID, messageName, null, category);
    }

    public static void sendNotify(final String packName, final Player player, final String messageName, final String category) throws QuestRuntimeException {
        sendNotify(packName, player, messageName, null, category);
    }

    public static void sendNotify(final String packName, final String playerID, final String messageName, final String[] variables, final String category) throws QuestRuntimeException {
        sendNotify(packName, playerID, messageName, variables, category, null);
    }

    public static void sendNotify(final String packName, final Player player, final String messageName, final String[] variables, final String category) throws QuestRuntimeException {
        sendNotify(packName, player, messageName, variables, category, null);
    }

    public static void sendNotify(final String packName, final String playerID, final String messageName, final String[] variables, final String category, final Map<String, String> data) throws QuestRuntimeException {
        sendNotify(packName, PlayerConverter.getPlayer(playerID), messageName, variables, category, data);
    }

    /**
     * Sends a notification to player in his chosen language or default or English
     * (if previous not found). It will replace all {x} sequences with the
     * variables and play the sound. It will also add a prefix to the message.
     *
     * @param packName    ID of the pack
     * @param player      player
     * @param messageName ID of the message
     * @param variables   array of variables which will be inserted into the message
     * @param category    notification category
     * @param data        custom notifyIO data
     * @throws QuestRuntimeException thrown if it is not possible to send the notification
     */
    public static void sendNotify(final String packName, final Player player, final String messageName, final String[] variables, final String category, final Map<String, String> data) throws QuestRuntimeException {
        final String message = parseMessage(packName, player, messageName, variables);
        if (message == null || message.length() == 0) {
            return;
        }

        Notify.get(category, data).sendNotify(message, player);
    }

    public static String parseMessage(final String packName, final String playerID, final String messageName, final String... variables) {
        return parseMessage(packName, playerID, messageName, variables, null, (String) null);
    }

    public static String parseMessage(final String packName, final Player player, final String messageName, final String... variables) {
        return parseMessage(packName, player, messageName, variables, null, (String) null);
    }

    public static String parseMessage(final String packName, final String playerID, final String messageName, final String[] variables, final String prefixName,
                                      final String... prefixVariables) {
        return parseMessage(packName, PlayerConverter.getPlayer(playerID), messageName, variables, prefixName, prefixVariables);
    }

    /**
     * Retrieve's a message in the language of the player, replacing variables
     *
     * @param packName        ID of the pack
     * @param player          player
     * @param messageName     name of the message to retrieve
     * @param variables       Variables to replace in message
     * @param prefixName      ID of the prefix
     * @param prefixVariables array of variables which will be inserted into the prefix
     * @return The parsed message.
     */
    public static String parseMessage(final String packName, final Player player, final String messageName, final String[] variables, final String prefixName,
                                      final String... prefixVariables) {
        final PlayerData playerData = plugin.getPlayerData(PlayerConverter.getID(player));
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
                final String replacement = BetonQuest.getInstance().getVariableValue(packName, variable, PlayerConverter.getID(player));
                message = message.replace(variable, replacement);
            }
        }
        return message;
    }

    /**
     * Plays a sound specified in the plugin's config to the player
     *
     * @param playerID  the uuid of the player
     * @param soundName the name of the sound to play to the player
     */
    public static void playSound(final String playerID, final String soundName) {
        final Player player = PlayerConverter.getPlayer(playerID);
        if (player == null) {
            return;
        }
        final String rawSound = BetonQuest.getInstance().getConfig().getString("sounds." + soundName);
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
    public static List<String> getLanguages() {
        return LANGUAGES;
    }

    /**
     * @return the default package, as specified in the config
     */
    public static QuestPackage getDefaultPackage() {
        return getPackages().get(defaultPackage);
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    private static List<File> searchForPackages(final File root, final File file, final String packageIndicator, final String fileIndicator) throws IOException {
        if (!file.isDirectory()) {
            throw new IOException("File '" + file.getPath() + "' is not a directory!");
        }
        final File[] listFiles = file.listFiles();
        if (listFiles == null) {
            throw new IOException("Invalid list of file for directory '\" + file.getPath() + \"'!");
        }
        final List<File> files = new ArrayList<>();
        File main = null;
        for (final File subFile : listFiles) {
            if (subFile.isDirectory()) {
                try {
                    files.addAll(Objects.requireNonNull(searchForPackages(root, subFile, packageIndicator, fileIndicator)));
                } catch (final IOException e) {
                    LOG.warn(e.getMessage(), e);
                }
            } else {
                if ((packageIndicator + fileIndicator).equals(subFile.getName())) {
                    main = subFile;
                } else {
                    files.add(subFile);
                }
            }
        }
        if (main != null) {
            createPackage(root, main, files);
            files.clear();
        }
        return files;
    }

    private static void createPackage(final File root, final File main, final List<File> files) {
        final String packagePath = root.toURI().relativize(main.getParentFile().toURI())
                .toString().replace('/', ' ').trim().replaceAll(" ", CONFIG_PACKAGE_SEPARATOR);
        try {
            final QuestPackage pack = new QuestPackage(packagePath, main, files);
            if (!pack.getConfig().contains("enabled") || pack.isFromPackageConfig("enabled") && "true".equals(pack.getString("enabled"))) {
                PACKAGES.put(pack.getPackagePath(), pack);
            }
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            LOG.warn("QuestPackage '" + packagePath + "' could not be loaded, reason: " + e.getMessage(), e);
        }
    }
}
