package org.betonquest.betonquest.quest.objective.equip;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;

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
        final PlayerArmorChangeEvent.SlotType slotType = instruction.getEnum(PlayerArmorChangeEvent.SlotType.class);
        final Item item = instruction.getItem();
        return new EquipItemObjective(instruction, item, slotType);
    }
}
