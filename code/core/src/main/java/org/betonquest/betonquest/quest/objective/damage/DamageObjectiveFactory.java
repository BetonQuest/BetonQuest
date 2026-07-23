package org.betonquest.betonquest.quest.objective.damage;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
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
     * The name of the parameter that determines whether the player is the one attacking or taking damage.
     */
    public static final String ACTION_ARGUMENT = "action";

    /**
     * The name of the parameter that determines the type of damage to be dealt.
     */
    public static final String TYPE_ARGUMENT = "type";

    /**
     * The name of the parameter that defines the minimum amount of damage.
     */
    public static final String MIN_AMOUNT_ARGUMENT = "min";

    /**
     * The name of the parameter that defines the delay between each attack.
     */
    public static final String INTERVAL_ARGUMENT = "interval";

    /**
     * Creates a new DamageObjectiveFactory instance.
     */
    public DamageObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<Number> targetAmount = instruction.number().get();
        final Argument<String> action = instruction.string().get(ACTION_ARGUMENT, "deal");
        final Argument<List<String>> type = instruction.string().list().get(TYPE_ARGUMENT, List.of("entity_attack"));
        final Argument<Number> minAmount = instruction.number().get(MIN_AMOUNT_ARGUMENT, 0.0);
        final Argument<Number> interval = instruction.number().get(INTERVAL_ARGUMENT, 0L);
        final DamageObjective objective = new DamageObjective(service, targetAmount, action, type, minAmount, interval);
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
