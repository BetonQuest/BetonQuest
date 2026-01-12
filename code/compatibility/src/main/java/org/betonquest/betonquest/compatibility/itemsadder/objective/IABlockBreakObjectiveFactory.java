package org.betonquest.betonquest.compatibility.itemsadder.objective;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.itemsadder.ItemsAdderParser;
import org.bukkit.event.EventPriority;

/**
 * Factory to create break {@link IABlockObjective}s from {@link Instruction}s.
 */
public class IABlockBreakObjectiveFactory implements ObjectiveFactory {

    /**
     * The empty default constructor.
     */
    public IABlockBreakObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<CustomStack> itemID = instruction.parse(ItemsAdderParser.ITEMS_ADDER_PARSER).get();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get("amount", 1);
        final IABlockObjective objective = new IABlockObjective(service, targetAmount, "blocks_to_break", itemID);
        service.request(CustomBlockBreakEvent.class)
                .priority(EventPriority.MONITOR)
                .onlineHandler((event, profile) -> objective.handle(event.getNamespacedID(), profile))
                .subscribe(true);
        return objective;
    }
}
