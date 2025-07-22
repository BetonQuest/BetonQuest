package org.betonquest.betonquest.api.config;

import org.betonquest.betonquest.api.config.patcher.PatchTransformerRegistry;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Factory for {@link ConfigAccessor} instances.
 */
public interface ConfigAccessorFactory {

    /**
     * Loads a resourceFile.
     *
     * @param plugin       the plugin which is the source of the resource file
     * @param resourceFile the resource file to load from the plugin
     * @return the created {@link ConfigAccessor}
     * @throws InvalidConfigurationException thrown if the resourceFile could not be loaded
     * @throws FileNotFoundException         thrown if the {@code resourceFile} could not be found
     */
    ConfigAccessor create(Plugin plugin, String resourceFile) throws InvalidConfigurationException, FileNotFoundException;

    /**
     * Loads a configurationFile.
     *
     * @param configurationFile the {@link File} that is represented by this {@link FileConfigAccessor}
     * @return the created {@link FileConfigAccessor}
     * @throws InvalidConfigurationException thrown if the configurationFile could not be loaded
     * @throws FileNotFoundException         thrown if the {@code configurationFile} could not be found
     */
    FileConfigAccessor create(File configurationFile) throws InvalidConfigurationException, FileNotFoundException;

    /**
     * Loads a resourceFile and save a configurationFile.
     * If the configurationFile does not exist, the resourceFile will be loaded
     * and then saved as the configurationFile if given.
     *
     * @param configurationFile the {@link File} that is represented by this {@link FileConfigAccessor}
     * @param plugin            the plugin which is the source of the resource file
     * @param resourceFile      the resource file to load from the plugin
     * @return the created {@link FileConfigAccessor}
     * @throws InvalidConfigurationException thrown if the configurationFile or the resourceFile could not be loaded,
     *                                       or the resourceFile could not be saved to the configurationFile
     * @throws FileNotFoundException         thrown if the {@code configurationFile} or the {@code resourceFile}
     *                                       could not be found
     */
    FileConfigAccessor create(File configurationFile, Plugin plugin, String resourceFile) throws InvalidConfigurationException, FileNotFoundException;

    /**
     * Loads a resourceFile and save a configurationFile by using {@link #create(File, Plugin, String)}.
     * <br>
     * Additionally, attempts to patch the {@code configurationFile} with a patch file.
     * This patch file must exist in the same directory as the {@code resourceFile}.
     * Its name is the one of the {@code resourceFile} but with
     * '.patch' inserted between the file name and the file extension.
     * <br>
     * E.g:
     * {@code  config.yml & config.patch.yml}
     * <br><br>
     * This method uses the default patches, to override them
     * use {@link #createPatching(File, Plugin, String, PatchTransformerRegistry)} instead.
     * <br><br>
     *
     * @param configurationFile the {@link File} that is represented by this {@link FileConfigAccessor}
     * @param plugin            the plugin which is the source of the resource file
     * @param resourceFile      the resource file to load from the plugin
     * @return the created {@link FileConfigAccessor}
     * @throws InvalidConfigurationException thrown if the configurationFile or the resourceFile could not be loaded,
     *                                       or the resourceFile could not be saved to the configurationFile
     * @throws FileNotFoundException         thrown if the {@code configurationFile} or the {@code resourceFile}
     *                                       could not be found
     */
    default FileConfigAccessor createPatching(final File configurationFile, final Plugin plugin, final String resourceFile) throws InvalidConfigurationException, FileNotFoundException {
        return createPatching(configurationFile, plugin, resourceFile, null);
    }

    /**
     * Loads a resourceFile and save a configurationFile by using {@link #create(File, Plugin, String)}.
     * <br>
     * Additionally, attempts to patch the {@code configurationFile} with a patch file.
     * This patch file must exist in the same directory as the {@code resourceFile}.
     * Its name is the one of the {@code resourceFile} but with
     * '.patch' inserted between the file name and the file extension.
     * <br>
     * E.g:
     * {@code  config.yml & config.patch.yml}
     * <br><br>
     * This method uses the passed {@link PatchTransformerRegistry},
     * if you want to use the default patches use {@link #createPatching(File, Plugin, String)} instead.
     * <br><br>
     *
     * @param configurationFile          the {@link File} that is represented by this {@link FileConfigAccessor}
     * @param plugin                     the plugin which is the source of the resource file
     * @param resourceFile               the resource file to load from the plugin
     * @param patchTransformerRegisterer a function that registers the transformers to be used for patching
     * @return the created {@link FileConfigAccessor}
     * @throws InvalidConfigurationException thrown if the configurationFile or the resourceFile could not be loaded,
     *                                       or the resourceFile could not be saved to the configurationFile
     * @throws FileNotFoundException         thrown if the {@code configurationFile} or the {@code resourceFile}
     *                                       could not be found
     */
    FileConfigAccessor createPatching(File configurationFile, Plugin plugin, String resourceFile, @Nullable PatchTransformerRegistry patchTransformerRegisterer) throws InvalidConfigurationException, FileNotFoundException;
}
