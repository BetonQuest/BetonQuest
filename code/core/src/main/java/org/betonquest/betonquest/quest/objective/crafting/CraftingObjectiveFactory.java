package org.betonquest.betonquest.quest.objective.crafting;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.ItemStackCraftedEvent;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Factory for creating {@link CraftingObjective} instances from {@link Instruction}s.
 */
public class CraftingObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new CraftingObjectiveFactory instance.
     */
    public CraftingObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<ItemWrapper> item = instruction.item().get();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final CraftingObjective objective = new CraftingObjective(instruction, targetAmount, item);
        service.request(CraftItemEvent.class).priority(EventPriority.MONITOR)
                .handler(objective::onCrafting, this::fromEvent).subscribe(true);
        service.request(ItemStackCraftedEvent.class)
                .handler(objective::handleCustomCraft, this::fromProfileEvent).subscribe(false);
        return objective;
    }

    @Nullable
    private Player fromEvent(final CraftItemEvent event) {
        return event.getWhoClicked() instanceof final Player player ? player : null;
    }

    private UUID fromProfileEvent(final ItemStackCraftedEvent event) {
        return event.getProfile().getPlayerUUID();
    }
}
