package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective;

import net.Indyuce.mmoitems.api.event.item.ApplyGemStoneEvent;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;

/**
 * Factory for creating {@link MMOItemsApplyGemObjective} instances from {@link Instruction}s.
 */
public class MMOItemsApplyGemObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the MMOItemsApplyGemObjectiveFactory.
     */
    public MMOItemsApplyGemObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<String> itemType = instruction.string().get();
        final Argument<String> itemID = instruction.string().get();
        final Argument<String> gemID = instruction.string().get();
        final MMOItemsApplyGemObjective objective = new MMOItemsApplyGemObjective(service, itemType, itemID, gemID);
        service.request(ApplyGemStoneEvent.class).onlineHandler(objective::onApplyGem)
                .player(ApplyGemStoneEvent::getPlayer).subscribe(true);
        return objective;
    }
}
