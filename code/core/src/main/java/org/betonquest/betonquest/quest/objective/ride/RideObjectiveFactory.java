package org.betonquest.betonquest.quest.objective.ride;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.entity.EntityType;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.Optional;

/**
 * Factory for creating {@link RideObjective} instances from {@link Instruction}s.
 */
public class RideObjectiveFactory implements ObjectiveFactory {

    /**
     * Any property for the entity type.
     */
    private static final String ANY_PROPERTY = "any";

    /**
     * Creates a new instance of the RideObjectiveFactory.
     */
    public RideObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<Optional<EntityType>> vehicle = instruction.enumeration(EntityType.class)
                .prefilterOptional(ANY_PROPERTY, null).get();
        final RideObjective objective = new RideObjective(service, vehicle);
        service.request(EntityMountEvent.class).onlineHandler(objective::onMount)
                .entity(EntityMountEvent::getEntity).subscribe(true);
        return objective;
    }
}
