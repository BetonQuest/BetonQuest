package org.betonquest.betonquest.config;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.api.config.ConfigurationFileFactory;
import org.betonquest.betonquest.api.config.patcher.PatchTransformerRegisterer;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.config.patcher.DefaultPatchTransformerRegisterer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Factory for {@link ConfigurationFile} instances.
 */
public class DefaultConfigurationFileFactory implements ConfigurationFileFactory {
    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The {@link ConfigAccessorFactory} to use for creating {@link ConfigAccessor} instances.
     */
    private final ConfigAccessorFactory configAccessorFactory;

    /**
     * Creates a new DefaultConfigurationFileFactory instance.
     *
     * @param loggerFactory         logger factory to use
     * @param log                   the logger that will be used for logging
     * @param configAccessorFactory the factory that will be used to create {@link ConfigAccessor}s
     */
    public DefaultConfigurationFileFactory(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log, final ConfigAccessorFactory configAccessorFactory) {
        this.loggerFactory = loggerFactory;
        this.log = log;
        this.configAccessorFactory = configAccessorFactory;
    }

    @Override
    @SuppressWarnings("NullAway")
    public ConfigurationFile create(final File configurationFile, final Plugin plugin, final String resourceFile, @Nullable final PatchTransformerRegisterer patchTransformerRegisterer) throws InvalidConfigurationException, FileNotFoundException {
        final ConfigAccessor accessor = configAccessorFactory.create(configurationFile, plugin, resourceFile);
        final ConfigAccessor resourceAccessor = configAccessorFactory.create(plugin, resourceFile);
        accessor.getConfig().setDefaults(resourceAccessor.getConfig());
        accessor.getConfig().options().copyDefaults(true);
        try {
            accessor.save();
        } catch (final IOException e) {
            throw new InvalidConfigurationException("Default values were applied to the config but could not be saved! Reason: " + e.getMessage(), e);
        }
        final ConfigAccessor patchAccessor = createPatchAccessor(plugin, resourceFile);
        final Patcher patcher;
        if (patchAccessor == null) {
            patcher = null;
        } else {
            final BetonQuestLogger patcherLogger = BetonQuest.getInstance().getLoggerFactory().create(Patcher.class, "ConfigurationFile Patcher");
            patcher = new Patcher(patcherLogger, accessor.getConfig(), patchAccessor.getConfig());
        }
        (patchTransformerRegisterer == null ? new DefaultPatchTransformerRegisterer() : patchTransformerRegisterer).registerTransformers(patcher);
        final BetonQuestLogger logger = loggerFactory.create(ConfigurationFileImpl.class, "ConfigurationFile");
        return new ConfigurationFileImpl(logger, accessor, patcher, plugin.getDataFolder().getParentFile().toURI());
    }

    @Nullable
    private ConfigAccessor createPatchAccessor(final Plugin plugin, final String resourceFile) throws InvalidConfigurationException {
        int index = resourceFile.lastIndexOf('.');
        final int separatorIndex = resourceFile.lastIndexOf(File.pathSeparator);
        if (index < separatorIndex) {
            index = -1;
        }
        if (index == -1) {
            index = resourceFile.length();
        }
        final String resourceFilePatch = resourceFile.substring(0, index) + ".patch" + resourceFile.substring(index);
        try {
            return configAccessorFactory.create(plugin, resourceFilePatch);
        } catch (final FileNotFoundException e) {
            log.debug(e.getMessage(), e);
        }
        return null;
    }
}
