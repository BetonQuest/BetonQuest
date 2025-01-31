package org.betonquest.betonquest.quest.variable.tag;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.PluginMessage;

import java.util.List;

/**
 * An abstract class for creating Tag variables.
 *
 * @param <T> the data holder type
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractTagVariable<T> {

    /**
     * The data holder.
     */
    protected final T data;

    /**
     * The tag to check for.
     */
    protected final String tagName;

    /**
     * The quest package to check for the tag.
     */
    protected final QuestPackage questPackage;

    /**
     * Whether to return true/false or the configured messages.
     */
    protected final boolean papiMode;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Constructs a new GlobalTagVariable.
     *
     * @param pluginMessage the {@link PluginMessage} instance
     * @param data          the data holder
     * @param tagName       the tag to check for
     * @param questPackage  the quest package to check for the tag
     * @param papiMode      whether to return true/false or the configured messages
     */
    public AbstractTagVariable(final PluginMessage pluginMessage, final T data, final String tagName, final QuestPackage questPackage, final boolean papiMode) {
        this.pluginMessage = pluginMessage;
        this.data = data;
        this.tagName = tagName;
        this.questPackage = questPackage;
        this.papiMode = papiMode;
    }

    /**
     * Returns the value of the variable.
     *
     * @param tags the tags to check
     * @return the value of the variable
     */
    public String getValueFor(final List<String> tags) {
        final String lang = Config.getLanguage();

        if (tags.contains(questPackage.getQuestPath() + "." + tagName)) {
            return papiMode ? pluginMessage.getMessage(lang, "condition_variable_met") : "true";
        }
        return papiMode ? pluginMessage.getMessage(lang, "condition_variable_not_met") : "false";
    }
}
