package org.betonquest.betonquest.quest.objective.equip;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

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
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<PlayerArmorChangeEvent.SlotType> slotType = instruction.enumeration(PlayerArmorChangeEvent.SlotType.class).get();
        final Argument<ItemWrapper> item = instruction.item().get();
        final EquipItemObjective objective = new EquipItemObjective(service, item, slotType);
        service.request(PlayerArmorChangeEvent.class).onlineHandler(objective::onEquipmentChange)
                .player(PlayerArmorChangeEvent::getPlayer).subscribe(false);
        return objective;
    }
}
