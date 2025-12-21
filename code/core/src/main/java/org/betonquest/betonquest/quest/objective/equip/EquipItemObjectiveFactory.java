package org.betonquest.betonquest.quest.objective.equip;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.instruction.argument.InstructionIdentifierArgument;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

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
        final Variable<PlayerArmorChangeEvent.SlotType> slotType = instruction.get(DefaultArgumentParsers.forEnumeration(PlayerArmorChangeEvent.SlotType.class));
        final Variable<Item> item = instruction.get(InstructionIdentifierArgument.ITEM);
        return new EquipItemObjective(instruction, item, slotType);
    }
}
