package org.betonquest.betonquest.compatibility.worldguard.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerlessCondition;
import org.betonquest.betonquest.api.quest.npc.NpcID;

/**
 * Factory to create {@link NpcRegionCondition}s from {@link Instruction}s.
 */
public class NpcRegionConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Data used for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for NPC Region Conditions.
     *
     * @param featureApi the Feature API
     * @param data       the data for primary server thread access
     */
    public NpcRegionConditionFactory(final FeatureApi featureApi, final PrimaryServerThreadData data) {
        this.featureApi = featureApi;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerCondition(parseInstruction(instruction), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessCondition(parseInstruction(instruction), data);
    }

    private NullableConditionAdapter parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<NpcID> npcId = instruction.get(NpcID::new);
        final Variable<String> region = instruction.get(DefaultArgumentParsers.STRING);
        return new NullableConditionAdapter(new NpcRegionCondition(featureApi, npcId, region));
    }
}
