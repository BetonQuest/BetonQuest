package org.betonquest.betonquest.config;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Loads and sends messages from the plugins messages.yml file and messages-internal.yml file.
 */
public class PluginMessage {
    /**
     * The scheme for a JAR file.
     */
    public static final String SCHEME_JAR = "jar";

    /**
     * The {@link PlayerDataStorage} to get the language from.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * The messages configuration file.
     */
    private final Map<String, FileConfigAccessor> messages;

    /**
     * The internal messages configuration file.
     */
    private final ConfigAccessor internal;

    /**
     * Creates a new instance of the PluginMessage handler.
     *
     * @param instance              the BetonQuest instance
     * @param playerDataStorage     the {@link PlayerDataStorage} instance
     * @param configAccessorFactory the config accessor factory
     * @throws QuestException if the messages could not be loaded
     */
    public PluginMessage(final BetonQuest instance, final PlayerDataStorage playerDataStorage, final ConfigAccessorFactory configAccessorFactory) throws QuestException {
        this.playerDataStorage = playerDataStorage;

        try {
            messages = loadMessages(instance, configAccessorFactory);
            internal = configAccessorFactory.create(instance, "messages-internal.yml");
        } catch (InvalidConfigurationException | URISyntaxException | IOException e) {
            throw new QuestException("Failed to load messages", e);
        }
    }

    private Map<String, FileConfigAccessor> loadMessages(final Plugin plugin, final ConfigAccessorFactory configAccessorFactory) throws URISyntaxException, IOException, InvalidConfigurationException {
        final File root = plugin.getDataFolder();
        final Map<String, FileConfigAccessor> messages = new HashMap<>();
        for (final Map.Entry<String, String> entry : loadMessages(plugin).entrySet()) {
            messages.put(entry.getKey(), configAccessorFactory.createPatching(new File(root, entry.getValue()), plugin, entry.getValue()));
        }
        for (final Map.Entry<String, String> file : loadMessages(root).entrySet()) {
            if (!messages.containsKey(file.getKey())) {
                messages.put(file.getKey(), configAccessorFactory.create(new File(root, file.getValue())));
            }
        }
        return messages;
    }

    private Map<String, String> loadMessages(final Plugin plugin) throws URISyntaxException, IOException {
        final URI uri = plugin.getClass().getResource("/lang").toURI();
        if (SCHEME_JAR.equals(uri.getScheme())) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                return loadMessages(fileSystem.getPath("lang"), Path::toString);
            }
        } else {
            return loadMessages(Paths.get(uri), Path::toString);
        }
    }

    private Map<String, String> loadMessages(final File root) throws IOException {
        final Path langFolder = new File(root, "lang").toPath();
        return loadMessages(langFolder, path -> root.toPath().relativize(path).toString());
    }

    private Map<String, String> loadMessages(final Path path, final Function<Path, String> valueResolver) throws IOException {
        try (Stream<Path> files = Files.list(path)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().endsWith(".yml"))
                    .filter(file -> !file.getFileName().toString().endsWith(".patch.yml"))
                    .collect(Collectors.toMap(file -> file.getFileName().toString().replace(".yml", ""),
                            valueResolver));
        }
    }

    /**
     * Retrieves the languages available in the messages configuration.
     *
     * @return the {@link Set} of languages
     */
    public final Set<String> getLanguages() {
        return messages.keySet();
    }

    /**
     * Reloads the configuration.
     *
     * @throws IOException if the configuration could not be reloaded
     */
    public void reload() throws IOException {
        for (final FileConfigAccessor config : messages.values()) {
            config.reload();
        }
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
        String result = getMessageFromSpecificLanguage(language, message);
        if (result == null) {
            result = getMessageFromSpecificLanguage(Config.getLanguage(), message);
        }
        if (result == null) {
            result = getMessageFromSpecificLanguage("en-US", message);
        }
        if (result == null) {
            result = internal.getConfig().getString(message);
        }
        if (result == null) {
            throw new IllegalArgumentException("Message " + message + " not found in the configuration");
        }
        for (final Replacement variable : variables) {
            result = result.replace("{" + variable.variable + "}", variable.replacement);
        }
        return ChatColor.translateAlternateColorCodes('&', result);
    }

    @Nullable
    private String getMessageFromSpecificLanguage(final String language, final String message) {
        final ConfigAccessor config = messages.get(language);
        if (config == null) {
            return null;
        }
        return config.getString(message);
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
