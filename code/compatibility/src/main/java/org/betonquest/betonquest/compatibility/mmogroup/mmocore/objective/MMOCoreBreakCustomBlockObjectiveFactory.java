package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import net.Indyuce.mmocore.api.event.CustomBlockMineEvent;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;

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
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<String> desiredBlockId = instruction.string().get("block").orElse(null);
        if (desiredBlockId == null) {
            throw new QuestException("Missing required argument: block");
        }
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final MMOCoreBreakCustomBlockObjective objective = new MMOCoreBreakCustomBlockObjective(instruction, targetAmount, desiredBlockId);
        service.request(CustomBlockMineEvent.class).onlineHandler(objective::onBlockBreak)
                .player(CustomBlockMineEvent::getPlayer).subscribe(true);
        return objective;
    }
}
