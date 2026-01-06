package org.betonquest.betonquest.quest.objective.logout;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Factory for creating {@link LogoutObjective} instances from {@link Instruction}s.
 */
public class LogoutObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the LogoutObjectiveFactory.
     */
    public LogoutObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final LogoutObjective objective = new LogoutObjective(instruction);
        service.request(PlayerQuitEvent.class).priority(EventPriority.LOWEST)
                .onlineHandler(objective::onQuit).player(PlayerQuitEvent::getPlayer).subscribe(true);
        return objective;
    }
}
