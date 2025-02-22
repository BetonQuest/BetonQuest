package org.betonquest.betonquest.config;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.patcher.Patcher;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

/**
 * Facade for easy loading and saving of configs.
 */
public final class StandardPatchingConfigAccessor extends StandardConfigAccessor {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a new {@link StandardPatchingConfigAccessor}.
     * Patches the configuration file held by the {@code accessor} with the patch file from the {@code patchAccessor}.
     *
     * @param log               a {@link BetonQuestLogger} instance
     * @param configurationFile the {@link File} that is represented by this {@link StandardConfigAccessor}
     * @param plugin            the plugin which is the source of the resource file
     * @param resourceFile      the resource file to load from the plugin
     * @param patcher           a {@link Patcher} instance that holds the patches to apply
     * @param relativeRoot      the root to relativize the config accessors to for logging
     * @throws InvalidConfigurationException if patch modifications couldn't be saved
     * @throws FileNotFoundException         thrown if the {@code configurationFile} or the {@code resourceFile}
     *                                       could not be found
     */
    public StandardPatchingConfigAccessor(final BetonQuestLogger log, final File configurationFile, final Plugin plugin,
                                          final String resourceFile, @Nullable final Patcher patcher, final URI relativeRoot)
            throws InvalidConfigurationException, FileNotFoundException {
        super(configurationFile, plugin, resourceFile);
        this.log = log;
        if (patcher != null && patchConfig(configurationFile, patcher, relativeRoot)) {
            try {
                save();
            } catch (final IOException e) {
                throw new InvalidConfigurationException("The configuration file was patched but could not be saved! Reason: " + e.getMessage(), e);
            }
        }
    }

    private boolean patchConfig(final File configurationFile, final Patcher patcher, final URI relativeRoot) {
        if (patcher.hasUpdate()) {
            final URI configPath = configurationFile.getAbsoluteFile().toURI();
            final String relativePath = relativeRoot.relativize(configPath).getPath();

            log.info("Updating config file '" + relativePath + "' from version '" + patcher.getCurrentConfigVersion()
                    + "' to version '" + patcher.getNextConfigVersion().getVersion() + "'");

            final boolean flawless = patcher.patch();
            if (flawless) {
                log.info("Patching complete!");
            } else {
                log.warn("The patching progress did not go flawlessly. However, this does not mean your configs "
                        + "are now corrupted. Please check the errors above to see what the patcher did. "
                        + "You might want to adjust your config manually depending on that information.");
            }
            return true;
        }
        log.debug("No patch found.");

        return patcher.updateVersion();
    }
}
