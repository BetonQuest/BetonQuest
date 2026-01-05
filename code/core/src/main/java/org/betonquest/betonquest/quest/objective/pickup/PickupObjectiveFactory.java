package org.betonquest.betonquest.quest.objective.pickup;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.List;

/**
 * Factory for creating {@link PickupObjective} instances from {@link Instruction}s.
 */
public class PickupObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the PickupObjectiveFactory.
     */
    public PickupObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<List<ItemWrapper>> pickupItems = instruction.item().list().get();
        final Argument<Number> targetAmount = instruction.number().get("amount", 1);
        final PickupObjective objective = new PickupObjective(instruction, targetAmount, pickupItems);
        service.request(EntityPickupItemEvent.class)
                .handler(objective::onPickup, EntityPickupItemEvent::getEntity).subscribe(true);
        return objective;
    }
}
