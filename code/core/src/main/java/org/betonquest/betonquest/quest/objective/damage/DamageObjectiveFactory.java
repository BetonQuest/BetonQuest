package org.betonquest.betonquest.quest.objective.damage;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.lib.argument.type.TimeUnit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Factory for creating {@link DamageObjective} instances from {@link Instruction}s.
 */
public class DamageObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new DamageObjectiveFactory instance.
     */
    public DamageObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<Number> targetAmount = instruction.number().get();
        final Argument<DamageAction> action = instruction.enumeration(DamageAction.class).get("action", DamageAction.BOTH);
        final Argument<List<EntityDamageEvent.DamageCause>> type = instruction.enumeration(EntityDamageEvent.DamageCause.class)
                .list().get("type", List.of(EntityDamageEvent.DamageCause.ENTITY_ATTACK));
        final Argument<Number> minAmount = instruction.number().get("min", 0.0);
        final Argument<Number> interval = instruction.number().get("interval", 0L);
        final Argument<TimeUnit> timeUnit = instruction.enumeration(TimeUnit.class).get("unit", TimeUnit.SECONDS);
        final DamageObjective objective = new DamageObjective(service, targetAmount, action, type, minAmount, interval, timeUnit);
        service.request(EntityDamageByEntityEvent.class)
                .priority(EventPriority.HIGHEST).onlineHandler(objective::onDamageDealt)
                .player(event -> resolveAttackingPlayer(event.getDamager()))
                .subscribe(false);
        service.request(EntityDamageEvent.class)
                .priority(EventPriority.HIGHEST).onlineHandler(objective::onDamageTaken)
                .player(event -> event.getEntity() instanceof final Player player ? player : null)
                .subscribe(false);
        return objective;
    }

    private @Nullable Player resolveAttackingPlayer(final Entity damager) {
        if (damager instanceof final Player player) {
            return player;
        }
        if (damager instanceof final Projectile projectile && projectile.getShooter() instanceof final Player shooter) {
            return shooter;
        }
        return null;
    }
}
