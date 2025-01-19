package org.betonquest.betonquest.config;

import org.betonquest.betonquest.api.bukkit.config.custom.ConfigurationSectionDecorator;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;

/**
 * Facade for easy loading and saving of configs.
 */
public final class ConfigurationFileImpl extends ConfigurationSectionDecorator implements ConfigurationFile {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Holds the config file.
     */
    private final ConfigAccessor accessor;

    /**
     * Creates a new {@link ConfigurationFileImpl}.
     * Patches the configuration file held by the {@code accessor} with the patch file from the {@code patchAccessor}.
     *
     * @param log          a {@link BetonQuestLogger} instance
     * @param accessor     a {@link ConfigAccessor} that holds the config file
     * @param patcher      a {@link Patcher} instance that holds the patches to apply
     * @param relativeRoot the root to relativize the config accessors to for logging
     * @throws InvalidConfigurationException if patch modifications couldn't be saved
     */
    public ConfigurationFileImpl(final BetonQuestLogger log, final ConfigAccessor accessor, @Nullable final Patcher patcher, final URI relativeRoot) throws InvalidConfigurationException {
        super(accessor.getConfig());
        this.log = log;
        this.accessor = accessor;
        if (patcher != null && patchConfig(patcher, relativeRoot)) {
            try {
                accessor.save();
            } catch (final IOException e) {
                throw new InvalidConfigurationException("The configuration file was patched but could not be saved! Reason: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Patches the config with the given patch config.
     *
     * @param patcher the patcher to use
     * @return if the file was modified
     */
    private boolean patchConfig(final Patcher patcher, final URI relativeRoot) {
        if (patcher.hasUpdate()) {
            final URI configPath = accessor.getConfigurationFile().getAbsoluteFile().toURI();
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

    @Override
    public boolean save() throws IOException {
        return accessor.save();
    }

    @Override
    public boolean delete() throws IOException {
        return accessor.delete();
    }

    @Override
    public boolean reload() throws IOException {
        if (accessor.reload()) {
            original = accessor.getConfig();
            return true;
        }
        return false;
    }
}
