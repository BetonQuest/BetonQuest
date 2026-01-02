package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;

/**
 * Factory for creating {@link MMOCoreProfessionObjective} instances from {@link Instruction}s.
 */
public class MMOCoreProfessionObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the MMOCoreProfessionObjectiveFactory.
     */
    public MMOCoreProfessionObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<String> professionName = instruction.string().get();
        final Argument<Number> targetLevel = instruction.number().get();
        final MMOCoreProfessionObjective objective = new MMOCoreProfessionObjective(instruction, professionName, targetLevel);
        service.request(PlayerLevelUpEvent.class).handler(objective::onLevelUp, PlayerLevelUpEvent::getPlayer).subscribe(true);
        return objective;
    }
}
