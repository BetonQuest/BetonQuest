package org.betonquest.betonquest.compatibility.citizens.condition.distance;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link NPCDistanceCondition}s from {@link Instruction}s.
 */
public class NPCDistanceConditionFactory implements PlayerConditionFactory {
    /**
     * Data used for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new factory for NPC Distance Conditions.
     *
     * @param data          the data for primary server thread access
     * @param loggerFactory the logger factory to create class specific logger
     */
    public NPCDistanceConditionFactory(final PrimaryServerThreadData data, final BetonQuestLoggerFactory loggerFactory) {
        this.data = data;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final int npcId = instruction.getInt();
        if (npcId < 0) {
            throw new QuestException("NPC ID cannot be less than 0");
        }
        final VariableNumber distance = instruction.getVarNum();
        return new PrimaryServerThreadPlayerCondition(new OnlineConditionAdapter(
                new NPCDistanceCondition(npcId, distance),
                loggerFactory.create(NPCDistanceCondition.class),
                instruction.getPackage()
        ), data);
    }
}
