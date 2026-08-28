package org.betonquest.betonquest.quest.objective.trade;

import io.papermc.paper.event.player.PlayerPurchaseEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.event.EventPriority;

import java.util.Collections;
import java.util.List;

/**
 * Factory for creating {@link TradeObjective} instances from {@link Instruction}s.
 */
public class TradeObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the TradeObjectiveFactory.
     */
    public TradeObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<List<ItemWrapper>> ingredients = instruction.item().list().get("ingredients", Collections.emptyList());
        final Argument<List<ItemWrapper>> results = instruction.item().list().get("results", Collections.emptyList());
        final FlagArgument<Boolean> exact = instruction.bool().getFlag("exact", true);
        final TradeObjective tradeObjective = new TradeObjective(service, ingredients, results, exact);
        service.request(PlayerPurchaseEvent.class)
                .onlineHandler(tradeObjective::onTrade)
                .player(PlayerPurchaseEvent::getPlayer)
                .priority(EventPriority.MONITOR)
                .subscribe(true);
        return tradeObjective;
    }
}
