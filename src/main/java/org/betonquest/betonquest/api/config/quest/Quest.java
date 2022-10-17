package org.betonquest.betonquest.api.config.quest;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Basic functionality of a quest made from multiple files.
 */
public interface Quest {
    /**
     * Gets the path that addresses this {@link QuestPackage}.
     *
     * @return the address
     */
    String getQuestPath();

    /**
     * Gets the merged {@link MultiSectionConfiguration} that represents this {@link QuestPackage}.
     *
     * @return a config extending {@link MultiConfiguration}
     */
    MultiConfiguration getConfig();

    /**
     * Checks if the given {@code path} is defined in the {@link Quest}'s config or not.
     * If it is defined in another config than the Quest's config, a {@link InvalidConfigurationException} is thrown.
     * This can be used to validate that a given {@code path} is only defined in the {@link Quest}'s config.
     *
     * @param path to check
     * @return true if the path is defined in the quests config
     * @throws InvalidConfigurationException if the path is defined in another config than the Quest's config
     */
    boolean isDefinedInQuestConfigOrThrow(String path) throws InvalidConfigurationException;

    /**
     * Tries to save all modifications in the {@link MultiSectionConfiguration} to files.
     *
     * @return true, and only true if there are no unsaved changes
     * @throws IOException thrown if an exception was thrown by calling {@link ConfigAccessor#save()}
     *                     or {@link MultiSectionConfiguration#getUnsavedConfigs()} returned a {@link ConfigurationSection},
     *                     that is not represented by this {@link QuestPackage}
     */
    boolean saveAll() throws IOException;

    /**
     * Gets the existing {@link ConfigAccessor} for the {@code relativePath}.
     * If the {@link ConfigAccessor} for the {@code relativePath} does not exist, a new one is created.
     *
     * @param relativePath the relative path from the root of the package
     * @return the already existing or new created {@link ConfigAccessor}
     * @throws InvalidConfigurationException thrown if there was an exception creating the new {@link ConfigAccessor}
     * @throws FileNotFoundException         thrown if the file for the new {@link ConfigAccessor} could not be found
     */
    ConfigAccessor getOrCreateConfigAccessor(String relativePath) throws InvalidConfigurationException, FileNotFoundException;
}
