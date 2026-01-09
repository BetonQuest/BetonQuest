package org.betonquest.betonquest.compatibility.itemsadder.action;

import dev.lone.itemsadder.api.CustomStack;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.compatibility.itemsadder.ItemsAdderParser;
import org.bukkit.Location;

/**
 * Factory to {@link IASetBlockAction}s from {@link Instruction}s.
 */
public class IASetBlockAtActionFactory implements PlayerActionFactory {

    /**
     * The empty default constructor.
     */
    public IASetBlockAtActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<CustomStack> itemID = instruction.parse(ItemsAdderParser.ITEMS_ADDER_PARSER).get();
        final Argument<Location> location = instruction.location().get();
        return new IASetBlockAction(itemID, location);
    }
}
