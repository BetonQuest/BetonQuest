package org.betonquest.betonquest.config;

import org.betonquest.betonquest.config.patcher.Patcher;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Facade for easy loading and saving of configs.
 */
public final class StandardPatchingConfigAccessor extends StandardConfigAccessor {

    /**
     * Creates a new {@link StandardPatchingConfigAccessor}.
     * Patches the configuration file held by the {@code accessor} with the patch file from the {@code patchAccessor}.
     *
     * @param configurationFile the {@link File} that is represented by this {@link StandardConfigAccessor}
     * @param plugin            the plugin which is the source of the resource file
     * @param resourceFile      the resource file to load from the plugin
     * @param patcher           a {@link Patcher} instance that holds the patches to apply
     * @throws InvalidConfigurationException if patch modifications couldn't be saved
     * @throws FileNotFoundException         thrown if the {@code configurationFile} or the {@code resourceFile}
     *                                       could not be found
     */
    public StandardPatchingConfigAccessor(final File configurationFile, final Plugin plugin, final String resourceFile,
                                          @Nullable final Patcher patcher)
            throws InvalidConfigurationException, FileNotFoundException {
        super(configurationFile, plugin, resourceFile);
        if (patcher != null) {
            patcher.patch(this);
        }
    }
}
