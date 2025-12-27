package org.betonquest.betonquest.quest.event.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
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
     * Create a new factory for Npc Teleport Events.
     *
     * @param featureApi the Feature API
     */
    public NpcTeleportEventFactory(final FeatureApi featureApi) {
        this.featureApi = featureApi;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createNpcTeleportEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createNpcTeleportEvent(instruction);
    }

    private NullableEventAdapter createNpcTeleportEvent(final Instruction instruction) throws QuestException {
        final Argument<NpcID> npcId = instruction.parse(NpcID::new).get();
        final Argument<Location> location = instruction.location().get();
        final FlagArgument<Boolean> spawn = instruction.bool().getFlag("spawn", false);
        return new NullableEventAdapter(new NpcTeleportEvent(featureApi, npcId, location, spawn));
    }
}
