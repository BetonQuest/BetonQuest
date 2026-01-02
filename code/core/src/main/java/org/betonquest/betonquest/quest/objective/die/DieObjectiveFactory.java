package org.betonquest.betonquest.quest.objective.die;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.Nullable;

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
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final FlagArgument<Boolean> cancel = instruction.bool().getFlag("cancel", true);
        final Argument<Location> location = instruction.location().get("respawn").orElse(null);
        final DieObjective objective = new DieObjective(instruction, cancel, location);
        service.request(EntityDeathEvent.class).priority(EventPriority.MONITOR)
                .handler(objective::onDeath, this::fromEvent).subscribe(true);
        service.request(PlayerRespawnEvent.class).priority(EventPriority.MONITOR)
                .handler(objective::onRespawn, PlayerRespawnEvent::getPlayer).subscribe(true);
        service.request(EntityDamageEvent.class).priority(EventPriority.HIGH)
                .handler(objective::onLastDamage, this::fromEvent).subscribe(true);
        return objective;
    }

    @Nullable
    private Player fromEvent(final EntityEvent event) {
        return event.getEntity() instanceof final Player player ? player : null;
    }
}
