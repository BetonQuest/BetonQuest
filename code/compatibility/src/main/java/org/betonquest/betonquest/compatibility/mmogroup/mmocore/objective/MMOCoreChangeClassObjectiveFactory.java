package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import net.Indyuce.mmocore.api.event.PlayerChangeClassEvent;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;

/**
 * Factory for creating {@link MMOCoreChangeClassObjective} instances from {@link Instruction}s.
 */
public class MMOCoreChangeClassObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the MMOCoreChangeClassObjectiveFactory.
     */
    public MMOCoreChangeClassObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<String> targetClassName = instruction.string().get("class").orElse(null);
        final MMOCoreChangeClassObjective objective = new MMOCoreChangeClassObjective(instruction, targetClassName);
        service.request(PlayerChangeClassEvent.class).onlineHandler(objective::onClassChange)
                .player(PlayerChangeClassEvent::getPlayer).subscribe(true);
        return objective;
    }
}
