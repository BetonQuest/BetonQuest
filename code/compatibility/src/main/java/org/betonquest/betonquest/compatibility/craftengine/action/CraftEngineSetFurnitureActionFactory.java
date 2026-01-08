package org.betonquest.betonquest.compatibility.craftengine.action;

import net.momirealms.craftengine.core.item.CustomItem;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.compatibility.craftengine.CraftEngineParser;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * A factory class responsible for parsing and creating instances of {@link CraftEngineSetFurnitureAction}.
 *
 * <p>This factory processes the instruction to extract the CraftEngine custom item,
 * the target location, and the optional sound setting required to place furniture.</p>
 */
public class CraftEngineSetFurnitureActionFactory implements PlayerActionFactory {

    /**
     * The empty default constructor.
     */
    public CraftEngineSetFurnitureActionFactory() {
        // Empty
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<CustomItem<ItemStack>> customItem = instruction.parse(CraftEngineParser.CRAFT_ENGINE_PARSER).get();
        final Argument<Location> location = instruction.location().get();
        final Argument<Boolean> playSound = instruction.bool().get("playSound", false);
        return new CraftEngineSetFurnitureAction(customItem, location, playSound);
    }
}
