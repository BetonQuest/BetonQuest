package org.betonquest.betonquest.quest.action.hologram;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.compatibility.holograms.HologramIdentifier;
import org.betonquest.betonquest.compatibility.holograms.HologramLoop;

import java.util.List;

/**
 * Action to update the visibility or content of holograms to one player.
 */
public class UpdateHologramActionFactory implements PlayerActionFactory {

    /**
     * Hologram loop with location holograms.
     */
    private final HologramLoop locationHologramLoop;

    /**
     * Hologram loop with npc holograms.
     */
    private final HologramLoop npcHologramLoop;

    /**
     * Create the hologram update action factory.
     *
     * @param locationHologramLoop the hologram loop with location holograms
     * @param npcHologramLoop      the hologram loop with npc holograms
     */
    public UpdateHologramActionFactory(final HologramLoop locationHologramLoop, final HologramLoop npcHologramLoop) {
        this.locationHologramLoop = locationHologramLoop;
        this.npcHologramLoop = npcHologramLoop;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<UpdateMode> mode = instruction.enumeration(UpdateMode.class).get("mode", UpdateMode.ALL);
        final Argument<List<HologramIdentifier>> holo = instruction.identifier(HologramIdentifier.class).list().notEmpty().get("holo").orElse(null);
        final Argument<List<HologramIdentifier>> npcHolo = instruction.identifier(HologramIdentifier.class).list().notEmpty().get("npcHolo").orElse(null);
        return new OnlineActionAdapter(new UpdateHologramAction(locationHologramLoop, npcHologramLoop, mode, holo, npcHolo));
    }
}
