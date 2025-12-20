package org.betonquest.betonquest.quest.condition.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
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
import org.bukkit.Location;

/**
 * Factory to create {@link NpcLocationCondition}s from {@link DefaultInstruction}s.
 */
public class NpcLocationConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Quest Type API.
     */
    private final FeatureApi featureApi;

    /**
     * Data used for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for NPC Location Conditions.
     *
     * @param featureApi the Feature API
     * @param data       the data to use for syncing to the primary server thread
     */
    public NpcLocationConditionFactory(final FeatureApi featureApi, final PrimaryServerThreadData data) {
        this.featureApi = featureApi;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final DefaultInstruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerCondition(parseNpcLocationCondition(instruction), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final DefaultInstruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessCondition(parseNpcLocationCondition(instruction), data);
    }

    private NullableConditionAdapter parseNpcLocationCondition(final DefaultInstruction instruction) throws QuestException {
        final Variable<NpcID> npcId = instruction.get(NpcID::new);
        final Variable<Location> location = instruction.get(Argument.LOCATION);
        final Variable<Number> radius = instruction.get(Argument.NUMBER);
        return new NullableConditionAdapter(new NpcLocationCondition(featureApi, npcId, location, radius));
    }
}
