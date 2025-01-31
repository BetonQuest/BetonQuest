package org.betonquest.betonquest.quest.variable.condition;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory to create {@link ConditionVariable}s from {@link Instruction}s.
 */
public class ConditionVariableFactory implements PlayerVariableFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Create the Condition Variable Factory.
     *
     * @param questTypeAPI  the Quest Type API
     * @param dataStorage   the storage providing player data
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public ConditionVariableFactory(final QuestTypeAPI questTypeAPI, final PlayerDataStorage dataStorage, final PluginMessage pluginMessage) {
        this.questTypeAPI = questTypeAPI;
        this.dataStorage = dataStorage;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        final ConditionID conditionId = instruction.getID(ConditionID::new);
        final boolean papiMode = instruction.hasArgument("papiMode");
        return new ConditionVariable(pluginMessage, conditionId, papiMode, questTypeAPI, dataStorage);
    }
}
