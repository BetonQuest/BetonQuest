package org.betonquest.betonquest.quest.condition.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.npc.NpcID;

/**
 * Factory to create {@link NpcDistanceCondition}s from {@link Instruction}s.
 */
public class NpcDistanceConditionFactory implements PlayerConditionFactory {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new factory for NPC Distance Conditions.
     *
     * @param featureApi    the Feature API
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public NpcDistanceConditionFactory(final FeatureApi featureApi, final BetonQuestLoggerFactory loggerFactory) {
        this.featureApi = featureApi;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<NpcID> npcId = instruction.get(NpcID::new);
        final Variable<Number> distance = instruction.get(instruction.getParsers().number());
        return new OnlineConditionAdapter(new NpcDistanceCondition(featureApi, npcId, distance),
                loggerFactory.create(NpcDistanceCondition.class), instruction.getPackage());
    }
}
