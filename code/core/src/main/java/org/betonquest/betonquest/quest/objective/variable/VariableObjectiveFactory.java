package org.betonquest.betonquest.quest.objective.variable;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Factory for creating {@link VariableObjective} instances from {@link Instruction}s.
 */
public class VariableObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new VariableObjectiveFactory instance.
     */
    public VariableObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final boolean noChat = instruction.bool().getFlag("no-chat", true)
                .getValue(null).orElse(false);
        if (noChat) {
            return new VariableObjective(instruction);
        }
        final ChatVariableObjective objective = new ChatVariableObjective(instruction);
        service.request(AsyncPlayerChatEvent.class).onlineHandler(objective::onChat)
                .player(AsyncPlayerChatEvent::getPlayer).subscribe(true);
        return objective;
    }
}
