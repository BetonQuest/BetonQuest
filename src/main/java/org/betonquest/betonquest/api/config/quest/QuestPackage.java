package org.betonquest.betonquest.api.config.quest;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Functionality for a quest to get all related information.
 */
@SuppressWarnings("PMD.CommentRequired")
public interface QuestPackage {
    /**
     * Gets the path that addresses this {@link QuestPackage}.
     *
     * @return the address
     */
    String getQuestPath();

    /**
     * Gets the merged {@link MultiConfiguration} that represents this {@link QuestPackage}.
     *
     * @return a config extending {@link MultiConfiguration}
     */
    MultiConfiguration getConfig();

    /**
     * Gets a list of all templates that are applied to this {@link QuestPackage} and all inherited templates.
     *
     * @return a list of all templates.
     */
    List<String> getTemplates();

    /**
     * Checks if this {@link QuestPackage} has a given template.
     * This will also check for any templates that are defined in the templates of the templates.
     *
     * @param templatePath The path of the template to check for
     * @return true if the template is defined in this {@link QuestPackage} or any of its templates
     */
    boolean hasTemplate(String templatePath);

    /**
     * Tries to save all modifications in the {@link MultiConfiguration} to their files.
     *
     * @return true, and only true if there are no unsaved changes
     * @throws IOException thrown if an exception was thrown by calling
     *                     {@link org.betonquest.betonquest.api.config.FileConfigAccessor#save()}
     *                     or {@link MultiConfiguration#getUnsavedConfigs()} returned a {@link ConfigurationSection},
     *                     that is not represented by this {@link QuestPackage}
     */
    boolean saveAll() throws IOException;

    /**
     * Gets the existing {@link ConfigAccessor} for the {@code relativePath}.
     * If the {@link ConfigAccessor} for the {@code relativePath} does not exist, a new one is created.
     *
     * @param relativePath the relative path from the root of the {@link QuestPackage}
     * @return the already existing or newly created {@link ConfigAccessor}
     * @throws InvalidConfigurationException thrown if there was an exception creating the new {@link ConfigAccessor}
     * @throws FileNotFoundException         thrown if the file for the new {@link ConfigAccessor} could not be found
     */
    ConfigAccessor getOrCreateConfigAccessor(String relativePath) throws InvalidConfigurationException, FileNotFoundException;

    @Nullable
    @Deprecated
    String getRawString(String address);

    /**
     * Resolves all global variables in the given string.
     *
     * @param input the string to resolve
     * @return the resolved string
     * @deprecated instead use
     * {@link org.betonquest.betonquest.variables.GlobalVariableResolver#resolve(QuestPackage, String)}.
     */
    @Deprecated
    String subst(String input);

    @Nullable
    @Deprecated
    String getString(String address);

    @Contract("_, !null -> !null")
    @Nullable
    @Deprecated
    String getString(String address, @Nullable String def);

    @Nullable
    @Deprecated
    String getFormattedString(String address);
}
