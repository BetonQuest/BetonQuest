package org.betonquest.betonquest.config.quest;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.KeyConflictException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a basic implementation for managing a quest's files.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class Quest {
    /**
     * The merged {@link MultiConfiguration} that represents this {@link Quest}.
     */
    protected final MultiConfiguration config;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The factory that will be used to create {@link ConfigAccessor}s.
     */
    private final ConfigAccessorFactory configAccessorFactory;

    /**
     * The address of this {@link Quest}.
     */
    private final String questPath;

    /**
     * The root folder of this {@link Quest}.
     */
    private final File root;

    /**
     * The list of all {@link ConfigAccessor}s of this {@link Quest}.
     */
    private final List<ConfigAccessor> configs;

    /**
     * Creates a new {@link Quest}. The {@code questPath} represents the address of this {@link Quest}.
     * <p>
     * All {@code files} are merged into one {@link MultiConfiguration} config.
     *
     * @param log                   the logger that will be used for logging
     * @param configAccessorFactory the factory that will be used to create {@link ConfigAccessor}s
     * @param questPath             the path that addresses this {@link Quest}
     * @param root                  the root file of this {@link Quest}
     * @param files                 all files contained in this {@link Quest}
     * @throws InvalidConfigurationException thrown if a {@link ConfigAccessor} could not be created
     *                                       or an exception occurred while creating the {@link MultiConfiguration}
     * @throws FileNotFoundException         thrown if a file could not be found during the creation
     *                                       of a {@link ConfigAccessor}
     */
    public Quest(final BetonQuestLogger log, final ConfigAccessorFactory configAccessorFactory, final String questPath, final File root, final List<File> files) throws InvalidConfigurationException, FileNotFoundException {
        this.log = log;
        this.configAccessorFactory = configAccessorFactory;
        this.questPath = questPath;
        this.root = root;
        this.configs = new ArrayList<>();

        final Map<ConfigurationSection, String> configurations = new HashMap<>();
        for (final File file : files) {
            final ConfigAccessor configAccessor = configAccessorFactory.create(file);
            configs.add(configAccessor);
            configurations.put(configAccessor.getConfig(), getRelativePath(root, file));
        }
        try {
            config = new MultiSectionConfiguration(new ArrayList<>(configurations.keySet()));
        } catch (final KeyConflictException e) {
            throw new InvalidConfigurationException(e.resolvedMessage(configurations), e);
        }
    }

    private static String getRelativePath(final File questFile, final File otherFile) {
        return questFile.toURI().relativize(otherFile.toURI()).getPath();
    }

    /**
     * Gets the path that addresses this {@link QuestPackage}.
     *
     * @return the address
     */
    public String getQuestPath() {
        return questPath;
    }

    /**
     * Tries to save all modifications in the {@link MultiSectionConfiguration} to files.
     *
     * @return true, and only true if there are no unsaved changes
     * @throws IOException thrown if an exception was thrown by calling {@link ConfigAccessor#save()}
     *                     or {@link MultiSectionConfiguration#getUnsavedConfigs()} returned a {@link ConfigurationSection},
     *                     that is not represented by this {@link QuestPackage}
     */
    public boolean saveAll() throws IOException {
        boolean exceptionOccurred = false;
        unsaved:
        for (final ConfigurationSection unsavedConfig : config.getUnsavedConfigs()) {
            for (final ConfigAccessor configAccessor : configs) {
                if (unsavedConfig.equals(configAccessor.getConfig())) {
                    try {
                        configAccessor.save();
                    } catch (final IOException e) {
                        log.warn("Could not save file '" + configAccessor.getConfigurationFile().getPath() + "'! Reason: " + e.getMessage(), e);
                        exceptionOccurred = true;
                    }
                    continue unsaved;
                }
            }
            log.warn("No related ConfigAccessor found for ConfigurationSection '" + unsavedConfig.getName() + "'!");
            exceptionOccurred = true;
        }
        if (exceptionOccurred) {
            throw new IOException("It was not possible to save everything to files in the Quest '" + questPath + "'!");
        }
        return config.needSave();
    }

    /**
     * Gets the existing {@link ConfigAccessor} for the {@code relativePath}.
     * If the {@link ConfigAccessor} for the {@code relativePath} does not exist, a new one is created.
     *
     * @param relativePath the relative path from the root of the package
     * @return the already existing or newly created {@link ConfigAccessor}
     * @throws InvalidConfigurationException thrown if there was an exception creating the new {@link ConfigAccessor}
     * @throws FileNotFoundException         thrown if the file for the new {@link ConfigAccessor} could not be found
     */
    public ConfigAccessor getOrCreateConfigAccessor(final String relativePath) throws InvalidConfigurationException, FileNotFoundException {
        for (final ConfigAccessor configAccessor : configs) {
            if (root.toURI().relativize(configAccessor.getConfigurationFile().toURI()).getPath().equals(relativePath)) {
                return configAccessor;
            }
        }
        return createConfigAccessor(relativePath, root);
    }

    private ConfigAccessor createConfigAccessor(final String relativePath, final File root) throws InvalidConfigurationException, FileNotFoundException {
        final File newConfig = new File(root, relativePath);
        final File newConfigParent = newConfig.getParentFile();
        if (!newConfigParent.exists() && !newConfigParent.mkdirs()) {
            throw new InvalidConfigurationException("It was not possible to create the folders for the file '" + newConfig.getPath() + "'!");
        }
        try {
            if (!newConfig.createNewFile()) {
                throw new InvalidConfigurationException("It was not possible to create the file '" + newConfig.getPath() + "'!");
            }
        } catch (final IOException e) {
            throw new InvalidConfigurationException(e.getMessage(), e);
        }
        final ConfigAccessor newAccessor = configAccessorFactory.create(newConfig);
        configs.add(newAccessor);
        return newAccessor;
    }

    @Override
    public String toString() {
        return getQuestPath();
    }
}
