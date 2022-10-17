package org.betonquest.betonquest.modules.config.quest;

import lombok.CustomLog;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.KeyConflictException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This is a basic implementation of {@link Quest}, that manage files of a quest.
 */
@CustomLog(topic = "Quest")
public abstract class QuestImpl implements Quest {
    /**
     * The merged {@link MultiConfiguration} that represents this {@link Quest}
     */
    protected final MultiConfiguration config;
    /**
     * The address of this {@link Quest}
     */
    private final String questPath;
    /**
     * The root quest {@link ConfigAccessor} that represents this {@link Quest}
     */
    private final ConfigAccessor questConfig;
    /**
     * The list of all {@link ConfigAccessor}s of this {@link Quest}
     */
    private final List<ConfigAccessor> configs;

    /**
     * Creates a new {@link Quest}. The {@code questPath} represents the address of this {@link Quest}.
     * The {@code questFile} is the root file of the {@link Quest},
     * while the {@code files} are all other files except the {@code questFile} file.
     * <p>
     * All files are merged into one {@link MultiConfiguration} config.
     *
     * @param questPath the path that address this {@link Quest}
     * @param questFile the file that represent the root of this {@link Quest}
     * @param files     all files contained by this {@link Quest} except the {@code questFile}
     * @throws InvalidConfigurationException thrown if a {@link ConfigAccessor} could not be created
     *                                       or an exception occurred while creating the {@link MultiConfiguration}
     * @throws FileNotFoundException         thrown if a file could not be found during the creation
     *                                       of a {@link ConfigAccessor}
     */
    public QuestImpl(final String questPath, final File questFile, final List<File> files) throws InvalidConfigurationException, FileNotFoundException {
        this.questPath = questPath;
        this.questConfig = ConfigAccessor.create(questFile);
        this.configs = new ArrayList<>();

        final HashMap<ConfigurationSection, String> configurations = new HashMap<>();
        configurations.put(this.questConfig.getConfig(), getRelativePath(questFile, questFile));
        for (final File file : files) {
            final ConfigAccessor configAccessor = ConfigAccessor.create(file);
            configs.add(configAccessor);
            configurations.put(configAccessor.getConfig(), getRelativePath(questFile, file));
        }
        try {
            config = new MultiSectionConfiguration(new ArrayList<>(configurations.keySet()));
        } catch (final KeyConflictException e) {
            throw new InvalidConfigurationException(e.resolvedMessage(configurations), e);
        }
    }

    private static String getRelativePath(final File questFile, final File otherFile) {
        return questFile.getParentFile().toURI().relativize(otherFile.toURI()).getPath();
    }

    @Override
    public String getQuestPath() {
        return questPath;
    }

    @Override
    public boolean isDefinedInQuestConfigOrThrow(final String path) throws InvalidConfigurationException {
        final ConfigurationSection configuration = config.getSourceConfigurationSection(path);
        if (configuration == null) {
            return false;
        }
        if (configuration.equals(questConfig.getConfig())) {
            return true;
        }
        throw new InvalidConfigurationException("The section '" + path + "' need to be defined in the '" + questConfig.getConfigurationFile().getName() + "' file");
    }

    @Override
    public boolean saveAll() throws IOException {
        boolean exceptionOccurred = false;
        unsaved:
        for (final ConfigurationSection unsavedConfig : config.getUnsavedConfigs()) {
            for (final ConfigAccessor configAccessor : configs) {
                if (unsavedConfig.equals(configAccessor.getConfig())) {
                    try {
                        configAccessor.save();
                    } catch (final IOException e) {
                        LOG.warn("Could not save file '" + configAccessor.getConfigurationFile().getPath() + "'! Reason: " + e.getMessage(), e);
                        exceptionOccurred = true;
                    }
                    continue unsaved;
                }
            }
            LOG.warn("No related ConfigAccessor found for ConfigurationSection '" + unsavedConfig.getName() + "'!");
            exceptionOccurred = true;
        }
        if (exceptionOccurred) {
            throw new IOException("It was not possible to save everything to files in the Quest '" + questPath + "'!");
        }
        return config.needSave();
    }

    @Override
    public ConfigAccessor getOrCreateConfigAccessor(final String relativePath) throws InvalidConfigurationException, FileNotFoundException {
        final File root = questConfig.getConfigurationFile().getParentFile();
        if (root.toURI().relativize(questConfig.getConfigurationFile().toURI()).getPath().equals(relativePath)) {
            return questConfig;
        }
        for (final ConfigAccessor configAccessor : configs) {
            if (root.toURI().relativize(configAccessor.getConfigurationFile().toURI()).getPath().equals(relativePath)) {
                return configAccessor;
            }
        }
        return createConfigAccessor(relativePath, root);
    }

    @NotNull
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
        final ConfigAccessor newAccessor = ConfigAccessor.create(newConfig);
        configs.add(newAccessor);
        return newAccessor;
    }

    @Override
    public String toString() {
        return getQuestPath();
    }
}
