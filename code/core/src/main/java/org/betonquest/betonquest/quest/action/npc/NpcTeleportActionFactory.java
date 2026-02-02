package org.betonquest.betonquest.quest.action.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
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
     * Create a new factory for Npc Teleport Actions.
     *
     * @param featureApi the Feature API
     */
    public NpcTeleportActionFactory(final FeatureApi featureApi) {
        this.featureApi = featureApi;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createNpcTeleportAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createNpcTeleportAction(instruction);
    }

    private NullableActionAdapter createNpcTeleportAction(final Instruction instruction) throws QuestException {
        final Argument<NpcIdentifier> npcId = instruction.identifier(NpcIdentifier.class).get();
        final Argument<Location> location = instruction.location().get();
        final FlagArgument<Boolean> spawn = instruction.bool().getFlag("spawn", true);
        return new NullableActionAdapter(new NpcTeleportAction(featureApi, npcId, location, spawn));
    }
}
