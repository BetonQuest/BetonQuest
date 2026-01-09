package org.betonquest.betonquest.quest.objective.fish;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.Location;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * Factory for creating {@link FishObjective} instances from {@link Instruction}s.
 */
public class FishObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the FishObjectiveFactory.
     */
    public FishObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<ItemWrapper> item = instruction.item().get();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final Argument<Location> hookTargetLocation = instruction.location().get("hookLocation").orElse(null);
        final Argument<Number> range = instruction.number().get("range").orElse(null);
        final FishObjective objective = new FishObjective(service, targetAmount, item, hookTargetLocation, range);
        service.request(PlayerFishEvent.class).priority(EventPriority.MONITOR).onlineHandler(objective::onFishCatch)
                .player(PlayerFishEvent::getPlayer).subscribe(true);
        return objective;
    }
}
