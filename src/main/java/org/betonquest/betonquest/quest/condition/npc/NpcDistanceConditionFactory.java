package org.betonquest.betonquest.quest.condition.npc;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.registry.processor.NpcProcessor;

/**
 * Factory to create {@link NpcDistanceCondition}s from {@link Instruction}s.
 */
public class NpcDistanceConditionFactory implements PlayerConditionFactory {
    /**
     * Processor to get npc.
     */
    private final NpcProcessor npcProcessor;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new factory for NPC Distance Conditions.
     *
     * @param npcProcessor  the processor to get npc
     * @param data          the data used for checking the condition on the main thread
     * @param loggerFactory logger factory to use
     */
    public NpcDistanceConditionFactory(final NpcProcessor npcProcessor, final PrimaryServerThreadData data,
                                       final BetonQuestLoggerFactory loggerFactory) {
        this.npcProcessor = npcProcessor;
        this.data = data;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final NpcID npcId = instruction.getID(NpcID::new);
        final VariableNumber distance = instruction.get(VariableNumber::new);
        return new PrimaryServerThreadPlayerCondition(new OnlineConditionAdapter(
                new NpcDistanceCondition(npcProcessor, npcId, distance),
                loggerFactory.create(NpcDistanceCondition.class),
                instruction.getPackage()
        ), data);
    }
}
