package org.betonquest.betonquest.quest.condition.npc;

import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link NpcDistanceCondition}s from {@link Instruction}s.
 */
public class NpcDistanceConditionFactory implements PlayerConditionFactory {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new factory for NPC Distance Conditions.
     *
     * @param featureApi    the Feature API
     * @param data          the data used for checking the condition on the main thread
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public NpcDistanceConditionFactory(final FeatureApi featureApi, final PrimaryServerThreadData data,
                                       final BetonQuestLoggerFactory loggerFactory) {
        this.featureApi = featureApi;
        this.data = data;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<NpcID> npcId = instruction.get(NpcID::new);
        final Variable<Number> distance = instruction.get(Argument.NUMBER);
        return new PrimaryServerThreadPlayerCondition(new OnlineConditionAdapter(
                new NpcDistanceCondition(featureApi, npcId, distance),
                loggerFactory.create(NpcDistanceCondition.class),
                instruction.getPackage()
        ), data);
    }
}
