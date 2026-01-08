package org.betonquest.betonquest.quest.objective.shear;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.DyeColor;
import org.bukkit.event.player.PlayerShearEntityEvent;

/**
 * Factory for creating {@link ShearObjective} instances from {@link Instruction}s.
 */
public class ShearObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the ShearObjectiveFactory.
     */
    public ShearObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final Argument<String> name = instruction.string().get("name").orElse(null);
        final Argument<DyeColor> color = instruction.enumeration(DyeColor.class).get("color").orElse(null);
        final ShearObjective objective = new ShearObjective(service, targetAmount, name, color);
        service.request(PlayerShearEntityEvent.class).onlineHandler(objective::onShear)
                .player(PlayerShearEntityEvent::getPlayer).subscribe(true);
        return objective;
    }
}
