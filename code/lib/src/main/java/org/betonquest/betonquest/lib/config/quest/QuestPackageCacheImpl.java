package org.betonquest.betonquest.lib.config.quest;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageCache;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.LogSource;
import org.betonquest.betonquest.lib.config.section.unmodifiable.UnmodifiableConfigurationSection;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of {@link QuestPackageCache}.
 */
public class QuestPackageCacheImpl implements QuestPackageCache {

    /**
     * The supplier that provides the underlying {@link FileConfigAccessor}.
     */
    private final Supplier<FileConfigAccessor> accessor;

    /**
     * Custom logger for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Source the cache belongs to.
     */
    private final LogSource logSource;

    /**
     * Creates a new {@link QuestPackageCacheImpl}.
     *
     * @param accessor  the supplier for the file config accessor
     * @param log       the custom logger for this class
     * @param logSource the source the cache belongs to.
     */
    public QuestPackageCacheImpl(final Supplier<FileConfigAccessor> accessor, final BetonQuestLogger log, final LogSource logSource) {
        this.accessor = Suppliers.memoize(accessor);
        this.log = log;
        this.logSource = logSource;
    }

    private FileConfigAccessor getAccessor() {
        final FileConfigAccessor cacheAccessor = accessor.get();
        if (cacheAccessor == null) {
            throw new IllegalStateException("Accessor is null");
        }
        return cacheAccessor;
    }

    @Override
    public void write(final String key, final QuestConsumer<ConfigurationSection> cacheSection) {
        final FileConfigAccessor cacheAccessor = getAccessor();
        final ConfigurationSection section = cacheAccessor.getConfigurationSection(key);
        try {
            cacheSection.accept(Objects.requireNonNullElseGet(section, () -> cacheAccessor.createSection(key)));
        } catch (final QuestException e) {
            log.warn(logSource, "There was an error while trying to cache some values for '%s' for package '%s', reason: %s"
                    .formatted(key, logSource, e.getMessage()), e);
        }
        try {
            cacheAccessor.save();
        } catch (final IOException e) {
            log.warn(logSource, "Failed to save the cache '%s' for package '%s', reason: %s"
                    .formatted(key, logSource, e.getMessage()), e);
        }
    }

    @Override
    public Optional<ConfigurationSection> read(final String key) {
        final FileConfigAccessor cacheAccessor = getAccessor();
        final ConfigurationSection section = cacheAccessor.getConfigurationSection(key);
        if (section == null) {
            return Optional.empty();
        }
        return Optional.of(new UnmodifiableConfigurationSection(section));
    }
}
