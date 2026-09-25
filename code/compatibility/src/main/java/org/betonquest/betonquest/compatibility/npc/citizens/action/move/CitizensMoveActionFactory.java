package org.betonquest.betonquest.compatibility.npc.citizens.action.move;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensArgument;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

/**
 * Factory for {@link CitizensMoveAction} from the {@link Instruction}.
 */
public class CitizensMoveActionFactory implements PlayerActionFactory {

    /**
     * The npc manager to use.
     */
    private final NpcManager npcManager;

    /**
     * Move instance to handle movement of Citizens NPCs.
     */
    private final CitizensMoveController citizensMoveController;

    /**
     * The Citizens argument parser.
     */
    private final CitizensArgument citizensArgument;

    /**
     * Create a new NPCTeleportActionFactory.
     *
     * @param npcManager             the npc manager to use
     * @param citizensArgument       the Citizens argument parser to use
     * @param citizensMoveController the move instance to handle movement of Citizens NPCs
     */
    public CitizensMoveActionFactory(final NpcManager npcManager, final CitizensArgument citizensArgument, final CitizensMoveController citizensMoveController) {
        this.npcManager = npcManager;
        this.citizensMoveController = citizensMoveController;
        this.citizensArgument = citizensArgument;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<NpcIdentifier> npcId = instruction.parse(citizensArgument).get();
        final Argument<List<Location>> locations = instruction.location().list().invalidate(List::isEmpty).get();
        final Argument<Number> waitTicks = instruction.number().get("wait", 0);
        final Argument<List<ActionIdentifier>> doneActions = instruction.identifier(ActionIdentifier.class).list().get("done", Collections.emptyList());
        final Argument<List<ActionIdentifier>> failActions = instruction.identifier(ActionIdentifier.class).list().get("fail", Collections.emptyList());
        final FlagArgument<Boolean> blockConversations = instruction.bool().getFlag("block", true);
        final CitizensMoveController.MoveData moveAction = new CitizensMoveController.MoveData(locations, waitTicks,
                doneActions, failActions, blockConversations);
        return new CitizensMoveAction(npcManager, npcId, citizensMoveController, moveAction);
    }
}
