package org.betonquest.betonquest.quest.variable.condition;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.id.ConditionID;

/**
 * Get the "fulfillment" status of a quest condition.
 */
public class ConditionVariable implements PlayerVariable {
    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Condition to check.
     */
    private final ConditionID conditionId;

    /**
     * If variable should be in PAPI style.
     */
    private final boolean papiMode;

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Create a new Condition variable.
     *
     * @param pluginMessage the {@link PluginMessage} instance
     * @param conditionId   the condition to get the "fulfillment" status
     * @param papiMode      if the return value should be in PAPI mode as defined in the documentation
     * @param questTypeAPI  the Quest Type API
     */
    public ConditionVariable(final PluginMessage pluginMessage, final ConditionID conditionId, final boolean papiMode, final QuestTypeAPI questTypeAPI) {
        this.pluginMessage = pluginMessage;
        this.conditionId = conditionId;
        this.papiMode = papiMode;
        this.questTypeAPI = questTypeAPI;
    }

    @Override
    public String getValue(final Profile profile) {
        if (questTypeAPI.condition(profile, conditionId)) {
            return papiMode ? pluginMessage.getMessage(profile, "condition_variable_met") : "true";
        }
        return papiMode ? pluginMessage.getMessage(profile, "condition_variable_not_met") : "false";
    }
}
