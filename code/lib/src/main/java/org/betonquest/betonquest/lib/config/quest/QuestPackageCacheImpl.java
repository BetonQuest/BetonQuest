package org.betonquest.betonquest.lib.config.quest;

import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageCache;
import org.betonquest.betonquest.lib.config.section.ConfigurationSectionDecorator;
import org.bukkit.configuration.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * Implementation of {@link QuestPackageCache}.
 */
public class QuestPackageCacheImpl extends ConfigurationSectionDecorator implements QuestPackageCache {

    /**
     * The underlying {@link FileConfigAccessor} that accesses the cache YAML file.
     */
    private final FileConfigAccessor accessor;

    /**
     * Creates a new {@link QuestPackageCacheImpl}.
     *
     * @param accessor the file config accessor for the cache YAML file
     */
    public QuestPackageCacheImpl(final FileConfigAccessor accessor) {
        super(accessor.getConfig());
        this.accessor = accessor;
    }

    @Override
    public boolean save() throws IOException {
        return accessor.save();
    }

    @Override
    public boolean delete() throws IOException {
        return accessor.delete();
    }

    @Override
    public void reload() throws IOException {
        accessor.reload();
    }

    @Override
    public File getConfigurationFile() {
        return accessor.getConfigurationFile();
    }

    @Override
    public Configuration getConfig() {
        return accessor.getConfig();
    }
}
