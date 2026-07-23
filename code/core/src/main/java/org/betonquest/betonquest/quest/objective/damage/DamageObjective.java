package org.betonquest.betonquest.quest.objective.damage;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The player must deal or take a specific amount of damage.
 */
public class DamageObjective extends CountingObjective {

    /**
     * Maximum duration in milliseconds to keep cache entries before removal (10 minutes).
     */
    private static final long CACHE_EXPIRATION_MS = 600_000L;

    /**
     * The action specifying whether the player deals or takes damage.
     */
    private final Argument<String> action;

    /**
     * The list of allowed damage types or causes.
     */
    private final Argument<List<String>> type;

    /**
     * The minimum amount of damage required per event.
     */
    private final Argument<Number> minAmount;

    /**
     * The minimum time interval in milliseconds between valid damage events.
     */
    private final Argument<Number> interval;

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
     * @throws QuestException if the instruction is invalid.
     */
    public DamageObjective(final ObjectiveService service, final Argument<Number> targetAmount,
                           final Argument<String> action, final Argument<List<String>> type,
                           final Argument<Number> minAmount, final Argument<Number> interval) throws QuestException {
        super(service, targetAmount, null);
        this.action = action;
        this.type = type;
        this.minAmount = minAmount;
        this.interval = interval;
    }

    /**
     * Handles when a player deals damage and updates objective progress.
     *
     * @param event         the Bukkit entity damage event
     * @param onlineProfile the profile of the player dealing damage
     * @throws QuestException if evaluating event arguments fails
     */
    public void onDamageDealt(final EntityDamageEvent event, final OnlineProfile onlineProfile) throws QuestException {
        processDamageEvent(event, onlineProfile, "deal");
    }

    /**
     * Handles when a player takes damage and updates objective progress.
     *
     * @param event         the Bukkit entity damage event
     * @param onlineProfile the profile of the player taking damage
     * @throws QuestException if evaluating event arguments fails
     */
    public void onDamageTaken(final EntityDamageEvent event, final OnlineProfile onlineProfile) throws QuestException {
        processDamageEvent(event, onlineProfile, "take");
    }

    private void processDamageEvent(final EntityDamageEvent event, final OnlineProfile profile, final String expectedAction)
            throws QuestException {
        if (!action.getValue(profile).equalsIgnoreCase(expectedAction)) {
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

    private boolean checkIsInvalidType(final OnlineProfile profile, final EntityDamageEvent.DamageCause cause)
            throws QuestException {
        final List<String> allowedTypes = type.getValue(profile);
        final String currentCause = cause.name();

        for (final String allowedType : allowedTypes) {
            if (allowedType.equalsIgnoreCase(currentCause)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkIsInvalidMin(final OnlineProfile profile, final double damage) throws QuestException {
        final double min = minAmount.getValue(profile).doubleValue();
        return damage < min;
    }

    private boolean checkIsOnCooldown(final OnlineProfile profile) throws QuestException {
        final long requiredInterval = interval.getValue(profile).longValue();

        if (requiredInterval <= 0) {
            return false;
        }

        final UUID profileId = profile.getProfileUUID();
        final long currentTime = System.currentTimeMillis();

        intervalTimes.entrySet().removeIf(entry -> (currentTime - entry.getValue()) > CACHE_EXPIRATION_MS);

        final Long lastTime = intervalTimes.get(profileId);
        if (lastTime != null) {
            final long timePassed = currentTime - lastTime;
            if (timePassed < requiredInterval) {
                return true;
            }
        }

        intervalTimes.put(profileId, currentTime);
        return false;
    }
}
