package org.betonquest.betonquest.quest.placeholder.condition;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.config.PluginMessage;

/**
 * Get the "fulfillment" status of a quest condition.
 */
public class ConditionPlaceholder implements PlayerPlaceholder {
    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Condition to check.
     */
    private final Argument<ConditionID> conditionId;

    /**
     * If placeholder should be in PAPI style.
     */
    private final FlagArgument<Boolean> papiMode;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Create a new Condition placeholder.
     *
     * @param pluginMessage the {@link PluginMessage} instance
     * @param conditionId   the condition to get the "fulfillment" status
     * @param papiMode      if the return value should be in PAPI mode as defined in the documentation
     * @param questTypeApi  the Quest Type API
     */
    public ConditionPlaceholder(final PluginMessage pluginMessage, final Argument<ConditionID> conditionId,
                             final FlagArgument<Boolean> papiMode, final QuestTypeApi questTypeApi) {
        this.pluginMessage = pluginMessage;
        this.conditionId = conditionId;
        this.papiMode = papiMode;
        this.questTypeApi = questTypeApi;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        final boolean papiMode = this.papiMode.getValue(profile).orElse(false);
        if (questTypeApi.condition(profile, conditionId.getValue(profile))) {
            return papiMode ? LegacyComponentSerializer.legacySection().serialize(pluginMessage.getMessage(profile, "condition_placeholder_met")) : "true";
        }
        return papiMode ? LegacyComponentSerializer.legacySection().serialize(pluginMessage.getMessage(profile, "condition_placeholder_not_met")) : "false";
    }
}
