package org.betonquest.betonquest.quest.objective.login;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveService;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Factory for creating {@link LoginObjective} instances from {@link Instruction}s.
 */
public class LoginObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the LoginObjectiveFactory.
     */
    public LoginObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final LoginObjective objective = new LoginObjective(service);
        service.request(PlayerJoinEvent.class).priority(EventPriority.HIGH).onlineHandler(objective::onJoin)
                .player(PlayerJoinEvent::getPlayer).subscribe(true);
        return objective;
    }
}
