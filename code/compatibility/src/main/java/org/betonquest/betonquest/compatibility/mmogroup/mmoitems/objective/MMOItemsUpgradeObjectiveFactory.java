package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective;

import net.Indyuce.mmoitems.api.event.item.UpgradeItemEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;

/**
 * Factory for creating {@link MMOItemsUpgradeObjective} instances from {@link Instruction}s.
 */
public class MMOItemsUpgradeObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the MMOItemsUpgradeObjectiveFactory.
     */
    public MMOItemsUpgradeObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<String> itemType = instruction.string().get();
        final Argument<String> itemID = instruction.string().get();
        final MMOItemsUpgradeObjective objective = new MMOItemsUpgradeObjective(service, itemType, itemID);
        service.request(UpgradeItemEvent.class).onlineHandler(objective::onUpgradeItem)
                .player(UpgradeItemEvent::getPlayer).subscribe(true);
        return objective;
    }
}
