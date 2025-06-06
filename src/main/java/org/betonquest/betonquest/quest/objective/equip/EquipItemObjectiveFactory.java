package org.betonquest.betonquest.quest.objective.equip;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Factory for creating {@link EquipItemObjective} instances from {@link Instruction}s.
 */
public class EquipItemObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the EquipItemObjectiveFactory.
     */
    public EquipItemObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<PlayerArmorChangeEvent.SlotType> slotType = instruction.get(Argument.ENUM(PlayerArmorChangeEvent.SlotType.class));
        final Variable<Item> item = instruction.get(PackageArgument.ITEM);
        return new EquipItemObjective(instruction, item, slotType);
    }
}
