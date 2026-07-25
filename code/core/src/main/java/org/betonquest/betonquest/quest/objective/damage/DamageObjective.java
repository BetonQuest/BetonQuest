package org.betonquest.betonquest.quest.objective.damage;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.lib.argument.type.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The player must deal or take a specific amount of damage.
 */
public class DamageObjective extends CountingObjective {

    /**
     * The action specifying whether the player deals or takes damage.
     */
    private final Argument<DamageAction> action;

    /**
     * The list of allowed damage types or causes.
     */
    private final Argument<List<EntityDamageEvent.DamageCause>> type;

    /**
     * The minimum amount of damage required per event.
     */
    private final Argument<Number> minAmount;

    /**
     * The minimum time interval in milliseconds between valid damage events.
     */
    private final Argument<Number> interval;

    /**
     * The unit of time used to convert the interval duration.
     */
    private final Argument<TimeUnit> timeUnit;

    /**
     * Stores the last damage event timestamp in milliseconds for each profile.
     */
    private final Map<UUID, Long> intervalTimes = new ConcurrentHashMap<>();

    /**
     * Constructs a new {@code DamageObjective} for the given {@code Instruction}.
     *
     * @param service      the objective service.
     * @param targetAmount the target amount of damage required.
     * @param action       the action specifying whether the player deals or takes damage.
     * @param type         the list of damage types or causes allowed.
     * @param minAmount    the minimum amount of damage required per event.
     * @param interval     the time interval in milliseconds between valid damage events.
     * @param timeUnit     the unit of time used to convert the interval duration
     * @throws QuestException if the instruction is invalid.
     */
    public DamageObjective(final ObjectiveService service, final Argument<Number> targetAmount,
                           final Argument<DamageAction> action, final @UnknownNullability Argument<List<EntityDamageEvent.DamageCause>> type,
                           final Argument<Number> minAmount, final Argument<Number> interval,
                           final Argument<TimeUnit> timeUnit) throws QuestException {
        super(service, targetAmount, null);
        this.action = action;
        this.type = type;
        this.minAmount = minAmount;
        this.interval = interval;
        this.timeUnit = timeUnit;
    }

    /**
     * Handles when a player deals damage and updates objective progress.
     *
     * @param event         the Bukkit entity damage event
     * @param onlineProfile the profile of the player dealing damage
     * @throws QuestException if evaluating event arguments fails
     */
    public void onDamageDealt(final EntityDamageEvent event, final OnlineProfile onlineProfile) throws QuestException {
        processDamageEvent(event, onlineProfile, DamageAction.DEAL);
    }

    /**
     * Handles when a player takes damage and updates objective progress.
     *
     * @param event         the Bukkit entity damage event
     * @param onlineProfile the profile of the player taking damage
     * @throws QuestException if evaluating event arguments fails
     */
    public void onDamageTaken(final EntityDamageEvent event, final OnlineProfile onlineProfile) throws QuestException {
        processDamageEvent(event, onlineProfile, DamageAction.TAKE);
    }

    private void processDamageEvent(final EntityDamageEvent event, final OnlineProfile profile, final DamageAction expectedAction)
            throws QuestException {
        if (action.getValue(profile) != expectedAction) {
            return;
        }

        if (checkIsInvalidType(profile, event.getCause())
                || checkIsInvalidMin(profile, event.getFinalDamage())
                || checkIsOnCooldown(profile)) {
            return;
        }

        getCountingData(profile).progress();
        completeIfDoneOrNotify(profile);
    }

    private boolean checkIsInvalidType(final OnlineProfile profile, final EntityDamageEvent.DamageCause currentCause)
            throws QuestException {
        final List<EntityDamageEvent.DamageCause> allowedTypes = type.getValue(profile);
        return !allowedTypes.contains(currentCause);
    }

    private boolean checkIsInvalidMin(final OnlineProfile profile, final double damage) throws QuestException {
        final double min = minAmount.getValue(profile).doubleValue();
        return damage < min;
    }

    private boolean checkIsOnCooldown(final OnlineProfile profile) throws QuestException {
        final long rawAmount = interval.getValue(profile).longValue();
        final TimeUnit unit = timeUnit.getValue(profile);

        final long requiredInterval = unit.getTicks(rawAmount);

        if (requiredInterval <= 0) {
            return false;
        }

        final long currentTick = Bukkit.getCurrentTick();
        final UUID profileId = profile.getProfileUUID();

        final Long expirationTime = intervalTimes.get(profileId);
        if (expirationTime != null && currentTick < expirationTime) {
            return true;
        }

        intervalTimes.put(profileId, currentTick + requiredInterval);
        return false;
    }
}
