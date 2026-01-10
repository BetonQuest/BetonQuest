package org.betonquest.betonquest.quest.objective.command;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveService;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Collections;
import java.util.List;

/**
 * Factory for creating {@link CommandObjective} instances from {@link Instruction}s.
 */
public class CommandObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the CommandObjectiveFactory.
     */
    public CommandObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<String> command = instruction.string().get();
        final FlagArgument<Boolean> ignoreCase = instruction.bool().getFlag("ignoreCase", true);
        final FlagArgument<Boolean> exact = instruction.bool().getFlag("exact", true);
        final FlagArgument<Boolean> cancel = instruction.bool().getFlag("cancel", true);
        final Argument<List<ActionID>> failEvents = instruction.parse(ActionID::new)
                .list().get("failActions", Collections.emptyList());
        final CommandObjective objective = new CommandObjective(service, command, ignoreCase, exact, cancel, failEvents);
        service.request(PlayerCommandPreprocessEvent.class).priority(EventPriority.LOWEST).onlineHandler(objective::onCommand)
                .player(PlayerCommandPreprocessEvent::getPlayer).subscribe(false);
        return objective;
    }
}
