package org.betonquest.betonquest.modules.config;

import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.bukkit.config.custom.ConfigurationSectionDecorator;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.api.config.patcher.PatchTransformerRegisterer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

/**
 * Facade for easy loading and saving of configs.
 */
public final class ConfigurationFileImpl extends ConfigurationSectionDecorator implements ConfigurationFile {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(ConfigurationFileImpl.class, "ConfigurationFile");

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
    private ConfigurationFileImpl(final ConfigAccessor accessor, final ConfigAccessor patchAccessor, final PatchTransformerRegisterer patchTransformerRegisterer, final URI relativeRoot) throws InvalidConfigurationException {
        super(accessor.getConfig());
        this.accessor = accessor;
        if (patchAccessor != null) {
            final Patcher patcher = new Patcher(accessor.getConfig(), patchAccessor.getConfig());
            patchTransformerRegisterer.registerTransformers(patcher);
            if (patchConfig(patcher, relativeRoot)) {
                try {
                    accessor.save();
                } catch (final IOException e) {
                    throw new InvalidConfigurationException("The configuration file was patched but could not be saved! Reason: " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Uses {@link ConfigAccessor#create(File, Plugin, String)} to either load or create a {@link ConfigurationFile}.
     * <br>
     * Additionally, attempts to patch the {@code configurationFile} with a patch file.
     * This patch file must exist in the same directory as the {@code resourceFile}.
     * Its name is the one of the {@code resourceFile} but with
     * '.patch' inserted between the file name and the file extension.
     * <br>
     * E.g:
     * {@code  config.yml & config.patch.yml}
     * <br><br>
     * Available patches can be explicitly overridden by passing a {@link PatchTransformerRegisterer}.
     * Otherwise, the default patches are used.
     * <br><br>
     *
     * @param configurationFile          where to load and save the config
     * @param plugin                     to load the jar resources from
     * @param resourceFile               path to the default config in the plugin's jar
     * @param patchTransformerRegisterer a function that registers the transformers to be used for patching
     * @return a new ConfigurationFile
     * @throws InvalidConfigurationException if the configuration is invalid or could not be saved
     * @throws FileNotFoundException         if the {@code configurationFile} or {@code resourceFile} could not be found
     */
    public static ConfigurationFile create(final File configurationFile, final Plugin plugin, final String resourceFile, final PatchTransformerRegisterer patchTransformerRegisterer) throws InvalidConfigurationException, FileNotFoundException {
        if (configurationFile == null || plugin == null || resourceFile == null) {
            throw new IllegalArgumentException("The configurationFile, plugin and resourceFile must be defined but were null.");
        }

        final ConfigAccessor accessor = ConfigAccessor.create(configurationFile, plugin, resourceFile);
        final ConfigAccessor resourceAccessor = ConfigAccessor.create(plugin, resourceFile);
        accessor.getConfig().setDefaults(resourceAccessor.getConfig());
        accessor.getConfig().options().copyDefaults(true);
        try {
            accessor.save();
        } catch (final IOException e) {
            throw new InvalidConfigurationException("Default values were applied to the config but could not be saved! Reason: " + e.getMessage(), e);
        }
        final ConfigAccessor patchAccessor = createPatchAccessor(plugin, resourceFile);
        return new ConfigurationFileImpl(accessor, patchAccessor,
                patchTransformerRegisterer == null ? new PatchTransformerRegisterer() {
                } : patchTransformerRegisterer, plugin.getDataFolder().getParentFile().toURI());
    }

    private static ConfigAccessor createPatchAccessor(final Plugin plugin, final String resourceFile) throws InvalidConfigurationException {
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
            return ConfigAccessor.create(plugin, resourceFilePatch);
        } catch (final FileNotFoundException e) {
            LOG.debug(e.getMessage(), e);
        }
        return null;
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

            LOG.info("Updating config file '" + relativePath + "' from version '" + patcher.getCurrentConfigVersion() +
                    "' to version '" + patcher.getNextConfigVersion().getVersion() + "'");

            final boolean flawless = patcher.patch();
            if (flawless) {
                LOG.info("Patching complete!");
            } else {
                LOG.warn("The patching progress did not go flawlessly. However, this does not mean your configs " +
                        "are now corrupted. Please check the errors above to see what the patcher did. " +
                        "You might want to adjust your config manually depending on that information.");
            }
            return true;
        }
        LOG.debug("No patch found.");

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
