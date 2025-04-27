package org.betonquest.betonquest.quest.variable.condition;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.variable.Variable;

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
    private final Variable<ConditionID> conditionId;

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
    public ConditionVariable(final PluginMessage pluginMessage, final Variable<ConditionID> conditionId, final boolean papiMode, final QuestTypeAPI questTypeAPI) {
        this.pluginMessage = pluginMessage;
        this.conditionId = conditionId;
        this.papiMode = papiMode;
        this.questTypeAPI = questTypeAPI;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        if (questTypeAPI.condition(profile, conditionId.getValue(profile))) {
            return papiMode ? LegacyComponentSerializer.legacySection().serialize(pluginMessage.getMessage("condition_variable_met").asComponent(profile)) : "true";
        }
        return papiMode ? LegacyComponentSerializer.legacySection().serialize(pluginMessage.getMessage("condition_variable_not_met").asComponent(profile)) : "false";
    }
}
