package org.betonquest.betonquest.compatibility.worldguard.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
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
     * Create a new factory for NPC Region Conditions.
     *
     * @param featureApi the Feature API
     */
    public NpcRegionConditionFactory(final FeatureApi featureApi) {
        this.featureApi = featureApi;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    private NullableConditionAdapter parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<NpcID> npcId = instruction.parse(NpcID::new).get();
        final Argument<String> region = instruction.string().get();
        return new NullableConditionAdapter(new NpcRegionCondition(featureApi, npcId, region));
    }
}
