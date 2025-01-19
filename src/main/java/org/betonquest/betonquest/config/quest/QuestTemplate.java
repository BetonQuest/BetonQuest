package org.betonquest.betonquest.config.quest;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback.MultiFallbackConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is an implementation of {@link Quest}, that gets and applies templates.
 */
public class QuestTemplate extends Quest {
    /**
     * A list of all templates that are applied to this {@link QuestTemplate}.
     */
    private final Set<String> templates;

    /**
     * The final {@link MultiConfiguration} that represents this {@link QuestTemplate}.
     */
    @Nullable
    private MultiConfiguration templateConfig;

    /**
     * Creates a new {@link QuestTemplate}. For more information see {@link Quest}.
     *
     * @param log                   the logger that will be used for logging
     * @param configAccessorFactory the factory that will be used to create {@link ConfigAccessor}s
     * @param questPath             the path that addresses this {@link QuestTemplate}
     * @param root                  the root file of this {@link QuestTemplate}
     * @param files                 all files contained by this {@link QuestTemplate}
     * @throws InvalidConfigurationException thrown if a {@link org.betonquest.betonquest.api.config.ConfigAccessor}
     *                                       could not be created or an exception occurred while creating the
     *                                       {@link MultiConfiguration}
     * @throws FileNotFoundException         thrown if a file could not be found during the creation
     *                                       of a {@link org.betonquest.betonquest.api.config.ConfigAccessor}
     */
    public QuestTemplate(final BetonQuestLogger log, final ConfigAccessorFactory configAccessorFactory, final String questPath, final File root, final List<File> files) throws InvalidConfigurationException, FileNotFoundException {
        super(log, configAccessorFactory, questPath, root, files);
        templates = new HashSet<>();
    }

    /**
     * Gets the merged {@link MultiSectionConfiguration} that represents this {@link QuestPackage}.
     *
     * @return a config extending {@link MultiConfiguration}
     */
    public MultiConfiguration getConfig() {
        if (templateConfig == null) {
            throw new IllegalStateException("The template config is not initialized yet");
        }
        return templateConfig;
    }

    /**
     * Gets a list of all templates that are applied to this {@link QuestTemplate} and all inherited templates.
     *
     * @return a list of all templates that are applied to this {@link QuestTemplate} and all inherited templates.
     */
    public List<String> getTemplates() {
        return new ArrayList<>(templates);
    }

    /**
     * Applies the given {@link QuestTemplate}s to this {@link QuestTemplate}.
     * The given QuestTemplates should contain all templates that are available.
     * The method will pick the templates from the given list.
     * If a template is not available, an {@link InvalidConfigurationException} will be thrown.
     *
     * @param questTemplates The list of all available {@link QuestTemplate}s
     * @throws InvalidConfigurationException thrown if a template is not available
     */
    public void applyQuestTemplates(final Map<String, QuestTemplate> questTemplates) throws InvalidConfigurationException {
        if (templateConfig != null) {
            return;
        }

        final List<String> templatePaths = config.getStringList("package.templates");
        if (templatePaths.isEmpty()) {
            templateConfig = config;
            return;
        }

        final List<QuestTemplate> templateConfigs = getOrderedQuestTemplates(questTemplates, templatePaths);
        templates.addAll(templatePaths);
        templates.addAll(templateConfigs.stream().map(QuestTemplate::getTemplates).flatMap(List::stream).toList());

        final MultiConfiguration mergedTemplates = templateConfigs.stream()
                .map(QuestTemplate::getConfig)
                .reduce(null, (previous, current) -> {
                    if (previous == null) {
                        return current;
                    }
                    return new MultiFallbackConfiguration(current, previous);
                });
        templateConfig = new MultiFallbackConfiguration(config, mergedTemplates);
    }

    private List<QuestTemplate> getOrderedQuestTemplates(final Map<String, QuestTemplate> questTemplates, final List<String> templatePaths) throws InvalidConfigurationException {
        Collections.reverse(templatePaths);
        final List<QuestTemplate> templateConfigs = new ArrayList<>();
        for (final String template : templatePaths) {
            final QuestTemplate questTemplate = questTemplates.get(template);
            if (questTemplate == null) {
                throw new InvalidConfigurationException("The template '" + template + "' does not exist");
            }
            try {
                questTemplate.applyQuestTemplates(questTemplates);
            } catch (final InvalidConfigurationException e) {
                throw new InvalidConfigurationException("The template '" + template + "' could not be loaded! Reason: " + e.getMessage(), e);
            }
            templateConfigs.add(questTemplate);
        }
        return templateConfigs;
    }
}
