package org.betonquest.betonquest.quest.objective.consume;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.player.PlayerItemConsumeEvent;

/**
 * Factory for creating {@link ConsumeObjective} instances from {@link Instruction}s.
 */
public class ConsumeObjectiveFactory implements ObjectiveFactory {

    /**
     * The name of the argument that determines the amount of items to consume.
     */
    public static final String AMOUNT_ARGUMENT = "amount";

    /**
     * Constructs a new ConsumeObjectiveFactory.
     */
    public ConsumeObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<ItemWrapper> item = instruction.item().get();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get(AMOUNT_ARGUMENT, 1);
        final ConsumeObjective objective = new ConsumeObjective(instruction, targetAmount, item);
        service.request(PlayerItemConsumeEvent.class).handler(objective::onConsume, PlayerItemConsumeEvent::getPlayer)
                .subscribe(true);
        return objective;
    }
}
