package org.betonquest.betonquest.quest.objective.die;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.Location;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Factory for creating {@link DieObjective} instances from {@link Instruction}s.
 */
public class DieObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the DieObjectiveFactory.
     */
    public DieObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final FlagArgument<Boolean> cancel = instruction.bool().getFlag("cancel", true);
        final Argument<Location> location = instruction.location().get("respawn").orElse(null);
        final DieObjective objective = new DieObjective(service, cancel, location);
        service.request(EntityDeathEvent.class).priority(EventPriority.MONITOR).onlineHandler(objective::onDeath)
                .entity(EntityDeathEvent::getEntity).subscribe(true);
        service.request(PlayerRespawnEvent.class).priority(EventPriority.MONITOR).onlineHandler(objective::onRespawn)
                .player(PlayerRespawnEvent::getPlayer).subscribe(true);
        service.request(EntityDamageEvent.class).priority(EventPriority.HIGH).onlineHandler(objective::onLastDamage)
                .entity(EntityDamageEvent::getEntity).subscribe(true);
        return objective;
    }
}
