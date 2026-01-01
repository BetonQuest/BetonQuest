package org.betonquest.betonquest.quest.placeholder.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.config.PluginMessage;

/**
 * Factory to create {@link ConditionPlaceholder}s from {@link Instruction}s.
 */
public class ConditionPlaceholderFactory implements PlayerPlaceholderFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Create the Condition Placeholder Factory.
     *
     * @param questTypeApi  the Quest Type API
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public ConditionPlaceholderFactory(final QuestTypeApi questTypeApi, final PluginMessage pluginMessage) {
        this.questTypeApi = questTypeApi;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ConditionID> conditionId = instruction.parse(ConditionID::new).get();
        final FlagArgument<Boolean> papiMode = instruction.bool().getFlag("papiMode", true);
        return new ConditionPlaceholder(pluginMessage, conditionId, papiMode, questTypeApi);
    }
}
