package org.betonquest.betonquest.meta;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.lib.config.section.unmodifiable.UnmodifiableConfigurationSection;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.intellij.lang.annotations.Pattern;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Handles metadata files and provides methods to access and modify them.
 */
public class MetaDataHandler implements MetaDataAcceptor, MetaDataProvider {

    /**
     * The folder where the metadata is stored.
     */
    private final File metadataFolder;

    /**
     * The factory to create {@link FileConfigAccessor}s.
     */
    private final ConfigAccessorFactory configAccessorFactory;

    /**
     * The logger to use.
     */
    private final BetonQuestLogger logger;

    /**
     * The metadata file accessors.
     */
    private final Map<String, FileConfigAccessor> metadata;

    /**
     * Creates a new instance of the MetaDataHandler.
     *
     * @param metaDataFolder        the folder where the metadata is stored
     * @param logger                the logger to use
     * @param configAccessorFactory the factory to create {@link FileConfigAccessor}s
     */
    public MetaDataHandler(final BetonQuestLogger logger, final File metaDataFolder, final ConfigAccessorFactory configAccessorFactory) {
        this.logger = logger;
        this.metadataFolder = metaDataFolder;
        this.configAccessorFactory = configAccessorFactory;
        this.metadata = new ConcurrentHashMap<>();
    }

    @Override
    public void accept(@Pattern(META_DATA_KEY_PATTERN) final String key, final QuestConsumer<ConfigurationSection> value) {
        final FileConfigAccessor accessor = accessKey(key);
        final Instant now = Instant.now();
        final String timestamp = String.valueOf(now.toEpochMilli());
        final ConfigurationSection section = accessor.createSection(timestamp);
        try {
            value.accept(section);
        } catch (final QuestException e) {
            logger.warn("Failed to set metadata for key '%s', an unexpected error happend: %s".formatted(key, e.getMessage()), e);
        }
        try {
            accessor.save();
        } catch (final IOException e) {
            logger.warn("Failed to save metadata for key: %s".formatted(key), e);
        }
    }

    @Override
    public void acceptChange(@Pattern(META_DATA_KEY_PATTERN) final String key, final QuestConsumer<ConfigurationSection> sectionConsumer, final Predicate<Map.Entry<Instant, ConfigurationSection>> changePredicate) {
        final Optional<Map.Entry<Instant, ConfigurationSection>> mostRecent = getMostRecent(key);
        if (mostRecent.map(changePredicate::test).orElse(true)) {
            accept(key, sectionConsumer);
        }
    }

    private FileConfigAccessor accessKey(final String key) {
        return metadata.computeIfAbsent(key, this::createAccessor);
    }

    private FileConfigAccessor createAccessor(final String key) {
        final File configurationFile = new File(metadataFolder, key + ".yml");
        try {
            if (configurationFile.exists() || configurationFile.createNewFile()) {
                return configAccessorFactory.create(configurationFile);
            }
            throw new IllegalStateException("Metadata file `%s` could not be accessed for key.".formatted(configurationFile));
        } catch (IOException | InvalidConfigurationException e) {
            throw new IllegalStateException("Metadata file `%s` could not be accessed for key.".formatted(configurationFile), e);
        }
    }

    @Override
    public Optional<Map.Entry<Instant, ConfigurationSection>> getMostRecent(final String key) {
        return query(key, history -> history.entrySet().stream()
                .max(Map.Entry.comparingByKey()));
    }

    @Override
    public Map<Instant, ConfigurationSection> getRecent(final String key, final long time, final ChronoUnit unit) {
        final Instant since = Instant.now().minus(time, unit);
        return query(key, history -> history.entrySet().stream()
                .filter(entry -> entry.getKey().isAfter(since))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @Override
    public Map<Instant, ConfigurationSection> getAll(final String key) {
        return query(key, Function.identity());
    }

    @Override
    public <T> T query(final String key, final Function<Map<Instant, ConfigurationSection>, T> query) {
        final FileConfigAccessor accessor = accessKey(key);
        final Set<String> values = accessor.getKeys(false);
        return query.apply(values.stream()
                .filter(accessor::isConfigurationSection)
                .map(entryKey -> Map.entry(Instant.ofEpochMilli(Long.parseLong(entryKey)),
                        new UnmodifiableConfigurationSection(Objects.requireNonNull(accessor.getConfigurationSection(entryKey)))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
}
