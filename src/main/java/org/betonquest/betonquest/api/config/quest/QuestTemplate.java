package org.betonquest.betonquest.api.config.quest;

import org.bukkit.configuration.InvalidConfigurationException;

import java.util.List;
import java.util.Map;

/**
 * Functionality to check and apply templates to a {@link org.betonquest.betonquest.api.config.quest.QuestTemplate}.
 */
public interface QuestTemplate extends Quest {
    /**
     * Gets a list of all templates that are applied to this {@link QuestTemplate} and all inherited templates.
     *
     * @return a list of all templates that are applied to this {@link QuestTemplate} and all inherited templates.
     */
    List<String> getTemplates();

    /**
     * Checks if this {@link QuestTemplate} has a given template.
     * This will also check for any templates that are defined in the templates of the templates.
     *
     * @param templatePath The path of the template to check for
     * @return true if the template is defined in this {@link QuestTemplate} or any of its templates
     */
    boolean hasTemplate(String templatePath);

    /**
     * Applies given {@link QuestTemplate}s to this {@link QuestTemplate}.
     * The given QuestTemplates should contain all templates that are available.
     * The method will pick the templates from the given list.
     * If a template is not available, a {@link InvalidConfigurationException} will be thrown.
     *
     * @param questTemplates The list of all available {@link QuestTemplate}s
     * @throws InvalidConfigurationException thrown if a template is not available
     */
    void applyQuestTemplates(Map<String, QuestTemplate> questTemplates) throws InvalidConfigurationException;
}
