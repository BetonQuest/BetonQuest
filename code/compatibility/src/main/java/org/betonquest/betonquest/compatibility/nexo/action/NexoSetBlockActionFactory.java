package org.betonquest.betonquest.compatibility.nexo.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.bukkit.Location;

/**
 * A factory class responsible for parsing and creating instances of {@link NexoSetBlockAction}.
 *
 * <p>This factory extracts the Nexo item ID and the target location from the
 * BetonQuest instruction to facilitate custom block placement.</p>
 */
public class NexoSetBlockActionFactory implements PlayerActionFactory {

    /**
     * The empty default constructor.
     */
    public NexoSetBlockActionFactory() {
        // Empty
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> itemId = instruction.string().get();
        final Argument<Location> location = instruction.location().get();
        return new NexoSetBlockAction(itemId, location);
    }
}
