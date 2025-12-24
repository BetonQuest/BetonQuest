package org.betonquest.betonquest.quest.objective.action;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Optional;

/**
 * Factory class for creating {@link ActionObjective} instances from {@link Instruction}s.
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
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Click> action = instruction.enumeration(Click.class).get();
        final Variable<Optional<BlockSelector>> selector = instruction.parse(DefaultArgumentParsers.BLOCK_SELECTOR)
                .prefilterOptional(ANY, null).get();
        final boolean exactMatch = instruction.hasArgument("exactMatch");
        final Variable<Location> loc = instruction.location().get("loc").orElse(null);
        final Variable<Number> range = instruction.number().get("range", 0);
        final boolean cancel = instruction.hasArgument("cancel");
        final Variable<Optional<EquipmentSlot>> hand = instruction.enumeration(EquipmentSlot.class)
                .validate(slot -> slot == EquipmentSlot.HAND || slot == EquipmentSlot.OFF_HAND, "Invalid hand value: '%s'")
                .prefilterOptional(ANY, null)
                .get("hand").orElse(null);
        final EquipmentSlot slot = hand == null ? null : hand.getValue(null).orElse(null);
        return new ActionObjective(instruction, action, selector, exactMatch, loc, range, cancel, slot);
    }
}
