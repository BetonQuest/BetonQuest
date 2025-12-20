package org.betonquest.betonquest.quest.objective.action;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.util.BlockSelector;
import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Optional;

/**
 * Factory class for creating {@link ActionObjective} instances from {@link DefaultInstruction}s.
 */
public class ActionObjectiveFactory implements ObjectiveFactory {

    /**
     * The "any" keyword.
     */
    private static final String ANY = "any";

    /**
     * Creates a new instance of the ActionObjectiveFactory.
     */
    public ActionObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final DefaultInstruction instruction) throws QuestException {
        final Variable<Click> action = instruction.get(Argument.ENUM(Click.class));
        final Variable<Optional<BlockSelector>> selector = instruction.get(Argument.BLOCK_SELECTOR.prefilterOptional(ANY, null));
        final boolean exactMatch = instruction.hasArgument("exactMatch");
        final Variable<Location> loc = instruction.getValue("loc", Argument.LOCATION);
        final Variable<Number> range = instruction.getValue("range", Argument.NUMBER, 0);
        final boolean cancel = instruction.hasArgument("cancel");
        final String handString = instruction.getValue("hand");
        final EquipmentSlot slot;
        if (handString == null || handString.equalsIgnoreCase(EquipmentSlot.HAND.toString())) {
            slot = EquipmentSlot.HAND;
        } else if (handString.equalsIgnoreCase(EquipmentSlot.OFF_HAND.toString())) {
            slot = EquipmentSlot.OFF_HAND;
        } else if (ANY.equalsIgnoreCase(handString)) {
            slot = null;
        } else {
            throw new QuestException("Invalid hand value: " + handString);
        }
        return new ActionObjective(instruction, action, selector, exactMatch, loc, range, cancel, slot);
    }
}
