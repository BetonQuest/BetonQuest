package org.betonquest.betonquest.quest.objective.jump;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;

/**
 * Factory for creating {@link JumpObjective} instances from {@link Instruction}s.
 */
public class JumpObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the JumpObjectiveFactory.
     */
    public JumpObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final JumpObjective objective = new JumpObjective(instruction, targetAmount);
        service.request(PlayerJumpEvent.class).onlineHandler(objective::onPlayerJump)
                .player(PlayerJumpEvent::getPlayer).subscribe(true);
        return objective;
    }
}
