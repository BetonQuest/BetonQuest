package org.betonquest.betonquest.modules.config.quest;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback.MultiFallbackConfiguration;
import org.betonquest.betonquest.api.config.quest.Quest;
import org.betonquest.betonquest.api.config.quest.QuestTemplate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is an implementation of {@link QuestTemplate}, that gets and applies templates.
 */
public class QuestTemplateImpl extends QuestImpl implements QuestTemplate {
    /**
     * A list of all templates that are applied to this {@link QuestTemplate}
     */
    private final Set<String> templates;
    /**
     * The final {@link MultiConfiguration} that represents this {@link QuestTemplate}
     */
    private MultiConfiguration templateConfig;

    /**
     * Creates a new {@link QuestTemplate}. For more information see {@link Quest}.
     *
     * @param questPath the path that address this {@link QuestTemplate}
     * @param questFile the file that represents the root of this {@link QuestTemplate}
     * @param files     all files contained by this {@link QuestTemplate} except the {@code questFile}
     * @throws InvalidConfigurationException thrown if a {@link org.betonquest.betonquest.api.config.ConfigAccessor}
     *                                       could not be created or an exception occurred while creating the
     *                                       {@link MultiConfiguration}
     * @throws FileNotFoundException         thrown if a file could not be found during the creation
     *                                       of a {@link org.betonquest.betonquest.api.config.ConfigAccessor}
     */
    public QuestTemplateImpl(final String questPath, final File questFile, final List<File> files) throws InvalidConfigurationException, FileNotFoundException {
        super(questPath, questFile, files);
        isDefinedInQuestConfigOrThrow("templates");
        templates = new HashSet<>();
    }

    @Override
    public MultiConfiguration getConfig() {
        if (templateConfig == null) {
            throw new IllegalStateException("The template config is not initialized yet");
        }
        return templateConfig;
    }

    @Override
    public List<String> getTemplates() {
        return new ArrayList<>(templates);
    }

    @Override
    public boolean hasTemplate(final String templatePath) {
        return templates.contains(templatePath);
    }

    @Override
    public void applyQuestTemplates(final Map<String, QuestTemplate> questTemplates) throws InvalidConfigurationException {
        if (templateConfig != null) {
            return;
        }

        final List<String> templatePaths = config.getStringList("templates");
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

    @NotNull
    private List<QuestTemplate> getOrderedQuestTemplates(final Map<String, QuestTemplate> questTemplates, final List<String> templatePaths) throws InvalidConfigurationException {
        Collections.reverse(templatePaths);
        final List<QuestTemplate> templateConfigs = new ArrayList<>();
        for (final String template : templatePaths) {
            final QuestTemplate questTemplate = questTemplates.get(template);
            if (questTemplate == null) {
                throw new InvalidConfigurationException("The template '" + template + "' does not exist");
            }
            questTemplate.applyQuestTemplates(questTemplates);
            templateConfigs.add(questTemplate);
        }
        return templateConfigs;
    }
}
