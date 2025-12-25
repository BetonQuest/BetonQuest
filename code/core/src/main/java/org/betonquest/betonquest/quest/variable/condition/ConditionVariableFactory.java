package org.betonquest.betonquest.quest.variable.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.config.PluginMessage;

/**
 * Factory to create {@link ConditionVariable}s from {@link Instruction}s.
 */
public class ConditionVariableFactory implements PlayerVariableFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Create the Condition Variable Factory.
     *
     * @param questTypeApi  the Quest Type API
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public ConditionVariableFactory(final QuestTypeApi questTypeApi, final PluginMessage pluginMessage) {
        this.questTypeApi = questTypeApi;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ConditionID> conditionId = instruction.parse(ConditionID::new).get();
        final boolean papiMode = instruction.hasArgument("papiMode");
        return new ConditionVariable(pluginMessage, conditionId, papiMode, questTypeApi);
    }
}
