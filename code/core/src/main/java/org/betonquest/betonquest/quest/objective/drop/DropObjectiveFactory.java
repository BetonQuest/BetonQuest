package org.betonquest.betonquest.quest.objective.drop;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.List;

/**
 * Factory for creating {@link DropObjective} instances from {@link Instruction}s.
 */
public class DropObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the DropObjectiveFactory.
     */
    public DropObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<List<ItemWrapper>> dropItems = instruction.item().list().get();
        final DropObjective dropObjective = new DropObjective(service, dropItems);
        service.request(PlayerDropItemEvent.class).onlineHandler(dropObjective::onDrop)
                .player(PlayerDropItemEvent::getPlayer).subscribe(true);
        return dropObjective;
    }
}
