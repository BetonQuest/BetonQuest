package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import net.Indyuce.mmocore.api.event.CustomBlockMineEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

/**
 * Factory for creating {@link MMOCoreBreakCustomBlockObjective} instances from {@link Instruction}s.
 */
public class MMOCoreBreakCustomBlockObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the MMOCoreBreakCustomBlockObjectiveFactory.
     */
    public MMOCoreBreakCustomBlockObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<String> desiredBlockId = instruction.string().get("block").orElse(null);
        if (desiredBlockId == null) {
            throw new QuestException("Missing required argument: block");
        }
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final MMOCoreBreakCustomBlockObjective objective = new MMOCoreBreakCustomBlockObjective(service, targetAmount, desiredBlockId);
        service.request(CustomBlockMineEvent.class).onlineHandler(objective::onBlockBreak)
                .player(CustomBlockMineEvent::getPlayer).subscribe(true);
        return objective;
    }
}
