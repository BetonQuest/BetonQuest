package org.betonquest.betonquest.quest.event.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadPlayerlessEvent;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.bukkit.Location;

/**
 * Factory for {@link NpcTeleportEvent} from the {@link Instruction}.
 */
public class NpcTeleportEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Quest Type API.
     */
    private final FeatureApi featureApi;

    /**
     * Data to use for syncing to the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for Npc Teleport Events.
     *
     * @param featureApi the Feature API
     * @param data       the data to use for syncing to the primary server thread
     */
    public NpcTeleportEventFactory(final FeatureApi featureApi, final PrimaryServerThreadData data) {
        this.featureApi = featureApi;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createNpcTeleportEvent(instruction), data);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessEvent(createNpcTeleportEvent(instruction), data);
    }

    private NullableEventAdapter createNpcTeleportEvent(final Instruction instruction) throws QuestException {
        final Variable<NpcID> npcId = instruction.parse(NpcID::new).get();
        final Variable<Location> location = instruction.location().get();
        final boolean spawn = instruction.hasArgument("spawn");
        return new NullableEventAdapter(new NpcTeleportEvent(featureApi, npcId, location, spawn));
    }
}
