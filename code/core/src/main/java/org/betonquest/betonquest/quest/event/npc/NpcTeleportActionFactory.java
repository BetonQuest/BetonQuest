package org.betonquest.betonquest.quest.event.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.bukkit.Location;

/**
 * Factory for {@link NpcTeleportAction} from the {@link Instruction}.
 */
public class NpcTeleportActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Quest Type API.
     */
    private final FeatureApi featureApi;

    /**
     * Create a new factory for Npc Teleport Events.
     *
     * @param featureApi the Feature API
     */
    public NpcTeleportActionFactory(final FeatureApi featureApi) {
        this.featureApi = featureApi;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createNpcTeleportEvent(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createNpcTeleportEvent(instruction);
    }

    private NullableActionAdapter createNpcTeleportEvent(final Instruction instruction) throws QuestException {
        final Argument<NpcID> npcId = instruction.parse(NpcID::new).get();
        final Argument<Location> location = instruction.location().get();
        final FlagArgument<Boolean> spawn = instruction.bool().getFlag("spawn", true);
        return new NullableActionAdapter(new NpcTeleportAction(featureApi, npcId, location, spawn));
    }
}
