package org.betonquest.betonquest.quest.objective.smelt;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Factory for creating {@link SmeltingObjective} instances from {@link Instruction}s.
 */
public class SmeltingObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the SmeltingObjectiveFactory.
     */
    public SmeltingObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<ItemWrapper> item = instruction.item().get();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final SmeltingObjective objective = new SmeltingObjective(service, targetAmount, item);
        service.request(InventoryClickEvent.class).onlineHandler(objective::onSmelting)
                .entity(InventoryClickEvent::getWhoClicked).subscribe(true);
        return objective;
    }
}
