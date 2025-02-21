package org.betonquest.betonquest.config;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
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
 * Factory for {@link ConfigAccessor} instances.
 */
public class DefaultConfigAccessorFactory implements ConfigAccessorFactory {
    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a new DefaultConfigAccessorFactory instance.
     *
     * @param loggerFactory logger factory to use
     * @param log           the logger that will be used for logging
     */
    public DefaultConfigAccessorFactory(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log) {
        this.loggerFactory = loggerFactory;
        this.log = log;
    }

    @Override
    public ConfigAccessor create(@Nullable final File configurationFile, @Nullable final Plugin plugin, @Nullable final String resourceFile) throws InvalidConfigurationException, FileNotFoundException {
        return new StandardConfigAccessor(configurationFile, plugin, resourceFile);
    }

    @Override
    public ConfigAccessor createPatching(final File configurationFile, final Plugin plugin, final String resourceFile, @Nullable final PatchTransformerRegisterer patchTransformerRegisterer) throws InvalidConfigurationException, FileNotFoundException {
        final ConfigAccessor accessor = create(configurationFile, plugin, resourceFile);
        final ConfigAccessor resourceAccessor = create(plugin, resourceFile);
        accessor.getConfig().setDefaults(resourceAccessor.getConfig());
        accessor.getConfig().options().copyDefaults(true);
        try {
            accessor.save();
        } catch (final IOException e) {
            throw new InvalidConfigurationException("Default values were applied to the config but could not be saved! Reason: " + e.getMessage(), e);
        }
        final Patcher patcher = createPatcher(patchTransformerRegisterer, createPatchAccessor(plugin, resourceFile), accessor);
        final BetonQuestLogger logger = loggerFactory.create(StandardPatchingConfigAccessor.class, "Config");
        return new StandardPatchingConfigAccessor(logger, configurationFile, plugin, resourceFile, patcher, plugin.getDataFolder().getParentFile().toURI());
    }

    @Nullable
    private Patcher createPatcher(@Nullable final PatchTransformerRegisterer patchTransformerRegisterer, @Nullable final ConfigAccessor patchAccessor, final ConfigAccessor accessor) {
        if (patchAccessor == null) {
            return null;
        }
        final BetonQuestLogger patcherLogger = loggerFactory.create(Patcher.class, "Config Patcher");
        final Patcher patcher = new Patcher(patcherLogger, accessor.getConfig(), patchAccessor.getConfig());
        (patchTransformerRegisterer == null ? new DefaultPatchTransformerRegisterer() : patchTransformerRegisterer).registerTransformers(patcher);
        return patcher;
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
            return create(null, plugin, resourceFilePatch);
        } catch (final FileNotFoundException e) {
            log.debug(e.getMessage(), e);
        }
        return null;
    }
}
