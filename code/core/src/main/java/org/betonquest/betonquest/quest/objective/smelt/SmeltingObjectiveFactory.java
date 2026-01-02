package org.betonquest.betonquest.quest.objective.smelt;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

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
        final SmeltingObjective objective = new SmeltingObjective(instruction, targetAmount, item);
        service.request(InventoryClickEvent.class)
                .handler(objective::onSmelting, this::fromEvent).subscribe(true);
        return objective;
    }

    @Nullable
    private Player fromEvent(final InventoryClickEvent event) {
        return event.getWhoClicked() instanceof final Player player ? player : null;
    }
}
