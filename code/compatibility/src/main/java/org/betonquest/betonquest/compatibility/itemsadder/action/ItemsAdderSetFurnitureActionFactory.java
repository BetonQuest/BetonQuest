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
 * A factory class responsible for parsing and creating instances of {@link ItemsAdderSetFurnitureAction}.
 *
 * <p>This factory parses the ItemsAdder custom stack and the target location from
 * the BetonQuest instruction to facilitate spawning custom furniture.</p>
 */
public class ItemsAdderSetFurnitureActionFactory implements PlayerActionFactory {

    /**
     * The empty default constructor.
     */
    public ItemsAdderSetFurnitureActionFactory() {
        // Empty
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<CustomStack> customStackArgument = instruction.parse(ItemsAdderParser.ITEMS_ADDER_PARSER).get();
        final Argument<Location> locationArgument = instruction.location().get();
        return new ItemsAdderSetFurnitureAction(customStackArgument, locationArgument);
    }
}
