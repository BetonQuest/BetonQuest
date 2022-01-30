package org.betonquest.betonquest.modules.config;

import lombok.CustomLog;
import org.betonquest.betonquest.api.bukkit.config.custom.ConfigurationSectionDecorator;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;

/**
 * Facade for easy loading and saving of configs.
 */
@CustomLog(topic = "ConfigurationFile")
public final class ConfigurationFileImpl extends ConfigurationSectionDecorator implements ConfigurationFile {

    /**
     * Holds the config file.
     */
    private final ConfigAccessor accessor;

    /**
     * Creates a new {@link ConfigurationFileImpl}.
     * Patches the configuration file held by the {@code accessor} with the patch file from the {@code patchAccessor}.
     * <br>
     * See {@link ConfigurationFile#create} for more information.
     *
     * @param accessor      a {@link ConfigAccessor} that holds the config file
     * @param patchAccessor a {@link ConfigAccessor} that holds the patch file
     * @throws InvalidConfigurationException if patch modifications couldn't be saved
     */
    public ConfigurationFileImpl(final ConfigAccessor accessor, final ConfigAccessor patchAccessor) throws InvalidConfigurationException {
        super(accessor.getConfig());
        this.accessor = accessor;
        if (patchAccessor != null && patchConfig(patchAccessor.getConfig())) {
            try {
                accessor.save();
            } catch (final IOException e) {
                throw new InvalidConfigurationException("The configuration file was patched but could not be saved! Reason: " + e.getCause().getMessage(), e);
            }
        }
    }

    /**
     * Logs that a patch file couldn't be found.
     *
     * @param e original exception
     */
    @SuppressWarnings("PMD.ShortVariable")
    public static void logMissingResourceFile(final InvalidConfigurationException e) {
        LOG.debug(e.getMessage(), e);
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private boolean patchConfig(final ConfigurationSection patchAccessorConfig) {
        return false;
    }

    @Override
    public void save() throws IOException {
        accessor.save();
    }
}
