package org.betonquest.betonquest.config;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.Triple;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.common.component.VariableComponent;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.message.ParsedMessage;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Loads and sends messages from the plugins messages.yml file and messages-internal.yml file.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class PluginMessage {
    /**
     * The scheme for a JAR file.
     */
    public static final String SCHEME_JAR = "jar";

    /**
     * The {@link VariableProcessor} instance.
     */
    private final VariableProcessor variableProcessor;

    /**
     * The {@link MessageParser} instance.
     */
    private final MessageParser messageParser;

    /**
     * The {@link PlayerDataStorage} to get the language from.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * The language provider instance.
     */
    private final LanguageProvider languageProvider;

    /**
     * The messages configuration file.
     */
    private final Map<String, FileConfigAccessor> messages;

    /**
     * The internal messages configuration file.
     */
    private final ConfigAccessor internal;

    /**
     * All loaded messages.
     */
    private Map<String, Message> loadedMessages;

    /**
     * Creates a new instance of the PluginMessage handler.
     *
     * @param instance              the BetonQuest instance
     * @param variableProcessor     the {@link VariableProcessor} instance
     * @param playerDataStorage     the {@link PlayerDataStorage} instance
     * @param messageParser         the {@link MessageParser} instance
     * @param configAccessorFactory the config accessor factory
     * @param languageProvider      the {@link LanguageProvider} instance
     * @throws QuestException if the messages could not be loaded
     */
    public PluginMessage(final BetonQuest instance, final VariableProcessor variableProcessor,
                         final PlayerDataStorage playerDataStorage, final MessageParser messageParser,
                         final ConfigAccessorFactory configAccessorFactory, final LanguageProvider languageProvider)
            throws QuestException {
        this.variableProcessor = variableProcessor;
        this.messageParser = messageParser;
        this.playerDataStorage = playerDataStorage;
        this.languageProvider = languageProvider;

        try {
            messages = loadMessageFiles(instance, configAccessorFactory);
            internal = configAccessorFactory.create(instance, "messages-internal.yml");
        } catch (InvalidConfigurationException | URISyntaxException | IOException e) {
            throw new QuestException("Failed to load messages", e);
        }
        loadedMessages = loadMessages();
    }

    private Map<String, FileConfigAccessor> loadMessageFiles(final Plugin plugin, final ConfigAccessorFactory configAccessorFactory) throws URISyntaxException, IOException, InvalidConfigurationException {
        final File root = plugin.getDataFolder();
        final Map<String, FileConfigAccessor> messages = new HashMap<>();
        for (final Map.Entry<String, String> entry : loadMessageFiles(plugin).entrySet()) {
            messages.put(entry.getKey(), configAccessorFactory.createPatching(new File(root, entry.getValue()), plugin, entry.getValue()));
        }
        for (final Map.Entry<String, String> file : loadMessageFiles(root).entrySet()) {
            if (!messages.containsKey(file.getKey())) {
                messages.put(file.getKey(), configAccessorFactory.create(new File(root, file.getValue())));
            }
        }
        return messages;
    }

    private Map<String, String> loadMessageFiles(final Plugin plugin) throws URISyntaxException, IOException {
        final URL resourceLang = plugin.getClass().getResource("/lang");
        if (resourceLang == null) {
            return Collections.emptyMap();
        }
        final URI uri = resourceLang.toURI();
        if (SCHEME_JAR.equals(uri.getScheme())) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                return loadMessageFiles(fileSystem.getPath("lang"), Path::toString);
            }
        } else {
            return loadMessageFiles(Paths.get(uri), Path::toString);
        }
    }

    private Map<String, String> loadMessageFiles(final File root) throws IOException {
        final Path langFolder = new File(root, "lang").toPath();
        return loadMessageFiles(langFolder, path -> root.toPath().relativize(path).toString());
    }

    private Map<String, String> loadMessageFiles(final Path path, final Function<Path, String> valueResolver) throws IOException {
        try (Stream<Path> files = Files.list(path)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().endsWith(".yml"))
                    .filter(file -> !file.getFileName().toString().endsWith(".patch.yml"))
                    .collect(Collectors.toMap(file -> file.getFileName().toString().replace(".yml", ""),
                            valueResolver));
        }
    }

    private Map<String, Message> loadMessages() throws QuestException {
        final Map<String, Map<String, String>> languageMessages = new HashMap<>();
        messages.entrySet().stream()
                .map(entry -> {
                    final FileConfigAccessor config = entry.getValue();
                    final List<String> keys = config.getKeys(true).stream()
                            .filter(key -> !config.isConfigurationSection(key))
                            .toList();
                    return Triple.of(entry.getKey(), config, keys);
                })
                .forEach(triple -> {
                    final String language = triple.getLeft();
                    final FileConfigAccessor config = triple.getMiddle();
                    final List<String> keys = triple.getRight();
                    for (final String key : keys) {
                        final String message = config.getString(key);
                        if (message != null) {
                            languageMessages.computeIfAbsent(key, k -> new HashMap<>()).put(language, message);
                        }
                    }
                });
        internal.getKeys(true).stream()
                .filter(key -> !internal.isConfigurationSection(key))
                .forEach(key -> {
                    final String message = internal.getString(key);
                    if (message != null) {
                        languageMessages.computeIfAbsent(key, k -> new HashMap<>()).putIfAbsent(languageProvider.getDefaultLanguage(), message);
                    }
                });

        final Map<String, Message> loadedMessages = new HashMap<>();
        for (final Map.Entry<String, Map<String, String>> entry : languageMessages.entrySet()) {
            final String key = entry.getKey();
            final Map<String, Variable<String>> values = new HashMap<>();
            for (final Map.Entry<String, String> value : entry.getValue().entrySet()) {
                values.put(value.getKey(), new Variable<>(variableProcessor, null, value.getValue(), Argument.STRING));
            }
            loadedMessages.put(key, new ParsedMessage(messageParser, values, playerDataStorage, languageProvider));
        }
        return loadedMessages;
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
     * @throws IOException    if the configuration could not be reloaded
     * @throws QuestException if the configuration could not be parsed
     */
    public void reload() throws IOException, QuestException {
        for (final FileConfigAccessor config : messages.values()) {
            config.reload();
        }
        loadedMessages = loadMessages();
    }

    /**
     * Retrieves the message from the configuration in the profile's language and replaces the variables.
     *
     * @param profile   the profile to get the message for
     * @param message   name of the message to retrieve
     * @param variables array of variables to replace
     * @return message with replaced variables in the profile's language or the default language or in english
     * @throws IllegalArgumentException if the message could not be found in the configuration
     * @throws QuestException           if the message could not be parsed
     */
    public Component getMessage(@Nullable final Profile profile, final String message, final VariableReplacement... variables) throws QuestException {
        final Message component = loadedMessages.get(message);
        if (component == null) {
            throw new IllegalArgumentException("Message not found: " + message);
        }
        return new VariableComponent(component.asComponent(profile)).resolve(variables);
    }
}
